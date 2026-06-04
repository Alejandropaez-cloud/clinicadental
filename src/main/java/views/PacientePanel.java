package views;

import controllers.controladores.PacienteController; // Controlador de persistencia de pacientes
import models.modelos.entidades.Paciente; // Entidad Paciente
import javax.swing.*; // Componentes Swing
import javax.swing.table.DefaultTableModel; // Modelo de datos para JTable
import java.awt.*; // Layouts y colores
import java.text.SimpleDateFormat; // Formato para mostrar fechas
import java.util.List; // Colecciones de datos

/**
 * Panel que administra la vista de Pacientes.
 * Permite listar, crear, editar y eliminar pacientes.
 */
public class PacientePanel extends JPanel {

    private PacienteController controller; // Controlador para las operaciones CRUD de pacientes
    private JTable table; // Tabla que muestra la lista de pacientes
    private DefaultTableModel model; // Modelo de tabla que contiene los datos
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Formato de fecha para mostrar fecha de nacimiento
    private Color azul = new Color(33, 150, 243); // Color azul para encabezados y botones
    private Color blanco = Color.WHITE; // Color blanco para texto

    /**
     * Constructor del panel de Pacientes.
     * Configura el layout, inicializa componentes y carga datos desde la BD.
     */
    public PacientePanel() {
        controller = new PacienteController(); // Crea el controlador de pacientes
        setLayout(new BorderLayout()); // Usa BorderLayout para organizar el panel
        setBackground(new Color(240, 245, 250)); // Establece color de fondo suave
        initComponents(); // Inicializa los componentes visuales
        loadData(); // Carga los datos iniciales en la tabla
    }

