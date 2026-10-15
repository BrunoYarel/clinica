package com.mycompany.clinica.vista;

import com.mycompany.clinica.servicio.Permisos;
import static com.mycompany.clinica.servicio.Permisos.Pantalla.CITAS;
import static com.mycompany.clinica.servicio.Permisos.Pantalla.ESPECIALIDADES;
import static com.mycompany.clinica.servicio.Permisos.Pantalla.HISTORIAL;
import static com.mycompany.clinica.servicio.Permisos.Pantalla.MEDICAMENTOS;
import static com.mycompany.clinica.servicio.Permisos.Pantalla.MEDICOS;
import static com.mycompany.clinica.servicio.Permisos.Pantalla.MI_AGENDA;
import static com.mycompany.clinica.servicio.Permisos.Pantalla.PACIENTES;
import static com.mycompany.clinica.servicio.Permisos.Pantalla.REPORTES;
import static com.mycompany.clinica.servicio.Permisos.Pantalla.USUARIOS;
import com.mycompany.clinica.servicio.Sesion;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * Menú principal. Diseño:
 *   - Izquierda: barra lateral oscura con el logo, las opciones de cuenta y el usuario.
 *   - Derecha: banner de bienvenida y tarjetas con los módulos que ve cada rol.
 */
public class MenuPrincipal extends JFrame {

    private JButton btnCambiarPassword;
    private JButton btnCrearUsuario;
    private JButton btnCerrarSesion;
    private JPanel areaContenido;
    private JPanel vistaModuloActual;

    public MenuPrincipal() {
        super("Clínica - Menú principal");
        initComponents();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(crearMenuLateral(), BorderLayout.WEST);
        areaContenido = new JPanel(new CardLayout());
        areaContenido.add(crearContenido(), "INICIO");
        raiz.add(areaContenido, BorderLayout.CENTER);
        setContentPane(raiz);

        setSize(1000, 620);
        setMinimumSize(new Dimension(860, 540));
    }

