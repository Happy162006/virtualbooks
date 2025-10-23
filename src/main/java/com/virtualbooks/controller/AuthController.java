package com.virtualbooks.controller;

import com.virtualbooks.dto.UsuarioRegistroDto;
import com.virtualbooks.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/auth/register")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("user", new UsuarioRegistroDto());
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String registrarUsuario(@ModelAttribute("user") UsuarioRegistroDto userDto, Model model) {
        try {
            usuarioService.registrarUsuario(userDto);
            model.addAttribute("successMessage", "Registro exitoso. Ahora puedes iniciar sesión.");
            model.addAttribute("user", new UsuarioRegistroDto());
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", userDto);
        }
        return "auth/register";
    }

    @GetMapping("/auth/login")
    public String mostrarLogin() {
        return "auth/login";
    }
}
