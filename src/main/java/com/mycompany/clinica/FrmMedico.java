package com.mycompany.clinica;

import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.JOptionPane;
import javax.swing.JDesktopPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FrmMedico extends javax.swing.JInternalFrame {

    public FrmMedico() {
        initComponents();
        cargarComboEspecialidades();
    }

    public void cargarComboEspecialidades() {
        String sql = "SELECT id_especialidad, nombre FROM especialidad";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            cboEspecialidad.removeAllItems();
            while (rs.next()) {
                cboEspecialidad.addItem(new Especialidad(
                    rs.getInt("id_especialidad"),
                    rs.getString("nombre")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar especialidades: " + e.getMessage());
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

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("REGISTRO DE MÉDICOS");

        jLabel1.setText("Nombres:");
        jLabel2.setText("Apellidos:");
        jLabel3.setText("CMP:");
        jLabel4.setText("Teléfono:");
        jLabel5.setText("Email:");
        jLabel6.setText("Especialidad:");

        btnGestionarEspecialidad.setText("Añadir / Ver");
        btnGestionarEspecialidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGestionarEspecialidadActionPerformed(evt);
            }
        });

        btnGuardar.setText("GUARDAR MÉDICO");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnGuardar, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombres, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(txtApellidos)
                    .addComponent(txtCmp)
                    .addComponent(txtTelefono)
                    .addComponent(txtEmail)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboEspecialidad, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGestionarEspecialidad, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNombres, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtApellidos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCmp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTelefono, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cboEspecialidad, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGestionarEspecialidad))
                .addGap(30, 30, 30)
                .addComponent(btnGuardar, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }

    private void btnGestionarEspecialidadActionPerformed(java.awt.event.ActionEvent evt) {
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

        String sql = "INSERT INTO medico (nombres, apellidos, cmp, telefono, email, id_especialidad) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConexion()) {
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Error de Conexión: No se pudo conectar a MySQL.");
                return;
            }
            
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombres);
                ps.setString(2, apellidos);
                ps.setString(3, cmp);
                ps.setString(4, telefono);
                ps.setString(5, email);
                ps.setInt(6, especialidadSeleccionada.getId());
                
                ps.executeUpdate(); 
                
                JOptionPane.showMessageDialog(this, "¡Médico registrado correctamente!");
                
                txtNombres.setText("");
                txtApellidos.setText("");
                txtCmp.setText("");
                txtTelefono.setText("");
                txtEmail.setText("");
                if (cboEspecialidad.getItemCount() > 0) {
                    cboEspecialidad.setSelectedIndex(0);
                }
            }
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
    private javax.swing.JTextField txtApellidos;
    private javax.swing.JTextField txtCmp;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtTelefono;
}
