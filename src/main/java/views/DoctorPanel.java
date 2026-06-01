package views;

import controllers.controladores.DoctorController;
import models.modelos.entidades.Doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de Doctores.
 */
public class DoctorPanel extends JPanel {

    private DoctorController controller;
    private JTable table;
    private DefaultTableModel tableModel;

    public DoctorPanel() {
        controller = new DoctorController();
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        String[] columnas = {"ID", "Nº Colegiado", "Nombre", "Especialidad", "Teléfono"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnNuevo = new JButton("Nuevo Doctor");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> mostrarDialogo(null));
        btnEditar.addActionListener(e -> editarDoctor());
        btnEliminar.addActionListener(e -> eliminarDoctor());

        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Doctor> doctores = controller.findAll();
        for (Doctor d : doctores) {
            tableModel.addRow(new Object[]{
                    d.getCodDoctor(),
                    d.getNumeroColegiado(),
                    d.getNombre(),
                    d.getEspecialidad(),
                    d.getTelefonoContacto()
            });
        }
    }

    private void mostrarDialogo(Doctor doctor) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                doctor == null ? "Nuevo Doctor" : "Editar Doctor", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtColegiado = new JTextField(20);
        JTextField txtNombre = new JTextField(20);
        JComboBox<String> cmbEspecialidad = new JComboBox<>(new String[]{
                "Odontología General", "Ortodoncia", "Endodoncia",
                "Periodoncia", "Cirugía Oral", "Implantología", "Estética Dental"
        });
        JTextField txtTelefono = new JTextField(20);

        if (doctor != null) {
            txtColegiado.setText(doctor.getNumeroColegiado());
            txtNombre.setText(doctor.getNombre());
            cmbEspecialidad.setSelectedItem(doctor.getEspecialidad());
            txtTelefono.setText(doctor.getTelefonoContacto());
        }

        String[] labels = {"Nº Colegiado:", "Nombre:", "Especialidad:", "Teléfono:"};
        JComponent[] fields = {txtColegiado, txtNombre, cmbEspecialidad, txtTelefono};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            form.add(fields[i], gbc);
        }

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            try {
                if (doctor == null) {
                    Doctor nuevo = new Doctor(txtColegiado.getText(), txtNombre.getText(),
                            (String) cmbEspecialidad.getSelectedItem());
                    nuevo.setTelefonoContacto(txtTelefono.getText());
                    controller.create(nuevo);
                } else {
                    doctor.setNumeroColegiado(txtColegiado.getText());
                    doctor.setNombre(txtNombre.getText());
                    doctor.setEspecialidad((String) cmbEspecialidad.getSelectedItem());
                    doctor.setTelefonoContacto(txtTelefono.getText());
                    controller.update(doctor);
                }
                loadData();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        form.add(panelBotones, gbc);

        dialog.add(form);
        dialog.setVisible(true);
    }

    private void editarDoctor() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un doctor para editar");
            return;
        }
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        Doctor doctor = controller.findById(id);
        mostrarDialogo(doctor);
    }

    private void eliminarDoctor() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un doctor para eliminar");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este doctor?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Integer id = (Integer) tableModel.getValueAt(row, 0);
            controller.delete(id);
            loadData();
        }
    }
}
