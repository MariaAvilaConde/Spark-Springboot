package com.universidad.sparkdb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Matricula implements Serializable {
    private Long id;
    private Long estudianteId;
    private Long carreraId;
    private String anioAcademico;
    private String estado;
    private String fechaMatricula;
}
