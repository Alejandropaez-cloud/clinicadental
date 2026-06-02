package views;

import controllers.controladores.TratamientoController;
import models.modelos.entidades.Tratamiento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de Tratamientos.
 */
public class TratamientoPanel extends JPanel {

    private TratamientoController controller;
    private JTable table;
    private DefaultTableModel tableModel;

    public TratamientoPanel() {
        controller = new TratamientoController();
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        String[] columnas = {"ID", "Nombre", "Descripción", "Precio (€)", "Duración (min)"};
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
        JButton btnNuevo = new JButton("Nuevo Tratamiento");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnNuevo.addActionListener(e -> mostrarDialogo(null));
        btnEditar.addActionListener(e -> editarTratamiento());
        btnEliminar.addActionListener(e -> eliminarTratamiento());

        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Tratamiento> tratamientos = controller.findAll();
        for (Tratamiento t : tratamientos) {
            tableModel.addRow(new Object[]{
                    t.getCodTratamiento(),
                    t.getNombreTratamiento(),
                    t.getDescripcion(),
                    t.getPrecioEstimado(),
                    t.getDuracionMinutos()
            });
        }
    }

    private void mostrarDialogo(Tratamiento tratamiento) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                tratamiento == null ? "Nuevo Tratamiento" : "Editar Tratamiento", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField(20);
        JTextField txtDescripcion = new JTextField(20);
        JTextField txtPrecio = new JTextField(20);
        JTextField txtDuracion = new JTextField(20);

        if (tratamiento != null) {
            txtNombre.setText(tratamiento.getNombreTratamiento());
            txtDescripcion.setText(tratamiento.getDescripcion());
            txtPrecio.setText(String.valueOf(tratamiento.getPrecioEstimado()));
            txtDuracion.setText(String.valueOf(tratamiento.getDuracionMinutos()));
        }

        String[] labels = {"Nombre:", "Descripción:", "Precio (€):", "Duración (min):"};
        JTextField[] fields = {txtNombre, txtDescripcion, txtPrecio, txtDuracion};

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
                Double precio = Double.parseDouble(txtPrecio.getText());
                Integer duracion = Integer.parseInt(txtDuracion.getText());

                if (tratamiento == null) {
                    Tratamiento nuevo = new Tratamiento(txtNombre.getText(), txtDescripcion.getText(),
                            precio, duracion);
                    controller.create(nuevo);
                } else {
                    tratamiento.setNombreTratamiento(txtNombre.getText());
                    tratamiento.setDescripcion(txtDescripcion.getText());
                    tratamiento.setPrecioEstimado(precio);
                    tratamiento.setDuracionMinutos(duracion);
                    controller.update(tratamiento);
                }
                loadData();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Precio y duración deben ser números");
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

    private void editarTratamiento() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un tratamiento para editar");
            return;
        }
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        Tratamiento tratamiento = controller.findById(id);
        mostrarDialogo(tratamiento);
    }

    private void eliminarTratamiento() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un tratamiento para eliminar");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este tratamiento?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Integer id = (Integer) tableModel.getValueAt(row, 0);
            controller.delete(id);
            loadData();
        }
    }
}
