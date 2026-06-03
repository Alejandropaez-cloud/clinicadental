package util;

// Importaciones para persistencia
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

// Importaciones para manejo de archivos
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Importaciones para manejo de fechas y tiempo
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Importaciones de utilidades generales
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// Clase de utilidades para realizar copias de seguridad y restauraciones
public class BackupUtil {

    // Ruta del directorio donde se almacenan las copias de seguridad
    private static final String BACKUP_ROOT = "backups";
    // Formato de fecha y hora para los nombres de las carpetas de backup
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    // DefiniciÃƒÂ³n de todas las tablas y sus columnas a respaldar
    // Cada fila es: {nombre_tabla, columna1, columna2, ...}
    private static final String[][] TABLAS = {
        {"Paciente",              "codPaciente", "DNI", "Nombre", "Apellidos", "Fecha_Nacimiento", "telefono", "email", "direccion"},
        {"Doctor",                "codDoctor", "NumeroColegiado", "Nombre", "Especialidad", "TelefonoContacto"},
        {"Tratamiento",           "codTratamiento", "NombreTratamiento", "Descripcion", "PrecioEstimado", "DuracionMinutos"},
        {"Historial_Clinico",     "codHistorial", "codPaciente", "Alergias", "EnfermedadesCronicas", "GrupoSanguineo", "ObservacionesGenerales", "FechaAlta"},
        {"Cita",                  "codCita", "codPaciente", "codDoctor", "fecha", "horaInicio", "horaFin", "estado", "fechaCreacion"},
        {"Cita_Tratamiento",      "id", "codCita", "codTratamiento", "Cantidad", "FechaRegistro"}
    };

    /**
     * Realiza una copia de seguridad de todas las tablas en archivos CSV.
     * Crea un directorio con timestamp y exporta los datos.
     */
    public static void realizarCopia() throws Exception {
        // Obtiene el EntityManager para acceder a la BD
        EntityManagerFactory emf = SharedEntityManagerFactory.getInstance();
        EntityManager em = emf.createEntityManager();

        // Crea un timestamp con formato yyyy-MM-dd_HH-mm-ss
        String timestamp = LocalDateTime.now().format(FMT);
        // Crea la ruta de la carpeta de backup: backups/2026-01-15_10-30-45/
        Path backupDir = Paths.get(BACKUP_ROOT, timestamp);
        // Crea los directorios necesarios
        Files.createDirectories(backupDir);

        // Para cada tabla definida en TABLAS
        for (String[] tablaInfo : TABLAS) {
            String nombreTabla = tablaInfo[0]; // Obtiene el nombre de la tabla
            Path csvFile = backupDir.resolve(nombreTabla + ".csv"); // Ruta del archivo CSV

            // Abre un BufferedWriter para escribir el CSV
            try (BufferedWriter writer = Files.newBufferedWriter(csvFile)) {
                // Escribe la cabecera del CSV (nombres de columnas sin el nombre de la tabla)
                String header = String.join(",", columnasSinNombreTabla(tablaInfo));
                writer.write(header);
                writer.newLine(); // Salto de lÃƒÂ­nea

                // Ejecuta un SELECT * en la tabla para obtener todos los datos
                List<Object[]> rows = em.createNativeQuery("SELECT * FROM " + nombreTabla).getResultList();
                // Para cada fila de datos
                for (Object[] row : rows) {
                    List<String> values = new ArrayList<>();
                    // Para cada columna en la fila
                    for (Object col : row) {
                        // Escapa caracteres especiales en CSV y agrega el valor
                        values.add(col != null ? escaparCSV(col.toString()) : "");
                    }
                    // Escribe la fila en el CSV
                    writer.write(String.join(",", values));
                    writer.newLine(); // Salto de lÃƒÂ­nea
                }
            }
        }

        em.close(); // Cierra el EntityManager
    }

