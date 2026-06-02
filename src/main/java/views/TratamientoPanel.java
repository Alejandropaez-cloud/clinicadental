package views;

import controllers.controladores.TratamientoController;
import models.modelos.entidades.Tratamiento;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TratamientoPanel extends JPanel {

    private TratamientoController controller;
    private JTable table;
    private DefaultTableModel model;
    private Color azul = new Color(33, 150, 243);
    private Color blanco = Color.WHITE;

    public TratamientoPanel() {
        controller = new TratamientoController();
        setLayout(new BorderLayout());
        setBackground(new Color(240, 245, 250));
        initComponents();
        loadData();
    }

    private void initComponents() {
        JLabel titulo = new JLabel("TRATAMIENTOS", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setForeground(azul);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        String[] cols = {"ID", "Nombre", "Descripcion", "Precio (€)", "Duracion (min)"};
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
        for (Tratamiento t : controller.findAll()) {
            model.addRow(new Object[]{
                t.getCodTratamiento(), t.getNombreTratamiento(), t.getDescripcion(),
                t.getPrecioEstimado(), t.getDuracionMinutos()
            });
        }
    }

    private void dialogo(Tratamiento tratamiento) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                tratamiento == null ? "Nuevo Tratamiento" : "Editar Tratamiento", true);
        d.setSize(350, 250);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(new Color(240, 245, 250));

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.setBackground(new Color(240, 245, 250));
        JTextField txtNom = new JTextField();
        JTextField txtDesc = new JTextField();
        JTextField txtPre = new JTextField();
        JTextField txtDur = new JTextField();

        if (tratamiento != null) {
            txtNom.setText(tratamiento.getNombreTratamiento());
            txtDesc.setText(tratamiento.getDescripcion());
            txtPre.setText(String.valueOf(tratamiento.getPrecioEstimado()));
            txtDur.setText(String.valueOf(tratamiento.getDuracionMinutos()));
        }

        form.add(new JLabel("Nombre:")); form.add(txtNom);
        form.add(new JLabel("Descripcion:")); form.add(txtDesc);
        form.add(new JLabel("Precio (€):")); form.add(txtPre);
        form.add(new JLabel("Duracion (min):")); form.add(txtDur);
        d.add(form, BorderLayout.CENTER);

        JPanel pnlBtn = new JPanel();
        pnlBtn.setBackground(new Color(240, 245, 250));
        JButton btnOk = new JButton("Guardar");
        btnOk.setBackground(azul); btnOk.setForeground(blanco); btnOk.setFocusPainted(false);
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(158, 158, 158)); btnCancel.setForeground(blanco); btnCancel.setFocusPainted(false);
        btnOk.addActionListener(e -> {
            try {
                if (tratamiento == null) {
                    controller.create(new Tratamiento(txtNom.getText(), txtDesc.getText(),
                            Double.parseDouble(txtPre.getText()), Integer.parseInt(txtDur.getText())));
                } else {
                    tratamiento.setNombreTratamiento(txtNom.getText());
                    tratamiento.setDescripcion(txtDesc.getText());
                    tratamiento.setPrecioEstimado(Double.parseDouble(txtPre.getText()));
                    tratamiento.setDuracionMinutos(Integer.parseInt(txtDur.getText()));
                    controller.update(tratamiento);
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
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un tratamiento"); return; }
        dialogo(controller.findById((Integer) model.getValueAt(row, 0)));
    }

    private void eliminar() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un tratamiento"); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar tratamiento?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.delete((Integer) model.getValueAt(row, 0));
            loadData();
        }
    }
}
