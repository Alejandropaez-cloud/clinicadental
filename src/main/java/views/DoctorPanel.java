package views;

import controllers.controladores.DoctorController; // Controlador CRUD de doctores
import models.modelos.entidades.Doctor; // Entidad Doctor
import javax.swing.*; // Componentes Swing
import javax.swing.table.DefaultTableModel; // Modelo de tabla para JTable
import java.awt.*; // Layout y colores
import java.util.List; // Listas de datos

/**
 * Panel que administra la vista de Doctores.
 * Permite listar, crear, editar y eliminar doctores.
 */
public class DoctorPanel extends JPanel {

    private DoctorController controller; // Controlador para operaciones de doctores
    private JTable table; // Tabla que muestra los doctores
    private DefaultTableModel model; // Modelo de datos de la tabla
    private Color azul = new Color(33, 150, 243); // Color azul principal
    private Color blanco = Color.WHITE; // Color blanco para texto

    /**
     * Constructor del panel de Doctores.
     * Inicializa la interfaz y carga los datos disponibles.
     */
    public DoctorPanel() {
        controller = new DoctorController(); // Crea el controlador de doctores
        setLayout(new BorderLayout()); // Usa BorderLayout para organizar el panel
        setBackground(new Color(240, 245, 250)); // Fondo del panel
        initComponents(); // Configura los componentes visuales
        loadData(); // Carga los datos iniciales en la tabla
    }

