package views;

import controllers.controladores.CitaController; // Controlador CRUD de citas
import controllers.controladores.PacienteController; // Controlador CRUD de pacientes
import controllers.controladores.DoctorController; // Controlador CRUD de doctores
import models.modelos.entidades.Cita; // Entidad Cita
import models.modelos.entidades.Paciente; // Entidad Paciente
import models.modelos.entidades.Doctor; // Entidad Doctor
import javax.swing.*; // Componentes Swing
import javax.swing.table.DefaultTableModel; // Modelo de tabla para JTable
import java.awt.*; // Layout y colores
import java.text.SimpleDateFormat; // Formato de fecha y hora
import java.util.List; // Colecciones de objetos

/**
 * Panel que administra la vista de Citas.
 * Permite gestionar citas programadas y su información asociada.
 */
public class CitaPanel extends JPanel {

    private CitaController citaController; // Controlador de citas
    private PacienteController pacienteController; // Controlador de pacientes
    private DoctorController doctorController; // Controlador de doctores
    private JTable table; // Tabla que muestra las citas
    private DefaultTableModel model; // Modelo de datos de la tabla
    private SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy"); // Formato de fecha para citas
    private SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm"); // Formato de hora para citas
    private Color azul = new Color(33, 150, 243); // Color azul para encabezados y botones
    private Color blanco = Color.WHITE; // Color blanco para texto

    /**
     * Constructor del panel de Citas.
     * Inicializa controladores y configura la vista.
     */
    public CitaPanel() {
        citaController = new CitaController(); // Crea el controlador de citas
        pacienteController = new PacienteController(); // Crea el controlador de pacientes
        doctorController = new DoctorController(); // Crea el controlador de doctores
        setLayout(new BorderLayout()); // Usa BorderLayout
        setBackground(new Color(240, 245, 250)); // Fondo suave
        initComponents(); // Inicializa los componentes visuales
        loadData(); // Carga las citas desde la base de datos
    }

