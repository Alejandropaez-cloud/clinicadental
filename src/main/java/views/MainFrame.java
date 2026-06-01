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

    private final PacientePanel pnlPaciente = new PacientePanel();
    private final DoctorPanel pnlDoctor = new DoctorPanel();
    private final CitaPanel pnlCita = new CitaPanel();
    private final TratamientoPanel pnlTratamiento = new TratamientoPanel();
    private final HistorialClinicoPanel pnlHistorial = new HistorialClinicoPanel();
    private final CitaTratamientoPanel pnlCitaTratamiento = new CitaTratamientoPanel();

    public MainFrame() {
        setTitle("Clínica Dental - Sistema de Gestión");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Pacientes", pnlPaciente);
        tabbedPane.addTab("Doctores", pnlDoctor);
        tabbedPane.addTab("Citas", pnlCita);
        tabbedPane.addTab("Tratamientos", pnlTratamiento);
        tabbedPane.addTab("Historial Clínico", pnlHistorial);
        tabbedPane.addTab("Cita-Tratamiento", pnlCitaTratamiento);

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
                    pnlPaciente.loadData();
                    pnlDoctor.loadData();
                    pnlCita.loadData();
                    pnlTratamiento.loadData();
                    pnlHistorial.loadData();
                    pnlCitaTratamiento.loadData();
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
