package views;

import util.BackupUtil;
import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de la aplicación.
 * Utiliza JTabbedPane para mostrar una pestaña por cada entidad:
 * Pacientes, Doctores, Citas y Tratamientos.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        // Configuración de la ventana principal
        setTitle("Clínica Dental - Sistema de Gestión");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla

        // JTabbedPane crea un sistema de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Pacientes", new PacientePanel());
        tabbedPane.addTab("Doctores", new DoctorPanel());
        tabbedPane.addTab("Citas", new CitaPanel());
        tabbedPane.addTab("Tratamientos", new TratamientoPanel());
        tabbedPane.addTab("Historial Clínico", new HistorialClinicoPanel());
        tabbedPane.addTab("Cita-Tratamiento", new CitaTratamientoPanel());

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel bottomPanel = new JPanel();

        JButton btnCopia = new JButton("Copia de Seguridad");
        btnCopia.addActionListener(e -> {
            try {
                BackupUtil.realizarCopia();
                JOptionPane.showMessageDialog(this, "Copia de seguridad realizada con éxito");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al hacer copia: " + ex.getMessage());
            }
        });
        bottomPanel.add(btnCopia);

        JButton btnRestaurar = new JButton("Restaurar");
        btnRestaurar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Se borrarán TODOS los datos actuales y se restaurará la última copia.\n¿Continuar?",
                "Confirmar restauración", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    BackupUtil.restaurarUltimaCopia();
                    JOptionPane.showMessageDialog(this, "Datos restaurados correctamente");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al restaurar: " + ex.getMessage());
                }
            }
        });
        bottomPanel.add(btnRestaurar);

        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        bottomPanel.add(btnSalir);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        // SwingUtilities.invokeLater asegura que la GUI se cree en el
        // Event Dispatch Thread (hilo seguro para componentes Swing)
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