    /**
     * Inicializa los controles visuales del panel de citas.
     */
    private void initComponents() {
        JLabel titulo = new JLabel("CITAS", SwingConstants.CENTER); // Título centrado
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f)); // Fuente en negrita
        titulo.setForeground(azul); // Color azul del texto
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Margen superior e inferior
        add(titulo, BorderLayout.NORTH); // Agrega el título en la parte superior

        String[] cols = {"ID", "Paciente", "Doctor", "Fecha", "Hora Inicio", "Hora Fin", "Estado"}; // Columnas de la tabla
        model = new DefaultTableModel(cols, 0) { // Modelo de tabla sin celdas editables
            public boolean isCellEditable(int r, int c) { return false; } // Evita edición en la tabla
        };
        table = new JTable(model); // Crea la tabla
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Selección única
        table.getTableHeader().setBackground(azul); // Encabezados azules
        table.getTableHeader().setForeground(blanco); // Texto blanco en encabezado
        table.getTableHeader().setFont(table.getFont().deriveFont(Font.BOLD, 12f)); // Fuente en encabezado
        table.setRowHeight(25); // Altura de fila
        add(new JScrollPane(table), BorderLayout.CENTER); // Agrega la tabla con scroll al centro

        JPanel pnl = new JPanel(); // Panel de botones en la parte inferior
        pnl.setBackground(new Color(240, 245, 250)); // Fondo del panel de botones
        pnl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); // Margen interno
        JButton btnNew = new JButton("Nuevo"); // Botón para nueva cita
        JButton btnEdit = new JButton("Editar"); // Botón para editar cita seleccionada
        JButton btnDel = new JButton("Eliminar"); // Botón para eliminar cita seleccionada
        btnNew.setBackground(azul); // Fondo azul
        btnNew.setForeground(blanco); // Texto blanco
        btnNew.setFocusPainted(false); // Sin borde de enfoque
        btnEdit.setBackground(new Color(255, 152, 0)); // Fondo naranja
        btnEdit.setForeground(blanco); // Texto blanco
        btnEdit.setFocusPainted(false); // Sin borde de enfoque
        btnDel.setBackground(new Color(244, 67, 54)); // Fondo rojo
        btnDel.setForeground(blanco); // Texto blanco
        btnDel.setFocusPainted(false); // Sin borde de enfoque
        btnNew.addActionListener(e -> dialogo(null)); // Abre diálogo para nueva cita
        btnEdit.addActionListener(e -> editar()); // Abre diálogo para editar
        btnDel.addActionListener(e -> eliminar()); // Elimina cita seleccionada
        pnl.add(btnNew); // Añade botón Nuevo
        pnl.add(btnEdit); // Añade botón Editar
        pnl.add(btnDel); // Añade botón Eliminar
        add(pnl, BorderLayout.SOUTH); // Agrega el panel de botones al sur
    }

    /**
     * Carga las citas desde la base de datos y actualiza la tabla.
     */
    public void loadData() {
        model.setRowCount(0); // Limpia filas anteriores
        for (Cita c : citaController.findAll()) { // Recorre cada cita recuperada
            model.addRow(new Object[]{
                c.getCodCita(), // ID de la cita
                c.getPaciente().getNombre() + " " + c.getPaciente().getApellidos(), // Nombre completo del paciente
                c.getDoctor().getNombre(), // Nombre del doctor
                c.getFecha() != null ? sdfFecha.format(c.getFecha()) : "", // Fecha formateada
                c.getHoraInicio() != null ? sdfHora.format(c.getHoraInicio()) : "", // Hora de inicio formateada
                c.getHoraFin() != null ? sdfHora.format(c.getHoraFin()) : "", // Hora de fin formateada
                c.getEstado() // Estado de la cita
            });
        }
    }

    /**
     * Abre un diálogo para crear o editar una cita.
     * Permite seleccionar paciente, doctor, fecha, horas y estado.
     */
    private void dialogo(Cita cita) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                cita == null ? "Nueva Cita" : "Editar Cita", true); // Diálogo modal
        d.setSize(350, 300); // Tamaño del diálogo
        d.setLocationRelativeTo(this); // Centra el diálogo respecto al panel
        d.getContentPane().setBackground(new Color(240, 245, 250)); // Fondo del diálogo

        List<Paciente> pacientes = pacienteController.findAll(); // Lista de pacientes
        List<Doctor> doctores = doctorController.findAll(); // Lista de doctores

        JComboBox<String> cmbPac = new JComboBox<>(); // Combo con pacientes
        for (Paciente p : pacientes)
            cmbPac.addItem(p.getCodPaciente() + " - " + p.getNombre() + " " + p.getApellidos()); // Añade cada paciente

        JComboBox<String> cmbDoc = new JComboBox<>(); // Combo con doctores
        for (Doctor doc : doctores)
            cmbDoc.addItem(doc.getCodDoctor() + " - " + doc.getNombre()); // Añade cada doctor

        JTextField txtFecha = new JTextField(); // Campo de fecha
        JTextField txtHoraIni = new JTextField(); // Campo de hora inicio
        JTextField txtHoraFin = new JTextField(); // Campo de hora fin
        JComboBox<String> cmbEst = new JComboBox<>(new String[]{"Programada", "Completada", "Cancelada"}); // Estado de la cita

        if (cita != null) { // Si se edita una cita existente
            for (int i = 0; i < cmbPac.getItemCount(); i++) // Busca paciente en combo
                if (cmbPac.getItemAt(i).startsWith(String.valueOf(cita.getPaciente().getCodPaciente())))
                    cmbPac.setSelectedIndex(i); // Selecciona paciente actual
            for (int i = 0; i < cmbDoc.getItemCount(); i++) // Busca doctor en combo
                if (cmbDoc.getItemAt(i).startsWith(String.valueOf(cita.getDoctor().getCodDoctor())))
                    cmbDoc.setSelectedIndex(i); // Selecciona doctor actual
            txtFecha.setText(cita.getFecha() != null ? sdfFecha.format(cita.getFecha()) : ""); // Carga fecha de la cita
            txtHoraIni.setText(cita.getHoraInicio() != null ? sdfHora.format(cita.getHoraInicio()) : ""); // Carga hora inicio
            txtHoraFin.setText(cita.getHoraFin() != null ? sdfHora.format(cita.getHoraFin()) : ""); // Carga hora fin
            cmbEst.setSelectedItem(cita.getEstado()); // Carga estado
        }

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5)); // Formulario de la cita
        form.setBackground(new Color(240, 245, 250)); // Fondo del formulario
        form.add(new JLabel("Paciente:")); form.add(cmbPac); // Etiqueta y combo de paciente
        form.add(new JLabel("Doctor:")); form.add(cmbDoc); // Etiqueta y combo de doctor
        form.add(new JLabel("Fecha (dd/MM/yyyy):")); form.add(txtFecha); // Etiqueta y campo fecha
        form.add(new JLabel("Hora Inicio (HH:mm):")); form.add(txtHoraIni); // Etiqueta y campo hora inicio
        form.add(new JLabel("Hora Fin (HH:mm):")); form.add(txtHoraFin); // Etiqueta y campo hora fin
        form.add(new JLabel("Estado:")); form.add(cmbEst); // Etiqueta y combo estado
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
                int iPac = cmbPac.getSelectedIndex(); // Índice de paciente seleccionado
                int iDoc = cmbDoc.getSelectedIndex(); // Índice de doctor seleccionado
                if (cita == null) { // Si se crea una cita nueva
                    Cita n = new Cita(pacientes.get(iPac), doctores.get(iDoc),
                            sdfFecha.parse(txtFecha.getText()), sdfHora.parse(txtHoraIni.getText()),
                            sdfHora.parse(txtHoraFin.getText()), (String) cmbEst.getSelectedItem()); // Crea nueva cita
                    citaController.create(n); // Guarda la cita
                } else { // Si se edita una cita existente
                    cita.setPaciente(pacientes.get(iPac)); // Actualiza paciente
                    cita.setDoctor(doctores.get(iDoc)); // Actualiza doctor
                    cita.setFecha(sdfFecha.parse(txtFecha.getText())); // Actualiza fecha
                    cita.setHoraInicio(sdfHora.parse(txtHoraIni.getText())); // Actualiza hora inicio
                    cita.setHoraFin(sdfHora.parse(txtHoraFin.getText())); // Actualiza hora fin
                    cita.setEstado((String) cmbEst.getSelectedItem()); // Actualiza estado
                    citaController.update(cita); // Guarda cambios
                }
                loadData(); // Recarga la tabla
                d.dispose(); // Cierra el diálogo
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage()); // Muestra mensaje de error
            }
        });
        btnCancel.addActionListener(e -> d.dispose()); // Cierra el diálogo sin guardar
        pnlBtn.add(btnOk); // Añade botón Guardar
        pnlBtn.add(btnCancel); // Añade botón Cancelar
        d.add(pnlBtn, BorderLayout.SOUTH); // Agrega el panel de botones al pie
        d.setVisible(true); // Muestra el diálogo
    }

    /**
     * Abre el diálogo de edición para la cita seleccionada.
     */
    private void editar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona una cita"); return; } // Valida selección
        dialogo(citaController.findById((Integer) model.getValueAt(row, 0))); // Abre diálogo con la cita seleccionada
    }

    /**
     * Elimina la cita seleccionada tras pedir confirmación.
     */
    private void eliminar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona una cita"); return; } // Valida selección
        if (JOptionPane.showConfirmDialog(this, "Eliminar cita?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Confirma eliminación
            citaController.delete((Integer) model.getValueAt(row, 0)); // Elimina la cita por ID
            loadData(); // Recarga la tabla
        }
    }
}
