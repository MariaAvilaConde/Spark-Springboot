package com.universidad.sparkdb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante implements Serializable {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String dni;
    private Integer edad;
}
