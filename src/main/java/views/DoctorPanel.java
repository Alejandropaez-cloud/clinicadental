package views;

import controllers.controladores.DoctorController;
import models.modelos.entidades.Doctor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel que administra la vista de Doctores.
 * Permite listar, crear, editar y eliminar doctores.
 */
public class DoctorPanel extends JPanel {

    private DoctorController controller;
    private JTable table;
    private DefaultTableModel model;
    private Color azul = new Color(33, 150, 243);
    private Color blanco = Color.WHITE;

    /**
     * Constructor del panel de Doctores.
     * Inicializa la interfaz y carga los datos disponibles.
     */
    public DoctorPanel() {
        controller = new DoctorController();
        setLayout(new BorderLayout());
        setBackground(new Color(240, 245, 250));
        initComponents();
        loadData();
    }

    /**
     * Construye los componentes visuales del panel de doctores.
     */
    private void initComponents() {
        JLabel titulo = new JLabel("DOCTORES", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setForeground(azul);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        String[] cols = {"ID", "N. Colegiado", "Nombre", "Especialidad", "Telefono"};
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
     * Recupera los doctores de la base de datos y actualiza la tabla.
     */
    /**
     * Recupera los doctores desde la base de datos y actualiza la tabla.
     */
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
        d.getContentPane().setBackground(new Color(240, 245, 250));

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.setBackground(new Color(240, 245, 250));
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

        form.add(new JLabel("N. Colegiado:")); form.add(txtCol);
        form.add(new JLabel("Nombre:")); form.add(txtNom);
        form.add(new JLabel("Especialidad:")); form.add(cmbEsp);
        form.add(new JLabel("Telefono:")); form.add(txtTel);

        d.add(form, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel();
        pnlBtn.setBackground(new Color(240, 245, 250));
        JButton btnOk = new JButton("Guardar");
        btnOk.setBackground(azul); btnOk.setForeground(blanco); btnOk.setFocusPainted(false);
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(158, 158, 158)); btnCancel.setForeground(blanco); btnCancel.setFocusPainted(false);
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

    /**
     * Abre el diálogo de edición para el doctor seleccionado.
     */
    private void editar() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un doctor"); return; }
        dialogo(controller.findById((Integer) model.getValueAt(row, 0)));
    }

    /**
     * Elimina el doctor seleccionado después de pedir confirmación.
     */
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
