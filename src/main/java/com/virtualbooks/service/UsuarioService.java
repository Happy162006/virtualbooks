package com.virtualbooks.service;

import com.virtualbooks.dto.UsuarioRegistroDto;
import com.virtualbooks.model.Usuario;
import com.virtualbooks.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrarUsuario(UsuarioRegistroDto dto) throws Exception {
        if (usuarioRepository.findByEmail(dto.getEmail()) != null) {
            throw new Exception("El email ya está registrado");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new Exception("Las contraseñas no coinciden");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(0);
        return usuarioRepository.save(usuario);
    }

    // 🔑 Nuevo método para obtener usuario por email
    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}

