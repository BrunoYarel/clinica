package com.mycompany.clinica;

public class FrmMedicamento extends javax.swing.JInternalFrame {

    public FrmMedicamento() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtPresentacion = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        jLabel1.setText("Nombre:");

        jLabel2.setText("Presentación:");

        btnGuardar.setText("GUARDAR MEDICAMENTO");
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
                    .addComponent(jLabel2))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(txtPresentacion))
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
                    .addComponent(txtPresentacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        pack();
    }                       

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // 1. Obtener textos limpiando espacios
        String nombre = txtNombre.getText().trim();
        String presentacion = txtPresentacion.getText().trim();

        // 2. Validar que las cajas de texto no estén vacías
        if (nombre.isEmpty() || presentacion.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor, llene todos los campos del medicamento.");
            return;
        }

        // 3. Sentencia SQL apuntando exactamente a tu tabla "medicamento"
        String sql = "INSERT INTO medicamento (nombre, presentacion) VALUES (?, ?)";

        // 4. Intentar la conexión e inserción segura
        try {
            java.sql.Connection con = Conexion.getConexion();
            
            if (con == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error: No se pudo conectar a MySQL. Verifica XAMPP.");
                return;
            }
            
            try (java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, presentacion);
                
                ps.executeUpdate(); // Inserta el medicamento en MySQL
                
                javax.swing.JOptionPane.showMessageDialog(this, "¡Medicamento guardado correctamente!");
                
                // 5. Limpiar los campos tras el éxito
                txtNombre.setText("");
                txtPresentacion.setText("");
            }
            con.close();

        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
        }
    }                                          

    // Variables de control nativas
    private javax.swing.JButton btnGuardar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField txtPresentacion;
    private javax.swing.JTextField txtNombre;                 
}
