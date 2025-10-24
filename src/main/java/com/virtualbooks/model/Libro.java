package com.virtualbooks.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario user;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Autor author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categoria category;

    @Column(length = 255, nullable = false)
    private String titulo;

    @Lob
    private String descripcion;

    private String imagen;

    @Column(name = "favorito")
    private boolean favorito = false; // ✅ Nuevo campo para marcar como favorito

    @CreationTimestamp
    private LocalDateTime createdAt;  // Fecha de creación automática

    @UpdateTimestamp
    private LocalDateTime updatedAt;  // Fecha de actualización automática
}
