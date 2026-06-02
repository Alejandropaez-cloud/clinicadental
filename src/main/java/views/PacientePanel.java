package views;

import controllers.controladores.PacienteController;
import models.modelos.entidades.Paciente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class PacientePanel extends JPanel {

    private PacienteController controller;
    private JTable table;
    private DefaultTableModel model;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private Color azul = new Color(33, 150, 243);
    private Color blanco = Color.WHITE;

    public PacientePanel() {
        controller = new PacienteController();
        setLayout(new BorderLayout());
        setBackground(new Color(240, 245, 250));
        initComponents();
        loadData();
    }

    private void initComponents() {
        JLabel titulo = new JLabel("PACIENTES", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setForeground(azul);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        String[] cols = {"ID", "DNI", "Nombre", "Apellidos", "Fecha Nac.", "Telefono", "Email", "Direccion"};
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

    public void loadData() {
        model.setRowCount(0);
        for (Paciente p : controller.findAll()) {
            model.addRow(new Object[]{
                p.getCodPaciente(), p.getDni(), p.getNombre(), p.getApellidos(),
                p.getFechaNacimiento() != null ? sdf.format(p.getFechaNacimiento()) : "",
                p.getTelefono(), p.getEmail(), p.getDireccion()
            });
        }
    }

    private void dialogo(Paciente paciente) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                paciente == null ? "Nuevo Paciente" : "Editar Paciente", true);
        d.setSize(350, 300);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(new Color(240, 245, 250));
        d.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        form.setBackground(new Color(240, 245, 250));
        JTextField txtDni = new JTextField();
        JTextField txtNom = new JTextField();
        JTextField txtApe = new JTextField();
        JTextField txtFecha = new JTextField();
        JTextField txtTel = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtDir = new JTextField();

        if (paciente != null) {
            txtDni.setText(paciente.getDni());
            txtNom.setText(paciente.getNombre());
            txtApe.setText(paciente.getApellidos());
            txtFecha.setText(paciente.getFechaNacimiento() != null ? sdf.format(paciente.getFechaNacimiento()) : "");
            txtTel.setText(paciente.getTelefono());
            txtEmail.setText(paciente.getEmail());
            txtDir.setText(paciente.getDireccion());
        }

        form.add(new JLabel("DNI:")); form.add(txtDni);
        form.add(new JLabel("Nombre:")); form.add(txtNom);
        form.add(new JLabel("Apellidos:")); form.add(txtApe);
        form.add(new JLabel("Fecha Nac (dd/MM/yyyy):")); form.add(txtFecha);
        form.add(new JLabel("Telefono:")); form.add(txtTel);
        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("Direccion:")); form.add(txtDir);

        d.add(form, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel();
        pnlBtn.setBackground(new Color(240, 245, 250));
        JButton btnOk = new JButton("Guardar");
        btnOk.setBackground(azul); btnOk.setForeground(blanco); btnOk.setFocusPainted(false);
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(158, 158, 158)); btnCancel.setForeground(blanco); btnCancel.setFocusPainted(false);
        btnOk.addActionListener(e -> {
            try {
                if (paciente == null) {
                    Paciente n = new Paciente(txtDni.getText(), txtNom.getText(), txtApe.getText(),
                            txtFecha.getText().isEmpty() ? null : sdf.parse(txtFecha.getText()));
                    n.setTelefono(txtTel.getText());
                    n.setEmail(txtEmail.getText());
                    n.setDireccion(txtDir.getText());
                    controller.create(n);
                } else {
                    paciente.setDni(txtDni.getText());
                    paciente.setNombre(txtNom.getText());
                    paciente.setApellidos(txtApe.getText());
                    paciente.setFechaNacimiento(txtFecha.getText().isEmpty() ? null : sdf.parse(txtFecha.getText()));
                    paciente.setTelefono(txtTel.getText());
                    paciente.setEmail(txtEmail.getText());
                    paciente.setDireccion(txtDir.getText());
                    controller.update(paciente);
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
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un paciente"); return; }
        dialogo(controller.findById((Integer) model.getValueAt(row, 0)));
    }

    private void eliminar() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un paciente"); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar paciente?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.delete((Integer) model.getValueAt(row, 0));
            loadData();
        }
    }
}
