package views;

import controllers.controladores.PacienteController;
import models.modelos.entidades.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Panel de gestión de Pacientes.
 * Muestra una tabla con todos los pacientes y botones para crear, editar y eliminar.
 */
public class PacientePanel extends JPanel {

    private PacienteController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public PacientePanel() {
        controller = new PacienteController();
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    /**
     * Inicializa los componentes del panel: tabla y botones.
     */
    private void initComponents() {
        // Modelo de tabla: define las columnas y hace que no sean editables
        String[] columnas = {"ID", "DNI", "Nombre", "Apellidos", "Fecha Nac.", "Teléfono", "Email", "Dirección"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // La tabla no se puede editar directamente
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo seleccionar una fila
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        JButton btnNuevo = new JButton("Nuevo Paciente");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> mostrarDialogo(null));
        btnEditar.addActionListener(e -> editarPaciente());
        btnEliminar.addActionListener(e -> eliminarPaciente());

        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Carga todos los pacientes de la BD y los muestra en la tabla.
     */
    public void loadData() {
        tableModel.setRowCount(0); // Limpiar tabla
        List<Paciente> pacientes = controller.findAll();
        for (Paciente p : pacientes) {
            tableModel.addRow(new Object[]{
                    p.getCodPaciente(),
                    p.getDni(),
                    p.getNombre(),
                    p.getApellidos(),
                    p.getFechaNacimiento() != null ? sdf.format(p.getFechaNacimiento()) : "",
                    p.getTelefono(),
                    p.getEmail(),
                    p.getDireccion()
            });
        }
    }

    /**
     * Abre un diálogo para crear o editar un paciente.
     * Si paciente es null → crea uno nuevo. Si no → edita el existente.
     */
    private void mostrarDialogo(Paciente paciente) {
        // Diálogo modal (bloquea la ventana principal mientras está abierto)
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                paciente == null ? "Nuevo Paciente" : "Editar Paciente", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        // Formulario con GridBagLayout (layout flexible)
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtDNI = new JTextField(20);
        JTextField txtNombre = new JTextField(20);
        JTextField txtApellidos = new JTextField(20);
        JTextField txtFecha = new JTextField(20);
        JTextField txtTelefono = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtDireccion = new JTextField(20);

        // Si estamos editando, rellenar los campos con los datos actuales
        if (paciente != null) {
            txtDNI.setText(paciente.getDni());
            txtNombre.setText(paciente.getNombre());
            txtApellidos.setText(paciente.getApellidos());
            txtFecha.setText(paciente.getFechaNacimiento() != null ? sdf.format(paciente.getFechaNacimiento()) : "");
            txtTelefono.setText(paciente.getTelefono());
            txtEmail.setText(paciente.getEmail());
            txtDireccion.setText(paciente.getDireccion());
        }

        // Añadir campos al formulario
        String[] labels = {"DNI:", "Nombre:", "Apellidos:", "Fecha Nac (dd/MM/yyyy):", "Teléfono:", "Email:", "Dirección:"};
        JTextField[] fields = {txtDNI, txtNombre, txtApellidos, txtFecha, txtTelefono, txtEmail, txtDireccion};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            form.add(fields[i], gbc);
        }

        // Botones Guardar y Cancelar
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            try {
                Date fechaNac = txtFecha.getText().isEmpty() ? null : sdf.parse(txtFecha.getText());

                if (paciente == null) {
                    // Crear nuevo paciente
                    Paciente nuevo = new Paciente(txtDNI.getText(), txtNombre.getText(),
                            txtApellidos.getText(), fechaNac);
                    nuevo.setTelefono(txtTelefono.getText());
                    nuevo.setEmail(txtEmail.getText());
                    nuevo.setDireccion(txtDireccion.getText());
                    controller.create(nuevo);
                } else {
                    // Editar paciente existente
                    paciente.setDni(txtDNI.getText());
                    paciente.setNombre(txtNombre.getText());
                    paciente.setApellidos(txtApellidos.getText());
                    paciente.setFechaNacimiento(fechaNac);
                    paciente.setTelefono(txtTelefono.getText());
                    paciente.setEmail(txtEmail.getText());
                    paciente.setDireccion(txtDireccion.getText());
                    controller.update(paciente);
                }
                loadData(); // Recargar tabla
                dialog.dispose(); // Cerrar diálogo
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Formato de fecha incorrecto. Usa dd/MM/yyyy");
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

    private void editarPaciente() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un paciente para editar");
            return;
        }
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        Paciente paciente = controller.findById(id);
        mostrarDialogo(paciente);
    }

    private void eliminarPaciente() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un paciente para eliminar");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este paciente?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Integer id = (Integer) tableModel.getValueAt(row, 0);
            controller.delete(id);
            loadData();
        }
    }
}
