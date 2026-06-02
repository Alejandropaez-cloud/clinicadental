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
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelCentral = new JPanel(cardLayout);

    public MainFrame() {
        setTitle("Clinica Dental");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panelCentral.add(pnlPaciente, "Pacientes");
        panelCentral.add(pnlDoctor, "Doctores");
        panelCentral.add(pnlCita, "Citas");
        panelCentral.add(pnlTratamiento, "Tratamientos");
        panelCentral.add(pnlHistorial, "Historial");
        panelCentral.add(pnlCitaTratamiento, "CitaTratamiento");

        JPanel nav = new JPanel(new GridLayout(6, 1, 5, 10));
        nav.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] names = {"Pacientes", "Doctores", "Citas", "Tratamientos", "Historial", "Cita-Tratamiento"};
        String[] keys = {"Pacientes", "Doctores", "Citas", "Tratamientos", "Historial", "CitaTratamiento"};
        for (int i = 0; i < names.length; i++) {
            JButton btn = new JButton(names[i]);
            String key = keys[i];
            btn.addActionListener(e -> cardLayout.show(panelCentral, key));
            nav.add(btn);
        }

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

        add(nav, BorderLayout.WEST);
        add(panelCentral, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        cardLayout.show(panelCentral, "Pacientes");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
