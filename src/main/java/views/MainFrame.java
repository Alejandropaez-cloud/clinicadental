package views;

import util.BackupUtil; // Importa utilidades de copia de seguridad
import javax.swing.*; // Importa componentes gráficos Swing
import javax.swing.border.EmptyBorder; // Importa para bordes vacíos
import java.awt.*; // Importa clases de diseño gráfico

/**
 * Ventana principal de la aplicación.
 * Contiene la navegación entre secciones de pacientes, doctores, citas,
 * tratamientos, historial clínico y la relación cita-tratamiento.
 */
public class MainFrame extends JFrame {

    // Paneles para cada sección de la aplicación
    private final PacientePanel pnlPaciente = new PacientePanel(); // Panel para gestionar pacientes
    private final DoctorPanel pnlDoctor = new DoctorPanel(); // Panel para gestionar doctores
    private final CitaPanel pnlCita = new CitaPanel(); // Panel para gestionar citas
    private final TratamientoPanel pnlTratamiento = new TratamientoPanel(); // Panel para gestionar tratamientos
    private final HistorialClinicoPanel pnlHistorial = new HistorialClinicoPanel(); // Panel para gestionar historiales
    private final CitaTratamientoPanel pnlCitaTratamiento = new CitaTratamientoPanel(); // Panel para gestionar relaciones cita-tratamiento
    
    // CardLayout permite cambiar entre paneles de forma eficiente
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelCentral = new JPanel(cardLayout); // Panel central que cambia entre vistas

    // Colores de la aplicación (tema azul y blanco)
    private Color azul = new Color(33, 150, 243); // Azul claro para header
    private Color azulOscuro = new Color(25, 118, 210); // Azul oscuro para botones
    private Color blanco = Color.WHITE; // Blanco
    private Color fondo = new Color(240, 245, 250); // Azul muy claro para fondo

    // Constructor: inicializa la interfaz gráfica
    public MainFrame() {
        setTitle("Clinica Dental"); // Título de la ventana
        setSize(950, 600); // Tamaño inicial
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        getContentPane().setBackground(fondo); // Establece color de fondo

        panelCentral.setBackground(fondo); // Fondo del panel central

        // Agrega todos los paneles al CardLayout con su clave identificadora
        panelCentral.add(pnlPaciente, "Pacientes");
        panelCentral.add(pnlDoctor, "Doctores");
        panelCentral.add(pnlCita, "Citas");
        panelCentral.add(pnlTratamiento, "Tratamientos");
        panelCentral.add(pnlHistorial, "Historial");
        panelCentral.add(pnlCitaTratamiento, "CitaTratamiento");

        // Crea el panel de navegación izquierdo con 6 botones en una columna
        JPanel nav = new JPanel(new GridLayout(6, 1, 5, 12)); // 6 filas, 1 columna, 5px horizontal, 12px vertical
        nav.setBackground(azul); // Fondo azul
        nav.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Márgenes internos
        
        // Nombres de los botones
        String[] names = {"Pacientes", "Doctores", "Citas", "Tratamientos", "Historial", "Cita-Tratamiento"};
        // Claves de los paneles en el CardLayout (deben coincidir con las añadidas arriba)
        String[] keys = {"Pacientes", "Doctores", "Citas", "Tratamientos", "Historial", "CitaTratamiento"};
        
        // Crea un botón para cada sección
        for (int i = 0; i < names.length; i++) {
            JButton btn = new JButton(names[i]); // Crea el botón con su nombre
            btn.setBackground(azulOscuro); // Fondo azul oscuro
            btn.setForeground(blanco); // Texto blanco
            btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f)); // Fuente: negrita, tamaño 14
            btn.setFocusPainted(false); // No muestra borde de enfoque
            btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Márgenes internos del botón
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mano
            
            String key = keys[i]; // Obtiene la clave del panel
            // Al hacer clic, muestra el panel correspondiente en el CardLayout
            btn.addActionListener(e -> cardLayout.show(panelCentral, key));
            nav.add(btn); // Añade el botón al panel de navegación
        }

        // Crea el panel inferior con botones de acciones globales
        JPanel bottom = new JPanel();
        bottom.setBackground(blanco); // Fondo blanco
        bottom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, azul), // Borde superior azul
            new EmptyBorder(8, 8, 8, 8) // Márgenes internos
        ));

        // Botón "Copia Seguridad" - verde
        JButton btnBackup = new JButton("Copia Seguridad");
        btnBackup.setBackground(new Color(76, 175, 80)); // Verde
        btnBackup.setForeground(blanco); // Texto blanco
        btnBackup.setFocusPainted(false);
        btnBackup.setFont(btnBackup.getFont().deriveFont(Font.BOLD, 12f));

        // Botón "Restaurar" - naranja
        JButton btnRestore = new JButton("Restaurar");
        btnRestore.setBackground(new Color(255, 152, 0)); // Naranja
        btnRestore.setForeground(blanco); // Texto blanco
        btnRestore.setFocusPainted(false);
        btnRestore.setFont(btnRestore.getFont().deriveFont(Font.BOLD, 12f));

        // Botón "Salir" - rojo
        JButton btnSalir = new JButton("Salir");
        btnSalir.setBackground(new Color(244, 67, 54)); // Rojo
        btnSalir.setForeground(blanco); // Texto blanco
        btnSalir.setFocusPainted(false);
        btnSalir.setFont(btnSalir.getFont().deriveFont(Font.BOLD, 12f));

        // Acción del botón "Copia Seguridad"
        btnBackup.addActionListener(e -> {
            try {
                BackupUtil.realizarCopia(); // Realiza la copia de seguridad
                JOptionPane.showMessageDialog(this, "Copia realizada"); // Muestra mensaje de éxito
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); // Muestra mensaje de error
            }
        });
        
        // Acción del botón "Restaurar"
        btnRestore.addActionListener(e -> {
            // Pide confirmación antes de restaurar (se borrarán todos los datos actuales)
            if (JOptionPane.showConfirmDialog(this,
                    "Se borraran todos los datos y se restaurara la ultima copia. Continuar?",
                    "Restaurar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    BackupUtil.restaurarUltimaCopia(); // Restaura la copia de seguridad
                    // Recarga todos los datos en los paneles
                    pnlPaciente.loadData();
                    pnlDoctor.loadData();
                    pnlCita.loadData();
                    pnlTratamiento.loadData();
                    pnlHistorial.loadData();
                    pnlCitaTratamiento.loadData();
                    JOptionPane.showMessageDialog(this, "Datos restaurados"); // Muestra mensaje de éxito
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); // Muestra mensaje de error
                }
            }
        });
        
        // Acción del botón "Salir"
        btnSalir.addActionListener(e -> System.exit(0)); // Cierra la aplicación

        // Agrega los botones al panel inferior
        bottom.add(btnBackup);
        bottom.add(btnRestore);
        bottom.add(btnSalir);

        // Agrupa los componentes en la ventana
        add(nav, BorderLayout.WEST); // Panel de navegación a la izquierda
        add(panelCentral, BorderLayout.CENTER); // Paneles de contenido en el centro
        add(bottom, BorderLayout.SOUTH); // Panel de botones abajo

        // Muestra el primer panel por defecto
        cardLayout.show(panelCentral, "Pacientes");
    }

    // Método principal: punto de entrada de la aplicación
    public static void main(String[] args) {
        // Ejecuta la interfaz en el hilo de eventos de Swing (thread-safe)
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
