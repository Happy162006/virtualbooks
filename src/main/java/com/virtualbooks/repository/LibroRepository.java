package com.virtualbooks.repository;

import com.virtualbooks.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByUserIdAndFavoritoTrue(Long userId);
    List<Libro> findTop5ByUserIdOrderByIdDesc(Long userId);
    List<Libro> findByUserId(Long userId);
}
