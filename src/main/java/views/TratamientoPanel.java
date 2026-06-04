package views;

import controllers.controladores.TratamientoController; // Controlador CRUD de tratamientos
import models.modelos.entidades.Tratamiento; // Entidad Tratamiento
import javax.swing.*; // Componentes Swing
import javax.swing.table.DefaultTableModel; // Modelo de tabla para JTable
import java.awt.*; // Layout y colores
import java.util.List; // Listas de entidades

/**
 * Panel que administra la vista de Tratamientos.
 * Permite listar, crear, editar y eliminar tratamientos dentales.
 */
public class TratamientoPanel extends JPanel {

    private TratamientoController controller; // Controlador para operaciones de tratamiento
    private JTable table; // Tabla que muestra los tratamientos
    private DefaultTableModel model; // Modelo de datos de la tabla
    private Color azul = new Color(33, 150, 243); // Color azul de la UI
    private Color blanco = Color.WHITE; // Color blanco para texto

    /**
     * Constructor del panel de Tratamientos.
     * Configura la interfaz y carga los registros existentes.
     */
    public TratamientoPanel() {
        controller = new TratamientoController(); // Crea el controlador de tratamientos
        setLayout(new BorderLayout()); // Usa BorderLayout
        setBackground(new Color(240, 245, 250)); // Fondo suave
        initComponents(); // Inicializa componentes visuales
        loadData(); // Carga datos iniciales
    }