    /**
     * Construye los componentes visuales del panel de doctores.
     */
    private void initComponents() {
        JLabel titulo = new JLabel("DOCTORES", SwingConstants.CENTER); // TÃƒÂ­tulo centrado
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f)); // Fuente negrita tamaÃƒÂ±o 18
        titulo.setForeground(azul); // Texto azul
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Margen interno
        add(titulo, BorderLayout.NORTH); // Agrega el tÃƒÂ­tulo arriba

        String[] cols = {"ID", "N. Colegiado", "Nombre", "Especialidad", "Telefono"}; // Columnas de la tabla
        model = new DefaultTableModel(cols, 0) { // Modelo de tabla no editable
            public boolean isCellEditable(int r, int c) { return false; } // Evita ediciÃƒÂ³n de celdas
        };
        table = new JTable(model); // Crea la tabla
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // SelecciÃƒÂ³n de una fila
        table.getTableHeader().setBackground(azul); // Encabezado azul
        table.getTableHeader().setForeground(blanco); // Texto blanco en encabezado
        table.getTableHeader().setFont(table.getFont().deriveFont(Font.BOLD, 12f)); // Fuente en encabezado
        table.setRowHeight(25); // Altura de fila
        add(new JScrollPane(table), BorderLayout.CENTER); // Agrega la tabla con scroll

        JPanel pnl = new JPanel(); // Panel para botones en la parte inferior
        pnl.setBackground(new Color(240, 245, 250)); // Fondo del panel de botones
        pnl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); // Margen interno
        JButton btnNew = new JButton("Nuevo"); // BotÃƒÂ³n para crear doctor
        JButton btnEdit = new JButton("Editar"); // BotÃƒÂ³n para editar doctor
        JButton btnDel = new JButton("Eliminar"); // BotÃƒÂ³n para eliminar doctor
        btnNew.setBackground(azul); // Fondo azul
        btnNew.setForeground(blanco); // Texto blanco
        btnNew.setFocusPainted(false); // Sin borde de enfoque
        btnEdit.setBackground(new Color(255, 152, 0)); // Fondo naranja
        btnEdit.setForeground(blanco); // Texto blanco
        btnEdit.setFocusPainted(false); // Sin borde de enfoque
        btnDel.setBackground(new Color(244, 67, 54)); // Fondo rojo
        btnDel.setForeground(blanco); // Texto blanco
        btnDel.setFocusPainted(false); // Sin borde de enfoque
        btnNew.addActionListener(e -> dialogo(null)); // Abre diÃƒÂ¡logo para nuevo doctor
        btnEdit.addActionListener(e -> editar()); // Abre diÃƒÂ¡logo para editar el doctor seleccionado
        btnDel.addActionListener(e -> eliminar()); // Elimina el doctor seleccionado
        pnl.add(btnNew); // AÃƒÂ±ade botÃƒÂ³n Nuevo
        pnl.add(btnEdit); // AÃƒÂ±ade botÃƒÂ³n Editar
        pnl.add(btnDel); // AÃƒÂ±ade botÃƒÂ³n Eliminar
        add(pnl, BorderLayout.SOUTH); // Agrega el panel de botones abajo
    }

    /**
     * Recupera los doctores desde la base de datos y actualiza la tabla.
     */
    public void loadData() {
        model.setRowCount(0); // Limpia la tabla
        for (Doctor d : controller.findAll()) { // Recorre los doctores recuperados
            model.addRow(new Object[]{
                d.getCodDoctor(), // ID del doctor
                d.getNumeroColegiado(), // NÃƒÂºmero de colegiado
                d.getNombre(), // Nombre del doctor
                d.getEspecialidad(), // Especialidad
                d.getTelefonoContacto() // TelÃƒÂ©fono de contacto
            });
        }
    }

    /**
     * Abre un diÃƒÂ¡logo para crear o editar un doctor.
     * Si se recibe un doctor existente, precarga sus datos para la ediciÃƒÂ³n.
     */
    private void dialogo(Doctor doctor) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                doctor == null ? "Nuevo Doctor" : "Editar Doctor", true); // DiÃƒÂ¡logo modal
        d.setSize(350, 250); // TamaÃƒÂ±o del diÃƒÂ¡logo
        d.setLocationRelativeTo(this); // Centra el diÃƒÂ¡logo
        d.getContentPane().setBackground(new Color(240, 245, 250)); // Fondo del diÃƒÂ¡logo

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5)); // Formulario 4x2
        form.setBackground(new Color(240, 245, 250)); // Fondo del formulario
        JTextField txtCol = new JTextField(); // Campo nÃƒÂºmero de colegiado
        JTextField txtNom = new JTextField(); // Campo nombre
        JComboBox<String> cmbEsp = new JComboBox<>(new String[]{ // Combo de especialidades
            "Odontologia General", "Ortodoncia", "Endodoncia",
            "Periodoncia", "Cirugia Oral", "Implantologia", "Estetica Dental"
        });
        JTextField txtTel = new JTextField(); // Campo telÃƒÂ©fono

        if (doctor != null) { // Si se edita
            txtCol.setText(doctor.getNumeroColegiado()); // Carga numero de colegiado
            txtNom.setText(doctor.getNombre()); // Carga nombre
            cmbEsp.setSelectedItem(doctor.getEspecialidad()); // Selecciona especialidad
            txtTel.setText(doctor.getTelefonoContacto()); // Carga telÃƒÂ©fono
        }

        form.add(new JLabel("N. Colegiado:")); form.add(txtCol); // Etiqueta y campo colegiado
        form.add(new JLabel("Nombre:")); form.add(txtNom); // Etiqueta y campo nombre
        form.add(new JLabel("Especialidad:")); form.add(cmbEsp); // Etiqueta y combo especialidad
        form.add(new JLabel("Telefono:")); form.add(txtTel); // Etiqueta y campo telÃƒÂ©fono

        d.add(form, BorderLayout.CENTER); // Agrega el formulario al centro del diÃƒÂ¡logo

        JPanel pnlBtn = new JPanel(); // Panel de botones
        pnlBtn.setBackground(new Color(240, 245, 250)); // Fondo de panel de botones
        JButton btnOk = new JButton("Guardar"); // BotÃƒÂ³n guardar
        btnOk.setBackground(azul); // Fondo azul
        btnOk.setForeground(blanco); // Texto blanco
        btnOk.setFocusPainted(false); // Sin borde de enfoque
        JButton btnCancel = new JButton("Cancelar"); // BotÃƒÂ³n cancelar
        btnCancel.setBackground(new Color(158, 158, 158)); // Fondo gris
        btnCancel.setForeground(blanco); // Texto blanco
        btnCancel.setFocusPainted(false); // Sin borde de enfoque
        btnOk.addActionListener(e -> {
            try {
                if (doctor == null) { // Nuevo doctor
                    Doctor n = new Doctor(txtCol.getText(), txtNom.getText(),
                            (String) cmbEsp.getSelectedItem()); // Crea entidad doctor
                    n.setTelefonoContacto(txtTel.getText()); // Asigna telÃƒÂ©fono
                    controller.create(n); // Inserta en BD
                } else { // Editar doctor existente
                    doctor.setNumeroColegiado(txtCol.getText()); // Actualiza nÃƒÂºmero de colegiado
                    doctor.setNombre(txtNom.getText()); // Actualiza nombre
                    doctor.setEspecialidad((String) cmbEsp.getSelectedItem()); // Actualiza especialidad
                    doctor.setTelefonoContacto(txtTel.getText()); // Actualiza telÃƒÂ©fono
                    controller.update(doctor); // Guarda cambios
                }
                loadData(); // Recarga la tabla
                d.dispose(); // Cierra el diÃƒÂ¡logo
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage()); // Muestra error
            }
        });
        btnCancel.addActionListener(e -> d.dispose()); // Cierra el diÃƒÂ¡logo
        pnlBtn.add(btnOk); // AÃƒÂ±ade botÃƒÂ³n Guardar
        pnlBtn.add(btnCancel); // AÃƒÂ±ade botÃƒÂ³n Cancelar
        d.add(pnlBtn, BorderLayout.SOUTH); // Agrega botones al pie del diÃƒÂ¡logo
        d.setVisible(true); // Hace visible el diÃƒÂ¡logo
    }

    /**
     * Abre el diÃƒÂ¡logo de ediciÃƒÂ³n para el doctor seleccionado.
     */
    private void editar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un doctor"); return; } // Valida selecciÃƒÂ³n
        dialogo(controller.findById((Integer) model.getValueAt(row, 0))); // Carga doctor por ID y abre diÃƒÂ¡logo
    }

    /**
     * Elimina el doctor seleccionado despuÃƒÂ©s de pedir confirmaciÃƒÂ³n.
     */
    private void eliminar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un doctor"); return; } // Valida selecciÃƒÂ³n
        if (JOptionPane.showConfirmDialog(this, "Eliminar doctor?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Solicita confirmaciÃƒÂ³n
            controller.delete((Integer) model.getValueAt(row, 0)); // Elimina doctor
            loadData(); // Recarga la tabla
        }
    }
}
