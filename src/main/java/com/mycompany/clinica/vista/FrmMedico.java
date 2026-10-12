package com.mycompany.clinica.vista;

import com.mycompany.clinica.dao.EspecialidadDAO;
import com.mycompany.clinica.dao.MedicoDAO;
import com.mycompany.clinica.modelo.Especialidad;
import com.mycompany.clinica.modelo.Medico;
import com.mycompany.clinica.servicio.Permisos;
import com.mycompany.clinica.utilidades.ModeloTabla;
import java.awt.Color;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;

public class FrmMedico extends javax.swing.JInternalFrame {

    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    private ModeloTabla modeloTabla;

    public FrmMedico() {
        initComponents();
        cargarComboEspecialidades();
        listarMedicos();
        this.setSize(1020, 520); // Tamaño ideal expandido para alta legibilidad
        aplicarPermisos();
    }

    /** Sin permiso de gestión la pantalla queda en modo solo consulta: se oculta el formulario y se deja la tabla. */
    private void aplicarPermisos() {
        if (!Permisos.puedeGestionarMedicos()) {
            jLabel1.setVisible(false);
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);
            jLabel4.setVisible(false);
            jLabel5.setVisible(false);
            jLabel6.setVisible(false);
            txtNombres.setVisible(false);
            txtApellidos.setVisible(false);
            txtCmp.setVisible(false);
            txtTelefono.setVisible(false);
            txtEmail.setVisible(false);
            cboEspecialidad.setVisible(false);
            btnGuardar.setVisible(false);
            btnGestionarEspecialidad.setVisible(false);
        }
    }

    public void cargarComboEspecialidades() {
        try {
            List<Especialidad> especialidades = especialidadDAO.listarEspecialidades();
            cboEspecialidad.removeAllItems();
            for (Especialidad especialidad : especialidades) {
                cboEspecialidad.addItem(especialidad);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar especialidades: " + e.getMessage());
        }
    }

    public void listarMedicos() {
        modeloTabla.setRowCount(0);
        try {
            for (Medico medico : medicoDAO.listarMedicos()) {
                modeloTabla.addRow(new Object[]{
                        medico.getIdMedico(),
                        medico.getNombres(),
                        medico.getApellidos(),
                        medico.getCmp(),
                        medico.getTelefono(),
                        medico.getEmail(),
                        medico.getEspecialidad()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al listar médicos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        txtNombres = new javax.swing.JTextField();
        txtApellidos = new javax.swing.JTextField();
        txtCmp = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();

        cboEspecialidad = new javax.swing.JComboBox<>();
        btnGestionarEspecialidad = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();

        jTable1 = new JTable();
        jScrollPane1 = new JScrollPane(jTable1);

        modeloTabla = new ModeloTabla("ID", "Nombres", "Apellidos", "CMP", "Teléfono", "Email", "Especialidad");
        jTable1.setModel(modeloTabla);

        // --- DISEÑO ELEGANTE DE LA TABLA ---
        jTable1.setRowHeight(28);
        jTable1.setShowGrid(true);
        jTable1.setGridColor(new Color(240, 240, 240));
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        jTable1.getTableHeader().setBackground(new Color(44, 62, 80));
        jTable1.getTableHeader().setForeground(Color.WHITE);
        jTable1.setSelectionBackground(new Color(232, 240, 254));
        jTable1.setSelectionForeground(Color.BLACK);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("MÓDULO DE GESTIÓN DE MÉDICOS");

        // --- FUENTES PARA ETIQUETAS ---
        Font fontLabels = new Font("Segoe UI", Font.BOLD, 12);
        Color colorTexto = new Color(70, 70, 70);

        jLabel1.setFont(fontLabels); jLabel1.setForeground(colorTexto); jLabel1.setText("Nombres:");
        jLabel2.setFont(fontLabels); jLabel2.setForeground(colorTexto); jLabel2.setText("Apellidos:");
        jLabel3.setFont(fontLabels); jLabel3.setForeground(colorTexto); jLabel3.setText("CMP:");
        jLabel4.setFont(fontLabels); jLabel4.setForeground(colorTexto); jLabel4.setText("Teléfono:");
        jLabel5.setFont(fontLabels); jLabel5.setForeground(colorTexto); jLabel5.setText("Email:");
        jLabel6.setFont(fontLabels); jLabel6.setForeground(colorTexto); jLabel6.setText("Especialidad:");

        // --- ESTILOS DE CAJAS DE TEXTO ---
        Font fontInputs = new Font("Segoe UI", Font.PLAIN, 13);
        txtNombres.setFont(fontInputs);
        txtApellidos.setFont(fontInputs);
        txtCmp.setFont(fontInputs);
        txtTelefono.setFont(fontInputs);
        txtEmail.setFont(fontInputs);
        cboEspecialidad.setFont(fontInputs);

        // --- BOTONES MINIMALISTAS Y ELEGANTES ---
        btnGestionarEspecialidad.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnGestionarEspecialidad.setBackground(new Color(52, 152, 219));
        btnGestionarEspecialidad.setForeground(Color.WHITE);
        btnGestionarEspecialidad.setFocusPainted(false);
        btnGestionarEspecialidad.setText("Añadir / Ver");
        btnGestionarEspecialidad.addActionListener(this::btnGestionarEspecialidadActionPerformed);

        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setText("GUARDAR MÉDICO");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jLabel5)
                                                        .addComponent(jLabel6))
                                                .addGap(25, 25, 25)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(txtNombres, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                                        .addComponent(txtApellidos)
                                                        .addComponent(txtCmp)
                                                        .addComponent(txtTelefono)
                                                        .addComponent(txtEmail)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(cboEspecialidad, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnGestionarEspecialidad, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE))))
                                        .addComponent(btnGuardar, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE))
                                .addGap(35, 35, 35)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                                .addGap(35, 35, 35))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel1)
                                                        .addComponent(txtNombres, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel2)
                                                        .addComponent(txtApellidos, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel3)
                                                        .addComponent(txtCmp, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel4)
                                                        .addComponent(txtTelefono, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel5)
                                                        .addComponent(txtEmail, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel6)
                                                        .addComponent(cboEspecialidad, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnGestionarEspecialidad, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
                                                .addGap(30, 30, 30)
                                                .addComponent(btnGuardar, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)))
                                .addGap(35, 35, 35))
        );

        pack();
    }

    private void btnGestionarEspecialidadActionPerformed(java.awt.event.ActionEvent evt) {
        if (!Permisos.puedeGestionarMedicos()) {
            JOptionPane.showMessageDialog(this, "No tiene permiso para realizar esta acción.",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FrmEspecialidad frmEsp = new FrmEspecialidad(this);
        JDesktopPane desktopPane = this.getDesktopPane();

        if (desktopPane != null) {
            desktopPane.add(frmEsp);
            frmEsp.toFront();
            frmEsp.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Error: No se encontró el contenedor principal.");
        }
    }
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        if (!Permisos.puedeGestionarMedicos()) {
            JOptionPane.showMessageDialog(this, "No tiene permiso para realizar esta acción.",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String cmp = txtCmp.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        Especialidad especialidadSeleccionada = (Especialidad) cboEspecialidad.getSelectedItem();

        if (nombres.isEmpty() || apellidos.isEmpty() || cmp.isEmpty() || especialidadSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Nombres, Apellidos, CMP y Especialidad son campos obligatorios.");
            return;
        }

        Medico medico = new Medico(nombres, apellidos, cmp, telefono, email, especialidadSeleccionada.getIdEspecialidad());

        try {
            medicoDAO.registrarMedico(medico);

            JOptionPane.showMessageDialog(this, "¡Médico registrado correctamente!");

            txtNombres.setText("");
            txtApellidos.setText("");
            txtCmp.setText("");
            txtTelefono.setText("");
            txtEmail.setText("");
            if (cboEspecialidad.getItemCount() > 0) {
                cboEspecialidad.setSelectedIndex(0);
            }

            listarMedicos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de Base de Datos: " + e.getMessage());
        }
    }

    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnGestionarEspecialidad;
    private javax.swing.JComboBox<Especialidad> cboEspecialidad;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtApellidos;
    private javax.swing.JTextField txtCmp;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtTelefono;
}
