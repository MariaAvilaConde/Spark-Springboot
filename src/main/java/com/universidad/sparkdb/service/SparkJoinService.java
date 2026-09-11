package com.universidad.sparkdb.service;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SparkJoinService {

    private static final Logger logger = LoggerFactory.getLogger(SparkJoinService.class);

    @Autowired
    private SparkSession sparkSession;

    // ── MySQL (Aiven) ── Tabla: estudiante
    @Value("${spring.datasource.mysql.url}")
    private String mysqlUrl;
    @Value("${spring.datasource.mysql.username}")
    private String mysqlUser;
    @Value("${spring.datasource.mysql.password}")
    private String mysqlPassword;

    // ── PostgreSQL Neon ── Tabla: carrera_universidad
    @Value("${spring.datasource.neon.url}")
    private String neonUrl;
    @Value("${spring.datasource.neon.username}")
    private String neonUser;
    @Value("${spring.datasource.neon.password}")
    private String neonPassword;

    // ── PostgreSQL Supabase ── Tabla: matricula
    @Value("${spring.datasource.supabase.url}")
    private String supabaseUrl;
    @Value("${spring.datasource.supabase.username}")
    private String supabaseUser;
    @Value("${spring.datasource.supabase.password}")
    private String supabasePassword;

    // ─────────────────────────────────────────
    // Lectores JDBC por base de datos
    // ─────────────────────────────────────────

    /** Lee una tabla desde MySQL (Aiven) */
    private Dataset<Row> readFromMySQL(String table) {
        logger.info("Reading '{}' from MySQL (Aiven)...", table);
        return sparkSession.read()
                .format("jdbc")
                .option("url",      mysqlUrl)
                .option("dbtable",  table)
                .option("user",     mysqlUser)
                .option("password", mysqlPassword)
                .option("driver",   "com.mysql.cj.jdbc.Driver")
                .load();
    }

    /** Lee una tabla desde PostgreSQL Neon */
    private Dataset<Row> readFromNeon(String table) {
        logger.info("Reading '{}' from PostgreSQL (Neon)...", table);
        return sparkSession.read()
                .format("jdbc")
                .option("url",      neonUrl)
                .option("dbtable",  table)
                .option("user",     neonUser)
                .option("password", neonPassword)
                .option("driver",   "org.postgresql.Driver")
                .load();
    }

    /** Lee una tabla desde PostgreSQL Supabase */
    private Dataset<Row> readFromSupabase(String table) {
        logger.info("Reading '{}' from PostgreSQL (Supabase)...", table);
        return sparkSession.read()
                .format("jdbc")
                .option("url",      supabaseUrl)
                .option("dbtable",  table)
                .option("user",     supabaseUser)
                .option("password", supabasePassword)
                .option("driver",   "org.postgresql.Driver")
                .load();
    }

    // ─────────────────────────────────────────
    // JOIN principal: 3 bases de datos
    // ─────────────────────────────────────────

    /**
     * INNER JOIN entre:
     *   - estudiante        → MySQL     (Aiven)
     *   - carrera_universidad → PostgreSQL (Neon)
     *   - matricula         → PostgreSQL (Supabase)
     */
    public List<Row> joinEstudianteCarrera() {
        logger.info("=== JOIN 3 BDs: MySQL(Aiven) + PostgreSQL(Neon) + PostgreSQL(Supabase) ===");

        Dataset<Row> estudiantes = readFromMySQL("estudiante");
        Dataset<Row> carreras    = readFromNeon("carrera_universidad");
        Dataset<Row> matriculas  = readFromSupabase("matricula");

        // Registrar como vistas temporales en Spark
        estudiantes.createOrReplaceTempView("estudiante");
        carreras.createOrReplaceTempView("carrera_universidad");
        matriculas.createOrReplaceTempView("matricula");

        // Spark SQL JOIN entre las 3 fuentes heterogéneas
        String sql = """
                SELECT
                    e.id              AS estudiante_id,
                    e.nombre          AS estudiante_nombre,
                    e.apellido        AS estudiante_apellido,
                    e.email           AS estudiante_email,
                    e.dni             AS dni,
                    c.id              AS carrera_id,
                    c.nombre          AS carrera_nombre,
                    c.facultad        AS facultad,
                    c.duracion_anios  AS duracion_anios,
                    c.modalidad       AS modalidad,
                    m.anio_academico  AS anio_academico,
                    m.estado          AS estado_matricula,
                    m.fecha_matricula AS fecha_matricula
                FROM matricula m
                INNER JOIN estudiante e
                    ON m.estudiante_id = e.id
                INNER JOIN carrera_universidad c
                    ON m.carrera_id = c.id
                ORDER BY e.apellido, e.nombre
                """;

        Dataset<Row> result = sparkSession.sql(sql);

        logger.info("JOIN completado. Total filas: {}", result.count());
        result.show(20, false);

        return result.collectAsList();
    }

    // ─────────────────────────────────────────
    // Estadísticas agregadas
    // ─────────────────────────────────────────

    public Map<String, Object> getJoinStats() {
        Dataset<Row> estudiantes = readFromMySQL("estudiante");
        Dataset<Row> carreras    = readFromNeon("carrera_universidad");
        Dataset<Row> matriculas  = readFromSupabase("matricula");

        estudiantes.createOrReplaceTempView("estudiante");
        carreras.createOrReplaceTempView("carrera_universidad");
        matriculas.createOrReplaceTempView("matricula");

        long totalEstudiantes = estudiantes.count();
        long totalCarreras    = carreras.count();
        long totalMatriculas  = matriculas.count();

        Dataset<Row> porCarrera = sparkSession.sql("""
                SELECT c.nombre AS carrera, COUNT(e.id) AS total_estudiantes
                FROM matricula m
                INNER JOIN estudiante e ON m.estudiante_id = e.id
                INNER JOIN carrera_universidad c ON m.carrera_id = c.id
                WHERE m.estado = 'ACTIVO'
                GROUP BY c.nombre
                ORDER BY total_estudiantes DESC
                """);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total_estudiantes", totalEstudiantes);
        stats.put("total_carreras",    totalCarreras);
        stats.put("total_matriculas",  totalMatriculas);
        stats.put("fuentes", Map.of(
                "estudiante",         "MySQL — Aiven",
                "carrera_universidad","PostgreSQL — Neon",
                "matricula",          "PostgreSQL — Supabase"
        ));
        stats.put("estudiantes_por_carrera_activa", porCarrera.collectAsList()
                .stream()
                .map(row -> Map.of(
                        "carrera",            row.getString(0),
                        "total_estudiantes",  row.getLong(1)
                ))
                .toList());

        return stats;
    }

    // ─────────────────────────────────────────
    // LEFT JOIN: estudiantes sin matrícula
    // ─────────────────────────────────────────

    public List<Row> getEstudiantesSinMatricula() {
        Dataset<Row> estudiantes = readFromMySQL("estudiante");
        Dataset<Row> matriculas  = readFromSupabase("matricula");

        estudiantes.createOrReplaceTempView("estudiante");
        matriculas.createOrReplaceTempView("matricula");

        Dataset<Row> result = sparkSession.sql("""
                SELECT e.id, e.nombre, e.apellido, e.email, e.dni
                FROM estudiante e
                LEFT JOIN matricula m ON e.id = m.estudiante_id
                WHERE m.id IS NULL
                ORDER BY e.apellido
                """);

        logger.info("Estudiantes sin matrícula: {}", result.count());
        return result.collectAsList();
    }
}
