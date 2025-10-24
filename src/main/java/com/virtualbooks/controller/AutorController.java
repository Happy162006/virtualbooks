package com.virtualbooks.controller;

import com.virtualbooks.model.Autor;
import com.virtualbooks.model.Usuario;
import com.virtualbooks.repository.UsuarioRepository;
import com.virtualbooks.service.AutorService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user/autores")
public class AutorController {

    private final AutorService autorService;
    private final UsuarioRepository usuarioRepository;

    // Carpeta externa para almacenar imágenes
    private final String UPLOAD_DIR = new File("uploads/").getAbsolutePath();

    public AutorController(AutorService autorService, UsuarioRepository usuarioRepository) {
        this.autorService = autorService;
        this.usuarioRepository = usuarioRepository;

        // Crear carpeta si no existe
        File carpeta = new File(UPLOAD_DIR);
        if (!carpeta.exists()) carpeta.mkdirs();
    }

    @GetMapping
    public String listarAutores(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername());
        List<Autor> autores = autorService.listarAutoresPorUsuario(usuario.getId());
        model.addAttribute("autores", autores);
        return "autores/list";
    }

    @GetMapping("/editar/{id}")
    @ResponseBody
    public Autor obtenerAutor(@PathVariable Long id) {
        return autorService.obtenerPorId(id);
    }

    // --- AGREGAR AUTOR CON IMAGEN ---
    @PostMapping("/agregar")
    @ResponseBody
    public String agregarAutor(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam String nombre,
                               @RequestParam String nacionalidad,
                               @RequestParam String fechaNacido,
                               @RequestParam(required = false) MultipartFile imagen) throws IOException {

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername());
        if (usuario == null) return "{\"status\":\"error\"}";

        Autor autor = new Autor();
        autor.setNombre(nombre);
        autor.setNacionalidad(nacionalidad);
        autor.setFechaNacido(fechaNacido);
        autor.setUsuario(usuario);

        if (imagen != null && !imagen.isEmpty()) {
            String nombreArchivo = UUID.randomUUID() + "_" + imagen.getOriginalFilename();
            File archivoDestino = new File(UPLOAD_DIR, nombreArchivo);
            imagen.transferTo(archivoDestino);

            autor.setImagen("/uploads/" + nombreArchivo); // Ruta web accesible
        }

        autorService.guardarAutor(autor);
        return "{\"status\":\"ok\", \"autorId\": " + autor.getId() + "}";
    }

    // --- EDITAR AUTOR CON IMAGEN ---
    @PostMapping("/editar/{id}")
    @ResponseBody
    public String editarAutor(@PathVariable Long id,
                              @RequestParam String nombre,
                              @RequestParam String nacionalidad,
                              @RequestParam String fechaNacido,
                              @RequestParam(required = false) MultipartFile imagen,
                              @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername());
        if (usuario == null) return "{\"status\":\"error\"}";

        Autor autor = autorService.obtenerPorId(id);
        if (autor == null) return "{\"status\":\"error\"}";

        autor.setNombre(nombre);
        autor.setNacionalidad(nacionalidad);
        autor.setFechaNacido(fechaNacido);
        autor.setUsuario(usuario);

        if (imagen != null && !imagen.isEmpty()) {
            // Eliminar imagen antigua si existe
            if (autor.getImagen() != null && !autor.getImagen().isEmpty()) {
                File antigua = new File("uploads/" + new File(autor.getImagen()).getName());
                if (antigua.exists()) antigua.delete();
            }

            String nombreArchivo = UUID.randomUUID() + "_" + imagen.getOriginalFilename();
            File archivoDestino = new File(UPLOAD_DIR, nombreArchivo);
            imagen.transferTo(archivoDestino);

            autor.setImagen("/uploads/" + nombreArchivo);
        }

        autorService.guardarAutor(autor);
        return "{\"status\":\"ok\"}";
    }

    // --- ELIMINAR AUTOR Y SU IMAGEN ---
    @PostMapping("/eliminar/{id}")
    @ResponseBody
    public String eliminarAutor(@PathVariable Long id) {
        Autor autor = autorService.obtenerPorId(id);
        if (autor != null) {
            // Eliminar imagen si existe
            if (autor.getImagen() != null && !autor.getImagen().isEmpty()) {
                File archivo = new File("uploads/" + new File(autor.getImagen()).getName());
                if (archivo.exists()) archivo.delete();
            }
            autorService.eliminarAutor(id);
        }
        return "{\"status\":\"ok\"}";
    }
}
