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
