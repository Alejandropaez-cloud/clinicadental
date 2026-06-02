# DOCUMENTACIÓN DEL PROYECTO: SISTEMA DE GESTIÓN DE CLÍNICA DENTAL

**Autor:** Alejandro Páez Milán  
**Asignatura:** 1º DAW  
**Tecnologías:** Java, JPA (EclipseLink), MySQL, Swing  
**IDE:** VS Code  
**Repositorio:** https://github.com/Alejandropaez-cloud/clinicadental

---

## ÍNDICE

1. [INTRODUCCIÓN](#1-introducción)
2. [ESTRUCTURA DEL PROYECTO](#2-estructura-del-proyecto)
3. [BASE DE DATOS](#3-base-de-datos)
4. [ENTIDADES JPA](#4-entidades-jpa)
5. [RELACIONES ENTRE ENTIDADES](#5-relaciones-entre-entidades)
6. [CONTROLADORES CRUD](#6-controladores-crud)
7. [INTERFAZ GRÁFICA](#7-interfaz-gráfica)
8. [BACKUP Y RESTAURACIÓN](#8-backup-y-restauración)
9. [CONFIGURACIÓN](#9-configuración)
10. [GUÍA DE EJECUCIÓN](#10-guía-de-ejecución)
11. [DIAGRAMA DE LA BASE DE DATOS](#11-diagrama-de-la-base-de-datos)

---

## 1. INTRODUCCIÓN

Aplicación Java de escritorio para la gestión de una clínica dental. Permite:

- **CRUD completo** de Pacientes, Doctores, Citas, Tratamientos, Historial Clínico y la relación Cita-Tratamiento.
- **Copia de seguridad / Restauración** mediante archivos CSV.
- **Interfaz gráfica** con navegación por botones y tablas dinámicas.
- **Persistencia** con JPA (EclipseLink) y MySQL.

**Arquitectura del proyecto:**

```
clinicadental/
├── src/main/java/
│   ├── daw/
│   │   └── Main.java                          → Punto de entrada
│   ├── controllers/controladores/             → Capa de negocio (CRUD)
│   │   ├── PacienteController.java
│   │   ├── DoctorController.java
│   │   ├── CitaController.java
│   │   ├── TratamientoController.java
│   │   ├── HistorialClinicoController.java
│   │   └── CitaTratamientoController.java
│   ├── models/modelos/entidades/              → Capa de datos (JPA)
│   │   ├── Paciente.java
│   │   ├── Doctor.java
│   │   ├── Cita.java
│   │   ├── Tratamiento.java
│   │   ├── HistorialClinico.java
│   │   └── CitaTratamiento.java
│   ├── views/                                 → Capa de presentación (Swing)
│   │   ├── MainFrame.java
│   │   ├── PacientePanel.java
│   │   ├── DoctorPanel.java
│   │   ├── CitaPanel.java
│   │   ├── TratamientoPanel.java
│   │   ├── HistorialClinicoPanel.java
│   │   └── CitaTratamientoPanel.java
│   └── util/
│       ├── SharedEntityManagerFactory.java    → Singleton EMF
│       └── BackupUtil.java                    → Backup/Restore CSV
├── src/main/resources/META-INF/
│   └── persistence.xml                        → Configuración JPA
├── pom.xml                                    → Maven
├── ClinicaDental.sql                          → Script BD
├── migracion_cita_tratamiento.sql             → Migración
├── .gitignore
└── documentacion.pdf
```

---

## 2. ESTRUCTURA DEL PROYECTO

### 2.1 Capas

El proyecto sigue una arquitectura multicapa:

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| **Presentación** | `views/` | Interfaces gráficas con Swing |
| **Negocio** | `controllers/controladores/` | Lógica CRUD, transacciones |
| **Datos** | `models/modelos/entidades/` | Entidades JPA mapeadas a tablas |
| **Utilidades** | `util/` | Fábrica de EntityManager, Backup |

### 2.2 Flujo de datos

```
[Usuario] ↔ [Vista (Swing)] ↔ [Controlador] ↔ [JPA] ↔ [MySQL]
```

El usuario interactúa con la interfaz (Swing), que llama a los controladores, que usan JPA para acceder a MySQL.

---

## 3. BASE DE DATOS

### 3.1 Esquema

Base de datos MySQL llamada `clinica_dental` con 6 tablas:

```sql
CREATE DATABASE IF NOT EXISTS clinica_dental;
USE clinica_dental;
```

### 3.2 Tablas y columnas

#### Paciente
| Columna | Tipo | Restricciones |
|---------|------|---------------|
| codPaciente | INT | PK, AUTO_INCREMENT |
| DNI | VARCHAR(9) | UNIQUE, NOT NULL |
| Nombre | VARCHAR(50) | NOT NULL |
| Apellidos | VARCHAR(100) | NOT NULL |
| Fecha_Nacimiento | DATE | NOT NULL |
| telefono | VARCHAR(15) | |
| email | VARCHAR(100) | |
| direccion | VARCHAR(200) | |

#### Doctor
| Columna | Tipo | Restricciones |
|---------|------|---------------|
| codDoctor | INT | PK, AUTO_INCREMENT |
| NumeroColegiado | VARCHAR(20) | UNIQUE, NOT NULL |
| Nombre | VARCHAR(50) | NOT NULL |
| Especialidad | VARCHAR(100) | NOT NULL |
| TelefonoContacto | VARCHAR(15) | |

#### Tratamiento
| Columna | Tipo | Restricciones |
|---------|------|---------------|
| codTratamiento | INT | PK, AUTO_INCREMENT |
| NombreTratamiento | VARCHAR(100) | NOT NULL |
| Descripcion | TEXT | |
| PrecioEstimado | DECIMAL(8,2) | NOT NULL, > 0 |
| DuracionMinutos | INT | NOT NULL, > 0 |

#### Historial_Clinico
| Columna | Tipo | Restricciones |
|---------|------|---------------|
| codHistorial | INT | PK, AUTO_INCREMENT |
| codPaciente | INT | FK → Paciente, UNIQUE, NOT NULL |
| Alergias | VARCHAR(500) | |
| EnfermedadesCronicas | VARCHAR(500) | |
| GrupoSanguineo | VARCHAR(5) | |
| ObservacionesGenerales | TEXT | |
| FechaAlta | DATETIME | DEFAULT CURRENT_TIMESTAMP |

#### Cita
| Columna | Tipo | Restricciones |
|---------|------|---------------|
| codCita | INT | PK, AUTO_INCREMENT |
| codPaciente | INT | FK → Paciente, NOT NULL |
| codDoctor | INT | FK → Doctor, NOT NULL |
| fecha | DATE | NOT NULL |
| horaInicio | TIME | NOT NULL |
| horaFin | TIME | NOT NULL |
| estado | VARCHAR(20) | NOT NULL, DEFAULT 'Programada' |
| fechaCreacion | DATETIME | DEFAULT CURRENT_TIMESTAMP |

#### Cita_Tratamiento
| Columna | Tipo | Restricciones |
|---------|------|---------------|
| id | INT | PK, AUTO_INCREMENT |
| codCita | INT | FK → Cita, NOT NULL |
| codTratamiento | INT | FK → Tratamiento, NOT NULL |
| Cantidad | INT | DEFAULT 1, > 0 |
| FechaRegistro | DATETIME | DEFAULT CURRENT_TIMESTAMP |

### 3.3 Restricciones adicionales

- **UNIQUE KEY** en `(codCita, codTratamiento)` en Cita_Tratamiento → evita duplicados
- **UNIQUE KEY** en `(codDoctor, Fecha, HoraInicio)` en Cita → evita solapamientos de doctores
- **CHECK** en `Especialidad` del Doctor → solo valores permitidos
- **CHECK** en `Estado` de Cita → 'Programada', 'Completada', 'Cancelada'
- **CHECK** en `PrecioEstimado` → > 0
- **CHECK** en `DuracionMinutos` → > 0
- **CHECK** en `HoraInicio < HoraFin` en Cita
- **FK con ON DELETE CASCADE** → al borrar un Paciente se borran sus Citas e Historial
- **FK con ON DELETE RESTRICT** → no se puede borrar un Doctor/Tratamiento si tiene Citas asociadas

---

## 4. ENTIDADES JPA

### 4.1 Paciente

**Clase:** `models/modelos/entidades/Paciente.java`

Mapea la tabla `Paciente`. Es la entidad principal del sistema.

**Named Queries:**
- `Paciente.findAll` → SELECT p FROM Paciente p
- `Paciente.findById` → busca por codPaciente
- `Paciente.findByDNI` → busca por DNI
- `Paciente.findByNombre` → busca por nombre
- `Paciente.findByApellidos` → busca por apellidos

**Relaciones:**
- **1:1** con HistorialClinico (mappedBy = "paciente", cascade ALL)
- **1:N** con Cita (mappedBy = "paciente", cascade PERSIST)

**Constructores:**
- `Paciente()` → vacío (requerido por JPA)
- `Paciente(Integer codPaciente)` → solo ID
- `Paciente(String dni, String nombre, String apellidos, Date fechaNacimiento)` → campos obligatorios

### 4.2 Doctor

**Clase:** `models/modelos/entidades/Doctor.java`

Mapea la tabla `Doctor`.

**Named Queries:**
- `Doctor.findAll` → SELECT d FROM Doctor d
- `Doctor.findById` → busca por codDoctor
- `Doctor.findByNombre` → busca por nombre
- `Doctor.findByEspecialidad` → busca por especialidad

**Relaciones:**
- **1:N** con Cita (mappedBy = "doctor", cascade PERSIST)

### 4.3 Tratamiento

**Clase:** `models/modelos/entidades/Tratamiento.java`

Mapea la tabla `Tratamiento`.

**Named Queries:**
- `Tratamiento.findAll` → SELECT t FROM Tratamiento t
- `Tratamiento.findById` → busca por codTratamiento
- `Tratamiento.findByNombre` → busca por nombre

**Relaciones:**
- **1:N** con CitaTratamiento (mappedBy = "tratamiento", cascade PERSIST)

### 4.4 Cita

**Clase:** `models/modelos/entidades/Cita.java`

Mapea la tabla `Cita`. Representa una cita programada.

**Named Queries:**
- `Cita.findAll` → SELECT c FROM Cita c
- `Cita.findById` → busca por codCita
- `Cita.findByFecha` → busca por fecha
- `Cita.findByEstado` → busca por estado

**Relaciones:**
- **N:1** con Paciente (JoinColumn codPaciente, optional = false)
- **N:1** con Doctor (JoinColumn codDoctor, optional = false)
- **1:N** con CitaTratamiento (mappedBy = "cita", cascade PERSIST, orphanRemoval = true)

**Anotaciones @Temporal:**
- `fecha` → DATE
- `horaInicio` → TIME
- `horaFin` → TIME
- `fechaCreacion` → TIMESTAMP

### 4.5 HistorialClinico

**Clase:** `models/modelos/entidades/HistorialClinico.java`

Mapea la tabla `Historial_Clinico`.

**Named Queries:**
- `HistorialClinico.findAll` → SELECT h FROM HistorialClinico h
- `HistorialClinico.findById` → busca por codHistorial
- `HistorialClinico.findByPaciente` → busca por paciente

**Relaciones:**
- **1:1** con Paciente (JoinColumn codPaciente, unique = true, optional = false)

### 4.6 CitaTratamiento

**Clase:** `models/modelos/entidades/CitaTratamiento.java`

Mapea la tabla `Cita_Tratamiento`. Es la tabla puente para la relación N:M entre Cita y Tratamiento.

**Diseño:** Sigue la directriz JPA de usar un @Id propio con @GeneratedValue en lugar de @IdClass compuesta. La unicidad de (codCita, codTratamiento) se garantiza mediante UNIQUE KEY en la BD.

**Named Queries:**
- `CitaTratamiento.findAll` → SELECT ct FROM CitaTratamiento ct
- `CitaTratamiento.findById` → busca por id
- `CitaTratamiento.findByCita` → busca por cita
- `CitaTratamiento.findByTratamiento` → busca por tratamiento

**Relaciones:**
- **N:1** con Cita (JoinColumn codCita, optional = false)
- **N:1** con Tratamiento (JoinColumn codTratamiento, optional = false)

---

## 5. RELACIONES ENTRE ENTIDADES

Diagrama de relaciones:

```
Paciente (1) ──── (1) HistorialClinico
    │
    │ (1:N)
    │
    └─── Cita (N) ──── (1) Doctor
            │
            │ (1:N)
            │
            └─── CitaTratamiento (N) ──── (1) Tratamiento
```

| Relación | Tipo | FK en | Cascade |
|----------|------|-------|---------|
| Paciente ↔ HistorialClinico | 1:1 | HistorialClinico.codPaciente | ALL |
| Paciente ↔ Cita | 1:N | Cita.codPaciente | PERSIST |
| Doctor ↔ Cita | 1:N | Cita.codDoctor | PERSIST |
| Cita ↔ CitaTratamiento | 1:N | CitaTratamiento.codCita | PERSIST |
| Tratamiento ↔ CitaTratamiento | 1:N | CitaTratamiento.codTratamiento | PERSIST |
| Cita ↔ Tratamiento | N:M | CitaTratamiento | (tabla puente) |

---

## 6. CONTROLADORES CRUD

### 6.1 Patrón común

Todos los controladores siguen el mismo patrón:

```java
public class XxxController {
    private final EntityManagerFactory emf;  // Desde SharedEntityManagerFactory

    public void create(Xxx entity)       // INSERT
    public Xxx findById(Integer id)      // SELECT por PK
    public List<Xxx> findAll()           // SELECT ALL
    public void update(Xxx entity)       // UPDATE (merge)
    public void delete(Integer id)       // DELETE por PK
    public void deleteAll()              // DELETE ALL + reset AUTO_INCREMENT
}
```

### 6.2 Gestión de transacciones

Cada método que modifica la base de datos sigue esta estructura:

```java
EntityManager em = getEntityManager();
EntityTransaction tx = em.getTransaction();
try {
    tx.begin();
    // operación...
    tx.commit();
} catch (Exception ex) {
    if (tx.isActive()) {
        tx.rollback();  // Deshacer cambios si hay error
    }
    throw new RuntimeException("Error...", ex);
} finally {
    em.close();  // Cerrar siempre el EntityManager
}
```

### 6.3 SharedEntityManagerFactory

**Clase:** `util/SharedEntityManagerFactory.java`

Singleton thread-safe que proporciona una única instancia de `EntityManagerFactory` para toda la aplicación. Así evitamos crear múltiples conexiones a la base de datos.

```java
public class SharedEntityManagerFactory {
    private static final EntityManagerFactory EMF =
        Persistence.createEntityManagerFactory("clinica_dental");

    public static EntityManagerFactory getInstance() { return EMF; }
    public static void close() { if (EMF.isOpen()) EMF.close(); }
}
```

---

## 7. INTERFAZ GRÁFICA

### 7.1 MainFrame

**Clase:** `views/MainFrame.java`

Ventana principal que contiene:

- **Panel de navegación izquierdo** (azul) con 6 botones: Pacientes, Doctores, Citas, Tratamientos, Historial, Cita-Tratamiento
- **Panel central** con `CardLayout` que muestra el panel seleccionado
- **Panel inferior** con 3 botones: Copia Seguridad (verde), Restaurar (naranja), Salir (rojo)

Usa `CardLayout` para cambiar entre las vistas, manteniendo la navegación y botones siempre visibles.

### 7.2 Paneles de entidades

Cada panel entidad sigue la misma estructura:

```
┌─────────────────────────────────┐
│      TÍTULO (Centrado, azul)    │
├─────────────────────────────────┤
│                                 │
│         JTable (datos)          │
│   Cabecera azul con texto blanco│
│                                 │
├─────────────────────────────────┤
│  [Nuevo]  [Editar]  [Eliminar]  │
└─────────────────────────────────┘
```

**Botones:**
- **Nuevo** (azul) → abre diálogo para crear registro
- **Editar** (naranja) → abre diálogo con datos del registro seleccionado
- **Eliminar** (rojo) → confirma y elimina el registro seleccionado

### 7.3 Diálogos

Cada diálogo usa `GridLayout` simple con etiquetas y campos. Contiene:
- **Guardar** (azul) → valida y persiste los datos
- **Cancelar** (gris) → cierra sin guardar

### 7.4 Colores

| Elemento | Color |
|----------|-------|
| Barra de navegación | Azul (#2196F3) |
| Botones navegación | Azul oscuro (#1976D2) |
| Títulos sección | Azul, negrita 18px |
| Cabeceras tabla | Azul (#2196F3) |
| Botón Nuevo | Azul |
| Botón Editar | Naranja (#FF9800) |
| Botón Eliminar | Rojo (#F44336) |
| Botón Copia Seguridad | Verde (#4CAF50) |
| Botón Restaurar | Naranja (#FF9800) |
| Botón Salir | Rojo (#F44336) |
| Botón Guardar | Azul |
| Botón Cancelar | Gris (#9E9E9E) |
| Fondo general | Gris claro (#F0F5FA) |

### 7.5 Detalles de cada panel

**PacientePanel:** Muestra ID, DNI, Nombre, Apellidos, Fecha Nac., Teléfono, Email, Dirección.  
**DoctorPanel:** Muestra ID, Nº Colegiado, Nombre, Especialidad, Teléfono. Especialidad es combo.  
**CitaPanel:** Muestra ID, Paciente, Doctor, Fecha, Hora Inicio, Hora Fin, Estado. Usa combos para seleccionar paciente y doctor.  
**TratamientoPanel:** Muestra ID, Nombre, Descripción, Precio (€), Duración (min).  
**HistorialClinicoPanel:** Muestra ID, Paciente, Alergias, Enfermedades, Grupo Sang., Fecha Alta. Valida que un paciente no tenga duplicado.  
**CitaTratamientoPanel:** Muestra ID, Cita, Tratamiento, Cantidad, Fecha Registro. Evita duplicados de (cita, tratamiento).

---

## 8. BACKUP Y RESTAURACIÓN

### 8.1 BackupUtil

**Clase:** `util/BackupUtil.java`

Proporciona dos operaciones:

#### realizarCopia()
1. Crea carpeta `backups/YYYY-MM-DD_HH-mm-ss/`
2. Para cada tabla, ejecuta `SELECT *` y guarda en CSV
3. Cada CSV tiene cabecera con nombres de columnas y datos separados por comas
4. Los valores con comas o saltos de línea se envuelven en comillas dobles

#### restaurarUltimaCopia()
1. Encuentra la carpeta de backup más reciente
2. Desactiva restricciones FK (`SET FOREIGN_KEY_CHECKS = 0`)
3. Borra todos los datos de las 6 tablas
4. Lee los CSVs y los inserta en orden inverso de dependencias
5. Reactiva restricciones FK
6. Limpia la caché L2 de EclipseLink para que la GUI muestre los datos nuevos

### 8.2 Archivos CSV generados

Por cada backup se generan 6 archivos:
- `Paciente.csv`
- `Doctor.csv`
- `Tratamiento.csv`
- `Historial_Clinico.csv`
- `Cita.csv`
- `Cita_Tratamiento.csv`

---

## 9. CONFIGURACIÓN

### 9.1 persistence.xml

**Ruta:** `src/main/resources/META-INF/persistence.xml`

```xml
<persistence-unit name="clinica_dental" transaction-type="RESOURCE_LOCAL">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <class>models.modelos.entidades.Paciente</class>
    <class>models.modelos.entidades.Doctor</class>
    <class>models.modelos.entidades.Cita</class>
    <class>models.modelos.entidades.Tratamiento</class>
    <class>models.modelos.entidades.CitaTratamiento</class>
    <class>models.modelos.entidades.HistorialClinico</class>
    <properties>
        <property name="javax.persistence.jdbc.url"
            value="jdbc:mysql://127.0.0.1:3306/clinica_dental?serverTimezone=UTC"/>
        <property name="javax.persistence.jdbc.user" value="alejandro"/>
        <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
        <property name="javax.persistence.jdbc.password" value="1234"/>
        <property name="eclipselink.logging.level" value="OFF"/>
    </properties>
</persistence-unit>
```

**Usuario BD:** alejandro / 1234

### 9.2 pom.xml

**Tecnologías:**
- Java 25
- Maven
- EclipseLink 2.7.12 (proveedor JPA)
- MySQL Connector/J 8.0.33
- Swing (incluido en JDK)

**Main class:** `daw.Main`

---

## 10. GUÍA DE EJECUCIÓN

### 10.1 Requisitos previos

- Java JDK 25+
- MySQL Server 8.0+ (ejecutándose en localhost:3306)
- VS Code con extensiones Java
- Maven (opcional, VS Code lo gestiona)

### 10.2 Pasos

1. **Crear la base de datos:**
   ```
   mysql -u alejandro -p < ClinicaDental.sql
   ```

2. **Abrir el proyecto en VS Code:**
   - Archivo → Abrir carpeta → seleccionar `clinicadental/`

3. **Compilar:**
   - VS Code compila automáticamente al guardar
   - O: `Ctrl+Shift+P` → Java: Clean

4. **Ejecutar:**
   - Abrir `src/main/java/daw/Main.java`
   - Click derecho → Run Java
   - O: presionar `F5`

5. **Verificar datos:**
   - Los 6 paneles deben mostrar los registros existentes
   - Probar crear, editar y eliminar registros
   - Probar Copia de Seguridad y Restaurar

### 10.3 Solución de problemas

**Error de conexión MySQL:**
- Verificar que MySQL esté corriendo: `net start MySQL`
- Verificar credenciales en persistence.xml

**Error con EclipseLink:**
- `Ctrl+Shift+P` → Java: Clean Java Language Server Workspace

**No se ven datos en la GUI:**
- Ejecutar el script SQL primero para poblar la BD

---

## 11. DIAGRAMA DE LA BASE DE DATOS

```
┌─────────────────────────┐       ┌──────────────────────────────┐
│        Paciente         │       │     Historial_Clinico        │
├─────────────────────────┤       ├──────────────────────────────┤
│ PK codPaciente INT      │──1:1──│ PK codHistorial INT          │
│    DNI VARCHAR(9)       │       │ FK codPaciente INT (UNIQUE)  │
│    Nombre VARCHAR       │       │    Alergias VARCHAR           │
│    Apellidos VARCHAR    │       │    EnfermedadesCronicas      │
│    Fecha_Nacimiento DATE│       │    GrupoSanguineo VARCHAR    │
│    telefono VARCHAR     │       │    ObservacionesGenerales    │
│    email VARCHAR       │       │    FechaAlta DATETIME        │
│    direccion VARCHAR    │       └──────────────────────────────┘
└──────────┬──────────────┘
           │ 1:N
           ▼
┌─────────────────────────┐       ┌──────────────────────────────┐
│          Cita           │       │          Doctor              │
├─────────────────────────┤       ├──────────────────────────────┤
│ PK codCita INT          │──N:1──│ PK codDoctor INT             │
│ FK codPaciente INT      │       │    NumeroColegiado VARCHAR   │
│ FK codDoctor INT        │       │    Nombre VARCHAR            │
│    fecha DATE           │       │    Especialidad VARCHAR      │
│    horaInicio TIME      │       │    TelefonoContacto VARCHAR  │
│    horaFin TIME         │       └──────────────────────────────┘
│    estado VARCHAR       │
│    fechaCreacion DATETIME│
└──────────┬──────────────┘
           │ 1:N
           ▼
┌─────────────────────────┐       ┌──────────────────────────────┐
│    Cita_Tratamiento     │       │        Tratamiento           │
├─────────────────────────┤       ├──────────────────────────────┤
│ PK id INT               │──N:1──│ PK codTratamiento INT        │
│ FK codCita INT          │       │    NombreTratamiento VARCHAR │
│ FK codTratamiento INT   │       │    Descripcion TEXT          │
│    Cantidad INT          │       │    PrecioEstimado DECIMAL    │
│    FechaRegistro DATETIME│       │    DuracionMinutos INT       │
└─────────────────────────┘       └──────────────────────────────┘
```

---

*Fin del documento*
