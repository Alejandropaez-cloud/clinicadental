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
        JLabel titulo = new JLabel("CITA-TRATAMIENTO", SwingConstants.CENTER); // TÃƒÂ­tulo centrado
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f)); // Fuente negrita
        titulo.setForeground(azul); // Texto azul
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Margen superior/inferior
        add(titulo, BorderLayout.NORTH); // Agrega tÃƒÂ­tulo arriba

        String[] cols = {"ID", "Cita", "Tratamiento", "Cantidad"}; // Columnas de la tabla
        model = new DefaultTableModel(cols, 0) { // Modelo lecturizable
            public boolean isCellEditable(int r, int c) { return false; } // Evita ediciÃƒÂ³n directa
        };
        table = new JTable(model); // Crea tabla
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // SelecciÃƒÂ³n ÃƒÂºnica
        table.getTableHeader().setBackground(azul); // Encabezado azul
        table.getTableHeader().setForeground(blanco); // Texto blanco
        table.getTableHeader().setFont(table.getFont().deriveFont(Font.BOLD, 12f)); // Fuente encabezado
        table.setRowHeight(25); // Altura de fila
        add(new JScrollPane(table), BorderLayout.CENTER); // Agrega tabla con scroll

        JPanel pnl = new JPanel(); // Panel de botones
        pnl.setBackground(new Color(240, 245, 250)); // Fondo panel
        pnl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); // Margen panel
        JButton btnNew = new JButton("Nuevo"); // BotÃƒÂ³n nuevo
        JButton btnEdit = new JButton("Editar"); // BotÃƒÂ³n editar
        JButton btnDel = new JButton("Eliminar"); // BotÃƒÂ³n eliminar
        btnNew.setBackground(azul); btnNew.setForeground(blanco); btnNew.setFocusPainted(false); // Estilo botÃƒÂ³n nuevo
        btnEdit.setBackground(new Color(255, 152, 0)); btnEdit.setForeground(blanco); btnEdit.setFocusPainted(false); // Estilo botÃƒÂ³n editar
        btnDel.setBackground(new Color(244, 67, 54)); btnDel.setForeground(blanco); btnDel.setFocusPainted(false); // Estilo botÃƒÂ³n eliminar
        btnNew.addActionListener(e -> dialogo(null)); // Abre diÃƒÂ¡logo para nueva relaciÃƒÂ³n
        btnEdit.addActionListener(e -> editar()); // Edita relaciÃƒÂ³n seleccionada
        btnDel.addActionListener(e -> eliminar()); // Elimina relaciÃƒÂ³n seleccionada
        pnl.add(btnNew); // AÃƒÂ±ade botÃƒÂ³n Nuevo
        pnl.add(btnEdit); // AÃƒÂ±ade botÃƒÂ³n Editar
        pnl.add(btnDel); // AÃƒÂ±ade botÃƒÂ³n Eliminar
        add(pnl, BorderLayout.SOUTH); // Agrega panel de botones abajo
    }

    /**
     * Carga las relaciones de cita-tratamiento desde la base de datos.
     */
    public void loadData() {
        model.setRowCount(0); // Limpia filas previas
        for (CitaTratamiento ct : controller.findAll()) { // Recorre cada relaciÃƒÂ³n
            model.addRow(new Object[]{
                ct.getId(), // ID relación
                ct.getCita().getCodCita(), // ID cita asociada
                ct.getTratamiento().getNombreTratamiento(), // Nombre del tratamiento
                ct.getCantidad() // Cantidad aplicada
            });
        }
    }

    /**
     * Abre un diÃƒÂ¡logo para crear o editar una relaciÃƒÂ³n cita-tratamiento.
     */
    private void dialogo(CitaTratamiento ct) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                ct == null ? "Nueva Cita-Tratamiento" : "Editar Cita-Tratamiento", true); // DiÃƒÂ¡logo modal
        d.setSize(350, 220); // TamaÃƒÂ±o del diÃƒÂ¡logo
        d.setLocationRelativeTo(this); // Centra el diÃƒÂ¡logo
        d.getContentPane().setBackground(new Color(240, 245, 250)); // Fondo diÃƒÂ¡logo

        JTextField txtCita = new JTextField(); // Campo cita
        JTextField txtTrat = new JTextField(); // Campo tratamiento
        JTextField txtCant = new JTextField(); // Campo cantidad

        if (ct != null) { // Si se edita una relaciÃƒÂ³n existente
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
        JButton btnOk = new JButton("Guardar"); // BotÃƒÂ³n guardar
        btnOk.setBackground(azul); btnOk.setForeground(blanco); btnOk.setFocusPainted(false); // Estilo botÃƒÂ³n guardar
        JButton btnCancel = new JButton("Cancelar"); // BotÃƒÂ³n cancelar
        btnCancel.setBackground(new Color(158, 158, 158)); btnCancel.setForeground(blanco); btnCancel.setFocusPainted(false); // Estilo botÃƒÂ³n cancelar
        btnOk.addActionListener(e -> {
            try {
                if (ct == null) { // Si se crea una relaciÃƒÂ³n nueva
                    CitaTratamiento n = new CitaTratamiento(); // Crea entidad nueva
                    n.setCantidad(Integer.parseInt(txtCant.getText())); // Asigna cantidad
                    controller.create(n); // Guarda la relaciÃƒÂ³n
                } else { // Si se edita una relaciÃƒÂ³n existente
                    ct.setCantidad(Integer.parseInt(txtCant.getText())); // Actualiza cantidad
                    controller.update(ct); // Guarda cambios
                }
                loadData(); // Recarga datos
                d.dispose(); // Cierra diÃƒÂ¡logo
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage()); // Muestra error
            }
        });
        btnCancel.addActionListener(e -> d.dispose()); // Cierra diÃƒÂ¡logo
        pnlBtn.add(btnOk); // AÃƒÂ±ade botÃƒÂ³n Guardar
        pnlBtn.add(btnCancel); // AÃƒÂ±ade botÃƒÂ³n Cancelar
        d.add(pnlBtn, BorderLayout.SOUTH); // Agrega botones abajo
        d.setVisible(true); // Muestra diÃƒÂ¡logo
    }

    /**
     * Abre el diÃƒÂ¡logo de ediciÃƒÂ³n para la relaciÃƒÂ³n seleccionada.
     */
    private void editar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona una relaciÃƒÂ³n"); return; } // Valida selecciÃƒÂ³n
        dialogo(controller.findById((Integer) model.getValueAt(row, 0))); // Abre diÃƒÂ¡logo con relaciÃƒÂ³n seleccionada
    }

    /**
     * Elimina la relaciÃƒÂ³n seleccionada despuÃƒÂ©s de pedir confirmaciÃƒÂ³n.
     */
    private void eliminar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona una relaciÃƒÂ³n"); return; } // Valida selecciÃƒÂ³n
        if (JOptionPane.showConfirmDialog(this, "Eliminar relaciÃƒÂ³n?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Confirma eliminaciÃƒÂ³n
            controller.delete((Integer) model.getValueAt(row, 0)); // Elimina relaciÃƒÂ³n
            loadData(); // Recarga datos
        }
    }
}
