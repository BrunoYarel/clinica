package com.mycompany.clinica.vista;

import com.mycompany.clinica.dao.MedicamentoDAO;
import com.mycompany.clinica.modelo.Medicamento;
import com.mycompany.clinica.servicio.Permisos;
import com.mycompany.clinica.utilidades.ModeloTabla;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * Catálogo de medicamentos: el administrador los registra y edita; el médico los elige al emitir recetas.
 * Mismo estilo que FrmMedico: formulario a la izquierda y tabla a la derecha. Al hacer clic en una fila
 * de la tabla, sus datos pasan al formulario para editarlos.
 */
public class FrmMedicamento extends javax.swing.JInternalFrame {

    private final MedicamentoDAO medicamentoDAO = new MedicamentoDAO();
    private final List<Medicamento> medicamentos = new ArrayList<>();   // lo que muestra la tabla, en el mismo orden

    private ModeloTabla modeloTabla;
    private int idEditando = 0;   // 0 = registrando uno nuevo; > 0 = editando ese medicamento

    public FrmMedicamento() {
        initComponents();
        listarMedicamentos();
        this.setSize(900, 460);
        aplicarPermisos();
    }

    /** Sin permiso de gestión la pantalla queda en modo solo consulta: se oculta el formulario y se deja la tabla. */
    private void aplicarPermisos() {
        if (!Permisos.puedeGestionarMedicamentos()) {
            pnlFormulario.setVisible(false);
        }
    }

    public void listarMedicamentos() {
        modeloTabla.setRowCount(0);
        medicamentos.clear();
        try {
            medicamentos.addAll(medicamentoDAO.listarMedicamentos());
            for (Medicamento m : medicamentos) {
                modeloTabla.addRow(new Object[]{m.getIdMedicamento(), m.getNombre(), m.getPresentacion()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al listar medicamentos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        Font fontLabels = new Font("Segoe UI", Font.BOLD, 12);
        Font fontInputs = new Font("Segoe UI", Font.PLAIN, 13);
        Color colorTexto = new Color(70, 70, 70);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("MÓDULO DE GESTIÓN DE MEDICAMENTOS");

        // --- FORMULARIO (izquierda) ---
        lblModo = new JLabel("Nuevo medicamento");
        lblModo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblModo.setForeground(colorTexto);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(fontLabels);
        lblNombre.setForeground(colorTexto);

        JLabel lblPresentacion = new JLabel("Presentación:");
        lblPresentacion.setFont(fontLabels);
        lblPresentacion.setForeground(colorTexto);

        txtNombre = new JTextField();
        txtNombre.setFont(fontInputs);
        txtPresentacion = new JTextField();
        txtPresentacion.setFont(fontInputs);

        btnGuardar = new JButton("GUARDAR MEDICAMENTO");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnNuevo = new JButton("NUEVO / LIMPIAR");
        btnNuevo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnNuevo.setBackground(new Color(52, 152, 219));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.addActionListener(e -> limpiarFormulario());

        pnlFormulario = new JPanel(new GridBagLayout());
        pnlFormulario.setPreferredSize(new Dimension(310, 0));
        agregarFila(pnlFormulario, lblModo, 0, 0);
        agregarFila(pnlFormulario, lblNombre, 1, 22);
        agregarFila(pnlFormulario, txtNombre, 2, 6);
        agregarFila(pnlFormulario, lblPresentacion, 3, 16);
        agregarFila(pnlFormulario, txtPresentacion, 4, 6);
        agregarFila(pnlFormulario, btnGuardar, 5, 26);
        agregarFila(pnlFormulario, btnNuevo, 6, 10);

        // Empuja todo hacia arriba
        GridBagConstraints relleno = new GridBagConstraints();
        relleno.gridx = 0;
        relleno.gridy = 7;
        relleno.weighty = 1.0;
        pnlFormulario.add(new JPanel(), relleno);

        // --- TABLA (derecha), mismo diseño que la de FrmMedico ---
        modeloTabla = new ModeloTabla("ID", "Nombre", "Presentación");
        jTable1 = new JTable(modeloTabla);
        jTable1.setRowHeight(28);
        jTable1.setShowGrid(true);
        jTable1.setGridColor(new Color(240, 240, 240));
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        jTable1.getTableHeader().setBackground(new Color(44, 62, 80));
        jTable1.getTableHeader().setForeground(Color.WHITE);
        jTable1.setSelectionBackground(new Color(232, 240, 254));
        jTable1.setSelectionForeground(Color.BLACK);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(260);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(240);
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });

        JScrollPane jScrollPane1 = new JScrollPane(jTable1);

        JPanel contenido = new JPanel(new BorderLayout(30, 0));
        contenido.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));
        contenido.add(pnlFormulario, BorderLayout.WEST);
        contenido.add(jScrollPane1, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(contenido, BorderLayout.CENTER);
    }

    /** Agrega un componente en una fila del formulario, ocupando todo el ancho. */
    private static void agregarFila(JPanel panel, java.awt.Component componente, int fila, int espacioArriba) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(espacioArriba, 0, 0, 0);
        panel.add(componente, gbc);
    }