    /**
     * Construye los componentes visuales del panel de tratamientos.
     */
    private void initComponents() {
        JLabel titulo = new JLabel("TRATAMIENTOS", SwingConstants.CENTER); // Tí­tulo del panel
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f)); // Fuente en negrita
        titulo.setForeground(azul); // Texto azul
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Margen superior/inferior
        add(titulo, BorderLayout.NORTH); // Agrega el tí­tulo arriba

        String[] cols = {"ID", "Nombre", "Descripción", "Precio", "Duración (min)"}; // Columnas de la tabla
        model = new DefaultTableModel(cols, 0) { // Modelo de tabla no editable
            public boolean isCellEditable(int r, int c) { return false; } // Evita cambios directos
        };
        table = new JTable(model); // Crea la tabla
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite seleccionar una fila
        table.getTableHeader().setBackground(azul); // Fondo azul en encabezado
        table.getTableHeader().setForeground(blanco); // Texto blanco en encabezado
        table.getTableHeader().setFont(table.getFont().deriveFont(Font.BOLD, 12f)); // Fuente negrita en encabezado
        table.setRowHeight(25); // Altura de fila
        add(new JScrollPane(table), BorderLayout.CENTER); // Agrega la tabla con scroll al centro

        JPanel pnl = new JPanel(); // Panel inferior de botones
        pnl.setBackground(new Color(240, 245, 250)); // Fondo del panel
        pnl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); // Margen interno
        JButton btnNew = new JButton("Nuevo"); // Botón nuevo
        JButton btnEdit = new JButton("Editar"); // Botón editar
        JButton btnDel = new JButton("Eliminar"); // Botón eliminar
        btnNew.setBackground(azul); // Fondo azul
        btnNew.setForeground(blanco); // Texto blanco
        btnNew.setFocusPainted(false); // Sin borde de enfoque
        btnEdit.setBackground(new Color(255, 152, 0)); // Fondo naranja
        btnEdit.setForeground(blanco); // Texto blanco
        btnEdit.setFocusPainted(false); // Sin borde de enfoque
        btnDel.setBackground(new Color(244, 67, 54)); // Fondo rojo
        btnDel.setForeground(blanco); // Texto blanco
        btnDel.setFocusPainted(false); // Sin borde de enfoque
        btnNew.addActionListener(e -> dialogo(null)); // Abre dialogo para nuevo tratamiento
        btnEdit.addActionListener(e -> editar()); // Abre dialogo para editar tratamiento seleccionado
        btnDel.addActionListener(e -> eliminar()); // Elimina tratamiento seleccionado
        pnl.add(btnNew); // Añade botón Nuevo
        pnl.add(btnEdit); // Añade botón Editar
        pnl.add(btnDel); // Añade botón Eliminar
        add(pnl, BorderLayout.SOUTH); // Agrega panel de botones abajo
    }

    /**
     * Carga los tratamientos desde la base de datos y actualiza la tabla.
     */
    public void loadData() {
        model.setRowCount(0); // Limpia filas de la tabla
        for (Tratamiento t : controller.findAll()) { // Recorre tratamientos recuperados
            model.addRow(new Object[]{
                t.getCodTratamiento(), // ID del tratamiento
                t.getNombreTratamiento(), // Nombre del tratamiento
                t.getDescripcion(), // Descripción del tratamiento
                t.getPrecioEstimado(), // Precio estimado
                t.getDuracionMinutos() // Duración en minutos
            });
        }
    }

    /**
     * Abre un dialogo para crear o editar un tratamiento.
     * Si se edita, precarga los valores actuales del tratamiento.
     */
    private void dialogo(Tratamiento tratamiento) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                tratamiento == null ? "Nuevo Tratamiento" : "Editar Tratamiento", true); // Dialogo modal
        d.setSize(350, 250); // Tamaño del dialogo
        d.setLocationRelativeTo(this); // Centra el dialogo
        d.getContentPane().setBackground(new Color(240, 245, 250)); // Fondo del dialogo

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5)); // Formulario 4x2
        form.setBackground(new Color(240, 245, 250)); // Fondo del formulario
        JTextField txtNom = new JTextField(); // Campo nombre
        JTextField txtDesc = new JTextField(); // Campo descripción
        JTextField txtPre = new JTextField(); // Campo precio
        JTextField txtDur = new JTextField(); // Campo duración

        if (tratamiento != null) { // Si se edita un tratamiento existente
            txtNom.setText(tratamiento.getNombreTratamiento()); // Carga nombre
            txtDesc.setText(tratamiento.getDescripcion()); // Carga descripción
            txtPre.setText(String.valueOf(tratamiento.getPrecioEstimado())); // Carga precio
            txtDur.setText(String.valueOf(tratamiento.getDuracionMinutos())); // Carga duración
        }

        form.add(new JLabel("Nombre:")); form.add(txtNom); // Etiqueta y campo nombre
        form.add(new JLabel("Descripcion:")); form.add(txtDesc); // Etiqueta y campo descripción
        form.add(new JLabel("Precio (Ã¢â€šÂ¬):")); form.add(txtPre); // Etiqueta y campo precio
        form.add(new JLabel("Duracion (min):")); form.add(txtDur); // Etiqueta y campo duración
        d.add(form, BorderLayout.CENTER); // Agrega el formulario al centro

        JPanel pnlBtn = new JPanel(); // Panel de botones
        pnlBtn.setBackground(new Color(240, 245, 250)); // Fondo del panel
        JButton btnOk = new JButton("Guardar"); // Botón guardar
        btnOk.setBackground(azul); // Fondo azul
        btnOk.setForeground(blanco); // Texto blanco
        btnOk.setFocusPainted(false); // Sin borde de enfoque
        JButton btnCancel = new JButton("Cancelar"); // Botón cancelar
        btnCancel.setBackground(new Color(158, 158, 158)); // Fondo gris
        btnCancel.setForeground(blanco); // Texto blanco
        btnCancel.setFocusPainted(false); // Sin borde de enfoque
        btnOk.addActionListener(e -> {
            try {
                if (tratamiento == null) { // Si se crea un nuevo tratamiento
                    controller.create(new Tratamiento(txtNom.getText(), txtDesc.getText(),
                            Double.parseDouble(txtPre.getText()), Integer.parseInt(txtDur.getText()))); // Crea y guarda nuevo tratamiento
                } else { // Si se edita un tratamiento existente
                    tratamiento.setNombreTratamiento(txtNom.getText()); // Actualiza nombre
                    tratamiento.setDescripcion(txtDesc.getText()); // Actualiza descripción
                    tratamiento.setPrecioEstimado(Double.parseDouble(txtPre.getText())); // Actualiza precio
                    tratamiento.setDuracionMinutos(Integer.parseInt(txtDur.getText())); // Actualiza duración
                    controller.update(tratamiento); // Guarda cambios
                }
                loadData(); // Recarga la tabla
                d.dispose(); // Cierra el dialogo
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage()); // Muestra mensaje de error
            }
        });
        btnCancel.addActionListener(e -> d.dispose()); // Cierra el dialogo sin guardar
        pnlBtn.add(btnOk); // Añade botón Guardar
        pnlBtn.add(btnCancel); // Añade botón Cancelar
        d.add(pnlBtn, BorderLayout.SOUTH); // Agrega el panel de botones al fondo
        d.setVisible(true); // Muestra el dialogo
    }

    /**
     * Abre el dialogo de edición para el tratamiento seleccionado.
     */
    private void editar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un tratamiento"); return; } // Valida selección
        dialogo(controller.findById((Integer) model.getValueAt(row, 0))); // Carga tratamiento seleccionado y abre dialogo
    }

    /**
     * Elimina el tratamiento seleccionado despuÃƒÂ©s de pedir confirmación.
     */
    private void eliminar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un tratamiento"); return; } // Valida selección
        if (JOptionPane.showConfirmDialog(this, "Eliminar tratamiento?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Confirma eliminación
            controller.delete((Integer) model.getValueAt(row, 0)); // Elimina tratamiento
            loadData(); // Recarga la tabla
        }
    }
}
