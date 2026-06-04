package views;

import controllers.controladores.CitaTratamientoController; // Controlador CRUD de CitaTratamiento
import models.modelos.entidades.CitaTratamiento; // Entidad CitaTratamiento
import javax.swing.*; // Componentes Swing
import javax.swing.table.DefaultTableModel; // Modelo de tabla para JTable
import java.awt.*; // Layouts y colores
import java.util.List; // Listas

/**
 * Panel que administra la vista de Cita-Tratamiento.
 * Permite listar, crear, editar y eliminar relaciones entre citas y tratamientos.
 */
public class CitaTratamientoPanel extends JPanel {

    private CitaTratamientoController controller; // Controlador de relaciones cita-tratamiento
    private JTable table; // Tabla que muestra las relaciones
    private DefaultTableModel model; // Modelo de datos
    private Color azul = new Color(33, 150, 243); // Color azul principal
    private Color blanco = Color.WHITE; // Color blanco

    /**
     * Constructor del panel de CitaTratamiento.
     * Inicializa la interfaz y carga los datos existentes.
     */
    public CitaTratamientoPanel() {
        controller = new CitaTratamientoController(); // Crea controlador
        setLayout(new BorderLayout()); // Usa BorderLayout
        setBackground(new Color(240, 245, 250)); // Fondo suave
        initComponents(); // Inicializa componentes visuales
        loadData(); // Carga datos desde BD
    }

