package views;

import controllers.controladores.CitaController;
import controllers.controladores.PacienteController;
import controllers.controladores.DoctorController;
import models.modelos.entidades.Cita;
import models.modelos.entidades.Paciente;
import models.modelos.entidades.Doctor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel que administra la vista de Citas.
 * Permite gestionar citas programadas y su información asociada.
 */
public class CitaPanel extends JPanel {

    private CitaController citaController;
    private PacienteController pacienteController;
    private DoctorController doctorController;
    private JTable table;
    private DefaultTableModel model;
    private SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
    private Color azul = new Color(33, 150, 243);
    private Color blanco = Color.WHITE;

    /**
     * Constructor del panel de Citas.
     * Inicializa controladores y configura la vista.
     */
    public CitaPanel() {
        citaController = new CitaController();
        pacienteController = new PacienteController();
        doctorController = new DoctorController();
        setLayout(new BorderLayout());
        setBackground(new Color(240, 245, 250));
        initComponents();
        loadData();
    }

    /**
     * Inicializa los controles visuales del panel de citas.
     */
    private void initComponents() {
        JLabel titulo = new JLabel("CITAS", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setForeground(azul);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        String[] cols = {"ID", "Paciente", "Doctor", "Fecha", "Hora Inicio", "Hora Fin", "Estado"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(azul);
        table.getTableHeader().setForeground(blanco);
        table.getTableHeader().setFont(table.getFont().deriveFont(Font.BOLD, 12f));
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnl = new JPanel();
        pnl.setBackground(new Color(240, 245, 250));
        pnl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        JButton btnNew = new JButton("Nuevo");
        JButton btnEdit = new JButton("Editar");
        JButton btnDel = new JButton("Eliminar");
        btnNew.setBackground(azul); btnNew.setForeground(blanco); btnNew.setFocusPainted(false);
        btnEdit.setBackground(new Color(255, 152, 0)); btnEdit.setForeground(blanco); btnEdit.setFocusPainted(false);
        btnDel.setBackground(new Color(244, 67, 54)); btnDel.setForeground(blanco); btnDel.setFocusPainted(false);
        btnNew.addActionListener(e -> dialogo(null));
        btnEdit.addActionListener(e -> editar());
        btnDel.addActionListener(e -> eliminar());
        pnl.add(btnNew);
        pnl.add(btnEdit);
        pnl.add(btnDel);
        add(pnl, BorderLayout.SOUTH);
    }

    /**
     * Carga las citas desde la base de datos y actualiza la tabla.
     */
    public void loadData() {
        model.setRowCount(0);
        for (Cita c : citaController.findAll()) {
            model.addRow(new Object[]{
                c.getCodCita(),
                c.getPaciente().getNombre() + " " + c.getPaciente().getApellidos(),
                c.getDoctor().getNombre(),
                c.getFecha() != null ? sdfFecha.format(c.getFecha()) : "",
                c.getHoraInicio() != null ? sdfHora.format(c.getHoraInicio()) : "",
                c.getHoraFin() != null ? sdfHora.format(c.getHoraFin()) : "",
                c.getEstado()
            });
        }
    }

    private void dialogo(Cita cita) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                cita == null ? "Nueva Cita" : "Editar Cita", true);
        d.setSize(350, 300);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(new Color(240, 245, 250));

        List<Paciente> pacientes = pacienteController.findAll();
        List<Doctor> doctores = doctorController.findAll();

        JComboBox<String> cmbPac = new JComboBox<>();
        for (Paciente p : pacientes)
            cmbPac.addItem(p.getCodPaciente() + " - " + p.getNombre() + " " + p.getApellidos());

        JComboBox<String> cmbDoc = new JComboBox<>();
        for (Doctor doc : doctores)
            cmbDoc.addItem(doc.getCodDoctor() + " - " + doc.getNombre());

        JTextField txtFecha = new JTextField();
        JTextField txtHoraIni = new JTextField();
        JTextField txtHoraFin = new JTextField();
        JComboBox<String> cmbEst = new JComboBox<>(new String[]{"Programada", "Completada", "Cancelada"});

        if (cita != null) {
            for (int i = 0; i < cmbPac.getItemCount(); i++)
                if (cmbPac.getItemAt(i).startsWith(String.valueOf(cita.getPaciente().getCodPaciente())))
                    cmbPac.setSelectedIndex(i);
            for (int i = 0; i < cmbDoc.getItemCount(); i++)
                if (cmbDoc.getItemAt(i).startsWith(String.valueOf(cita.getDoctor().getCodDoctor())))
                    cmbDoc.setSelectedIndex(i);
            txtFecha.setText(cita.getFecha() != null ? sdfFecha.format(cita.getFecha()) : "");
            txtHoraIni.setText(cita.getHoraInicio() != null ? sdfHora.format(cita.getHoraInicio()) : "");
            txtHoraFin.setText(cita.getHoraFin() != null ? sdfHora.format(cita.getHoraFin()) : "");
            cmbEst.setSelectedItem(cita.getEstado());
        }

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.setBackground(new Color(240, 245, 250));
        form.add(new JLabel("Paciente:")); form.add(cmbPac);
        form.add(new JLabel("Doctor:")); form.add(cmbDoc);
        form.add(new JLabel("Fecha (dd/MM/yyyy):")); form.add(txtFecha);
        form.add(new JLabel("Hora Inicio (HH:mm):")); form.add(txtHoraIni);
        form.add(new JLabel("Hora Fin (HH:mm):")); form.add(txtHoraFin);
        form.add(new JLabel("Estado:")); form.add(cmbEst);
        d.add(form, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel();
        pnlBtn.setBackground(new Color(240, 245, 250));
        JButton btnOk = new JButton("Guardar");
        btnOk.setBackground(azul); btnOk.setForeground(blanco); btnOk.setFocusPainted(false);
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(158, 158, 158)); btnCancel.setForeground(blanco); btnCancel.setFocusPainted(false);
        btnOk.addActionListener(e -> {
            try {
                int iPac = cmbPac.getSelectedIndex();
                int iDoc = cmbDoc.getSelectedIndex();
                if (cita == null) {
                    Cita n = new Cita(pacientes.get(iPac), doctores.get(iDoc),
                            sdfFecha.parse(txtFecha.getText()), sdfHora.parse(txtHoraIni.getText()),
                            sdfHora.parse(txtHoraFin.getText()), (String) cmbEst.getSelectedItem());
                    citaController.create(n);
                } else {
                    cita.setPaciente(pacientes.get(iPac));
                    cita.setDoctor(doctores.get(iDoc));
                    cita.setFecha(sdfFecha.parse(txtFecha.getText()));
                    cita.setHoraInicio(sdfHora.parse(txtHoraIni.getText()));
                    cita.setHoraFin(sdfHora.parse(txtHoraFin.getText()));
                    cita.setEstado((String) cmbEst.getSelectedItem());
                    citaController.update(cita);
                }
                loadData();
                d.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage());
            }
        });
        btnCancel.addActionListener(e -> d.dispose());
        pnlBtn.add(btnOk);
        pnlBtn.add(btnCancel);
        d.add(pnlBtn, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    /**
     * Abre el diálogo de edición para la cita seleccionada.
     */
    private void editar() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona una cita"); return; }
        dialogo(citaController.findById((Integer) model.getValueAt(row, 0)));
    }

    /**
     * Elimina la cita seleccionada tras pedir confirmación.
     */
    private void eliminar() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona una cita"); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar cita?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            citaController.delete((Integer) model.getValueAt(row, 0));
            loadData();
        }
    }
}
