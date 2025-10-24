package com.virtualbooks.controller;

import com.virtualbooks.model.Libro;
import com.virtualbooks.model.Usuario;
import com.virtualbooks.service.AutorService;
import com.virtualbooks.service.CategoriaService;
import com.virtualbooks.service.LibroService;
import com.virtualbooks.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/user/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private AutorService autorService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UsuarioService usuarioService;

    // 📖 Mostrar lista de libros
    @GetMapping
    public String listarLibros(Model model) {
        Usuario user = obtenerUsuarioAutenticado();
        if (user == null) return "redirect:/login";

        model.addAttribute("libros", libroService.listar(user));
        model.addAttribute("autores", autorService.listarAutoresPorUsuario(user.getId()));
        model.addAttribute("categorias", categoriaService.listarCategoriasPorUsuario(user.getId()));

        return "libros/misLibros";
    }

    // 🔄 Toggle favorito
    @PostMapping("/favorito/{id}")
    @ResponseBody
    public String toggleFavorito(@PathVariable Long id) {
        Usuario user = obtenerUsuarioAutenticado();
        if (user == null) return "{\"status\":\"error\",\"message\":\"Usuario no autenticado\"}";

        Libro libro = libroService.obtenerPorId(id).orElse(null);
        if (libro == null) return "{\"status\":\"not_found\"}";

        libro.setFavorito(!libro.isFavorito());
        libroService.guardar(libro);

        return "{\"status\":\"ok\",\"favorito\":" + libro.isFavorito() + "}";
    }

    // 📖 Listar libros favoritos
    @GetMapping("/favoritos")
    public String listarFavoritos(Model model) {
        Usuario user = obtenerUsuarioAutenticado();
        if (user == null) return "redirect:/login";

        List<Libro> favoritos = libroService.listarFavoritos(user);
        model.addAttribute("libros", favoritos);

        return "libros/librosFav"; // Debe existir: src/main/resources/templates/libros/librosFav.html
    }

    // ➕ Agregar nuevo libro
    @PostMapping("/agregar")
    @ResponseBody
    public String agregarLibro(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam Long authorId,
            @RequestParam Long categoryId,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) {
        try {
            Usuario user = obtenerUsuarioAutenticado();
            if (user == null) return "{\"status\":\"error\",\"message\":\"Usuario no autenticado\"}";

            Libro libro = new Libro();
            libro.setUser(user);
            libro.setTitulo(titulo);
            libro.setDescripcion(descripcion);
            libro.setAuthor(autorService.obtenerPorId(authorId));
            libro.setCategory(categoriaService.obtenerPorId(categoryId));

            // Guardar imagen
            if (imagen != null && !imagen.isEmpty()) {
                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

                String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                Path ruta = uploadDir.resolve(nombreArchivo);
                Files.write(ruta, imagen.getBytes());

                libro.setImagen("/uploads/" + nombreArchivo);
            }

            libroService.guardar(libro);
            return "{\"status\":\"ok\"}";
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"status\":\"error\"}";
        }
    }

    // 🔑 Método helper para obtener el usuario autenticado
    private Usuario obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) return null;

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return usuarioService.obtenerPorEmail(userDetails.getUsername());
    }
}
