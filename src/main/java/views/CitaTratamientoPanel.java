package views;

import controllers.controladores.CitaTratamientoController;
import controllers.controladores.CitaController;
import controllers.controladores.TratamientoController;
import models.modelos.entidades.Cita;
import models.modelos.entidades.CitaTratamiento;
import models.modelos.entidades.Tratamiento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CitaTratamientoPanel extends JPanel {

    private CitaTratamientoController controller;
    private CitaController citaController;
    private TratamientoController tratamientoController;
    private JTable table;
    private DefaultTableModel tableModel;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public CitaTratamientoPanel() {
        controller = new CitaTratamientoController();
        citaController = new CitaController();
        tratamientoController = new TratamientoController();
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        String[] columnas = {"ID", "Cita", "Tratamiento", "Cantidad", "Fecha Registro"};
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
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> mostrarDialogo(null));
        btnEditar.addActionListener(e -> editarRegistro());
        btnEliminar.addActionListener(e -> eliminarRegistro());

        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<CitaTratamiento> lista = controller.findAll();
        for (CitaTratamiento ct : lista) {
            Cita c = ct.getCita();
            Tratamiento t = ct.getTratamiento();
            String infoCita = c.getCodCita() + " - " + c.getPaciente().getNombre()
                    + " " + c.getPaciente().getApellidos()
                    + " (" + new SimpleDateFormat("dd/MM/yyyy").format(c.getFecha()) + ")";
            tableModel.addRow(new Object[]{
                    ct.getId(),
                    infoCita,
                    t.getNombreTratamiento(),
                    ct.getCantidad(),
                    ct.getFechaRegistro() != null ? sdf.format(ct.getFechaRegistro()) : ""
            });
        }
    }

    private void mostrarDialogo(CitaTratamiento registro) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                registro == null ? "Nuevo Detalle Cita-Tratamiento" : "Editar Detalle", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ComboBox de citas
        List<Cita> citas = citaController.findAll();
        JComboBox<String> cmbCita = new JComboBox<>();
        for (Cita c : citas) {
            cmbCita.addItem(c.getCodCita() + " - " + c.getPaciente().getNombre()
                    + " " + c.getPaciente().getApellidos()
                    + " (" + new SimpleDateFormat("dd/MM/yyyy").format(c.getFecha()) + ")");
        }

        // ComboBox de tratamientos
        List<Tratamiento> tratamientos = tratamientoController.findAll();
        JComboBox<String> cmbTratamiento = new JComboBox<>();
        for (Tratamiento t : tratamientos) {
            cmbTratamiento.addItem(t.getCodTratamiento() + " - " + t.getNombreTratamiento()
                    + " (" + t.getPrecioEstimado() + "€)");
        }

        JTextField txtCantidad = new JTextField(10);
        JLabel lblFecha = new JLabel("(se asigna automáticamente)");

        if (registro != null) {
            for (int i = 0; i < cmbCita.getItemCount(); i++) {
                if (cmbCita.getItemAt(i).startsWith(String.valueOf(registro.getCita().getCodCita()))) {
                    cmbCita.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < cmbTratamiento.getItemCount(); i++) {
                if (cmbTratamiento.getItemAt(i).startsWith(String.valueOf(registro.getTratamiento().getCodTratamiento()))) {
                    cmbTratamiento.setSelectedIndex(i);
                    break;
                }
            }
            txtCantidad.setText(String.valueOf(registro.getCantidad()));
            lblFecha.setText(registro.getFechaRegistro() != null ? sdf.format(registro.getFechaRegistro()) : "");
        } else {
            txtCantidad.setText("1");
        }

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Cita:"), gbc);
        gbc.gridx = 1;
        form.add(cmbCita, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Tratamiento:"), gbc);
        gbc.gridx = 1;
        form.add(cmbTratamiento, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1;
        form.add(txtCantidad, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Fecha Registro:"), gbc);
        gbc.gridx = 1;
        form.add(lblFecha, gbc);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            try {
                int idxCita = cmbCita.getSelectedIndex();
                int idxTrat = cmbTratamiento.getSelectedIndex();
                if (idxCita == -1 || idxTrat == -1) {
                    JOptionPane.showMessageDialog(dialog, "Selecciona una cita y un tratamiento");
                    return;
                }

                Integer cantidad = Integer.parseInt(txtCantidad.getText());
                if (cantidad < 1) {
                    JOptionPane.showMessageDialog(dialog, "La cantidad debe ser mayor que 0");
                    return;
                }

                if (registro == null) {
                    // Verificar duplicado
                    List<CitaTratamiento> existentes = controller.findAll();
                    Cita citaSel = citas.get(idxCita);
                    Tratamiento tratSel = tratamientos.get(idxTrat);
                    for (CitaTratamiento ct : existentes) {
                        if (ct.getCita().getCodCita().equals(citaSel.getCodCita())
                                && ct.getTratamiento().getCodTratamiento().equals(tratSel.getCodTratamiento())) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Esta cita ya tiene ese tratamiento asignado");
                            return;
                        }
                    }

                    CitaTratamiento nuevo = new CitaTratamiento(citaSel, tratSel, cantidad);
                    controller.create(nuevo);
                } else {
                    registro.setCita(citas.get(idxCita));
                    registro.setTratamiento(tratamientos.get(idxTrat));
                    registro.setCantidad(cantidad);
                    controller.update(registro);
                }
                loadData();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "La cantidad debe ser un número entero");
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
        form.add(panelBotones, gbc);

        dialog.add(form);
        dialog.setVisible(true);
    }

    private void editarRegistro() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un registro para editar");
            return;
        }
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        CitaTratamiento registro = controller.findById(id);
        mostrarDialogo(registro);
    }

    private void eliminarRegistro() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un registro para eliminar");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de eliminar este detalle?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Integer id = (Integer) tableModel.getValueAt(row, 0);
            controller.delete(id);
            loadData();
        }
    }
}
