package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.mycompany.clinica.Conexion;



/**
 * Interfaz limpia y funcional para el registro de nuevos usuarios en el sistema.
 * Reemplaza el diseño rígido de NetBeans por una disposición modular y adaptable.
 * 
 * @author PC-03
 */
public class NuevoUsuario extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(NuevoUsuario.class.getName());

    // --- Componentes con nombres coherentes y tipos correctos ---
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblRol;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPasswordUsuario;
    private javax.swing.JLabel lblPasswordAdmin;
    
    private javax.swing.JTextField txtNombre;
    private javax.swing.JPasswordField txtPasswordUsuario;
    private javax.swing.JPasswordField txtPasswordAdmin;
    private javax.swing.JComboBox<String> cbxRol;
    
    private javax.swing.JButton btnCrear;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnIniciarSesion;

    /**
     * Constructor de la interfaz
     */
    public NuevoUsuario() {
        super("Registro de Usuarios");
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana automáticamente en la pantalla
    }

    /**
     * Inicializa y organiza los componentes con un diseño limpio y moderno.
     */
    private void initComponents() {
        // Contenedor principal con márgenes estéticos
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 15));
        panelPrincipal.setBorder(new EmptyBorder(20, 25, 20, 25));
        panelPrincipal.setBackground(new Color(245, 247, 250));

        // --- ENCABEZADO ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);

        lblTitulo = new JLabel("Crear Perfil de Usuario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(33, 37, 41));
        panelHeader.add(lblTitulo, BorderLayout.WEST);

        btnIniciarSesion = new JButton("Iniciar Sesión");
        btnIniciarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnIniciarSesion.setFocusPainted(false);
        btnIniciarSesion.addActionListener(this::btnIniciarSesionActionPerformed);
        panelHeader.add(btnIniciarSesion, BorderLayout.EAST);

        // --- FORMULARIO (Diseño adaptativo con GridBagLayout) ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        // Fila 0: Nombre de Usuario
        lblNombre = new JLabel("Nombre:");
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(lblNombre, gbc);
        
        txtNombre = new JTextField();
        txtNombre.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panelForm.add(txtNombre, gbc);

        // Fila 1: Rol
        lblRol = new JLabel("Rol:");
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(lblRol, gbc);

        cbxRol = new JComboBox<>(new String[] { "Seleccione un Rol...", "Administrador", "Usuario Estándar", "Invitado" });
        cbxRol.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panelForm.add(cbxRol, gbc);

        // Fila 2: Contraseña de Usuario
        lblPasswordUsuario = new JLabel("Contraseña de usuario:");
        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(lblPasswordUsuario, gbc);

        txtPasswordUsuario = new JPasswordField();
        txtPasswordUsuario.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panelForm.add(txtPasswordUsuario, gbc);

        // Fila 3: Contraseña de Administrador
        lblPasswordAdmin = new JLabel("Contraseña de Admin:");
        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(lblPasswordAdmin, gbc);

        txtPasswordAdmin = new JPasswordField();
        txtPasswordAdmin.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        panelForm.add(txtPasswordAdmin, gbc);

        // --- BOTONES DE ACCIÓN ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setOpaque(false);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnLimpiar.setPreferredSize(new Dimension(120, 35));
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        btnCrear = new JButton("Crear");
        btnCrear.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCrear.setBackground(new Color(40, 167, 69)); // Color verde corporativo
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setPreferredSize(new Dimension(120, 35));
        btnCrear.setFocusPainted(false);
        btnCrear.addActionListener(this::btnCrearActionPerformed);

        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCrear);

        // Montaje de paneles al contenedor principal
        panelPrincipal.add(panelHeader, BorderLayout.NORTH);
        panelPrincipal.add(panelForm, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        this.setContentPane(panelPrincipal);
        this.pack();
        this.setMinimumSize(new Dimension(500, 380));
    }

    /**
     * Evento para limpiar todos los campos del panel.
     */
    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        txtNombre.setText("");
        txtPasswordUsuario.setText("");
        txtPasswordAdmin.setText("");
        cbxRol.setSelectedIndex(0);
        txtNombre.requestFocus();
    }                                          

    /**
     * Evento para capturar los datos y proceder con la creación del perfil.
     */
        /**
     * Evento para capturar los datos y proceder con la creación del perfil.
     */
        /**
     * Evento para capturar los datos y proceder con la creación del perfil.
     */
    private void btnCrearActionPerformed(java.awt.event.ActionEvent evt) {
        String nombre = txtNombre.getText().trim();
        String passUsuario = new String(txtPasswordUsuario.getPassword());
        String passAdmin = new String(txtPasswordAdmin.getPassword());
        String rolSeleccionado = (String) cbxRol.getSelectedItem();

        // 1. Validación de campos vacíos
        if (nombre.isEmpty() || passUsuario.isEmpty() || passAdmin.isEmpty() || cbxRol.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos requeridos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Mapear el rol seleccionado al ENUM exacto de tu Base de Datos
        // 2. Mapear el rol seleccionado al ENUM exacto de tu Base de Datos (Caso sensitivo)
String rolBD = "";
if (rolSeleccionado.equalsIgnoreCase("Administrador")) {
    rolBD = "Admin"; 
} else if (rolSeleccionado.equalsIgnoreCase("Médico") || rolSeleccionado.equalsIgnoreCase("Medico")) {
    rolBD = "Medico"; // Exactamente como está en tu ENUM de MySQL
} else if (rolSeleccionado.equalsIgnoreCase("Usuario Estándar") || rolSeleccionado.equalsIgnoreCase("Recepcionista")) {
    rolBD = "Recepcionista"; // Exactamente como está en tu ENUM de MySQL
}


        // 3. Sentencia SQL con los nombres exactos de tu tabla 'usuario'
        // Incluimos 'estado' configurado por defecto en 1 (Activo)
        String sql = "INSERT INTO usuario (username, password_hash, rol, estado) VALUES (?, ?, ?, 1)";

        // 4. Conexión a la Base de Datos e Inserción
        try (java.sql.Connection con = Conexion.conectar();
             java.sql.PreparedStatement pst = con.prepareStatement(sql)) {

            if (con == null) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos. Verifique XAMPP.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Asignar los valores correspondientes a los parámetros '?'
            pst.setString(1, nombre);
            pst.setString(2, passUsuario); // Recomendable en el futuro aplicar hashing
            pst.setString(3, rolBD);        // 'Admin', 'Medico' o 'Recepcionista'

            // Ejecutar la inserción
            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "El usuario '" + nombre + "' ha sido registrado con éxito.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
                
                // Limpiar campos y volver al Login automaticamente
                btnLimpiarActionPerformed(evt);
                btnIniciarSesionActionPerformed(evt);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el usuario. Intente nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (java.sql.SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al registrar usuario en la BD", e);
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }



    /**
     * Evento para redireccionar a la pantalla de login.
     */
    private void btnIniciarSesionActionPerformed(java.awt.event.ActionEvent evt) {
        Login ventanaLogin = new Login();
        
        // 2. Hacer visible la ventana de Login
        ventanaLogin.setVisible(true);
        
        // 3. Centrar la ventana de Login en la pantalla (opcional pero recomendado)
        ventanaLogin.setLocationRelativeTo(null);
        
        // 4. Cerrar la ventana actual de Registro (NuevoUsuario)
        this.dispose();
    }

    /**
     * @param args los argumentos de la línea de comandos
     */
    public static void main(String args[]) {
        try {
            // Aplica el diseño visual nativo del sistema operativo del usuario
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error configurando el Look and Feel", ex);
        }

        /* Crea y muestra la interfaz gráfica */
        java.awt.EventQueue.invokeLater(() -> new NuevoUsuario().setVisible(true));
    }
}
