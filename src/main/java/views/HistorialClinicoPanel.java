package views;

import controllers.controladores.HistorialClinicoController; // Controlador CRUD de historial clínico
import models.modelos.entidades.HistorialClinico; // Entidad Historial Clinico
import javax.swing.*; // Componentes Swing
import javax.swing.table.DefaultTableModel; // Modelo de tabla para JTable
import java.awt.*; // Layout y colores
import java.text.SimpleDateFormat; // Formato de fecha
import java.util.List; // Listas de entidades

/**
 * Panel que administra la vista de Historial Clinico.
 * Permite listar, crear, editar y eliminar registros de historial clínico.
 */
public class HistorialClinicoPanel extends JPanel {

    private HistorialClinicoController controller; // Controlador de historial clí­nico
    private JTable table; // Tabla que muestra los historiales
    private DefaultTableModel model; // Modelo de datos
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Formato de fecha
    private Color azul = new Color(33, 150, 243); // Color principal
    private Color blanco = Color.WHITE; // Color del texto

    /**
     * Constructor del panel de Historial Clinico.
     * Configura el UI y carga los registros existentes.
     */
    public HistorialClinicoPanel() {
        controller = new HistorialClinicoController(); // Crea controlador
        setLayout(new BorderLayout()); // Usa BorderLayout
        setBackground(new Color(240, 245, 250)); // Fondo suave
        initComponents(); // Inicializa componentes
        loadData(); // Carga datos
    }

