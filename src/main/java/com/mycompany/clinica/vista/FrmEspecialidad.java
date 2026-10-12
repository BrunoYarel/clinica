package com.mycompany.clinica.vista;

import com.mycompany.clinica.dao.EspecialidadDAO;
import com.mycompany.clinica.modelo.Especialidad;
import com.mycompany.clinica.servicio.Permisos;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class FrmEspecialidad extends javax.swing.JInternalFrame {

    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

    private FrmMedico frmMedicoPadre;

    public FrmEspecialidad() {
        initComponents();
        aplicarPermisos();
    }

    /** Sin permiso para gestionar especialidades el formulario queda deshabilitado. */
    private void aplicarPermisos() {
        if (!Permisos.puedeGestionarEspecialidades()) {
            txtNombre.setEnabled(false);
            txtDescripcion.setEnabled(false);
            btnGuardar.setEnabled(false);
        }
    }

    public FrmEspecialidad(FrmMedico frmMedicoPadre) {
        this();
        this.frmMedicoPadre = frmMedicoPadre;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtDescripcion = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        jLabel1.setText("Nombre:");

        jLabel2.setText("Descripción:");

        btnGuardar.setText("GUARDAR");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2))
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                        .addComponent(txtDescripcion))
                                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(35, 35, 35)
                                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(40, Short.MAX_VALUE))
        );

        pack();
    }

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        if (!Permisos.puedeGestionarEspecialidades()) {
            JOptionPane.showMessageDialog(this, "No tiene permiso para realizar esta acción.",
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la especialidad es obligatorio.");
            return;
        }

        Especialidad especialidad = new Especialidad();
        especialidad.setNombre(nombre);
        especialidad.setDescripcion(descripcion);

        try {
            especialidadDAO.registrarEspecialidad(especialidad);

            JOptionPane.showMessageDialog(this, "¡Especialidad guardada correctamente!");
            txtNombre.setText("");
            txtDescripcion.setText("");

            // Avisa al formulario de médicos para que recargue el combo de especialidades
            if (frmMedicoPadre != null) {
                frmMedicoPadre.cargarComboEspecialidades();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos al insertar: " + e.getMessage());
        }
    }

    private javax.swing.JButton btnGuardar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtNombre;
}
