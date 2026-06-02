package views;

import controllers.controladores.CitaController;
import controllers.controladores.PacienteController;
import controllers.controladores.DoctorController;
import controllers.controladores.TratamientoController;
import models.modelos.entidades.Cita;
import models.modelos.entidades.CitaTratamiento;
import models.modelos.entidades.Paciente;
import models.modelos.entidades.Doctor;
import models.modelos.entidades.Tratamiento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Panel de gestión de Citas.
 * Permite crear citas asociando un paciente y un doctor,
 * e incluir tratamientos en cada cita.
 */
public class CitaPanel extends JPanel {

    private CitaController citaController;
    private PacienteController pacienteController;
    private DoctorController doctorController;
    private TratamientoController tratamientoController;
    private JTable table;
    private DefaultTableModel tableModel;
    private SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");

    public CitaPanel() {
        citaController = new CitaController();
        pacienteController = new PacienteController();
        doctorController = new DoctorController();
        tratamientoController = new TratamientoController();
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        String[] columnas = {"ID", "Paciente", "Doctor", "Fecha", "Hora Inicio", "Hora Fin", "Estado"};
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
        JButton btnNueva = new JButton("Nueva Cita");
        JButton btnEditar = new JButton("Editar Estado");
        JButton btnEliminar = new JButton("Eliminar");

        btnNueva.addActionListener(e -> mostrarDialogo(null));
        btnEditar.addActionListener(e -> editarCita());
        btnEliminar.addActionListener(e -> eliminarCita());

        buttonPanel.add(btnNueva);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Cita> citas = citaController.findAll();
        for (Cita c : citas) {
            tableModel.addRow(new Object[]{
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

    private void mostrarDialogo(Cita cita) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                cita == null ? "Nueva Cita" : "Editar Cita", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ComboBox de pacientes
        List<Paciente> pacientes = pacienteController.findAll();
        JComboBox<String> cmbPaciente = new JComboBox<>();
        for (Paciente p : pacientes) {
            cmbPaciente.addItem(p.getCodPaciente() + " - " + p.getNombre() + " " + p.getApellidos());
        }

        // ComboBox de doctores
        List<Doctor> doctores = doctorController.findAll();
        JComboBox<String> cmbDoctor = new JComboBox<>();
        for (Doctor d : doctores) {
            cmbDoctor.addItem(d.getCodDoctor() + " - " + d.getNombre());
        }

        JTextField txtFecha = new JTextField(20);
        JTextField txtHoraInicio = new JTextField(20);
        JTextField txtHoraFin = new JTextField(20);
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"Programada", "Completada", "Cancelada"});

        if (cita != null) {
            // Seleccionar el paciente correcto en el combo
            for (int i = 0; i < cmbPaciente.getItemCount(); i++) {
                if (cmbPaciente.getItemAt(i).startsWith(String.valueOf(cita.getPaciente().getCodPaciente()))) {
                    cmbPaciente.setSelectedIndex(i);
                    break;
                }
            }
            // Seleccionar el doctor correcto en el combo
            for (int i = 0; i < cmbDoctor.getItemCount(); i++) {
                if (cmbDoctor.getItemAt(i).startsWith(String.valueOf(cita.getDoctor().getCodDoctor()))) {
                    cmbDoctor.setSelectedIndex(i);
                    break;
                }
            }
            txtFecha.setText(cita.getFecha() != null ? sdfFecha.format(cita.getFecha()) : "");
            txtHoraInicio.setText(cita.getHoraInicio() != null ? sdfHora.format(cita.getHoraInicio()) : "");
            txtHoraFin.setText(cita.getHoraFin() != null ? sdfHora.format(cita.getHoraFin()) : "");
            cmbEstado.setSelectedItem(cita.getEstado());
        }

        String[] labels = {"Paciente:", "Doctor:", "Fecha (dd/MM/yyyy):", "Hora Inicio (HH:mm):", "Hora Fin (HH:mm):", "Estado:"};
        JComponent[] fields = {cmbPaciente, cmbDoctor, txtFecha, txtHoraInicio, txtHoraFin, cmbEstado};

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
                // Obtener paciente y doctor seleccionados
                int idxPaciente = cmbPaciente.getSelectedIndex();
                int idxDoctor = cmbDoctor.getSelectedIndex();
                Paciente pacienteSeleccionado = pacientes.get(idxPaciente);
                Doctor doctorSeleccionado = doctores.get(idxDoctor);

                Date fecha = sdfFecha.parse(txtFecha.getText());
                Date horaInicio = sdfHora.parse(txtHoraInicio.getText());
                Date horaFin = sdfHora.parse(txtHoraFin.getText());
                String estado = (String) cmbEstado.getSelectedItem();

                if (cita == null) {
                    Cita nueva = new Cita(pacienteSeleccionado, doctorSeleccionado,
                            fecha, horaInicio, horaFin, estado);
                    citaController.create(nueva);
                } else {
                    cita.setPaciente(pacienteSeleccionado);
                    cita.setDoctor(doctorSeleccionado);
                    cita.setFecha(fecha);
                    cita.setHoraInicio(horaInicio);
                    cita.setHoraFin(horaFin);
                    cita.setEstado(estado);
                    citaController.update(cita);
                }
                loadData();
                dialog.dispose();
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Formato de fecha/hora incorrecto");
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

    private void editarCita() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita para editar");
            return;
        }
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        Cita cita = citaController.findById(id);
        mostrarDialogo(cita);
    }

    private void eliminarCita() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita para eliminar");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar esta cita?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Integer id = (Integer) tableModel.getValueAt(row, 0);
            citaController.delete(id);
            loadData();
        }
    }
}
