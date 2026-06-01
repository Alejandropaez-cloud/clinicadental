-- MIGRACIÓN: Añadir columna id a Cita_Tratamiento
-- La entidad CitaTratamiento.java usa @Id @GeneratedValue con columna "id"
-- Si tu BD aún tiene la PK compuesta (codCita, codTratamiento), ejecuta esto:

USE clinica_dental;

-- 1. Añadir columna id como clave primaria autoincremental
ALTER TABLE Cita_Tratamiento
    ADD COLUMN id INT NOT NULL FIRST;

-- 2. Asignar IDs secuenciales a los registros existentes
SET @row_num = 0;
UPDATE Cita_Tratamiento SET id = (@row_num := @row_num + 1) ORDER BY codCita, codTratamiento;

-- 3. Eliminar la PK compuesta y establecer id como nueva PK
ALTER TABLE Cita_Tratamiento
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (id),
    MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT;

-- 4. Mantener la unicidad de (codCita, codTratamiento) con UNIQUE
ALTER TABLE Cita_Tratamiento
    ADD UNIQUE KEY uq_cita_tratamiento (codCita, codTratamiento);
