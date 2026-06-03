package daw;

// Importa la clase MainFrame que contiene la interfaz gráfica
import views.MainFrame;

/**
 * Clase principal de la aplicación.
 * Sirve como punto de entrada para lanzar la aplicación.
 * Simplemente delega la ejecución a MainFrame.main()
 */
public class Main {

    /**
     * Método main: punto de entrada de la aplicación.
     * Recibe los argumentos de lí­nea de comandos y los pasa a MainFrame.
     * @param args argumentos de lí­nea de comandos (no se usan actualmente)
     */
    public static void main(String[] args) {
        MainFrame.main(args); // Inicia la interfaz gráfica principal
    }
}
