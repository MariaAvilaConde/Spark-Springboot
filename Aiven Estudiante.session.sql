-- ============================================
-- MySQL (Aiven) - Tabla: Estudiante
-- NOTA: Trabajamos directamente en 'defaultdb'
-- ya que Aiven no permite CREATE DATABASE
-- ============================================

-- Tabla Estudiante
CREATE TABLE IF NOT EXISTS estudiante (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    apellido   VARCHAR(100) NOT NULL,
    email      VARCHAR(150) UNIQUE NOT NULL,
    dni        VARCHAR(20)  UNIQUE NOT NULL,
    edad       INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Datos de prueba - Estudiantes
-- ============================================
INSERT INTO estudiante (nombre, apellido, email, dni, edad) VALUES
('Carlos',    'Garcia',    'carlos.garcia@uni.edu',     '12345678', 20),
('Maria',     'Lopez',     'maria.lopez@uni.edu',       '23456789', 22),
('Andres',    'Martinez',  'andres.martinez@uni.edu',   '34567890', 21),
('Lucia',     'Hernandez', 'lucia.hernandez@uni.edu',   '45678901', 23),
('Diego',     'Torres',    'diego.torres@uni.edu',      '56789012', 19),
('Valentina', 'Flores',    'valentina.flores@uni.edu',  '67890123', 24),
('Miguel',    'Ramirez',   'miguel.ramirez@uni.edu',    '78901234', 20),
('Sofia',     'Castro',    'sofia.castro@uni.edu',      '89012345', 22),
('Sebastian', 'Morales',   'sebastian.morales@uni.edu', '90123456', 21),
('Paula',     'Jimenez',   'paula.jimenez@uni.edu',     '01234567', 25);


SELECT * FROM estudiante;