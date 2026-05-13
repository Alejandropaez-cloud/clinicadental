
-- SCRIPT DE CREACIÓN DE BASE DE DATOS - CLÍNICA DENTAL
-- Alejandro Páez Milán (1º DAW)
-- Base de datos completa para la gestión de una clínica dental

 
-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS clinica_dental;
USE clinica_dental;
 
-- TABLA: PACIENTE

CREATE TABLE Paciente (
    codPaciente INT PRIMARY KEY AUTO_INCREMENT,
    DNI VARCHAR(9) UNIQUE NOT NULL,
    Nombre VARCHAR(50) NOT NULL,
    Apellidos VARCHAR(100) NOT NULL,
    Fecha_Nacimiento DATE NOT NULL,
    telefono VARCHAR(15),
    email VARCHAR(100),
    direccion VARCHAR(200),
    CONSTRAINT chk_dni_format CHECK (LENGTH(DNI) = 9),
    CONSTRAINT chk_email_format CHECK (Email LIKE '%@%.%' OR Email IS NULL)
);
 
-- TABLA: HISTORIAL_CLINICO

CREATE TABLE Historial_Clinico (
    codHistorial INT PRIMARY KEY AUTO_INCREMENT,
    codPaciente INT NOT NULL UNIQUE,
    Alergias VARCHAR(500),
    EnfermedadesCronicas VARCHAR(500),
    GrupoSanguineo VARCHAR(5),
    ObservacionesGenerales TEXT,
    FechaAlta DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_paciente_historial FOREIGN KEY (codPaciente) 
        REFERENCES Paciente(codPaciente) ON DELETE CASCADE ON UPDATE CASCADE
);
 
-- TABLA: DOCTOR

CREATE TABLE Doctor (
    codDoctor INT PRIMARY KEY AUTO_INCREMENT,
    NumeroColegiado VARCHAR(20) UNIQUE NOT NULL,
    Nombre VARCHAR(50) NOT NULL,
    Especialidad VARCHAR(100) NOT NULL,
    TelefonoContacto VARCHAR(15),
    CONSTRAINT chk_especialidad CHECK (Especialidad IN ('Odontología General', 'Ortodoncia', 'Endodoncia', 'Periodoncia', 'Cirugía Oral', 'Implantología', 'Estética Dental'))
);
 
-- TABLA: CITA

CREATE TABLE Cita (
    codCita INT PRIMARY KEY AUTO_INCREMENT,
    codPaciente INT NOT NULL,
    codDoctor INT NOT NULL,
    fecha DATE NOT NULL,
    horaInicio TIME NOT NULL,
    horaFin TIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Programada',
    fechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cita_paciente FOREIGN KEY (codPaciente) 
        REFERENCES Paciente(codPaciente) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cita_doctor FOREIGN KEY (codDoctor) 
        REFERENCES Doctor(codDoctor) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_estado CHECK (Estado IN ('Programada', 'Completada', 'Cancelada')),
    CONSTRAINT chk_horas CHECK (HoraInicio < HoraFin),
    UNIQUE KEY uq_doctor_fecha_hora (codDoctor, Fecha, HoraInicio)
);
 
-- TABLA: TRATAMIENTO

CREATE TABLE Tratamiento (
    codTratamiento INT PRIMARY KEY AUTO_INCREMENT,
    NombreTratamiento VARCHAR(100) NOT NULL,
    Descripcion TEXT,
    PrecioEstimado DECIMAL(8, 2) NOT NULL,
    DuracionMinutos INT NOT NULL,
    CONSTRAINT chk_precio CHECK (PrecioEstimado > 0),
    CONSTRAINT chk_duracion CHECK (DuracionMinutos > 0)
);
 
-- TABLA: CITA_TRATAMIENTO

CREATE TABLE Cita_Tratamiento (
    codCita INT NOT NULL,
    codTratamiento INT NOT NULL,
    Cantidad INT DEFAULT 1,
    FechaRegistro DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (codCita, codTratamiento),
    CONSTRAINT fk_cita_tratamiento_cita FOREIGN KEY (codCita) 
        REFERENCES Cita(codCita) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cita_tratamiento_tratamiento FOREIGN KEY (codTratamiento) 
        REFERENCES Tratamiento(codTratamiento) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_cantidad CHECK (Cantidad > 0)
);
 
-- INSERCIONES DE DATOS DE EJEMPLO
 
-- INSERCIÓN DE PACIENTES (5 registros) 
-- Creamos cinco pacientes

INSERT INTO Paciente (DNI, Nombre, Apellidos, Fecha_Nacimiento, Telefono, Email, Direccion) VALUES
('12345678A', 'Juan', 'García López', '1985-03-15', '600123456', 'juan.garcia@email.com', 'Calle Principal 123, Estepona'),
('87654321B', 'María', 'Rodríguez Martínez', '1990-07-22', '600234567', 'maria.rodriguez@email.com', 'Avenida del Mar 456, Estepona'),
('11223344C', 'Carlos', 'Fernández González', '1988-11-08', '600345678', 'carlos.fernandez@email.com', 'Calle La Paz 789, Estepona'),
('55667788D', 'Ana', 'López Sánchez', '1992-05-30', '600456789', 'ana.lopez@email.com', 'Plaza Mayor 101, Estepona'),
('99887766E', 'Miguel', 'Sánchez Ruiz', '1995-09-12', '600567890', 'miguel.sanchez@email.com', 'Calle del Sol 202, Estepona');
 
