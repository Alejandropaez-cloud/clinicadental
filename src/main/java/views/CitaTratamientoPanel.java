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
    private DefaultTableModel model;
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
        String[] cols = {"ID", "Cita", "Tratamiento", "Cantidad", "Fecha Registro"};
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
        for (CitaTratamiento ct : controller.findAll()) {
            Cita c = ct.getCita();
            Tratamiento t = ct.getTratamiento();
            String infoCita = c.getCodCita() + " - " + c.getPaciente().getNombre()
                    + " " + c.getPaciente().getApellidos()
                    + " (" + new SimpleDateFormat("dd/MM/yyyy").format(c.getFecha()) + ")";
            model.addRow(new Object[]{
                ct.getId(), infoCita, t.getNombreTratamiento(),
                ct.getCantidad(), ct.getFechaRegistro() != null ? sdf.format(ct.getFechaRegistro()) : ""
            });
        }
    }

    private void dialogo(CitaTratamiento registro) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                registro == null ? "Nuevo Detalle" : "Editar Detalle", true);
        d.setSize(400, 250);
        d.setLocationRelativeTo(this);

        List<Cita> citas = citaController.findAll();
        List<Tratamiento> tratamientos = tratamientoController.findAll();

        JComboBox<String> cmbCita = new JComboBox<>();
        for (Cita c : citas)
            cmbCita.addItem(c.getCodCita() + " - " + c.getPaciente().getNombre()
                    + " " + c.getPaciente().getApellidos()
                    + " (" + new SimpleDateFormat("dd/MM/yyyy").format(c.getFecha()) + ")");

        JComboBox<String> cmbTrat = new JComboBox<>();
        for (Tratamiento t : tratamientos)
            cmbTrat.addItem(t.getCodTratamiento() + " - " + t.getNombreTratamiento()
                    + " (" + t.getPrecioEstimado() + "€)");

        JTextField txtCant = new JTextField("1");

        if (registro != null) {
            for (int i = 0; i < cmbCita.getItemCount(); i++)
                if (cmbCita.getItemAt(i).startsWith(String.valueOf(registro.getCita().getCodCita())))
                    cmbCita.setSelectedIndex(i);
            for (int i = 0; i < cmbTrat.getItemCount(); i++)
                if (cmbTrat.getItemAt(i).startsWith(String.valueOf(registro.getTratamiento().getCodTratamiento())))
                    cmbTrat.setSelectedIndex(i);
            txtCant.setText(String.valueOf(registro.getCantidad()));
        }

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("Cita:")); form.add(cmbCita);
        form.add(new JLabel("Tratamiento:")); form.add(cmbTrat);
        form.add(new JLabel("Cantidad:")); form.add(txtCant);
        d.add(form, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel();
        JButton btnOk = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");
        btnOk.addActionListener(e -> {
            try {
                int iCita = cmbCita.getSelectedIndex();
                int iTrat = cmbTrat.getSelectedIndex();
                if (iCita == -1 || iTrat == -1) { JOptionPane.showMessageDialog(d, "Selecciona cita y tratamiento"); return; }
                int cant = Integer.parseInt(txtCant.getText());
                if (cant < 1) { JOptionPane.showMessageDialog(d, "Cantidad debe ser > 0"); return; }
                if (registro == null) {
                    Cita citaSel = citas.get(iCita);
                    Tratamiento tratSel = tratamientos.get(iTrat);
                    for (CitaTratamiento ct : controller.findAll())
                        if (ct.getCita().getCodCita().equals(citaSel.getCodCita())
                                && ct.getTratamiento().getCodTratamiento().equals(tratSel.getCodTratamiento())) {
                            JOptionPane.showMessageDialog(d, "Esta cita ya tiene ese tratamiento");
                            return;
                        }
                    controller.create(new CitaTratamiento(citaSel, tratSel, cant));
                } else {
                    registro.setCita(citas.get(iCita));
                    registro.setTratamiento(tratamientos.get(iTrat));
                    registro.setCantidad(cant);
                    controller.update(registro);
                }
                loadData();
                d.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "La cantidad debe ser un numero");
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
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un registro"); return; }
        dialogo(controller.findById((Integer) model.getValueAt(row, 0)));
    }

    private void eliminar() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un registro"); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar registro?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.delete((Integer) model.getValueAt(row, 0));
            loadData();
        }
    }
}
