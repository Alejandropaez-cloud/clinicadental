package views;

import util.BackupUtil; // Importa utilidades de copia de seguridad
import javax.swing.*; // Importa componentes grÃƒÂ¡ficos Swing
import javax.swing.border.EmptyBorder; // Importa para bordes vacÃƒÂ­os
import java.awt.*; // Importa clases de diseÃƒÂ±o grÃƒÂ¡fico

/**
 * Ventana principal de la aplicación.
 * Contiene la navegación entre secciones de pacientes, doctores, citas,
 * tratamientos, historial clínico y la relación cita-tratamiento.
 */
public class MainFrame extends JFrame {

    private final PacientePanel pnlPaciente = new PacientePanel(); // Panel para gestionar pacientes
    private final DoctorPanel pnlDoctor = new DoctorPanel(); // Panel para gestionar doctores
    private final CitaPanel pnlCita = new CitaPanel(); // Panel para gestionar citas
    private final TratamientoPanel pnlTratamiento = new TratamientoPanel(); // Panel para gestionar tratamientos
    private final HistorialClinicoPanel pnlHistorial = new HistorialClinicoPanel(); // Panel para gestionar historiales clÃƒÂ­nicos
    private final CitaTratamientoPanel pnlCitaTratamiento = new CitaTratamientoPanel(); // Panel para gestionar la relación cita-tratamiento

    private final CardLayout cardLayout = new CardLayout(); // Layout para cambiar entre paneles
    private final JPanel panelCentral = new JPanel(cardLayout); // Panel central que contiene todas las vistas

    private Color azul = new Color(33, 150, 243); // Color azul principal
    private Color azulOscuro = new Color(25, 118, 210); // Color azul oscuro para botones
    private Color blanco = Color.WHITE; // Color blanco para texto y fondos claros
    private Color fondo = new Color(240, 245, 250); // Color de fondo suave

    /**
     * Constructor de la ventana principal.
     * Configura la ventana, crea la navegación y agrega los paneles de cada módulo.
     */
    public MainFrame() {
        setTitle("Clinica Dental"); // Tí­tulo de la ventana
        setSize(950, 600); // Tamaño de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana
        setLocationRelativeTo(null); // Centra la ventana en pantalla
        getContentPane().setBackground(fondo); // Establece el color de fondo del contenedor principal

        panelCentral.setBackground(fondo); // Establece el color de fondo del panel central

        panelCentral.add(pnlPaciente, "Pacientes"); // Agrega el panel de pacientes
        panelCentral.add(pnlDoctor, "Doctores"); // Agrega el panel de doctores
        panelCentral.add(pnlCita, "Citas"); // Agrega el panel de citas
        panelCentral.add(pnlTratamiento, "Tratamientos"); // Agrega el panel de tratamientos
        panelCentral.add(pnlHistorial, "Historial"); // Agrega el panel de historial clí­nico
        panelCentral.add(pnlCitaTratamiento, "CitaTratamiento"); // Agrega el panel de relación cita-tratamiento

        JPanel nav = new JPanel(new GridLayout(6, 1, 5, 12)); // Panel de navegación con 6 filas y 1 columna
        nav.setBackground(azul); // Fondo azul para navegaciÃƒÂ³n
        nav.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Margen interno del panel de navegación

        String[] names = {"Pacientes", "Doctores", "Citas", "Tratamientos", "Historial", "Cita-Tratamiento"}; // Texto de botones
        String[] keys = {"Pacientes", "Doctores", "Citas", "Tratamientos", "Historial", "CitaTratamiento"}; // Claves para CardLayout

        for (int i = 0; i < names.length; i++) {
            JButton btn = new JButton(names[i]); // Crea botón de navegación
            btn.setBackground(azulOscuro); // Establece color de fondo
            btn.setForeground(blanco); // Establece color de texto
            btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f)); // Fuente en negrita tamaño 14
            btn.setFocusPainted(false); // Deshabilita el efecto de enfoque
            btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen interno del botón
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cambia el cursor a mano