-- INSERCIÓN DE HISTORIALES CLÍNICOS (5 registros - uno por paciente)
-- Nos inventamos cinco historiales clinicos

INSERT INTO Historial_Clinico (codPaciente, Alergias, EnfermedadesCronicas, GrupoSanguineo, ObservacionesGenerales) VALUES
(1, 'Penicilina, Ibuprofeno', NULL, 'O+', 'Paciente con bruxismo. Requiere protector nocturno.'),
(2, NULL, 'Diabetes tipo 2', 'A+', 'Revisiones más frecuentes recomendadas. Control de glucosa importante.'),
(3, 'Látex', 'Hipertensión', 'B+', 'Utilizar guantes de nitrilo. Monitoreo de presión arterial.'),
(4, NULL, NULL, 'AB-', 'Paciente con sensibilidad dental moderada. Usar pasta desensibilizante.'),
(5, 'Aspirina', 'Trastorno de coagulación', 'O-', 'Realizar pruebas de coagulación antes de cirugías. Historial de sangrado prolongado.');
 
-- INSERCIÓN DE DOCTORES (5 registros)
-- Creamos cinco doctores

INSERT INTO Doctor (NumeroColegiado, Nombre, Especialidad, TelefonoContacto) VALUES
('COL001/2020', 'Dr. Antonio Martínez', 'Odontología General', '952123456'),
('COL002/2019', 'Dra. Isabel García', 'Ortodoncia', '952234567'),
('COL003/2021', 'Dr. Rafael López', 'Endodoncia', '952345678'),
('COL004/2018', 'Dra. Catalina Ruiz', 'Cirugía Oral', '952456789'),
('COL005/2022', 'Dr. Fernando Díaz', 'Implantología', '952567890');
 
-- INSERCIÓN DE CITAS (5 registros)
-- Fechas futuras para que sean más realistas

INSERT INTO Cita (codPaciente, codDoctor, Fecha, HoraInicio, HoraFin, Estado) VALUES
(1, 1, '2026-05-10', '09:00:00', '09:30:00', 'Programada'),
(2, 2, '2026-05-11', '10:00:00', '10:45:00', 'Programada'),
(3, 3, '2026-05-12', '11:30:00', '12:15:00', 'Completada'),
(4, 4, '2026-05-13', '14:00:00', '15:00:00', 'Cancelada'),
(5, 5, '2026-05-14', '15:30:00', '16:30:00', 'Programada');
 
-- INSERCIÓN DE TRATAMIENTOS (5 registros)
-- Nos inventamos 5 tratamientos

INSERT INTO Tratamiento (NombreTratamiento, Descripcion, PrecioEstimado, DuracionMinutos) VALUES
('Limpieza Dental', 'Limpieza y pulido de dientes con eliminación de sarro y placa bacteriana', 50.00, 30),
('Ortodoncia', 'Colocación y ajuste de aparatos de ortodoncia para corrección de maloclusión', 150.00, 45),
('Endodoncia', 'Tratamiento de conducto radicular para salvar dientes con pulpa inflamada o necrosada', 200.00, 60),
('Extracción Dental', 'Extracción quirúrgica de piezas dentales', 75.00, 30),
('Implante Dental', 'Colocación de implante dental de titanio con corona prostética', 800.00, 90);
 
-- INSERCIÓN DE CITA-TRATAMIENTO (5 registros)
-- Relación N:M entre citas y tratamientos realizados

INSERT INTO Cita_Tratamiento (codCita, codTratamiento, Cantidad) VALUES
(1, 1, 1),
(2, 2, 1),
(3, 3, 1),
(4, 4, 1),
(5, 5, 1);
 
-- CONSULTAS DE VERIFICACIÓN (Pruebas)

-- Ver todos los pacientes registrados
SELECT * FROM Paciente;
 
-- Ver historiales clínicos con nombres de pacientes
SELECT h.codHistorial, p.Nombre, p.Apellidos, h.Alergias, h.EnfermedadesCronicas, h.GrupoSanguineo
FROM Historial_Clinico h
INNER JOIN Paciente p ON h.codPaciente = p.codPaciente;
 
-- Ver citas programadas con información completa
SELECT c.codCita, p.Nombre AS Paciente, d.Nombre AS Doctor, c.Fecha, c.HoraInicio, c.HoraFin, c.Estado
FROM Cita c
INNER JOIN Paciente p ON c.codPaciente = p.codPaciente
INNER JOIN Doctor d ON c.codDoctor = d.codDoctor
ORDER BY c.Fecha;
 
-- Ver tratamientos realizados en cada cita
SELECT c.codCita, p.Nombre AS Paciente, t.NombreTratamiento, ct.Cantidad, t.PrecioEstimado
FROM Cita_Tratamiento ct
INNER JOIN Cita c ON ct.codCita = c.codCita
INNER JOIN Paciente p ON c.codPaciente = p.codPaciente
INNER JOIN Tratamiento t ON ct.codTratamiento = t.codTratamiento;
 
-- Contar registros en cada tabla
SELECT 'Paciente' AS Tabla, COUNT(*) AS Total FROM Paciente
UNION ALL
SELECT 'Historial_Clinico', COUNT(*) FROM Historial_Clinico
UNION ALL
SELECT 'Doctor', COUNT(*) FROM Doctor
UNION ALL
SELECT 'Cita', COUNT(*) FROM Cita
UNION ALL
SELECT 'Tratamiento', COUNT(*) FROM Tratamiento
UNION ALL
SELECT 'Cita_Tratamiento', COUNT(*) FROM Cita_Tratamiento;
 