    // =====================================================================
    //  BARRA LATERAL (izquierda)
    // =====================================================================
    private JPanel crearMenuLateral() {
        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setBackground(Estilo.AZUL_OSCURO);
        lateral.setPreferredSize(new Dimension(250, 0));

        // --- Arriba: logo y nombre ---
        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        marca.setOpaque(false);
        marca.setBorder(BorderFactory.createEmptyBorder(26, 14, 26, 14));
        marca.add(Estilo.logo(40, Estilo.PRIMARIO, Color.WHITE));
        marca.add(Estilo.texto("Clínica", Font.BOLD, 22, Color.WHITE));

        // --- Centro: opciones de cuenta ---
        btnCambiarPassword = Estilo.botonMenu("Cambiar contraseña");
        btnCambiarPassword.addActionListener(e -> abrirCambiarPassword());

        btnCrearUsuario = Estilo.botonMenu("Crear nuevo perfil");
        btnCrearUsuario.addActionListener(e -> abrirNuevoUsuario());

        JPanel opciones = new JPanel(new GridLayout(0, 1, 0, 6));
        opciones.setOpaque(false);
        if (Permisos.puedeVer(Permisos.Pantalla.MI_AGENDA)) {    // el médico no tiene tarjetas: su otro módulo va aquí
            opciones.add(Estilo.texto("  MÓDULOS", Font.BOLD, 11, new Color(100, 116, 139)));
            JButton btnHistorial = Estilo.botonMenu("Historial clínico");
            btnHistorial.addActionListener(e -> abrirModulo("Historial"));
            opciones.add(btnHistorial);
        }
        opciones.add(Estilo.texto("  CUENTA", Font.BOLD, 11, new Color(100, 116, 139)));
        opciones.add(btnCambiarPassword);
        if (Permisos.puedeGestionarUsuarios()) {                 // solo el administrador puede crear perfiles
            opciones.add(btnCrearUsuario);
        }

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        centro.add(opciones, BorderLayout.NORTH);     // NORTH para que los botones no se estiren

        // --- Abajo: datos del usuario y cerrar sesión ---
        String nombre = Sesion.estaActiva() ? Sesion.getUsername() : "Invitado";
        String rol = Sesion.estaActiva() ? Estilo.nombreRol(Sesion.getRol()) : "Sin sesión";

        JPanel datos = new JPanel(new GridLayout(2, 1));
        datos.setOpaque(false);
        datos.add(Estilo.texto(nombre, Font.BOLD, 14, Color.WHITE));
        datos.add(Estilo.texto(rol, Font.PLAIN, 12, new Color(148, 163, 184)));

        JPanel usuario = new JPanel(new BorderLayout(12, 0));
        usuario.setOpaque(false);
        usuario.setBorder(BorderFactory.createEmptyBorder(0, 6, 14, 6));
        usuario.add(Estilo.icono(nombre.substring(0, 1).toUpperCase(), Estilo.PRIMARIO, 42), BorderLayout.WEST);
        usuario.add(datos, BorderLayout.CENTER);

        btnCerrarSesion = Estilo.botonMenu("Cerrar sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        JPanel abajo = new JPanel(new BorderLayout());
        abajo.setOpaque(false);
        abajo.setBorder(BorderFactory.createEmptyBorder(0, 14, 18, 14));
        abajo.add(usuario, BorderLayout.NORTH);
        abajo.add(btnCerrarSesion, BorderLayout.SOUTH);

        lateral.add(marca, BorderLayout.NORTH);
        lateral.add(centro, BorderLayout.CENTER);
        lateral.add(abajo, BorderLayout.SOUTH);
        return lateral;
    }

    // =====================================================================
    //  CONTENIDO (derecha)
    // =====================================================================
    private JPanel crearContenido() {
        // Inicio del médico = Mi agenda (en lugar del banner y las tarjetas); la barra lateral se conserva
        if (Permisos.puedeVer(Permisos.Pantalla.MI_AGENDA)) {
            JPanel agenda = instanciar("AgendaPanel", JPanel.class);
            return agenda != null ? agenda : crearPanelNoDisponible("Mi agenda");
        }

        JPanel contenido = new JPanel(new BorderLayout(0, 26));
        contenido.setBackground(Estilo.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        // --- Banner de bienvenida ---
        String nombre = Sesion.estaActiva() ? Sesion.getUsername() : "Invitado";
        String rol = Sesion.estaActiva() ? Estilo.nombreRol(Sesion.getRol()) : "Sin sesión";

        JPanel banner = Estilo.panelDegradado(Estilo.PRIMARIO, Estilo.AZUL, 24);
        banner.setLayout(new GridLayout(2, 1, 0, 4));
        banner.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
        banner.add(Estilo.texto("¡Bienvenido, " + nombre + "!", Font.BOLD, 26, Color.WHITE));
        banner.add(Estilo.texto("Sesión iniciada como " + rol + ". Elige un módulo para comenzar.",
                Font.PLAIN, 14, Estilo.TEXTO_CLARO));

        // --- Tarjetas de módulos (según el rol) ---
        JPanel tarjetas = new JPanel(new GridLayout(0, 3, 20, 20));
        tarjetas.setOpaque(false);

        // Una tarjeta por cada pantalla que el rol puede ver (la matriz vive en Permisos)
        for (Permisos.Pantalla pantalla : Permisos.pantallasDelRol()) {
            String titulo = pantalla.getTitulo();
            switch (pantalla) {
                case PACIENTES:
                    tarjetas.add(crearTarjeta(titulo, "Datos de pacientes", "P", new Color(13, 148, 136)));
                    break;
                case CITAS:
                    tarjetas.add(crearTarjeta(titulo, "Agenda de citas", "C", new Color(37, 99, 235)));
                    break;
                case ESPECIALIDADES:
                    tarjetas.add(crearTarjeta(titulo, "Registro de especialidades", "E", new Color(217, 119, 6)));
                    break;
                case MEDICOS:
                    tarjetas.add(crearTarjeta(titulo, "Registro de médicos", "M", new Color(219, 39, 119)));
                    break;
                case MEDICAMENTOS:
                    tarjetas.add(crearTarjeta(titulo, "Catálogo de medicamentos", "Rx", new Color(22, 163, 74)));
                    break;
                case HISTORIAL:
                    tarjetas.add(crearTarjeta(titulo, "Historial clínico (consulta)", "H", new Color(8, 145, 178)));
                    break;
                case REPORTES:
                    tarjetas.add(crearTarjeta(titulo, "Reportes del sistema", "R", new Color(79, 70, 229)));
                    break;
                case USUARIOS:
                    tarjetas.add(crearTarjeta(titulo, "Gestión de usuarios", "U", new Color(71, 85, 105)));
                    break;
                case MI_AGENDA:
                    tarjetas.add(crearTarjeta(titulo, "Tus citas y atenciones", "A", new Color(124, 58, 237)));
                    break;
                default:
                    break;   // INICIO es este mismo menú: no lleva tarjeta
            }
        }

        JPanel envoltura = new JPanel(new BorderLayout());   // evita que las tarjetas se estiren hacia abajo
        envoltura.setOpaque(false);
        envoltura.add(tarjetas, BorderLayout.NORTH);

        JPanel zonaModulos = new JPanel(new BorderLayout(0, 14));
        zonaModulos.setOpaque(false);
        zonaModulos.add(Estilo.texto("Módulos del sistema", Font.BOLD, 18, Estilo.TEXTO), BorderLayout.NORTH);
        zonaModulos.add(envoltura, BorderLayout.CENTER);

        contenido.add(banner, BorderLayout.NORTH);
        contenido.add(zonaModulos, BorderLayout.CENTER);
        return contenido;
    }

    /** Crea una tarjeta clicable con icono, título y descripción. */
    private JPanel crearTarjeta(String titulo, String descripcion, String letra, Color color) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 14));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel filaIcono = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaIcono.setOpaque(false);
        filaIcono.add(Estilo.icono(letra, color, 46));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(Estilo.texto(titulo, Font.BOLD, 17, Estilo.TEXTO));
        textos.add(Estilo.texto(descripcion, Font.PLAIN, 12, Estilo.TEXTO_SUAVE));