    /**
     * Inicializa los componentes visuales del panel.
     */
    private void initComponents() {
        JLabel titulo = new JLabel("HISTORIAL CLINICO", SwingConstants.CENTER); // Título centrado
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f)); // Fuente negrita
        titulo.setForeground(azul); // Texto azul
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Margen superior/inferior
        add(titulo, BorderLayout.NORTH); // Agrega título arriba

        String[] cols = {"ID", "Paciente", "Alergias", "Enfermedades Cronicas", "Grupo Sanguineo", "Observaciones", "Fecha Alta"}; // Columnas de historiales
        model = new DefaultTableModel(cols, 0) { // Modelo no editable
            public boolean isCellEditable(int r, int c) { return false; } // Evita edición directa
        };
        table = new JTable(model); // Crea tabla
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Selección única
        table.getTableHeader().setBackground(azul); // Encabezado azul
        table.getTableHeader().setForeground(blanco); // Texto blanco
        table.getTableHeader().setFont(table.getFont().deriveFont(Font.BOLD, 12f)); // Fuente del encabezado
        table.setRowHeight(25); // Altura de fila
        add(new JScrollPane(table), BorderLayout.CENTER); // Agrega tabla con scroll

        JPanel pnl = new JPanel(); // Panel de botones
        pnl.setBackground(new Color(240, 245, 250)); // Fondo panel
        pnl.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); // Margen panel
        JButton btnNew = new JButton("Nuevo"); // Botón nuevo registro
        JButton btnEdit = new JButton("Editar"); // Botón editar registro
        JButton btnDel = new JButton("Eliminar"); // Botón eliminar registro
        btnNew.setBackground(azul); btnNew.setForeground(blanco); btnNew.setFocusPainted(false); // Estilo botón nuevo
        btnEdit.setBackground(new Color(255, 152, 0)); btnEdit.setForeground(blanco); btnEdit.setFocusPainted(false); // Estilo botón editar
        btnDel.setBackground(new Color(244, 67, 54)); btnDel.setForeground(blanco); btnDel.setFocusPainted(false); // Estilo botón eliminar
        btnNew.addActionListener(e -> dialogo(null)); // Abre dialogo para nuevo registro
        btnEdit.addActionListener(e -> editar()); // Edita registro seleccionado
        btnDel.addActionListener(e -> eliminar()); // Elimina registro seleccionado
        pnl.add(btnNew); // Añade botón Nuevo
        pnl.add(btnEdit); // Añade botón Editar
        pnl.add(btnDel); // Añade botón Eliminar
        add(pnl, BorderLayout.SOUTH); // Agrega panel de botones abajo
    }

    /**
     * Carga los historiales clí­nicos desde la base de datos y actualiza la tabla.
     */
    public void loadData() {
        model.setRowCount(0); // Limpia filas
        for (HistorialClinico h : controller.findAll()) { // Recorre registros
            model.addRow(new Object[]{
                h.getCodHistorial(), // ID de historial
                h.getPaciente().getNombre() + " " + h.getPaciente().getApellidos(), // Paciente completo
                h.getAlergias(), // Alergias
                h.getEnfermedadesCronicas(), // Enfermedades crónicas
                h.getGrupoSanguineo(), // Grupo sanguíneo
                h.getObservacionesGenerales(), // Observaciones generales
                h.getFechaAlta() != null ? sdf.format(h.getFechaAlta()) : "" // Fecha de alta formateada
            });
        }
    }

    /**
     * Abre un dialogo para crear o editar un historial clí­nico.
     */
    private void dialogo(HistorialClinico historial) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                historial == null ? "Nuevo Historial" : "Editar Historial", true); // Dialogo modal
        d.setSize(350, 250); // Tamaño del dialogo
        d.setLocationRelativeTo(this); // Centrado
        d.getContentPane().setBackground(new Color(240, 245, 250)); // Fondo dialogo

        JTextField txtPaciente = new JTextField(); // Campo paciente
        txtPaciente.setEditable(false); // El paciente no se edita aquí
        JTextField txtAlergias = new JTextField(); // Campo alergias
        JTextField txtEnfermedades = new JTextField(); // Campo enfermedades crónicas
        JTextField txtGrupoSanguineo = new JTextField(); // Campo grupo sanguíneo
        JTextField txtObservaciones = new JTextField(); // Campo observaciones
        JTextField txtFecha = new JTextField(); // Campo fecha de alta

        if (historial != null) { // Si se edita un historial existente
            txtPaciente.setText(historial.getPaciente().getNombre() + " " + historial.getPaciente().getApellidos()); // Carga paciente
            txtAlergias.setText(historial.getAlergias()); // Carga alergias
            txtEnfermedades.setText(historial.getEnfermedadesCronicas()); // Carga enfermedades crónicas
            txtGrupoSanguineo.setText(historial.getGrupoSanguineo()); // Carga grupo sanguíneo
            txtObservaciones.setText(historial.getObservacionesGenerales()); // Carga observaciones
            txtFecha.setText(historial.getFechaAlta() != null ? sdf.format(historial.getFechaAlta()) : ""); // Carga fecha de alta
        }

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5)); // Formulario 6 filas x 2 columnas
        form.setBackground(new Color(240, 245, 250)); // Fondo formulario
        form.add(new JLabel("Paciente:")); form.add(txtPaciente); // Paciente
        form.add(new JLabel("Alergias:")); form.add(txtAlergias); // Alergias
        form.add(new JLabel("Enfermedades Cronicas:")); form.add(txtEnfermedades); // Enfermedades crónicas
        form.add(new JLabel("Grupo Sanguineo:")); form.add(txtGrupoSanguineo); // Grupo sanguíneo
        form.add(new JLabel("Observaciones:")); form.add(txtObservaciones); // Observaciones
        form.add(new JLabel("Fecha Alta (dd/MM/yyyy):")); form.add(txtFecha); // Fecha
        d.add(form, BorderLayout.CENTER); // Agrega formulario al centro

        JPanel pnlBtn = new JPanel(); // Panel de botones
        pnlBtn.setBackground(new Color(240, 245, 250)); // Fondo
        JButton btnOk = new JButton("Guardar"); // Botón guardar
        btnOk.setBackground(azul); btnOk.setForeground(blanco); btnOk.setFocusPainted(false); // Estilo botón guardar
        JButton btnCancel = new JButton("Cancelar"); // Botón cancelar
        btnCancel.setBackground(new Color(158, 158, 158)); btnCancel.setForeground(blanco); btnCancel.setFocusPainted(false); // Estilo botón cancelar
        btnOk.addActionListener(e -> {
            try {
                if (historial == null) { // Creación
                    HistorialClinico n = new HistorialClinico(); // Crea nueva entidad
                    n.setPaciente(null); // El paciente no se selecciona directamente en este formulario
                    n.setAlergias(txtAlergias.getText()); // Asigna alergias
                    n.setEnfermedadesCronicas(txtEnfermedades.getText()); // Asigna enfermedades crónicas
                    n.setGrupoSanguineo(txtGrupoSanguineo.getText()); // Asigna grupo sanguíneo
                    n.setObservacionesGenerales(txtObservaciones.getText()); // Asigna observaciones
                    n.setFechaAlta(txtFecha.getText().isEmpty() ? null : sdf.parse(txtFecha.getText())); // Asigna fecha de alta
                    controller.create(n); // Guarda el historial
                } else { // Edición
                    historial.setAlergias(txtAlergias.getText()); // Actualiza alergias
                    historial.setEnfermedadesCronicas(txtEnfermedades.getText()); // Actualiza enfermedades crónicas
                    historial.setGrupoSanguineo(txtGrupoSanguineo.getText()); // Actualiza grupo sanguíneo
                    historial.setObservacionesGenerales(txtObservaciones.getText()); // Actualiza observaciones
                    historial.setFechaAlta(txtFecha.getText().isEmpty() ? null : sdf.parse(txtFecha.getText())); // Actualiza fecha de alta
                    controller.update(historial); // Guarda cambios
                }
                loadData(); // Recarga datos
                d.dispose(); // Cierra dialogo
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage()); // Muestra error
            }
        });
        btnCancel.addActionListener(e -> d.dispose()); // Cierra diálogo
        pnlBtn.add(btnOk); // Añade botón Guardar
        pnlBtn.add(btnCancel); // Añade botón Cancelar
        d.add(pnlBtn, BorderLayout.SOUTH); // Agrega botones en la parte inferior
        d.setVisible(true); // Muestra el diálogo
    }

    /**
     * Abre el diálogo de edición para el historial seleccionado.
     */
    private void editar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un historial"); return; } // Valida selección
        dialogo(controller.findById((Integer) model.getValueAt(row, 0))); // Abre diálogo con historial seleccionado
    }

    /**
     * Elimina el historial seleccionado después de pedir confirmación.
     */
    private void eliminar() {
        int row = table.getSelectedRow(); // Obtiene fila seleccionada
        if (row == -1) { JOptionPane.showMessageDialog(this, "Selecciona un historial"); return; } // Valida selección
        if (JOptionPane.showConfirmDialog(this, "Eliminar historial?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Confirma eliminación
            controller.delete((Integer) model.getValueAt(row, 0)); // Elimina historial
            loadData(); // Recarga tabla
        }
    }
}
