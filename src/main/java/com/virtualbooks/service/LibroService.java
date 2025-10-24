package com.virtualbooks.service;

import com.virtualbooks.model.Libro;
import com.virtualbooks.model.Usuario;
import com.virtualbooks.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    // Listar todos los libros de un usuario
    public List<Libro> listar(Usuario user) {
        return libroRepository.findByUserId(user.getId());
    }

    // Listar solo libros favoritos de un usuario
    public List<Libro> listarFavoritos(Usuario user) {
        return libroRepository.findByUserIdAndFavoritoTrue(user.getId());
    }

    public Optional<Libro> obtenerPorId(Long id) {
        return libroRepository.findById(id);
    }

    public Libro guardar(Libro libro) {
        return libroRepository.save(libro);
    }

    public void eliminar(Long id) {
        libroRepository.deleteById(id);
    }
}
