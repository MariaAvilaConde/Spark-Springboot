-- ============================================
-- PostgreSQL (Supabase) - Tabla: Matricula
-- ============================================

CREATE TABLE IF NOT EXISTS matricula (
    id               BIGSERIAL    PRIMARY KEY,
    estudiante_id    BIGINT       NOT NULL,
    carrera_id       BIGINT       NOT NULL,
    anio_academico   VARCHAR(10)  NOT NULL,
    estado           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO'
                         CHECK (estado IN ('ACTIVO','INACTIVO','GRADUADO','RETIRADO')),
    fecha_matricula  DATE         NOT NULL,
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Datos de prueba - Matrículas
-- Relacionan IDs de estudiante (MySQL/Aiven)
-- con IDs de carrera (PostgreSQL/Neon)
-- ============================================
INSERT INTO matricula (estudiante_id, carrera_id, anio_academico, estado, fecha_matricula) VALUES
(1,  1, '2024-I',  'ACTIVO',    '2024-03-01'),
(2,  2, '2024-I',  'ACTIVO',    '2024-03-01'),
(3,  1, '2024-I',  'ACTIVO',    '2024-03-02'),
(4,  3, '2024-I',  'ACTIVO',    '2024-03-02'),
(5,  2, '2023-II', 'ACTIVO',    '2023-08-15'),
(6,  4, '2024-I',  'ACTIVO',    '2024-03-03'),
(7,  1, '2023-I',  'GRADUADO',  '2023-03-01'),
(8,  3, '2024-I',  'ACTIVO',    '2024-03-04'),
(9,  5, '2022-I',  'RETIRADO',  '2022-03-01'),
(10, 4, '2024-I',  'ACTIVO',    '2024-03-05');
