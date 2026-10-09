package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
    private void btnCrearActionPerformed(java.awt.event.ActionEvent evt) {
        String nombre = txtNombre.getText().trim();
        String passUsuario = new String(txtPasswordUsuario.getPassword());
        String passAdmin = new String(txtPasswordAdmin.getPassword());
        String rolSeleccionado = (String) cbxRol.getSelectedItem();

        // Validación de campos vacíos
        if (nombre.isEmpty() || passUsuario.isEmpty() || passAdmin.isEmpty() || cbxRol.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos requeridos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ESPACIO DE LOGICA: Aquí mandas las variables a tu controlador/base de datos
        logger.info("Intento de registro de usuario: " + nombre + " con Rol: " + rolSeleccionado);
        
        JOptionPane.showMessageDialog(this, "El usuario '" + nombre + "' ha sido creado con éxito.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
        btnLimpiarActionPerformed(evt);
    }

    /**
     * Evento para redireccionar a la pantalla de login.
     */
    private void btnIniciarSesionActionPerformed(java.awt.event.ActionEvent evt) {
        // Aquí puedes instanciar tu ventana de Login
        // Ejemplo: new Login().setVisible(true);
        // this.dispose();
        JOptionPane.showMessageDialog(this, "Redireccionando al panel de Inicio de Sesión...", "Navegación", JOptionPane.INFORMATION_MESSAGE);
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
