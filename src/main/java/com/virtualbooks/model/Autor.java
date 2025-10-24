package com.virtualbooks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "autores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String nacionalidad;

    // Guardamos la fecha como String
    private String fechaNacido;

    // Nueva propiedad: ruta o nombre del archivo de imagen
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario usuario;
}
