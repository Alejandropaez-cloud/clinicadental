package views;

import util.BackupUtil;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final PacientePanel pnlPaciente = new PacientePanel();
    private final DoctorPanel pnlDoctor = new DoctorPanel();
    private final CitaPanel pnlCita = new CitaPanel();
    private final TratamientoPanel pnlTratamiento = new TratamientoPanel();
    private final HistorialClinicoPanel pnlHistorial = new HistorialClinicoPanel();
    private final CitaTratamientoPanel pnlCitaTratamiento = new CitaTratamientoPanel();

    public MainFrame() {
        setTitle("Clinica Dental");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Pacientes", pnlPaciente);
        tabs.addTab("Doctores", pnlDoctor);
        tabs.addTab("Citas", pnlCita);
        tabs.addTab("Tratamientos", pnlTratamiento);
        tabs.addTab("Historial", pnlHistorial);
        tabs.addTab("Cita-Tratamiento", pnlCitaTratamiento);

        add(tabs, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnBackup = new JButton("Copia Seguridad");
        btnBackup.addActionListener(e -> {
            try {
                BackupUtil.realizarCopia();
                JOptionPane.showMessageDialog(this, "Copia realizada");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        JButton btnRestore = new JButton("Restaurar");
        btnRestore.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "Se borraran todos los datos y se restaurara la ultima copia. Continuar?",
                    "Restaurar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    BackupUtil.restaurarUltimaCopia();
                    pnlPaciente.loadData();
                    pnlDoctor.loadData();
                    pnlCita.loadData();
                    pnlTratamiento.loadData();
                    pnlHistorial.loadData();
                    pnlCitaTratamiento.loadData();
                    JOptionPane.showMessageDialog(this, "Datos restaurados");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));

        bottom.add(btnBackup);
        bottom.add(btnRestore);
        bottom.add(btnSalir);
        add(bottom, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
