package com.mycompany.clinica.vista;

import com.mycompany.clinica.dao.MedicoDAO;
import com.mycompany.clinica.modelo.Medico;
import com.mycompany.clinica.servicio.Permisos;
import com.mycompany.clinica.servicio.Sesion;
import com.mycompany.clinica.servicio.UsuarioService;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Registro de nuevos usuarios (crear perfil). Para crear uno hay que escribir
 * la contraseña de un administrador, que se verifica contra la base de datos.
 *
 * @author PC-03
 */
public class NuevoUsuario extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(NuevoUsuario.class.getName());

    // Opciones del combo y su valor exacto en el ENUM de la base de datos (mismo orden)
    private static final String[] ROLES_MOSTRADOS = {"Seleccione un rol...", "Administrador", "Recepcionista", "Médico"};
    private static final String[] ROLES_BD = {"", Sesion.ADMIN, Sesion.RECEPCIONISTA, Sesion.MEDICO};
    private static final String OPCION_MEDICO = "Seleccione un médico...";

    private JTextField txtNombre;
    private JComboBox<String> cbxRol;
    private JComboBox<String> cbxMedico;
    private JPanel panelMedico;
    // id_medico de cada médico del combo, en el mismo orden (el índice del combo es posición + 1, por "Seleccione...")
    private final List<Integer> idsMedicos = new ArrayList<>();
    private JPasswordField txtPasswordUsuario;
    private JPasswordField txtPasswordConfirmar;
    private JPasswordField txtPasswordAdmin;
    private JLabel lblError;
    private JButton btnCrear;
    private JButton btnLimpiar;
    private JButton btnVolver;

    public NuevoUsuario() {
        super("Registro de Usuarios");
        initComponents();
        // Al cerrar con la X también se vuelve a la pantalla anterior (no se cierra todo el programa)
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                volver();
            }
        });
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // ---------- CABECERA ----------
        JPanel titulos = new JPanel(new GridLayout(2, 1, 0, 3));
        titulos.setOpaque(false);
        titulos.add(Estilo.texto("Crear nuevo perfil", Font.BOLD, 24, Color.WHITE));
        titulos.add(Estilo.texto("Registra a un usuario del sistema y asígnale un rol",
                Font.PLAIN, 13, Estilo.TEXTO_CLARO));

        // Si un admin ya inició sesión vuelve al menú; si no, al login
        btnVolver = Estilo.botonClaro(Sesion.estaActiva() ? "Volver al menú" : "Volver al login");
        btnVolver.addActionListener(e -> volver());
        JPanel envoltura = new JPanel(new GridBagLayout());   // centra el botón sin estirarlo
        envoltura.setOpaque(false);
        envoltura.add(btnVolver);

        JPanel cabecera = Estilo.panelDegradado(Estilo.PRIMARIO_OSCURO, Estilo.AZUL, 0);
        cabecera.setLayout(new BorderLayout(14, 0));
        cabecera.setBorder(BorderFactory.createEmptyBorder(22, 30, 22, 30));
        cabecera.add(Estilo.logo(52, Color.WHITE, Estilo.PRIMARIO_OSCURO), BorderLayout.WEST);
        cabecera.add(titulos, BorderLayout.CENTER);
        cabecera.add(envoltura, BorderLayout.EAST);

        // ---------- FORMULARIO ----------
        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setBackground(Color.WHITE);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(26, 30, 26, 30));

        txtNombre = Estilo.campoTexto();
        cbxRol = Estilo.combo(ROLES_MOSTRADOS);
        cbxRol.addActionListener(e -> cambiarRol());

        // Campo "Médico": oculto hasta que se elige el rol Médico (va dentro de la fila 0, así no se renumeran las demás)
        cbxMedico = Estilo.combo(new String[]{OPCION_MEDICO});
        panelMedico = new JPanel(new BorderLayout());
        panelMedico.setOpaque(false);
        panelMedico.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        panelMedico.add(Estilo.campo("Médico", cbxMedico), BorderLayout.CENTER);
        panelMedico.setVisible(false);

        JPanel filaRol = new JPanel(new BorderLayout());
        filaRol.setOpaque(false);
        filaRol.add(dosColumnas(
                Estilo.campo("Nombre de usuario", txtNombre),
                Estilo.campo("Rol", cbxRol)), BorderLayout.CENTER);
        filaRol.add(panelMedico, BorderLayout.SOUTH);
        Estilo.agregar(cuerpo, filaRol, 0, 0);

        txtPasswordUsuario = Estilo.campoPassword();
        txtPasswordConfirmar = Estilo.campoPassword();
        Estilo.agregar(cuerpo, dosColumnas(
                Estilo.campo("Contraseña", txtPasswordUsuario),
                Estilo.campo("Confirmar contraseña", txtPasswordConfirmar)), 1, 16);

        // Sección de autorización del administrador
        Estilo.agregar(cuerpo, Estilo.linea(), 2, 22);
        Estilo.agregar(cuerpo, Estilo.texto("Autorización del administrador", Font.BOLD, 15, Estilo.TEXTO), 3, 18);
        Estilo.agregar(cuerpo, Estilo.texto("Para crear un perfil se necesita la contraseña de un administrador.",
                Font.PLAIN, 12, Estilo.TEXTO_SUAVE), 4, 2);

        txtPasswordAdmin = Estilo.campoPassword();
        Estilo.agregar(cuerpo, Estilo.campo("Contraseña de administrador", txtPasswordAdmin), 5, 12);

        Estilo.agregar(cuerpo, Estilo.verPassword(txtPasswordUsuario, txtPasswordConfirmar, txtPasswordAdmin), 6, 10);

        lblError = Estilo.textoError();
        Estilo.agregar(cuerpo, lblError, 7, 6);

        // ---------- BOTONES ----------
        btnLimpiar = Estilo.botonSecundario("Limpiar");
        btnLimpiar.addActionListener(e -> limpiar());

        btnCrear = Estilo.botonPrimario("Crear perfil");
        btnCrear.addActionListener(e -> crear());

        JPanel botones = new JPanel(new GridLayout(1, 2, 14, 0));
        botones.setOpaque(false);
        botones.add(btnLimpiar);
        botones.add(btnCrear);
        Estilo.agregar(cuerpo, botones, 8, 8);

        // ---------- ARMADO ----------
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(cabecera, BorderLayout.NORTH);
        raiz.add(cuerpo, BorderLayout.CENTER);
        setContentPane(raiz);

        getRootPane().setDefaultButton(btnCrear);
        setResizable(false);
        pack();
    }

    /** Pone dos componentes lado a lado, cada uno con la mitad del ancho. */
    private JPanel dosColumnas(Component izquierda, Component derecha) {
        JPanel fila = new JPanel(new GridLayout(1, 2, 16, 0));
        fila.setOpaque(false);
        fila.add(izquierda);
        fila.add(derecha);
        return fila;
    }

    /** true si el rol elegido en el combo es Médico. */
    private boolean esRolMedico() {
        int indice = cbxRol.getSelectedIndex();
        return indice >= 0 && Sesion.MEDICO.equals(ROLES_BD[indice]);
    }

    /** Al cambiar el rol: con Médico se carga y muestra el combo de médico; con otro rol se oculta. */
    private void cambiarRol() {
        if (esRolMedico()) {
            lblError.setText(" ");
            cargarMedicos();
            panelMedico.setVisible(true);
            pack();
        } else if (panelMedico.isVisible()) {
            reiniciarCampoMedico();
            lblError.setText(" ");
        }
    }

    /** Llena el combo de médico con los médicos registrados: "Apellidos, Nombres — Especialidad (CMP)". */
    private void cargarMedicos() {
        List<String> etiquetas = new ArrayList<>();
        etiquetas.add(OPCION_MEDICO);
        idsMedicos.clear();
        try {
            List<Medico> medicos = new MedicoDAO().listarMedicos();
            for (Medico m : medicos) {
                etiquetas.add(m.getApellidos() + ", " + m.getNombres()
                        + " — " + m.getEspecialidad() + " (" + m.getCmp() + ")");
                idsMedicos.add(m.getIdMedico());
            }
            if (medicos.isEmpty()) {
                lblError.setText("No hay médicos registrados. Regístrelo primero en el módulo Médicos.");
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al cargar los médicos", e);
            lblError.setText("No se pudieron cargar los médicos: " + e.getMessage());
        }
        cbxMedico.setModel(new DefaultComboBoxModel<>(etiquetas.toArray(new String[0])));
    }

    /** Deja el combo de médico en su estado inicial (solo "Seleccione...") y lo oculta. */
    private void reiniciarCampoMedico() {
        idsMedicos.clear();
        cbxMedico.setModel(new DefaultComboBoxModel<>(new String[]{OPCION_MEDICO}));
        if (panelMedico.isVisible()) {
            panelMedico.setVisible(false);
            pack();
        }
    }

    /** BOTÓN LIMPIAR: solo vacía los campos (no usa la base de datos). */
    private void limpiar() {
        txtNombre.setText("");
        txtPasswordUsuario.setText("");
        txtPasswordConfirmar.setText("");
        txtPasswordAdmin.setText("");
        cbxRol.setSelectedIndex(0);
        reiniciarCampoMedico();
        lblError.setText(" ");
        txtNombre.requestFocus();
    }

    /** BOTÓN CREAR PERFIL: valida los datos, verifica al admin y guarda el usuario en la base de datos. */
    private void crear() {
        if (!Permisos.puedeGestionarUsuarios()) {
            lblError.setText("No tiene permiso para crear usuarios.");
            return;
        }
        lblError.setText(" ");
        String nombre = txtNombre.getText().trim();
        String passUsuario = new String(txtPasswordUsuario.getPassword());
        String passConfirmar = new String(txtPasswordConfirmar.getPassword());
        String passAdmin = new String(txtPasswordAdmin.getPassword());
        int indiceRol = cbxRol.getSelectedIndex();

        // 1. Validaciones (antes de tocar la base de datos)
        if (nombre.isEmpty() || passUsuario.isEmpty() || passConfirmar.isEmpty()
                || passAdmin.isEmpty() || indiceRol == 0) {
            lblError.setText("Por favor, complete todos los campos requeridos.");
            return;
        }
        if (nombre.length() < UsuarioService.MIN_USERNAME || nombre.length() > UsuarioService.MAX_USERNAME) {
            lblError.setText("El nombre de usuario debe tener entre " + UsuarioService.MIN_USERNAME
                    + " y " + UsuarioService.MAX_USERNAME + " caracteres.");
            return;
        }
        if (passUsuario.length() < UsuarioService.MIN_PASSWORD) {
            lblError.setText("La contraseña debe tener al menos " + UsuarioService.MIN_PASSWORD + " caracteres.");
            return;
        }
        if (!passUsuario.equals(passConfirmar)) {
            lblError.setText("Las contraseñas no coinciden.");
            return;
        }
        int idMedico = 0;   // solo se llena cuando el rol es Médico
        if (esRolMedico()) {
            int indiceMedico = cbxMedico.getSelectedIndex();
            if (indiceMedico <= 0 || indiceMedico > idsMedicos.size()) {
                lblError.setText("Seleccione el médico al que pertenece este usuario.");
                return;
            }
            idMedico = idsMedicos.get(indiceMedico - 1);
        }

        // 2. Base de datos: verificar al admin y guardar
        try {
            if (!UsuarioService.verificarPasswordAdmin(passAdmin)) {
                lblError.setText("La contraseña de administrador es incorrecta.");
                txtPasswordAdmin.setText("");
                txtPasswordAdmin.requestFocus();
                return;
            }

            if (idMedico > 0 && UsuarioService.medicoYaTieneUsuario(idMedico)) {
                lblError.setText("Ese médico ya tiene un usuario asignado.");
                return;
            }

            if (UsuarioService.crearUsuario(nombre, passUsuario, ROLES_BD[indiceRol], idMedico)) {
                JOptionPane.showMessageDialog(this, "El usuario '" + nombre + "' ha sido registrado con éxito.",
                        "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                limpiar();
                volver();
            } else {
                lblError.setText("No se pudo registrar el usuario. Intente nuevamente.");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            lblError.setText("El usuario '" + nombre + "' ya existe. Elija otro nombre.");
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al registrar usuario en la BD", e);
            JOptionPane.showMessageDialog(this, "Error de base de datos:\n" + e.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** BOTÓN VOLVER: regresa al menú (si hay sesión) o al login. */
    private void volver() {
        if (Sesion.estaActiva()) {
            new MenuPrincipal().setVisible(true);
        } else {
            new Login().setVisible(true);
        }
        dispose();
    }
}