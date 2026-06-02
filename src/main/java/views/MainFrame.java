package views;

import util.BackupUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

    private Color azul = new Color(33, 150, 243);
    private Color azulOscuro = new Color(25, 118, 210);
    private Color blanco = Color.WHITE;
    private Color fondo = new Color(240, 245, 250);

    public MainFrame() {
        setTitle("Clinica Dental");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(fondo);

        panelCentral.setBackground(fondo);

        panelCentral.add(pnlPaciente, "Pacientes");
        panelCentral.add(pnlDoctor, "Doctores");
        panelCentral.add(pnlCita, "Citas");
        panelCentral.add(pnlTratamiento, "Tratamientos");
        panelCentral.add(pnlHistorial, "Historial");
        panelCentral.add(pnlCitaTratamiento, "CitaTratamiento");

        JPanel nav = new JPanel(new GridLayout(6, 1, 5, 12));
        nav.setBackground(azul);
        nav.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        String[] names = {"Pacientes", "Doctores", "Citas", "Tratamientos", "Historial", "Cita-Tratamiento"};
        String[] keys = {"Pacientes", "Doctores", "Citas", "Tratamientos", "Historial", "CitaTratamiento"};
        for (int i = 0; i < names.length; i++) {
            JButton btn = new JButton(names[i]);
            btn.setBackground(azulOscuro);
            btn.setForeground(blanco);
            btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String key = keys[i];
            btn.addActionListener(e -> cardLayout.show(panelCentral, key));
            nav.add(btn);
        }

        JPanel bottom = new JPanel();
        bottom.setBackground(blanco);
        bottom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, azul),
            new EmptyBorder(8, 8, 8, 8)
        ));

        JButton btnBackup = new JButton("Copia Seguridad");
        btnBackup.setBackground(new Color(76, 175, 80));
        btnBackup.setForeground(blanco);
        btnBackup.setFocusPainted(false);
        btnBackup.setFont(btnBackup.getFont().deriveFont(Font.BOLD, 12f));

        JButton btnRestore = new JButton("Restaurar");
        btnRestore.setBackground(new Color(255, 152, 0));
        btnRestore.setForeground(blanco);
        btnRestore.setFocusPainted(false);
        btnRestore.setFont(btnRestore.getFont().deriveFont(Font.BOLD, 12f));

        JButton btnSalir = new JButton("Salir");
        btnSalir.setBackground(new Color(244, 67, 54));
        btnSalir.setForeground(blanco);
        btnSalir.setFocusPainted(false);
        btnSalir.setFont(btnSalir.getFont().deriveFont(Font.BOLD, 12f));

        btnBackup.addActionListener(e -> {
            try {
                BackupUtil.realizarCopia();
                JOptionPane.showMessageDialog(this, "Copia realizada");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
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
