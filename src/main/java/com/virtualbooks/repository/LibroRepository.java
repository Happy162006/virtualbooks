package com.virtualbooks.repository;

import com.virtualbooks.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    // Todos los libros de un usuario
    List<Libro> findByUserId(Long userId);

    // Solo libros favoritos de un usuario
    List<Libro> findByUserIdAndFavoritoTrue(Long userId);
}
