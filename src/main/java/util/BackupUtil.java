package util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BackupUtil {

    private static final String BACKUP_ROOT = "backups";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private static final String[][] TABLAS = {
        {"Paciente",              "codPaciente", "DNI", "Nombre", "Apellidos", "Fecha_Nacimiento", "telefono", "email", "direccion"},
        {"Doctor",                "codDoctor", "NumeroColegiado", "Nombre", "Especialidad", "TelefonoContacto"},
        {"Tratamiento",           "codTratamiento", "NombreTratamiento", "Descripcion", "PrecioEstimado", "DuracionMinutos"},
        {"Historial_Clinico",     "codHistorial", "codPaciente", "Alergias", "EnfermedadesCronicas", "GrupoSanguineo", "ObservacionesGenerales", "FechaAlta"},
        {"Cita",                  "codCita", "codPaciente", "codDoctor", "fecha", "horaInicio", "horaFin", "estado", "fechaCreacion"},
        {"Cita_Tratamiento",      "id", "codCita", "codTratamiento", "Cantidad", "FechaRegistro"}
    };

    public static void realizarCopia() throws Exception {
        EntityManagerFactory emf = SharedEntityManagerFactory.getInstance();
        EntityManager em = emf.createEntityManager();

        String timestamp = LocalDateTime.now().format(FMT);
        Path backupDir = Paths.get(BACKUP_ROOT, timestamp);
        Files.createDirectories(backupDir);

        for (String[] tablaInfo : TABLAS) {
            String nombreTabla = tablaInfo[0];
            Path csvFile = backupDir.resolve(nombreTabla + ".csv");

            try (BufferedWriter writer = Files.newBufferedWriter(csvFile)) {
                String header = String.join(",", columnasSinNombreTabla(tablaInfo));
                writer.write(header);
                writer.newLine();

                List<Object[]> rows = em.createNativeQuery("SELECT * FROM " + nombreTabla).getResultList();
                for (Object[] row : rows) {
                    List<String> values = new ArrayList<>();
                    for (Object col : row) {
                        values.add(col != null ? escaparCSV(col.toString()) : "");
                    }
                    writer.write(String.join(",", values));
                    writer.newLine();
                }
            }
        }

        em.close();
    }

    public static void restaurarUltimaCopia() throws Exception {
        Path backupRoot = Paths.get(BACKUP_ROOT);
        if (!Files.exists(backupRoot)) {
            throw new RuntimeException("No hay copias de seguridad disponibles");
        }

        Optional<Path> latest;
        try (var dirStream = Files.list(backupRoot)) {
            latest = dirStream
                .filter(Files::isDirectory)
                .max(Comparator.comparing(p -> p.getFileName().toString()));
        }

        if (latest.isEmpty()) {
            throw new RuntimeException("No hay copias de seguridad disponibles");
        }

        Path backupDir = latest.get();

        EntityManagerFactory emf = SharedEntityManagerFactory.getInstance();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

            for (String[] tablaInfo : TABLAS) {
                em.createNativeQuery("DELETE FROM " + tablaInfo[0]).executeUpdate();
            }

            for (int i = TABLAS.length - 1; i >= 0; i--) {
                String[] tablaInfo = TABLAS[i];
                String nombreTabla = tablaInfo[0];
                Path csvFile = backupDir.resolve(nombreTabla + ".csv");

                if (!Files.exists(csvFile)) continue;

                List<String[]> data = leerCSV(csvFile);
                if (data.isEmpty() || data.size() < 2) continue;

                String[] columnas = data.get(0);
                long totalFilas = 0;

                for (int r = 1; r < data.size(); r++) {
                    String[] valores = data.get(r);
                    StringBuilder sql = new StringBuilder();
                    sql.append("INSERT INTO ").append(nombreTabla).append(" (");
                    sql.append(String.join(",", columnas));
                    sql.append(") VALUES (");

                    for (int c = 0; c < valores.length; c++) {
                        if (c > 0) sql.append(",");
                        if (valores[c] == null || valores[c].isEmpty()) {
                            sql.append("NULL");
                        } else {
                            sql.append("'").append(valores[c].replace("'", "''")).append("'");
                        }
                    }
                    sql.append(")");

                    em.createNativeQuery(sql.toString()).executeUpdate();
                    totalFilas++;
                }
            }

            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();

            tx.commit();

            // Limpiar caché L2 de EclipseLink para que las próximas consultas
            // obtengan los datos reales de la BD y no objetos antiguos en caché
            emf.getCache().evictAll();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            try {
                em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            } catch (Exception ignored) {}
            throw new RuntimeException("Error al restaurar copia", ex);
        } finally {
            em.close();
        }
    }

    private static String[] columnasSinNombreTabla(String[] tablaInfo) {
        String[] columnas = new String[tablaInfo.length - 1];
        System.arraycopy(tablaInfo, 1, columnas, 0, columnas.length);
        return columnas;
    }

    private static String escaparCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static List<String[]> leerCSV(Path file) throws IOException {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                data.add(parsearLineaCSV(line));
            }
        }
        return data;
    }

    private static String[] parsearLineaCSV(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());

        return fields.toArray(new String[0]);
    }
}
