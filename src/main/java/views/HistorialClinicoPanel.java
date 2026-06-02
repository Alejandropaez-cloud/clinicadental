package views;

import controllers.controladores.HistorialClinicoController;
import controllers.controladores.PacienteController;
import models.modelos.entidades.HistorialClinico;
import models.modelos.entidades.Paciente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistorialClinicoPanel extends JPanel {

    private HistorialClinicoController controller;
    private PacienteController pacienteController;
    private JTable table;
    private DefaultTableModel model;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public HistorialClinicoPanel() {
        controller = new HistorialClinicoController();
        pacienteController = new PacienteController();
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        String[] cols = {"ID", "Paciente", "Alergias", "Enfermedades", "Grupo Sang.", "Fecha Alta"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnl = new JPanel();
        JButton btnNew = new JButton("Nuevo");
        JButton btnEdit = new JButton("Editar");
        JButton btnDel = new JButton("Eliminar");
        btnNew.addActionListener(e -> dialogo(null));
        btnEdit.addActionListener(e -> editar());
        btnDel.addActionListener(e -> eliminar());
        pnl.add(btnNew);
        pnl.add(btnEdit);
        pnl.add(btnDel);
        add(pnl, BorderLayout.SOUTH);
    }

    public void loadData() {
        model.setRowCount(0);
        for (HistorialClinico h : controller.findAll()) {
            model.addRow(new Object[]{
                h.getCodHistorial(),
                h.getPaciente().getNombre() + " " + h.getPaciente().getApellidos(),
                h.getAlergias(), h.getEnfermedadesCronicas(), h.getGrupoSanguineo(),
                h.getFechaAlta() != null ? sdf.format(h.getFechaAlta()) : ""
            });
        }
    }

    private void dialogo(HistorialClinico historial) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                historial == null ? "Nuevo Historial" : "Editar Historial", true);
        d.setSize(400, 300);
        d.setLocationRelativeTo(this);

        List<Paciente> pacientes = pacienteController.findAll();
        JComboBox<String> cmbPac = new JComboBox<>();
        for (Paciente p : pacientes)
            cmbPac.addItem(p.getCodPaciente() + " - " + p.getNombre() + " " + p.getApellidos());

        JTextField txtAlerg = new JTextField();
        JTextField txtEnf = new JTextField();
        JComboBox<String> cmbGrupo = new JComboBox<>(new String[]{"", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        JTextField txtObs = new JTextField();

        if (historial != null) {
            for (int i = 0; i < cmbPac.getItemCount(); i++)
                if (cmbPac.getItemAt(i).startsWith(String.valueOf(historial.getPaciente().getCodPaciente())))
                    cmbPac.setSelectedIndex(i);
            cmbPac.setEnabled(false);
            txtAlerg.setText(historial.getAlergias());
            txtEnf.setText(historial.getEnfermedadesCronicas());
            cmbGrupo.setSelectedItem(historial.getGrupoSanguineo());
            txtObs.setText(historial.getObservacionesGenerales());
        }

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.add(new JLabel("Paciente:")); form.add(cmbPac);
        form.add(new JLabel("Alergias:")); form.add(txtAlerg);
        form.add(new JLabel("Enfermedades:")); form.add(txtEnf);
        form.add(new JLabel("Grupo Sang.:")); form.add(cmbGrupo);
        form.add(new JLabel("Observaciones:")); form.add(txtObs);
        d.add(form, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel();
        JButton btnOk = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");
        btnOk.addActionListener(e -> {
            try {
                if (historial == null) {
                    int idx = cmbPac.getSelectedIndex();
                    if (idx == -1) { JOptionPane.showMessageDialog(d, "Selecciona un paciente"); return; }
                    for (HistorialClinico hc : controller.findAll())
                        if (hc.getPaciente().getCodPaciente().equals(pacientes.get(idx).getCodPaciente())) {
                            JOptionPane.showMessageDialog(d, "Este paciente ya tiene historial");
                            return;
                        }
                    HistorialClinico n = new HistorialClinico(pacientes.get(idx));
                    n.setAlergias(txtAlerg.getText());
                    n.setEnfermedadesCronicas(txtEnf.getText());
                    n.setGrupoSanguineo((String) cmbGrupo.getSelectedItem());
                    n.setObservacionesGenerales(txtObs.getText());
                    controller.create(n);
                } else {
                    historial.setAlergias(txtAlerg.getText());
                    historial.setEnfermedadesCronicas(txtEnf.getText());
                    historial.setGrupoSanguineo((String) cmbGrupo.getSelectedItem());
                    historial.setObservacionesGenerales(txtObs.getText());
                    controller.update(historial);
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

    private void editar() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un historial"); return; }
        dialogo(controller.findById((Integer) model.getValueAt(row, 0)));
    }

    private void eliminar() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un historial"); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar historial?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.delete((Integer) model.getValueAt(row, 0));
            loadData();
        }
    }
}
