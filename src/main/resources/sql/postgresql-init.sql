-- ============================================
-- PostgreSQL (Neon) - Carrera Universidad
-- ============================================

-- Tabla Carrera_Universidad
CREATE TABLE IF NOT EXISTS carrera_universidad (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(200) NOT NULL,
    facultad        VARCHAR(200) NOT NULL,
    duracion_anios  INT          NOT NULL,
    modalidad       VARCHAR(50)  DEFAULT 'PRESENCIAL',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Sample Data - Carreras
-- ============================================
INSERT INTO carrera_universidad (nombre, facultad, duracion_anios, modalidad) VALUES
('Ingenieria de Sistemas',          'Facultad de Ingenieria',             5, 'PRESENCIAL'),
('Administracion de Empresas',      'Facultad de Ciencias Empresariales', 5, 'PRESENCIAL'),
('Medicina',                        'Facultad de Ciencias de la Salud',   6, 'PRESENCIAL'),
('Derecho',                         'Facultad de Derecho',                5, 'PRESENCIAL'),
('Psicologia',                      'Facultad de Humanidades',            5, 'SEMIPRESENCIAL'),
('Ingenieria Civil',                'Facultad de Ingenieria',             5, 'PRESENCIAL'),
('Contabilidad',                    'Facultad de Ciencias Empresariales', 4, 'PRESENCIAL'),
('Arquitectura',                    'Facultad de Arte y Diseno',          5, 'PRESENCIAL');