            String key = keys[i]; // Clave asociada al panel
            btn.addActionListener(e -> cardLayout.show(panelCentral, key)); // Muestra el panel correspondiente al pulsar
            nav.add(btn); // AÃƒÂ±ade el botón al panel de navegación
        }

        JPanel bottom = new JPanel(); // Panel inferior para acciones globales
        bottom.setBackground(blanco); // Fondo blanco
        bottom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, azul), // Borde superior azul
            new EmptyBorder(8, 8, 8, 8) // Margen interno del panel inferior
        ));

        JButton btnBackup = new JButton("Copia Seguridad"); // Botón para copiar datos
        btnBackup.setBackground(new Color(76, 175, 80)); // Color verde
        btnBackup.setForeground(blanco); // Texto blanco
        btnBackup.setFocusPainted(false); // Sin borde de enfoque
        btnBackup.setFont(btnBackup.getFont().deriveFont(Font.BOLD, 12f)); // Fuente negrita tamaño 12

        JButton btnRestore = new JButton("Restaurar"); // Botón para restaurar copias
        btnRestore.setBackground(new Color(255, 152, 0)); // Color naranja
        btnRestore.setForeground(blanco); // Texto blanco
        btnRestore.setFocusPainted(false); // Sin borde de enfoque
        btnRestore.setFont(btnRestore.getFont().deriveFont(Font.BOLD, 12f)); // Fuente negrita tamaño 12

        JButton btnSalir = new JButton("Salir"); // Botón para cerrar la aplicación
        btnSalir.setBackground(new Color(244, 67, 54)); // Color rojo
        btnSalir.setForeground(blanco); // Texto blanco
        btnSalir.setFocusPainted(false); // Sin borde de enfoque
        btnSalir.setFont(btnSalir.getFont().deriveFont(Font.BOLD, 12f)); // Fuente negrita tamaño 12

        btnBackup.addActionListener(e -> {
            try {
                BackupUtil.realizarCopia(); // Realiza la copia de seguridad
                JOptionPane.showMessageDialog(this, "Copia realizada"); // Muestra mensaje de éxito
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); // Muestra mensaje de error
            }
        });

        btnRestore.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "Se borraran todos los datos y se restaurara la ultima copia. Continuar?",
                    "Restaurar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    BackupUtil.restaurarUltimaCopia(); // Restaura la última copia
                    pnlPaciente.loadData(); // Recarga el panel de pacientes
                    pnlDoctor.loadData(); // Recarga el panel de doctores
                    pnlCita.loadData(); // Recarga el panel de citas
                    pnlTratamiento.loadData(); // Recarga el panel de tratamientos
                    pnlHistorial.loadData(); // Recarga el panel de historial cl­nico
                    pnlCitaTratamiento.loadData(); // Recarga el panel de relación cita-tratamiento
                    JOptionPane.showMessageDialog(this, "Datos restaurados"); // Muestra mensaje de éxito
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); // Muestra mensaje de error
                }
            }
        });

        btnSalir.addActionListener(e -> System.exit(0)); // Cierra la aplicación

        bottom.add(btnBackup); // Añade botón de copia de seguridad
        bottom.add(btnRestore); // Añade botón de restaurar
        bottom.add(btnSalir); // Añade botón de salir

        add(nav, BorderLayout.WEST); // Agrega panel de navegación a la izquierda
        add(panelCentral, BorderLayout.CENTER); // Agrega panel central al centro
        add(bottom, BorderLayout.SOUTH); // Agrega panel inferior abajo

        cardLayout.show(panelCentral, "Pacientes"); // Muestra el panel de pacientes por defecto
    }

    /**
     * Punto de entrada de la aplicación.
     * Arranca la interfaz de usuario dentro del hilo de eventos de Swing.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true)); // Inicia la UI en el hilo de eventos
    }
}
