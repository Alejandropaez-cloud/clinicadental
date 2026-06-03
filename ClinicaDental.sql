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
('12345678A', 'Juan', 'García López', '1990-05-14', '600111223', 'juan.garcia@gmail.com', 'Calle Mayor 12, Madrid'),
('23456789B', 'María', 'Martínez Ruiz', '1985-08-22', '600222334', 'maria.martinez@hotmail.com', 'Avda. Constitución 45, Sevilla'),
('34567890C', 'Ana', 'Fernández Pérez', '1995-12-03', '600333445', 'ana.fernandez@yahoo.es', 'Plaza España 7, Valencia'),
('45678901D', 'Carlos', 'Rodríguez Sánchez', '1978-03-19', '600444556', 'carlos.rodriguez@gmail.com', 'Calle Real 23, Barcelona'),
('56789012E', 'Laura', 'Díaz Gómez', '2000-07-30', '600555667', 'laura.diaz@outlook.com', 'Paseo Marítimo 8, Málaga'),
('67890123F', 'Pedro', 'López Hernández', '1982-11-11', '600666778', 'pedro.lopez@gmail.com', 'Calle Luna 34, Murcia'),
('78901234G', 'Sofía', 'Torres Jiménez', '1993-04-25', '600777889', 'sofia.torres@gmail.com', 'Calle Sol 15, Bilbao'),
('89012345H', 'David', 'Moreno Castro', '1987-09-14', '600888990', 'david.moreno@empresa.com', 'Avda. Libertad 28, Zaragoza');

INSERT INTO Doctor (NumeroColegiado, Nombre, Especialidad, TelefonoContacto) VALUES
('COL001/2020', 'Dr. Antonio Molina', 'Odontología General', '611111111'),
('COL002/2019', 'Dra. Isabel García', 'Ortodoncia', '622222222'),
('COL003/2021', 'Dr. Rafael López', 'Endodoncia', '633333333'),
('COL004/2019', 'Dra. Catalina Ruiz', 'Periodoncia', '644444444'),
('COL005/2022', 'Dr. Javier Moreno', 'Cirugía Oral', '655555555'),
('COL006/2023', 'Dra. Laura Sánchez', 'Implantología', '666666666');

INSERT INTO Tratamiento (NombreTratamiento, Descripcion, PrecioEstimado, DuracionMinutos) VALUES
('Limpieza Dental', 'Eliminación de sarro y placa bacteriana con ultrasonidos y pulido', 50.00, 30),
('Blanqueamiento Dental', 'Aplicación de peróxido de hidrógeno para blanquear la dentición', 200.00, 60),
('Empaste Composite', 'Relleno de caries con resina composite del color del diente', 80.00, 45),
('Extracción Muela Juicio', 'Extracción quirúrgica del tercer molar incluido o semi-incluido', 150.00, 60),
('Ortodoncia (Revisión)', 'Control y ajuste mensual del tratamiento de brackets', 60.00, 20),
('Implante Dental', 'Colocación de implante de titanio con corona protésica', 900.00, 90),
('Endodoncia', 'Tratamiento de conducto para eliminar la pulpa infectada', 250.00, 75),
('Férula Descarga', 'Fabricación y ajuste de férula para el bruxismo', 120.00, 40);

INSERT INTO Historial_Clinico (codPaciente, Alergias, EnfermedadesCronicas, GrupoSanguineo, ObservacionesGenerales) VALUES
(1, 'Penicilina, Ibuprofeno', 'Ninguna', 'A+', 'Paciente con bruxismo leve. Usar anestesia sin epinefrina'),
(2, 'Ninguna', 'Diabetes tipo 2', 'O+', 'Controlar niveles de azúcar antes de cualquier intervención. Citas preferiblemente por la mañana'),
(3, 'Ibuprofeno, Aspirina', 'Asma', 'B+', 'Evitar AINEs. Tener inhalador disponible en la consulta'),
(4, 'Ninguna', 'Hipertensión arterial', 'AB+', 'Monitorizar presión arterial antes de cada procedimiento'),
(5, 'Látex', 'Ninguna', 'A-', 'Usar guantes sin látex y material libre de látex en todos los procedimientos'),
(6, 'Sulfamidas', 'Ninguna', 'O-', 'Paciente sin complicaciones adicionales'),
(7, 'Anestesia local (lidocaína)', 'Ninguna', 'B-', 'Usar anestesia alternativa tipo mepivacaína'),
(8, 'Ninguna', 'Ninguna', 'O+', 'Paciente sano sin antecedentes de interés');

INSERT INTO Cita (codPaciente, codDoctor, fecha, horaInicio, horaFin, estado) VALUES
(1, 1, '2026-06-10', '10:00:00', '10:30:00', 'Completada'),
(2, 2, '2026-06-10', '11:00:00', '11:45:00', 'Programada'),
(3, 3, '2026-06-11', '09:30:00', '10:15:00', 'Programada'),
(4, 4, '2026-06-11', '12:00:00', '12:30:00', 'Programada'),
(5, 5, '2026-06-12', '16:00:00', '16:40:00', 'Cancelada'),
(6, 6, '2026-06-12', '17:00:00', '17:30:00', 'Programada'),
(7, 1, '2026-06-13', '09:00:00', '10:00:00', 'Programada'),
(8, 2, '2026-06-13', '10:30:00', '11:00:00', 'Programada');

INSERT INTO Cita_Tratamiento (codCita, codTratamiento, Cantidad) VALUES
(1, 1, 1),
(1, 3, 1),
(2, 2, 1),
(3, 3, 1),
(3, 7, 1),
(4, 4, 1),
(5, 5, 1),
(6, 6, 1),
(7, 8, 1),
(8, 1, 1);
