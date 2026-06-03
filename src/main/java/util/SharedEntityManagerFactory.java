package util;

// Importaciones para JPA (Java Persistence API)
import javax.persistence.EntityManagerFactory; // Factory para crear EntityManager
import javax.persistence.Persistence; // Carga la configuración de persistencia

/**
 * Fábrica compartida de EntityManagerFactory.
 * Esta clase implementa el patrón Singleton para garantizar que toda la aplicación
 * comparta una única instancia de EntityManagerFactory.
 *
 * ¿Por qué?: Crear múltiples instancias es ineficiente y consume recursos.
 * Una única instancia compartida es thread-safe por defecto.
 *
 * Patrón: Singleton con inicialización estática (thread-safe automáticamente).
 */
public class SharedEntityManagerFactory {

    // Única instancia de EntityManagerFactory para toda la aplicación
    // Se crea una sola vez cuando se carga esta clase
    // "clinica_dental" debe coincidir con el nombre de la persistence-unit en persistence.xml
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("clinica_dental");

    // Bloque estático: se ejecuta cuando se carga la clase por primera vez
    static {
        // Registra un hook de cierre para cuando se termina la aplicación
        // Esto garantiza que los recursos se liberen correctamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> close()));
    }

    /**
     * Obtiene la instancia única de EntityManagerFactory.
     * Este método es thread-safe porque siempre retorna la misma instancia estática.
     *
     * @return La instancia única de EntityManagerFactory para toda la aplicación
     */
    public static EntityManagerFactory getInstance() {
        return EMF; // Retorna la única instancia
    }

    /**
     * Cierra la fábrica y libera los recursos de la conexión a la BD.
     * Se llama automáticamente al terminar la aplicación (via shutdown hook),
     * pero también puede llamarse manualmente si es necesario.
     *
     * IMPORTANTE: Solo se debe llamar una sola vez al final del programa.
     */
    public static void close() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close(); // Cierra la fábrica y libera recursos
        }
    }
}