    /**
     * Inicializa los componentes visuales del panel.
     * Construye la tabla de pacientes y los botones de acción.
     */
    private void initComponents() {
        JLabel titulo = new JLabel("PACIENTES", SwingConstants.CENTER); // Ti­tulo del panel centrado
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f)); // Fuente en negrita y tamaño 18
        titulo.setForeground(azul); // Texto azul
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Margen superior e inferior
        add(titulo, BorderLayout.NORTH); // Agrega el título en la parte superior

        String[] cols = {"ID", "DNI", "Nombre", "Apellidos", "Fecha Nac.", "Telefono", "Email", "Direccion"}; // Columnas de la tabla
        model = new DefaultTableModel(cols, 0) { // Modelo no editable
            public boolean isCellEditable(int r, int c) { return false; } // Impide edición directa en la tabla
        };
        table = new JTable(model); // Crea la tabla con el modelo
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite seleccionar una sola fila
        table.getTableHeader().setBackground(azul); // Fondo azul en encabezados
        table.getTableHeader().setForeground(blanco); // Texto blanco en encabezados
        table.getTableHeader().setFont(table.getFont().deriveFont(Font.BOLD, 12f)); // Fuente negrita en encabezados
        table.setRowHeight(25); // Altura de fila de la tabla
        add(new JScrollPane(table), BorderLayout.CENTER); // Agrega la tabla dentro de un JScrollPane

        JPanel pnl = new JPanel(); // Panel de botones en la parte inferior
        pnl.setBackground(new Color(240, 245, 250)); // Fondo del panel de botones
        pnl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); // Margen interno
        JButton btnNew = new JButton("Nuevo"); // Botón para crear paciente
        JButton btnEdit = new JButton("Editar"); // Botón para editar paciente
        JButton btnDel = new JButton("Eliminar"); // Botón para eliminar paciente
        btnNew.setBackground(azul); // Color de fondo azul para el botón Nuevo
        btnNew.setForeground(blanco); // Texto blanco
        btnNew.setFocusPainted(false); // Sin borde de enfoque
        btnEdit.setBackground(new Color(255, 152, 0)); // Color naranja para editar
        btnEdit.setForeground(blanco); // Texto blanco
        btnEdit.setFocusPainted(false); // Sin borde de enfoque
        btnDel.setBackground(new Color(244, 67, 54)); // Color rojo para eliminar
        btnDel.setForeground(blanco); // Texto blanco
        btnDel.setFocusPainted(false); // Sin borde de enfoque
        btnNew.addActionListener(e -> dialogo(null)); // Abre el dialogo para crear un paciente
        btnEdit.addActionListener(e -> editar()); // Abre el dialogo para editar el paciente seleccionado
        btnDel.addActionListener(e -> eliminar()); // Elimina el paciente seleccionado
        pnl.add(btnNew); // Añade el botón Nuevo al panel
        pnl.add(btnEdit); // Añade el botón Editar al panel
        pnl.add(btnDel); // Añade el botón Eliminar al panel
        add(pnl, BorderLayout.SOUTH); // Agrega el panel de botones en la parte inferior
    }

    /**
     * Carga los pacientes desde la base de datos y los muestra en la tabla.
     * Se vacía el modelo previamente para evitar duplicados.
     */
    public void loadData() {
        model.setRowCount(0); // Elimina todas las filas actuales
        for (Paciente p : controller.findAll()) { // Recorre cada paciente devuelto por el controlador
            model.addRow(new Object[]{
                p.getCodPaciente(), // ID del paciente
                p.getDni(), // DNI del paciente
                p.getNombre(), // Nombre del paciente
                p.getApellidos(), // Apellidos del paciente
                p.getFechaNacimiento() != null ? sdf.format(p.getFechaNacimiento()) : "", // Fecha de nacimiento formateada
                p.getTelefono(), // Telefono del paciente
                p.getEmail(), // Email del paciente
                p.getDireccion() // Dirección del paciente
            });
        }
    }

    /**
     * Abre un dialogo que permite crear o editar un paciente.
     * Si se recibe un paciente, carga sus datos en el formulario para edición.
     */
    private void dialogo(Paciente paciente) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                paciente == null ? "Nuevo Paciente" : "Editar Paciente", true); // Crea dialogo modal
        d.setSize(350, 300); // Tamaño del dialogo
        d.setLocationRelativeTo(this); // Centra el dialogo respecto al panel
        d.getContentPane().setBackground(new Color(240, 245, 250)); // Establece color de fondo del dialogo
        d.setLayout(new BorderLayout()); // Usa BorderLayout para el dialogo

        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5)); // Crea el formulario con 7 filas y 2 columnas
        form.setBackground(new Color(240, 245, 250)); // Fondo del formulario
        JTextField txtDni = new JTextField(); // Campo DNI
        JTextField txtNom = new JTextField(); // Campo Nombre
        JTextField txtApe = new JTextField(); // Campo Apellidos
        JTextField txtFecha = new JTextField(); // Campo Fecha Nacimiento
        JTextField txtTel = new JTextField(); // Campo Telefono
        JTextField txtEmail = new JTextField(); // Campo Email
        JTextField txtDir = new JTextField(); // Campo Dirección

        if (paciente != null) { // Si se esta¡ editando un paciente existente
            txtDni.setText(paciente.getDni()); // Carga DNI
            txtNom.setText(paciente.getNombre()); // Carga nombre
            txtApe.setText(paciente.getApellidos()); // Carga apellidos
            txtFecha.setText(paciente.getFechaNacimiento() != null ? sdf.format(paciente.getFechaNacimiento()) : ""); // Carga fecha de nacimiento
            txtTel.setText(paciente.getTelefono()); // Carga telefono
            txtEmail.setText(paciente.getEmail()); // Carga email
            txtDir.setText(paciente.getDireccion()); // Carga dirección
        }

        form.add(new JLabel("DNI:")); form.add(txtDni); // Etiqueta y campo DNI
        form.add(new JLabel("Nombre:")); form.add(txtNom); // Etiqueta y campo Nombre
        form.add(new JLabel("Apellidos:")); form.add(txtApe); // Etiqueta y campo Apellidos
        form.add(new JLabel("Fecha Nac (dd/MM/yyyy):")); form.add(txtFecha); // Etiqueta y campo Fecha Nac.
        form.add(new JLabel("Telefono:")); form.add(txtTel); // Etiqueta y campo Telefono
        form.add(new JLabel("Email:")); form.add(txtEmail); // Etiqueta y campo Email
        form.add(new JLabel("Direccion:")); form.add(txtDir); // Etiqueta y campo Dirección

        d.add(form, BorderLayout.CENTER); // Agrega el formulario al centro del dialogo

        JPanel pnlBtn = new JPanel(); // Panel de botones
        pnlBtn.setBackground(new Color(240, 245, 250)); // Fondo del panel de botones
        JButton btnOk = new JButton("Guardar"); // Botón Guardar
        btnOk.setBackground(azul); // Fondo azul
        btnOk.setForeground(blanco); // Texto blanco
        btnOk.setFocusPainted(false); // Sin borde de enfoque
        JButton btnCancel = new JButton("Cancelar"); // Botón Cancelar
        btnCancel.setBackground(new Color(158, 158, 158)); // Fondo gris
        btnCancel.setForeground(blanco); // Texto blanco
        btnCancel.setFocusPainted(false); // Sin borde de enfoque
        btnOk.addActionListener(e -> {
            try {
                if (paciente == null) { // Si se esta¡ creando un paciente nuevo
                    Paciente n = new Paciente(txtDni.getText(), txtNom.getText(), txtApe.getText(),
                            txtFecha.getText().isEmpty() ? null : sdf.parse(txtFecha.getText())); // Crea nuevo paciente
                    n.setTelefono(txtTel.getText()); // Asigna telefono
                    n.setEmail(txtEmail.getText()); // Asigna email
                    n.setDireccion(txtDir.getText()); // Asigna dirección
                    controller.create(n); // Inserta paciente en la base de datos
                } else { // Si se está editando un paciente existente
                    paciente.setDni(txtDni.getText()); // Actualiza DNI
                    paciente.setNombre(txtNom.getText()); // Actualiza nombre
                    paciente.setApellidos(txtApe.getText()); // Actualiza apellidos
                    paciente.setFechaNacimiento(txtFecha.getText().isEmpty() ? null : sdf.parse(txtFecha.getText())); // Actualiza fecha de nacimiento
                    paciente.setTelefono(txtTel.getText()); // Actualiza telefono
                    paciente.setEmail(txtEmail.getText()); // Actualiza email
                    paciente.setDireccion(txtDir.getText()); // Actualiza dirección
                    controller.update(paciente); // Actualiza paciente en la base de datos
                }
                loadData(); // Recarga los datos en la tabla
                d.dispose(); // Cierra el dialogo
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage()); // Muestra error en ventana de dialogo
            }
        });
        btnCancel.addActionListener(e -> d.dispose()); // Cierra el dialogo sin guardar cambios
        pnlBtn.add(btnOk); // Añade botón Guardar
        pnlBtn.add(btnCancel); // Añade botón Cancelar
        d.add(pnlBtn, BorderLayout.SOUTH); // Agrega panel de botones al pie del dialogo
        d.setVisible(true); // Muestra el dialogo al usuario
    }

    /**
     * Abre el dialogo de edición para el paciente seleccionado.
     */
    private void editar() {
        int row = table.getSelectedRow(); // Obtiene la fila seleccionada en la tabla
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un paciente"); return; } // Muestra alerta si no hay selección
        dialogo(controller.findById((Integer) model.getValueAt(row, 0))); // Obtiene el paciente por ID y abre dialogo de edición
    }

    /**
     * Elimina el paciente seleccionado después de pedir confirmación.
     */
    private void eliminar() {
        int row = table.getSelectedRow(); // Obtiene la fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un paciente"); return; } // Muestra alerta si no hay selección
        if (JOptionPane.showConfirmDialog(this, "Eliminar paciente?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Confirma eliminación
            controller.delete((Integer) model.getValueAt(row, 0)); // Elimina paciente seleccionado
            loadData(); // Recarga la tabla
        }
    }
}
