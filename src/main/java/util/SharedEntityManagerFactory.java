package util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Fábrica compartida de EntityManagerFactory.
 * Todos los controladores usan esta misma instancia para evitar
 * crear múltiples conexiones a la base de datos innecesariamente.
 * 
 * Patrón: Singleton thread-safe mediante inicialización estática.
 */
public class SharedEntityManagerFactory {

    // Única instancia de EntityManagerFactory para toda la aplicación
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("clinica_dental");

    // Registrar el cierre al terminar la JVM
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> close()));
    }

    /**
     * @return La instancia única de EntityManagerFactory.
     */
    public static EntityManagerFactory getInstance() {
        return EMF;
    }

    /**
     * Cierra la fábrica al terminar la aplicación.
     * Debe llamarse una sola vez al final del programa.
     */
    public static void close() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }
}
