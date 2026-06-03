package util;

// Importaciones para JPA (Java Persistence API)
import javax.persistence.EntityManagerFactory; // Factory para crear EntityManager
import javax.persistence.Persistence; // Carga la configuraciÃƒÂ³n de persistencia

/**
 * FÃƒÂ¡brica compartida de EntityManagerFactory.
 * Esta clase implementa el patrÃƒÂ³n Singleton para garantizar que toda la aplicaciÃƒÂ³n
 * comparta una ÃƒÂºnica instancia de EntityManagerFactory.
 *
 * Ã‚Â¿Por quÃƒÂ©?: Crear mÃƒÂºltiples instancias es ineficiente y consume recursos.
 * Una ÃƒÂºnica instancia compartida es thread-safe por defecto.
 *
 * PatrÃƒÂ³n: Singleton con inicializaciÃƒÂ³n estÃƒÂ¡tica (thread-safe automÃƒÂ¡ticamente).
 */
public class SharedEntityManagerFactory {

    // ÃƒÅ¡nica instancia de EntityManagerFactory para toda la aplicaciÃƒÂ³n
    // Se crea una sola vez cuando se carga esta clase
    // "clinica_dental" debe coincidir con el nombre de la persistence-unit en persistence.xml
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("clinica_dental");

    // Bloque estÃƒÂ¡tico: se ejecuta cuando se carga la clase por primera vez
    static {
        // Registra un hook de cierre para cuando se termina la aplicaciÃƒÂ³n
        // Esto garantiza que los recursos se liberen correctamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> close()));
    }

    /**
     * Obtiene la instancia ÃƒÂºnica de EntityManagerFactory.
     * Este mÃƒÂ©todo es thread-safe porque siempre retorna la misma instancia estÃƒÂ¡tica.
     *
     * @return La instancia ÃƒÂºnica de EntityManagerFactory para toda la aplicaciÃƒÂ³n
     */
    public static EntityManagerFactory getInstance() {
        return EMF; // Retorna la ÃƒÂºnica instancia
    }

    /**
     * Cierra la fÃƒÂ¡brica y libera los recursos de la conexiÃƒÂ³n a la BD.
     * Se llama automÃƒÂ¡ticamente al terminar la aplicaciÃƒÂ³n (via shutdown hook),
     * pero tambiÃƒÂ©n puede llamarse manualmente si es necesario.
     *
     * IMPORTANTE: Solo se debe llamar una sola vez al final del programa.
     */
    public static void close() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close(); // Cierra la fÃƒÂ¡brica y libera recursos
        }
    }
}
