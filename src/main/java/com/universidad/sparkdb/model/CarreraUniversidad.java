package com.universidad.sparkdb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarreraUniversidad implements Serializable {
    private Long id;
    private String nombre;
    private String facultad;
    private Integer duracionAnios;
    private String modalidad;
}
