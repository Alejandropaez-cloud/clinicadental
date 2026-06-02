package views;

import controllers.controladores.DoctorController;
import models.modelos.entidades.Doctor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorPanel extends JPanel {

    private DoctorController controller;
    private JTable table;
    private DefaultTableModel model;

    public DoctorPanel() {
        controller = new DoctorController();
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        String[] cols = {"ID", "N. Colegiado", "Nombre", "Especialidad", "Telefono"};
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
        for (Doctor d : controller.findAll()) {
            model.addRow(new Object[]{
                d.getCodDoctor(), d.getNumeroColegiado(), d.getNombre(),
                d.getEspecialidad(), d.getTelefonoContacto()
            });
        }
    }

    private void dialogo(Doctor doctor) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                doctor == null ? "Nuevo Doctor" : "Editar Doctor", true);
        d.setSize(350, 250);
        d.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField txtCol = new JTextField();
        JTextField txtNom = new JTextField();
        JComboBox<String> cmbEsp = new JComboBox<>(new String[]{
            "Odontologia General", "Ortodoncia", "Endodoncia",
            "Periodoncia", "Cirugia Oral", "Implantologia", "Estetica Dental"
        });
        JTextField txtTel = new JTextField();

        if (doctor != null) {
            txtCol.setText(doctor.getNumeroColegiado());
            txtNom.setText(doctor.getNombre());
            cmbEsp.setSelectedItem(doctor.getEspecialidad());
            txtTel.setText(doctor.getTelefonoContacto());
        }

        form.add(new JLabel("N. Colegiado:"));
        form.add(txtCol);
        form.add(new JLabel("Nombre:"));
        form.add(txtNom);
        form.add(new JLabel("Especialidad:"));
        form.add(cmbEsp);
        form.add(new JLabel("Telefono:"));
        form.add(txtTel);

        d.add(form, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel();
        JButton btnOk = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");
        btnOk.addActionListener(e -> {
            try {
                if (doctor == null) {
                    Doctor n = new Doctor(txtCol.getText(), txtNom.getText(),
                            (String) cmbEsp.getSelectedItem());
                    n.setTelefonoContacto(txtTel.getText());
                    controller.create(n);
                } else {
                    doctor.setNumeroColegiado(txtCol.getText());
                    doctor.setNombre(txtNom.getText());
                    doctor.setEspecialidad((String) cmbEsp.getSelectedItem());
                    doctor.setTelefonoContacto(txtTel.getText());
                    controller.update(doctor);
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
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un doctor"); return; }
        dialogo(controller.findById((Integer) model.getValueAt(row, 0)));
    }

    private void eliminar() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un doctor"); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar doctor?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.delete((Integer) model.getValueAt(row, 0));
            loadData();
        }
    }
}
