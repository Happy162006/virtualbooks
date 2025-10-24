package com.virtualbooks.repository;

import com.virtualbooks.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findTop5ByUsuarioIdOrderByIdDesc(Long usuarioId);
    List<Autor> findByUsuarioId(Long usuarioId);
}