    /**
     * Restaura la ÃƒÂºltima copia de seguridad disponible.
     * Elimina todos los datos actuales y restaura desde el backup mÃƒÂ¡s reciente.
     */
    public static void restaurarUltimaCopia() throws Exception {
        Path backupRoot = Paths.get(BACKUP_ROOT); // Obtiene la ruta raÃƒÂ­z de backups
        if (!Files.exists(backupRoot)) {
            throw new RuntimeException("No hay copias de seguridad disponibles");
        }

        // Busca el directorio mÃƒÂ¡s reciente (por nombre de archivo)
        Optional<Path> latest;
        try (var dirStream = Files.list(backupRoot)) {
            latest = dirStream
                .filter(Files::isDirectory) // Solo directorios
                .max(Comparator.comparing(p -> p.getFileName().toString())); // El ÃƒÂºltimo por orden alfabÃƒÂ©tico
        }

        if (latest.isEmpty()) {
            throw new RuntimeException("No hay copias de seguridad disponibles");
        }

        Path backupDir = latest.get(); // Obtiene la ruta del backup mÃƒÂ¡s reciente

        // Obtiene el EntityManager y la transacciÃƒÂ³n
        EntityManagerFactory emf = SharedEntityManagerFactory.getInstance();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin(); // Inicia la transacciÃƒÂ³n

            // Desactiva las restricciones de claves forÃƒÂ¡neas para poder limpiar sin conflictos
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

            // Elimina todos los datos de todas las tablas
            for (String[] tablaInfo : TABLAS) {
                em.createNativeQuery("DELETE FROM " + tablaInfo[0]).executeUpdate();
            }

            // Restaura los datos en orden inverso (para respetar las FK)
            for (int i = TABLAS.length - 1; i >= 0; i--) {
                String[] tablaInfo = TABLAS[i];
                String nombreTabla = tablaInfo[0]; // Nombre de la tabla
                Path csvFile = backupDir.resolve(nombreTabla + ".csv"); // Ruta del CSV

                // Si el archivo no existe, salta esta tabla
                if (!Files.exists(csvFile)) continue;

                // Lee los datos del CSV
                List<String[]> data = leerCSV(csvFile);
                if (data.isEmpty() || data.size() < 2) continue; // Si estÃƒÂ¡ vacÃƒÂ­o, salta

                String[] columnas = data.get(0); // Primera fila: nombres de columnas
                long totalFilas = 0;

                // Restaura cada fila
                for (int r = 1; r < data.size(); r++) {
                    String[] valores = data.get(r);
                    // Construye un SQL INSERT
                    StringBuilder sql = new StringBuilder();
                    sql.append("INSERT INTO ").append(nombreTabla).append(" (");
                    sql.append(String.join(",", columnas)); // Columnas
                    sql.append(") VALUES (");

                    // Agrega los valores, escapando comillas
                    for (int c = 0; c < valores.length; c++) {
                        if (c > 0) sql.append(",");
                        if (valores[c] == null || valores[c].isEmpty()) {
                            sql.append("NULL"); // Si estÃƒÂ¡ vacÃƒÂ­o, inserta NULL
                        } else {
                            // Escapa las comillas internas
                            sql.append("'").append(valores[c].replace("'", "''")).append("'");
                        }
                    }
                    sql.append(")");

                    // Ejecuta el INSERT
                    em.createNativeQuery(sql.toString()).executeUpdate();
                    totalFilas++;
                }
            }

            // Reactiva las restricciones de claves forÃƒÂ¡neas
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();

            tx.commit(); // Confirma la transacciÃƒÂ³n

            // Limpiar cachÃƒÂ© L2 de EclipseLink para que las prÃƒÂ³ximas consultas
            // obtengan los datos reales de la BD y no objetos antiguos en cachÃƒÂ©
            emf.getCache().evictAll();
        } catch (Exception ex) {
            // Si hay error, revierte la transacciÃƒÂ³n
            if (tx.isActive()) tx.rollback();
            try {
                // Intenta reactivar las FK aunque haya error
                em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            } catch (Exception ignored) {}
            throw new RuntimeException("Error al restaurar copia", ex);
        } finally {
            em.close(); // Cierra el EntityManager
        }
    }

    /**
     * MÃƒÂ©todo auxiliar: obtiene las columnas sin el nombre de la tabla.
     * Usado para los encabezados del CSV.
     */
    private static String[] columnasSinNombreTabla(String[] tablaInfo) {
        // Copia todas las columnas excepto la primera (nombre de tabla)
        String[] columnas = new String[tablaInfo.length - 1];
        System.arraycopy(tablaInfo, 1, columnas, 0, columnas.length);
        return columnas;
    }

    /**
     * Escapa caracteres especiales para cumplir con el formato CSV.
     * Si el valor contiene comas, comillas o saltos de lÃƒÂ­nea, lo envuelve en comillas.
     */
    private static String escaparCSV(String value) {
        if (value == null) return "";
        // Si contiene caracteres especiales, envuelve en comillas y escapa las comillas internas
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value; // Retorna el valor sin cambios
    }

    /**
     * Lee un archivo CSV y retorna sus datos como una lista de arrays.
     */
    private static List<String[]> leerCSV(Path file) throws IOException {
        List<String[]> data = new ArrayList<>();
        // Abre el archivo para lectura
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            // Lee lÃƒÂ­nea por lÃƒÂ­nea
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue; // Salta lÃƒÂ­neas vacÃƒÂ­as
                // Parsea la lÃƒÂ­nea y agrega a la lista
                data.add(parsearLineaCSV(line));
            }
        }
        return data;
    }

    /**
     * Parsea una lÃƒÂ­nea CSV respetando las comillas.
     * Maneja correctamente valores con comas dentro de comillas.
     */
    private static String[] parsearLineaCSV(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false; // Flag para rastrear si estamos dentro de comillas
        StringBuilder current = new StringBuilder();

        // Procesa cada carÃƒÂ¡cter de la lÃƒÂ­nea
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Si es una comilla
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Si hay dos comillas seguidas, es una comilla escapada
                    current.append('"');
                    i++; // Salta la siguiente comilla
                } else {
                    // Cambia el estado de "dentro de comillas"
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Si es una coma y no estamos dentro de comillas, es un separador
                fields.add(current.toString().trim()); // Agrega el campo
                current = new StringBuilder(); // Reinicia el campo
            } else {
                // Cualquier otro carÃƒÂ¡cter se agrega al campo actual
                current.append(c);
            }
        }
        // Agrega el ÃƒÂºltimo campo
        fields.add(current.toString().trim());

        return fields.toArray(new String[0]); // Retorna como array
    }
}
