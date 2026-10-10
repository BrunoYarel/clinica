package com.mycompany.clinica;

public class FrmMedico extends javax.swing.JInternalFrame {

    public FrmMedico() {
        initComponents();
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
        txtIdEspecialidad = new javax.swing.JTextField();
        
        btnGuardar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        jLabel1.setText("Nombres:");
        jLabel2.setText("Apellidos:");
        jLabel3.setText("CMP:");
        jLabel4.setText("Teléfono:");
        jLabel5.setText("Email:");
        jLabel6.setText("ID Especialidad:");

        btnGuardar.setText("GUARDAR MÉDICO");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombres, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(txtApellidos)
                    .addComponent(txtCmp)
                    .addComponent(txtTelefono)
                    .addComponent(txtEmail)
                    .addComponent(txtIdEspecialidad))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCmp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtIdEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }                       

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // 1. Obtener textos limpiando espacios
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String cmp = txtCmp.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        String idEspecialidadStr = txtIdEspecialidad.getText().trim();

        // 2. Validaciones básicas
        if (nombres.isEmpty() || apellidos.isEmpty() || cmp.isEmpty() || idEspecialidadStr.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Nombres, Apellidos, CMP e ID Especialidad son campos obligatorios.");
            return;
        }

        // 3. Sentencia SQL adaptada a tu tabla MEDICO
        String sql = "INSERT INTO medico (nombres, apellidos, cmp, telefono, email, id_especialidad) VALUES (?, ?, ?, ?, ?, ?)";

        // 4. Intentar insertar a la Base de Datos usando tu clase Conexion existente
        try (java.sql.Connection con = Conexion.getConexion()) {
            
            if (con == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error de Conexión: No se pudo conectar a MySQL.");
                return;
            }
            
            try (java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombres);
                ps.setString(2, apellidos);
                ps.setString(3, cmp);
                ps.setString(4, telefono);
                ps.setString(5, email);
                
                // Convertir el ID de especialidad ingresado a número entero
                ps.setInt(6, Integer.parseInt(idEspecialidadStr));
                
                ps.executeUpdate(); 
                
                javax.swing.JOptionPane.showMessageDialog(this, "¡Médico registrado correctamente!");
                
                // 5. Limpiar todos los campos
                txtNombres.setText("");
                txtApellidos.setText("");
                txtCmp.setText("");
                txtTelefono.setText("");
                txtEmail.setText("");
                txtIdEspecialidad.setText("");
            }
        } catch (NumberFormatException nfe) {
            javax.swing.JOptionPane.showMessageDialog(this, "El ID de Especialidad debe ser un valor numérico válido.");
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error de Base de Datos: " + e.getMessage());
        }
    }                                          

    // Declaración de variables de la interfaz
    private javax.swing.JButton btnGuardar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField txtApellidos;
    private javax.swing.JTextField txtCmp;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtIdEspecialidad;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtTelefono;                 
}
