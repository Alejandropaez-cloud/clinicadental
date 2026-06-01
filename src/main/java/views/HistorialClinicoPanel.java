package views;

import controllers.controladores.HistorialClinicoController;
import controllers.controladores.PacienteController;
import models.modelos.entidades.HistorialClinico;
import models.modelos.entidades.Paciente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistorialClinicoPanel extends JPanel {

    private HistorialClinicoController controller;
    private PacienteController pacienteController;
    private JTable table;
    private DefaultTableModel tableModel;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public HistorialClinicoPanel() {
        controller = new HistorialClinicoController();
        pacienteController = new PacienteController();
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        String[] columnas = {"ID", "Paciente", "Alergias", "Enfermedades", "Grupo Sang.", "Fecha Alta"};
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
        JButton btnNuevo = new JButton("Nuevo Historial");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> mostrarDialogo(null));
        btnEditar.addActionListener(e -> editarHistorial());
        btnEliminar.addActionListener(e -> eliminarHistorial());

        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<HistorialClinico> lista = controller.findAll();
        for (HistorialClinico h : lista) {
            tableModel.addRow(new Object[]{
                    h.getCodHistorial(),
                    h.getPaciente().getNombre() + " " + h.getPaciente().getApellidos(),
                    h.getAlergias(),
                    h.getEnfermedadesCronicas(),
                    h.getGrupoSanguineo(),
                    h.getFechaAlta() != null ? sdf.format(h.getFechaAlta()) : ""
            });
        }
    }

    private void mostrarDialogo(HistorialClinico historial) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                historial == null ? "Nuevo Historial Clínico" : "Editar Historial Clínico", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ComboBox de pacientes (solo para nuevo, no se puede cambiar el paciente después)
        List<Paciente> pacientes = pacienteController.findAll();
        JComboBox<String> cmbPaciente = new JComboBox<>();
        for (Paciente p : pacientes) {
            cmbPaciente.addItem(p.getCodPaciente() + " - " + p.getNombre() + " " + p.getApellidos());
        }

        JTextField txtAlergias = new JTextField(20);
        JTextField txtEnfermedades = new JTextField(20);
        JComboBox<String> cmbGrupo = new JComboBox<>(new String[]{"", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        JTextArea txtObservaciones = new JTextArea(4, 20);
        txtObservaciones.setLineWrap(true);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);

        JLabel lblFechaAlta = new JLabel("(se asigna automáticamente)");

        if (historial != null) {
            for (int i = 0; i < cmbPaciente.getItemCount(); i++) {
                if (cmbPaciente.getItemAt(i).startsWith(String.valueOf(historial.getPaciente().getCodPaciente()))) {
                    cmbPaciente.setSelectedIndex(i);
                    break;
                }
            }
            cmbPaciente.setEnabled(false);
            txtAlergias.setText(historial.getAlergias());
            txtEnfermedades.setText(historial.getEnfermedadesCronicas());
            cmbGrupo.setSelectedItem(historial.getGrupoSanguineo());
            txtObservaciones.setText(historial.getObservacionesGenerales());
            lblFechaAlta.setText(historial.getFechaAlta() != null ? sdf.format(historial.getFechaAlta()) : "");
        }

        // Añadir campos
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1;
        form.add(cmbPaciente, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Alergias:"), gbc);
        gbc.gridx = 1;
        form.add(txtAlergias, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Enfermedades Crónicas:"), gbc);
        gbc.gridx = 1;
        form.add(txtEnfermedades, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Grupo Sanguíneo:"), gbc);
        gbc.gridx = 1;
        form.add(cmbGrupo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Observaciones:"), gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        form.add(scrollObs, gbc);

        row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Fecha Alta:"), gbc);
        gbc.gridx = 1;
        form.add(lblFechaAlta, gbc);

        // Botones
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            try {
                if (historial == null) {
                    int idxPaciente = cmbPaciente.getSelectedIndex();
                    if (idxPaciente == -1) {
                        JOptionPane.showMessageDialog(dialog, "Selecciona un paciente");
                        return;
                    }
                    Paciente pacienteSeleccionado = pacientes.get(idxPaciente);

                    // Verificar si el paciente ya tiene historial
                    List<HistorialClinico> existentes = controller.findAll();
                    for (HistorialClinico hc : existentes) {
                        if (hc.getPaciente().getCodPaciente().equals(pacienteSeleccionado.getCodPaciente())) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Este paciente ya tiene un historial clínico");
                            return;
                        }
                    }

                    HistorialClinico nuevo = new HistorialClinico(pacienteSeleccionado);
                    nuevo.setAlergias(txtAlergias.getText());
                    nuevo.setEnfermedadesCronicas(txtEnfermedades.getText());
                    nuevo.setGrupoSanguineo((String) cmbGrupo.getSelectedItem());
                    nuevo.setObservacionesGenerales(txtObservaciones.getText());
                    controller.create(nuevo);
                } else {
                    historial.setAlergias(txtAlergias.getText());
                    historial.setEnfermedadesCronicas(txtEnfermedades.getText());
                    historial.setGrupoSanguineo((String) cmbGrupo.getSelectedItem());
                    historial.setObservacionesGenerales(txtObservaciones.getText());
                    controller.update(historial);
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

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        form.add(panelBotones, gbc);

        dialog.add(form);
        dialog.setVisible(true);
    }

    private void editarHistorial() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un historial para editar");
            return;
        }
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        HistorialClinico historial = controller.findById(id);
        mostrarDialogo(historial);
    }

    private void eliminarHistorial() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un historial para eliminar");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este historial clínico?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Integer id = (Integer) tableModel.getValueAt(row, 0);
            controller.delete(id);
            loadData();
        }
    }
}
