-- SCRIPT DE CREACIÓN DE BASE DE DATOS - CLÍNICA DENTAL
-- Alejandro Páez Milán (1º DAW)
-- Base de datos completa para la gestión de una clínica dental

CREATE DATABASE IF NOT EXISTS clinica_dental;
USE clinica_dental;

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

CREATE TABLE Doctor (
    codDoctor INT PRIMARY KEY AUTO_INCREMENT,
    NumeroColegiado VARCHAR(20) UNIQUE NOT NULL,
    Nombre VARCHAR(50) NOT NULL,
    Especialidad VARCHAR(100) NOT NULL,
    TelefonoContacto VARCHAR(15),
    CONSTRAINT chk_especialidad CHECK (Especialidad IN ('Odontología General', 'Ortodoncia', 'Endodoncia', 'Periodoncia', 'Cirugía Oral', 'Implantología', 'Estética Dental'))
);

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

CREATE TABLE Tratamiento (
    codTratamiento INT PRIMARY KEY AUTO_INCREMENT,
    NombreTratamiento VARCHAR(100) NOT NULL,
    Descripcion TEXT,
    PrecioEstimado DECIMAL(8, 2) NOT NULL,
    DuracionMinutos INT NOT NULL,
    CONSTRAINT chk_precio CHECK (PrecioEstimado > 0),
    CONSTRAINT chk_duracion CHECK (DuracionMinutos > 0)
);

CREATE TABLE Cita_Tratamiento (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codCita INT NOT NULL,
    codTratamiento INT NOT NULL,
    Cantidad INT DEFAULT 1,
    FechaRegistro DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_cita_tratamiento (codCita, codTratamiento),
    CONSTRAINT fk_cita_tratamiento_cita FOREIGN KEY (codCita)
        REFERENCES Cita(codCita) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cita_tratamiento_tratamiento FOREIGN KEY (codTratamiento)
        REFERENCES Tratamiento(codTratamiento) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_cantidad CHECK (Cantidad > 0)
);

-- DATOS DE EJEMPLO (mínimo 5 registros por tabla)

INSERT INTO Paciente (DNI, Nombre, Apellidos, Fecha_Nacimiento, telefono, email, direccion) VALUES
('12345678A', 'María', 'García López', '1990-05-14', '600111223', 'maria.garcia@email.com', 'Calle Mayor 12, Madrid'),
('23456789B', 'Carlos', 'Martínez Ruiz', '1985-08-22', '600222334', 'carlos.martinez@email.com', 'Avda. Constitución 45, Sevilla'),
('34567890C', 'Ana', 'Fernández Pérez', '1995-12-03', '600333445', 'ana.fernandez@email.com', 'Plaza España 7, Valencia'),
('45678901D', 'Javier', 'Rodríguez Sánchez', '1978-03-19', '600444556', 'javier.rodriguez@email.com', 'Calle Real 23, Barcelona'),
('56789012E', 'Laura', 'Díaz Gómez', '2000-07-30', '600555667', 'laura.diaz@email.com', 'Paseo Marítimo 8, Málaga'),
('67890123F', 'Pedro', 'López Hernández', '1982-11-11', '600666778', NULL, 'Calle Luna 34, Murcia');

INSERT INTO Doctor (NumeroColegiado, Nombre, Especialidad, TelefonoContacto) VALUES
('COL-1001', 'Dr. Antonio', 'Odontología General', '600777889'),
('COL-1002', 'Dra. Beatriz', 'Ortodoncia', '600888990'),
('COL-1003', 'Dr. David', 'Endodoncia', '600999001'),
('COL-1004', 'Dra. Elena', 'Periodoncia', '600111002'),
('COL-1005', 'Dr. Fernando', 'Cirugía Oral', '600222003'),
('COL-1006', 'Dra. Clara', 'Implantología', '600333004');

INSERT INTO Cita (codPaciente, codDoctor, fecha, horaInicio, horaFin, estado) VALUES
(1, 1, '2026-06-10', '10:00:00', '10:30:00', 'Programada'),
(2, 2, '2026-06-10', '11:00:00', '11:45:00', 'Programada'),
(3, 3, '2026-06-11', '09:30:00', '10:15:00', 'Programada'),
(4, 4, '2026-06-11', '12:00:00', '12:30:00', 'Programada'),
(5, 5, '2026-06-12', '16:00:00', '16:40:00', 'Programada'),
(1, 6, '2026-06-12', '17:00:00', '17:30:00', 'Programada');

INSERT INTO Tratamiento (NombreTratamiento, Descripcion, PrecioEstimado, DuracionMinutos) VALUES
('Limpieza Dental', 'Eliminación de sarro y placa bacteriana', 50.00, 30),
('Blanqueamiento Dental', 'Aplicación de peróxido para blanquear dientes', 200.00, 60),
('Empaste', 'Relleno de caries con composite', 80.00, 45),
('Extracción Muela Juicio', 'Extracción quirúrgica del tercer molar', 150.00, 60),
('Ortodoncia (Revisión)', 'Control mensual del tratamiento de brackets', 60.00, 20),
('Implante Dental', 'Colocación de implante con corona', 900.00, 90);

INSERT INTO Cita_Tratamiento (codCita, codTratamiento, Cantidad) VALUES
(1, 1, 1),
(2, 2, 1),
(3, 3, 1),
(4, 4, 1),
(5, 5, 1),
(6, 6, 1);

INSERT INTO Historial_Clinico (codPaciente, Alergias, EnfermedadesCronicas, GrupoSanguineo, ObservacionesGenerales) VALUES
(1, 'Penicilina', 'Ninguna', 'A+', 'Paciente sin antecedentes relevantes'),
(2, 'Ninguna', 'Diabetes tipo 2', 'O+', 'Controlar niveles de azúcar antes de intervenciones'),
(3, 'Ibuprofeno, Aspirina', 'Asma', 'B+', 'Evitar antiinflamatorios no esteroideos'),
(4, 'Ninguna', 'Hipertensión arterial', 'AB+', 'Monitorear presión arterial en cada visita'),
(5, 'Látex', 'Ninguna', 'A-', 'Usar guantes sin látex en todo procedimiento'),
(6, 'Sulfamidas', 'Ninguna', 'O-', 'Paciente sin observaciones adicionales');