    /**
     * Inicializa los componentes visuales del panel.
     */
    private void initComponents() {
        JLabel titulo = new JLabel("CITA-TRATAMIENTO", SwingConstants.CENTER); // Título centrado
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f)); // Fuente negrita
        titulo.setForeground(azul); // Texto azul
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Margen superior/inferior
        add(titulo, BorderLayout.NORTH); // Agrega título arriba

        String[] cols = {"ID", "Cita", "Tratamiento", "Cantidad"}; // Columnas de la tabla
        model = new DefaultTableModel(cols, 0) { // Modelo lecturizable
            public boolean isCellEditable(int r, int c) { return false; } // Evita edición directa
        };
        table = new JTable(model); // Crea tabla
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Selección única
        table.getTableHeader().setBackground(azul); // Encabezado azul
        table.getTableHeader().setForeground(blanco); // Texto blanco
        table.getTableHeader().setFont(table.getFont().deriveFont(Font.BOLD, 12f)); // Fuente encabezado
        table.setRowHeight(25); // Altura de fila
        add(new JScrollPane(table), BorderLayout.CENTER); // Agrega tabla con scroll

        JPanel pnl = new JPanel(); // Panel de botones
        pnl.setBackground(new Color(240, 245, 250)); // Fondo panel
        pnl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); // Margen panel
        JButton btnNew = new JButton("Nuevo"); // Botón nuevo
        JButton btnEdit = new JButton("Editar"); // Botón editar
        JButton btnDel = new JButton("Eliminar"); // Botón eliminar
        btnNew.setBackground(azul); btnNew.setForeground(blanco); btnNew.setFocusPainted(false); // Estilo botón nuevo
        btnEdit.setBackground(new Color(255, 152, 0)); btnEdit.setForeground(blanco); btnEdit.setFocusPainted(false); // Estilo botón editar
        btnDel.setBackground(new Color(244, 67, 54)); btnDel.setForeground(blanco); btnDel.setFocusPainted(false); // Estilo botón eliminar
        btnNew.addActionListener(e -> dialogo(null)); // Abre diálogo para nueva relación
        btnEdit.addActionListener(e -> editar()); // Edita relación seleccionada
        btnDel.addActionListener(e -> eliminar()); // Elimina relación seleccionada
        pnl.add(btnNew); // Añade botón Nuevo
        pnl.add(btnEdit); // Añade botón Editar
        pnl.add(btnDel); // Añade botón Eliminar
        add(pnl, BorderLayout.SOUTH); // Agrega panel de botones abajo
    }

    /**
     * Carga las relaciones de cita-tratamiento desde la base de datos.
     */
    public void loadData() {
        model.setRowCount(0); // Limpia filas previas
        for (CitaTratamiento ct : controller.findAll()) { // Recorre cada relación
            model.addRow(new Object[]{
                ct.getId(), // ID relación
                ct.getCita().getCodCita(), // ID cita asociada
                ct.getTratamiento().getNombreTratamiento(), // Nombre del tratamiento
                ct.getCantidad() // Cantidad aplicada
            });
        }
    }

    /**
     * Abre un diálogo para crear o editar una relación cita-tratamiento.
     */
    private void dialogo(CitaTratamiento ct) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                ct == null ? "Nueva Cita-Tratamiento" : "Editar Cita-Tratamiento", true); // Diálogo modal
        d.setSize(350, 220); // Tamaño del diálogo
        d.setLocationRelativeTo(this); // Centra el diálogo
        d.getContentPane().setBackground(new Color(240, 245, 250)); // Fondo diálogo

        JTextField txtCita = new JTextField(); // Campo cita
        JTextField txtTrat = new JTextField(); // Campo tratamiento
        JTextField txtCant = new JTextField(); // Campo cantidad

        if (ct != null) { // Si se edita una relación existente
            txtCita.setText(String.valueOf(ct.getCita().getCodCita())); // Carga ID cita
            txtTrat.setText(ct.getTratamiento().getNombreTratamiento()); // Carga tratamiento
            txtCant.setText(String.valueOf(ct.getCantidad())); // Carga cantidad
        }

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5)); // Formulario 3x2
        form.setBackground(new Color(240, 245, 250)); // Fondo formulario
        form.add(new JLabel("Cita ID:")); form.add(txtCita); // Etiqueta y campo cita
        form.add(new JLabel("Tratamiento:")); form.add(txtTrat); // Etiqueta y campo tratamiento
        form.add(new JLabel("Cantidad:")); form.add(txtCant); // Etiqueta y campo cantidad
        d.add(form, BorderLayout.CENTER); // Agrega formulario al centro

        JPanel pnlBtn = new JPanel(); // Panel de botones
        pnlBtn.setBackground(new Color(240, 245, 250)); // Fondo panel
        JButton btnOk = new JButton("Guardar"); // Botón guardar
        btnOk.setBackground(azul); btnOk.setForeground(blanco); btnOk.setFocusPainted(false); // Estilo botón guardar
        JButton btnCancel = new JButton("Cancelar"); // Botón cancelar
        btnCancel.setBackground(new Color(158, 158, 158)); btnCancel.setForeground(blanco); btnCancel.setFocusPainted(false); // Estilo botón cancelar
        btnOk.addActionListener(e -> {
            try {
                if (ct == null) { // Si se crea una relación nueva
                    CitaTratamiento n = new CitaTratamiento(); // Crea entidad nueva
                    n.setCantidad(Integer.parseInt(txtCant.getText())); // Asigna cantidad
                    controller.create(n); // Guarda la relación
                } else { // Si se edita una relación existente
                    ct.setCantidad(Integer.parseInt(txtCant.getText())); // Actualiza cantidad
                    controller.update(ct); // Guarda cambios
                }
                loadData(); // Recarga datos
                d.dispose(); // Cierra diálogo
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage()); // Muestra error
            }
        });
        btnCancel.addActionListener(e -> d.dispose()); // Cierra diálogo
        pnlBtn.add(btnOk); // Añade botón Guardar
        pnlBtn.add(btnCancel); // Añade botón Cancelar
        d.add(pnlBtn, BorderLayout.SOUTH); // Agrega botones abajo
        d.setVisible(true); // Muestra diálogo
    }

    /**
     * Abre el diálogo de edición para la relación seleccionada.
     */
    private void editar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona una relación"); return; } // Valida selección
        dialogo(controller.findById((Integer) model.getValueAt(row, 0))); // Abre diálogo con relación seleccionada
    }

    /**
     * Elimina la relación seleccionada después de pedir confirmación.
     */
    private void eliminar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona una relación"); return; } // Valida selección
        if (JOptionPane.showConfirmDialog(this, "Eliminar relación?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Confirma eliminación
            controller.delete((Integer) model.getValueAt(row, 0)); // Elimina relación
            loadData(); // Recarga datos
        }
    }
}