        tarjeta.add(filaIcono, BorderLayout.NORTH);
        tarjeta.add(textos, BorderLayout.CENTER);

        // Clic = abrir el módulo; mouse encima = fondo turquesa muy claro
        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirModulo(titulo);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                tarjeta.setBackground(new Color(240, 253, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                tarjeta.setBackground(Color.WHITE);
            }
        });
        return tarjeta;
    }

    // =====================================================================
    //  ACCIONES DE LOS BOTONES
    // =====================================================================

    /** CLIC EN UNA TARJETA: aquí cada compañero abre la ventana de su módulo. */
    private void abrirModulo(String modulo) {
        Permisos.Pantalla pantalla = Permisos.Pantalla.deTitulo(modulo);
        if (pantalla != null && !Permisos.puedeVer(pantalla)) {
            JOptionPane.showMessageDialog(this, "No tiene permiso para acceder a este módulo.",
                    modulo, JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (modulo.equals("Pacientes")) {
            abrirModuloPacientes();
            return;
        }
        if (modulo.equals("Especialidades")) {
            abrirModuloEspecialidades();
            return;
        }
        if (modulo.equals("Médicos")) {
            abrirModuloMedicos();
            return;
        }
        if (modulo.equals("Citas")) {
            abrirModuloCitas();
            return;
        }
        if (modulo.equals("Historial")) {
            abrirModuloHistorial();
            return;
        }
        if (modulo.equals("Medicamentos")) {
            abrirModuloMedicamentos();
            return;
        }
        if (modulo.equals("Usuarios")) {
            abrirModuloEn(new FrmUsuarios());
            return;
        }
        // Reportes aún no tiene ventana: cuando exista, se conecta aquí
        // igual que los demás módulos (un método abrirModuloXxx() que llame a abrirModuloEn(...)).
        JOptionPane.showMessageDialog(this, "El módulo de " + modulo + " estará disponible pronto.",
                modulo, JOptionPane.INFORMATION_MESSAGE);
    }

    /** MÓDULO PACIENTES: se muestra dentro del área central del menú, sin abrir otra ventana. */
    private void abrirModuloPacientes() {
        mostrarModulo(new FrmPacientes(), "Gestión de pacientes");
    }

    /** MÓDULO HISTORIAL: conserva la barra lateral y el diseño del menú principal. */
    private void abrirModuloHistorial() {
        mostrarModulo(new FrmHistorial(), "Historial clínico");
    }

    /** Sustituye solo el contenido derecho; la ventana y la barra lateral permanecen visibles. */
    private void mostrarModulo(JPanel modulo, String titulo) {
        if (vistaModuloActual != null) {
            areaContenido.remove(vistaModuloActual);
        }
        JPanel vista = new JPanel(new BorderLayout(0, 18));
        vista.setBackground(Estilo.FONDO);
        vista.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel encabezado = Estilo.panelDegradado(Estilo.PRIMARIO, Estilo.AZUL, 20);
        encabezado.setLayout(new BorderLayout(12, 0));
        encabezado.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        encabezado.add(Estilo.texto(titulo, Font.BOLD, 22, Color.WHITE), BorderLayout.CENTER);
        JButton volver = Estilo.botonClaro("← Inicio");
        volver.addActionListener(e -> ((CardLayout) areaContenido.getLayout()).show(areaContenido, "INICIO"));
        encabezado.add(volver, BorderLayout.EAST);

        vista.add(encabezado, BorderLayout.NORTH);
        vista.add(modulo, BorderLayout.CENTER);
        vistaModuloActual = vista;
        areaContenido.add(vistaModuloActual, "MODULO_ACTUAL");
        ((CardLayout) areaContenido.getLayout()).show(areaContenido, "MODULO_ACTUAL");
        areaContenido.revalidate();
        areaContenido.repaint();
    }

    /** MÓDULO ESPECIALIDADES (registro de especialidades): FrmEspecialidad dentro de una ventana MDI. */
    private void abrirModuloEspecialidades() {
        abrirModuloEn(abrirMdi("FrmEspecialidad", "Especialidades", "Gestión de Especialidades",
                "REGISTRO DE ESPECIALIDADES", new Dimension(600, 500)));
    }

    /** MÓDULO MÉDICOS (registro de médicos y su especialidad): FrmMedico dentro de una ventana MDI. */
    private void abrirModuloMedicos() {
        // FrmMedico define su propio tamaño (1020x520, pensado para la tabla)
        abrirModuloEn(abrirMdi("FrmMedico", "Médicos", "Gestión de Médicos", "REGISTRO DE MÉDICOS", null));
    }

    /** MÓDULO MEDICAMENTOS (catálogo para las recetas): FrmMedicamento dentro de una ventana MDI. */
    private void abrirModuloMedicamentos() {
        abrirModuloEn(abrirMdi("FrmMedicamento", "Medicamentos", "Gestión de Medicamentos",
                "CATÁLOGO DE MEDICAMENTOS", null));
    }

    /** MÓDULO CITAS (agenda de citas, área A4): ventana FrmCitas. */
    private void abrirModuloCitas() {
        abrirModuloEn(abrirFrame("FrmCitas", "Citas"));
    }

    /**
     * Crea la pantalla de otro módulo a partir del nombre de su clase (paquete vista).
     * Equivale a new FrmXxx(), pero si esa clase todavía no existe en el proyecto (por ejemplo, en la
     * versión aislada de login y roles) devuelve null en lugar de impedir que todo el código compile.
     */
    private static <T> T instanciar(String clase, Class<T> tipo) {
        try {
            return tipo.cast(Class.forName("com.mycompany.clinica.vista." + clase)
                    .getDeclaredConstructor().newInstance());
        } catch (ClassNotFoundException e) {
            return null;   // el módulo de ese compañero no está en esta versión
        } catch (ReflectiveOperationException e) {
            // La clase existe pero su constructor falló: es un error real del módulo, no se oculta.
            throw new IllegalStateException("No se pudo crear " + clase, e);
        }
    }

    /** Abre un módulo que es un JFrame (FrmPrincipal, FrmCitas...). Devuelve null si no está disponible. */
    private JFrame abrirFrame(String clase, String modulo) {
        JFrame ventana = instanciar(clase, JFrame.class);
        if (ventana == null) {
            avisarNoDisponible(modulo);
        }
        return ventana;
    }

    /** Abre un módulo que es un JInternalFrame dentro de una ventana MDI. Devuelve null si no está disponible. */
    private JFrame abrirMdi(String clase, String modulo, String tituloVentana, String tituloBorde, Dimension tamano) {
        JInternalFrame formulario = instanciar(clase, JInternalFrame.class);
        if (formulario == null) {
            avisarNoDisponible(modulo);
            return null;
        }
        return VentanaMdi.abrir(tituloVentana, tituloBorde, formulario, tamano);
    }

    private void avisarNoDisponible(String modulo) {
        JOptionPane.showMessageDialog(this, "El módulo de " + modulo + " no está incluido en esta versión del proyecto.",
                modulo, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Pantalla de relleno para el inicio del médico cuando el módulo Mi agenda no está en el proyecto. */
    private static JPanel crearPanelNoDisponible(String modulo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Estilo.FONDO);
        JLabel texto = new JLabel("El módulo de " + modulo + " no está incluido en esta versión del proyecto.");
        texto.setForeground(Color.GRAY);
        panel.add(texto);
        return panel;
    }

    /**
     * Muestra la ventana de un módulo y oculta el menú; al cerrar el módulo, el menú reaparece
     * con la misma sesión. Las ventanas de los módulos se diseñaron para ejecutarse solas, así que
     * al cerrarse terminaban toda la aplicación (EXIT_ON_CLOSE). Aquí, al abrirlas desde el menú,
     * se cambia únicamente ese comportamiento para que cierren solo esa ventana.
     */
    private void abrirModuloEn(JFrame modulo) {
        if (modulo == null) {
            return;   // el módulo no está en el proyecto: ya se avisó al usuario
        }
        modulo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        modulo.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                MenuPrincipal.this.setVisible(true);   // vuelve el menú principal
            }
        });
        modulo.setVisible(true);
        setVisible(false);
    }

    /** BOTÓN CAMBIAR CONTRASEÑA: abre el diálogo (modal, bloquea el menú hasta cerrarlo). */
    private void abrirCambiarPassword() {
        new CambiarPassword(this).setVisible(true);
    }

    /** BOTÓN CREAR NUEVO PERFIL (solo Admin): abre el registro. */
    private void abrirNuevoUsuario() {
        new NuevoUsuario().setVisible(true);
        dispose();
    }

    /** BOTÓN CERRAR SESIÓN: borra la sesión y vuelve al login. */
    private void cerrarSesion() {
        Sesion.cerrar();
        new Login().setVisible(true);
        dispose();
    }
}