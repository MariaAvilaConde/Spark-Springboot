package com.universidad.sparkdb.controller;

import com.universidad.sparkdb.service.SparkJoinService;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/spark")
public class JoinController {

    private static final Logger logger = LoggerFactory.getLogger(JoinController.class);

    @Autowired
    private SparkJoinService sparkJoinService;

    // ─────────────────────────────────────────
    // Endpoints individuales por BD
    // ─────────────────────────────────────────

    /**
     * GET /api/spark/estudiantes
     * BD: MySQL — Aiven
     * Tabla: estudiante
     */
    @GetMapping("/estudiantes")
    public ResponseEntity<?> getEstudiantes() {
        try {
            List<Row> rows = sparkJoinService.getEstudiantes();
            List<Map<String, Object>> result = rows.stream().map(row -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id",         row.getAs("id"));
                map.put("nombre",     row.getAs("nombre"));
                map.put("apellido",   row.getAs("apellido"));
                map.put("email",      row.getAs("email"));
                map.put("dni",        row.getAs("dni"));
                map.put("edad",       row.getAs("edad"));
                map.put("fuente",     "MySQL — Aiven");
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of(
                    "base_de_datos", "MySQL — Aiven",
                    "tabla",         "estudiante",
                    "total",         result.size(),
                    "datos",         result
            ));
        } catch (Exception e) {
            logger.error("Error en /estudiantes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error leyendo estudiantes desde MySQL (Aiven)",
                                 "detalle", e.getMessage()));
        }
    }

    /**
     * GET /api/spark/carreras
     * BD: PostgreSQL — Neon
     * Tabla: carrera_universidad
     */
    @GetMapping("/carreras")
    public ResponseEntity<?> getCarreras() {
        try {
            List<Row> rows = sparkJoinService.getCarreras();
            List<Map<String, Object>> result = rows.stream().map(row -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id",             row.getAs("id"));
                map.put("nombre",         row.getAs("nombre"));
                map.put("facultad",       row.getAs("facultad"));
                map.put("duracion_anios", row.getAs("duracion_anios"));
                map.put("modalidad",      row.getAs("modalidad"));
                map.put("fuente",         "PostgreSQL — Neon");
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of(
                    "base_de_datos", "PostgreSQL — Neon",
                    "tabla",         "carrera_universidad",
                    "total",         result.size(),
                    "datos",         result
            ));
        } catch (Exception e) {
            logger.error("Error en /carreras: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error leyendo carreras desde PostgreSQL (Neon)",
                                 "detalle", e.getMessage()));
        }
    }

    /**
     * GET /api/spark/matriculas
     * BD: PostgreSQL — Supabase
     * Tabla: matricula
     */
    @GetMapping("/matriculas")
    public ResponseEntity<?> getMatriculas() {
        try {
            List<Row> rows = sparkJoinService.getMatriculas();
            List<Map<String, Object>> result = rows.stream().map(row -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id",              row.getAs("id"));
                map.put("estudiante_id",   row.getAs("estudiante_id"));
                map.put("carrera_id",      row.getAs("carrera_id"));
                map.put("anio_academico",  row.getAs("anio_academico"));
                map.put("estado",          row.getAs("estado"));
                map.put("fecha_matricula", row.getAs("fecha_matricula"));
                map.put("fuente",          "PostgreSQL — Supabase");
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of(
                    "base_de_datos", "PostgreSQL — Supabase",
                    "tabla",         "matricula",
                    "total",         result.size(),
                    "datos",         result
            ));
        } catch (Exception e) {
            logger.error("Error en /matriculas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error leyendo matrículas desde PostgreSQL (Supabase)",
                                 "detalle", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────
    // JOIN de las 3 BDs
    // ─────────────────────────────────────────

    /**
     * GET /api/spark/join
     * JOIN completo: Estudiante (MySQL) + Matricula (Supabase) + Carrera (Neon)
     */
    @GetMapping("/join")
    public ResponseEntity<?> joinEstudianteCarrera() {
        try {
            List<Row> rows = sparkJoinService.joinEstudianteCarrera();
            List<Map<String, Object>> result = rows.stream().map(row -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("estudiante_id",    row.getAs("estudiante_id"));
                map.put("nombre",           row.getAs("estudiante_nombre"));
                map.put("apellido",         row.getAs("estudiante_apellido"));
                map.put("email",            row.getAs("estudiante_email"));
                map.put("dni",              row.getAs("dni"));
                map.put("carrera_id",       row.getAs("carrera_id"));
                map.put("carrera",          row.getAs("carrera_nombre"));
                map.put("facultad",         row.getAs("facultad"));
                map.put("duracion_anios",   row.getAs("duracion_anios"));
                map.put("modalidad",        row.getAs("modalidad"));
                map.put("anio_academico",   row.getAs("anio_academico"));
                map.put("estado_matricula", row.getAs("estado_matricula"));
                map.put("fecha_matricula",  row.getAs("fecha_matricula"));
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of(
                    "fuentes", List.of("MySQL — Aiven", "PostgreSQL — Neon", "PostgreSQL — Supabase"),
                    "total",   result.size(),
                    "datos",   result
            ));
        } catch (Exception e) {
            logger.error("Error en /join: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",   "Error ejecutando el JOIN entre las 3 bases de datos",
                                 "detalle", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName(),
                                 "causa",   e.getCause() != null ? e.getCause().getMessage() : "desconocida"));
        }
    }

    /**
     * GET /api/spark/stats
     * Estadísticas agregadas con Spark
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            return ResponseEntity.ok(sparkJoinService.getJoinStats());
        } catch (Exception e) {
            logger.error("Error en /stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",   "Error obteniendo estadísticas",
                                 "detalle", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
    }

    /**
     * GET /api/spark/sin-matricula
     * Estudiantes sin matrícula (LEFT JOIN)
     */
    @GetMapping("/sin-matricula")
    public ResponseEntity<?> getEstudiantesSinMatricula() {
        try {
            List<Row> rows = sparkJoinService.getEstudiantesSinMatricula();
            List<Map<String, Object>> result = rows.stream().map(row -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id",       row.getAs("id"));
                map.put("nombre",   row.getAs("nombre"));
                map.put("apellido", row.getAs("apellido"));
                map.put("email",    row.getAs("email"));
                map.put("dni",      row.getAs("dni"));
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of(
                    "total", result.size(),
                    "datos", result
            ));
        } catch (Exception e) {
            logger.error("Error en /sin-matricula: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",   "Error obteniendo estudiantes sin matrícula",
                                 "detalle", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
        }
    }
}
