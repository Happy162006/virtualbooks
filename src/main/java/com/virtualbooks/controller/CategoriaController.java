package com.virtualbooks.controller;

import com.virtualbooks.model.Categoria;
import com.virtualbooks.model.Usuario;
import com.virtualbooks.repository.UsuarioRepository;
import com.virtualbooks.service.CategoriaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final UsuarioRepository usuarioRepository;

    public CategoriaController(CategoriaService categoriaService, UsuarioRepository usuarioRepository) {
        this.categoriaService = categoriaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String listarCategorias(@AuthenticationPrincipal UserDetails userDetails, org.springframework.ui.Model model) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername());
        List<Categoria> categorias = categoriaService.listarCategoriasPorUsuario(usuario.getId());
        model.addAttribute("categorias", categorias);
        return "autores/categorias";
    }

    // Guardar nueva categoría
    @PostMapping("/guardar")
    @ResponseBody
    public String guardarCategoria(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam String nombre,
                                   @RequestParam(required = false) String descripcion,
                                   @RequestParam(required = false) String codigo,
                                   @RequestParam(required = false) String tipo) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername());
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setCodigo(codigo);
        categoria.setTipo(tipo);
        categoria.setUsuario(usuario);

        categoriaService.guardarCategoria(categoria);
        return "{\"status\":\"ok\",\"message\":\"Categoría agregada correctamente\"}";
    }

    // Eliminar categoría
    @PostMapping("/eliminar/{id}")
    @ResponseBody
    public String eliminarCategoria(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername());
        Categoria categoria = categoriaService.obtenerPorId(id);

        if (categoria == null) return "{\"status\":\"error\",\"message\":\"Categoría no encontrada\"}";

        if (!categoria.getUsuario().getId().equals(usuario.getId())) {
            return "{\"status\":\"error\",\"message\":\"No tienes permiso para eliminar esta categoría\"}";
        }

        categoriaService.eliminarCategoria(id);
        return "{\"status\":\"ok\",\"message\":\"Categoría eliminada correctamente\"}";
    }

    // ✅ Obtener categoría para edición
    @GetMapping("/editar/{id}")
    @ResponseBody
    public CategoriaDTO obtenerCategoria(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername());
        Categoria categoria = categoriaService.obtenerPorId(id);

        if (categoria == null || !categoria.getUsuario().getId().equals(usuario.getId())) {
            return null;
        }

        return new CategoriaDTO(categoria.getNombre(), categoria.getDescripcion(), categoria.getCodigo(), categoria.getTipo());
    }

    // Actualizar categoría
    @PostMapping("/editar/{id}")
    @ResponseBody
    public String editarCategoria(@PathVariable Long id,
                                  @RequestParam String nombre,
                                  @RequestParam String descripcion,
                                  @RequestParam String codigo,
                                  @RequestParam String tipo,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername());
        Categoria categoria = categoriaService.obtenerPorId(id);

        if (categoria == null) return "{\"status\":\"error\",\"message\":\"Categoría no encontrada\"}";
        if (!categoria.getUsuario().getId().equals(usuario.getId())) {
            return "{\"status\":\"error\",\"message\":\"No tienes permiso para editar esta categoría\"}";
        }

        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setCodigo(codigo);
        categoria.setTipo(tipo);

        categoriaService.guardarCategoria(categoria);
        return "{\"status\":\"ok\",\"message\":\"Categoría actualizada correctamente\"}";
    }

    // DTO simple para evitar problemas de serialización
    public static class CategoriaDTO {
        private String nombre;
        private String descripcion;
        private String codigo;
        private String tipo;

        public CategoriaDTO(String nombre, String descripcion, String codigo, String tipo) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.codigo = codigo;
            this.tipo = tipo;
        }

        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public String getCodigo() { return codigo; }
        public String getTipo() { return tipo; }
    }
}