    /** Pasa al formulario los datos de la fila seleccionada (modo edición). */
    private void cargarSeleccionEnFormulario() {
        int fila = jTable1.getSelectedRow();
        if (fila < 0 || fila >= medicamentos.size() || !Permisos.puedeGestionarMedicamentos()) {
            return;
        }
        Medicamento m = medicamentos.get(fila);
        idEditando = m.getIdMedicamento();
        txtNombre.setText(m.getNombre());
        txtPresentacion.setText(m.getPresentacion());
        lblModo.setText("Editando medicamento #" + idEditando);
        btnGuardar.setText("ACTUALIZAR MEDICAMENTO");
    }

    /** Vuelve el formulario al modo "nuevo medicamento". */
    private void limpiarFormulario() {
        idEditando = 0;
        txtNombre.setText("");
        txtPresentacion.setText("");
        lblModo.setText("Nuevo medicamento");
        btnGuardar.setText("GUARDAR MEDICAMENTO");
        jTable1.clearSelection();
        txtNombre.requestFocusInWindow();
    }

    /** true si ya existe OTRO medicamento con el mismo nombre y presentación (sin distinguir mayúsculas). */
    private boolean existeDuplicado(String nombre, String presentacion) {
        for (Medicamento m : medicamentos) {
            if (m.getIdMedicamento() != idEditando
                    && m.getNombre().equalsIgnoreCase(nombre)
                    && m.getPresentacion().equalsIgnoreCase(presentacion)) {
                return true;
            }
        }
        return false;
    }

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        if (!Permisos.puedeGestionarMedicamentos()) {
            JOptionPane.showMessageDialog(this, "No tiene permiso para realizar esta acción.",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = txtNombre.getText().trim();
        String presentacion = txtPresentacion.getText().trim();

        if (nombre.isEmpty() || presentacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre y la presentación son campos obligatorios.");
            return;
        }
        if (nombre.length() > 100 || presentacion.length() > 100) {
            JOptionPane.showMessageDialog(this, "El nombre y la presentación no pueden superar los 100 caracteres.");
            return;
        }
        if (existeDuplicado(nombre, presentacion)) {
            JOptionPane.showMessageDialog(this, "Ese medicamento con esa presentación ya está registrado.");
            return;
        }

        boolean editando = idEditando > 0;
        Medicamento medicamento = new Medicamento(idEditando, nombre, presentacion);

        try {
            boolean guardado = editando
                    ? medicamentoDAO.actualizarMedicamento(medicamento)
                    : medicamentoDAO.registrarMedicamento(medicamento);

            if (guardado) {
                JOptionPane.showMessageDialog(this, editando
                        ? "¡Medicamento actualizado correctamente!"
                        : "¡Medicamento registrado correctamente!");
                limpiarFormulario();
                listarMedicamentos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el medicamento.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de Base de Datos: " + e.getMessage());
        }
    }

    private JPanel pnlFormulario;
    private JLabel lblModo;
    private JTextField txtNombre;
    private JTextField txtPresentacion;
    private JButton btnGuardar;
    private JButton btnNuevo;
    private JTable jTable1;
}
