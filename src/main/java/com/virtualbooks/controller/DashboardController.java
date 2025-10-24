package com.virtualbooks.controller;

import com.virtualbooks.model.Autor;
import com.virtualbooks.model.Categoria;
import com.virtualbooks.model.Libro;
import com.virtualbooks.model.Usuario;
import com.virtualbooks.repository.AutorRepository;
import com.virtualbooks.repository.CategoriaRepository;
import com.virtualbooks.repository.LibroRepository;
import com.virtualbooks.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class DashboardController {

    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final CategoriaRepository categoriaRepository;

    public DashboardController(UsuarioRepository usuarioRepository,
                               LibroRepository libroRepository,
                               AutorRepository autorRepository,
                               CategoriaRepository categoriaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario.getRol() == 1) {
            return "dashboard/admin"; // admin
        }

        // Estadísticas
        int cantidadLibros = libroRepository.findByUserId(usuario.getId()).size();
        int cantidadAutores = autorRepository.findByUsuarioId(usuario.getId()).size();
        int cantidadCategorias = categoriaRepository.findByUsuarioId(usuario.getId()).size();

        // Últimos agregados
        List<Libro> ultimosLibros = libroRepository.findTop5ByUserIdOrderByIdDesc(usuario.getId());
        List<Autor> ultimosAutores = autorRepository.findTop5ByUsuarioIdOrderByIdDesc(usuario.getId());
        List<Categoria> ultimasCategorias = categoriaRepository.findTop5ByUsuarioIdOrderByIdDesc(usuario.getId());

        // Datos generales para gráficas
        List<Libro> libros = libroRepository.findByUserId(usuario.getId());
        List<Autor> autores = autorRepository.findByUsuarioId(usuario.getId());
        List<Categoria> categorias = categoriaRepository.findByUsuarioId(usuario.getId());

        // Fecha y hora actual
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaHoraFormateada = fechaHoraActual.format(formatter);

        model.addAttribute("usuario", usuario);
        model.addAttribute("cantidadLibros", cantidadLibros);
        model.addAttribute("cantidadAutores", cantidadAutores);
        model.addAttribute("cantidadCategorias", cantidadCategorias);

        model.addAttribute("ultimosLibros", ultimosLibros);
        model.addAttribute("ultimosAutores", ultimosAutores);
        model.addAttribute("ultimasCategorias", ultimasCategorias);

        model.addAttribute("libros", libros);
        model.addAttribute("autores", autores);
        model.addAttribute("categorias", categorias);

        model.addAttribute("fechaHoraActual", fechaHoraFormateada);

        return "dashboard/user";
    }
}
