package com.mycompany.clinica.vista;

import com.mycompany.clinica.dao.MedicamentoDAO;
import com.mycompany.clinica.dao.MedicoDAO;
import com.mycompany.clinica.modelo.CitaAgenda;
import com.mycompany.clinica.modelo.Medicamento;
import com.mycompany.clinica.modelo.Medico;
import com.mycompany.clinica.servicio.AtencionService;
import com.mycompany.clinica.servicio.ReglaNegocioException;
import com.mycompany.clinica.servicio.Sesion;
import com.mycompany.clinica.utilidades.ModeloTabla;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * MI AGENDA: pantalla de inicio del médico. Muestra solo SUS citas de la fecha elegida y permite
 * atender la cita seleccionada (síntomas, diagnóstico, observaciones y receta) con DlgAtencion.
 *
 * Es un JPanel que MenuPrincipal coloca en el lugar del banner y las tarjetas cuando el rol es Médico,
 * así conserva la barra lateral (cambiar contraseña, cerrar sesión...) igual que los demás roles.
 */
public class AgendaPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(AgendaPanel.class.getName());

    private static final Locale ES = Locale.forLanguageTag("es-PE");
    private static final DateTimeFormatter FECHA_LARGA = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", ES);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] COLUMNAS = {"Hora", "Paciente", "DNI", "Motivo", "Estado"};
    private static final int COL_ESTADO = 4;
    private static final Border MARGEN_CELDA = BorderFactory.createEmptyBorder(0, 10, 0, 10);

    private final AtencionService servicio;
    private final MedicamentoDAO medicamentoDAO;
    private final String etiquetaMedico;

    private final List<CitaAgenda> citas = new ArrayList<>();   // lo que muestra la tabla, en el mismo orden
    private final ModeloTabla modelo = new ModeloTabla(COLUMNAS);

    private JSpinner spnFecha;
    private JLabel lblFechaLarga;
    private JLabel lblResumen;
    private JTable tblCitas;

    public AgendaPanel() {
        this(new AtencionService(), new MedicamentoDAO(), describirMedicoDeLaSesion());
    }

    /** Constructor con las dependencias a la vista (sirve para probar la pantalla sin base de datos). */
    AgendaPanel(AtencionService servicio, MedicamentoDAO medicamentoDAO, String etiquetaMedico) {
        this.servicio = servicio;
        this.medicamentoDAO = medicamentoDAO;
        this.etiquetaMedico = etiquetaMedico;
        initComponents();
        // Se carga cuando la ventana ya se pintó, para que un error de base de datos no bloquee el menú
        SwingUtilities.invokeLater(this::cargarAgenda);
    }

    // =====================================================================
    //  ARMADO DE LA PANTALLA
    // =====================================================================
    private void initComponents() {
        setLayout(new BorderLayout(0, 18));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        add(crearBanner(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        cuerpo.add(crearCabeceraFecha(), BorderLayout.NORTH);
        cuerpo.add(crearTabla(), BorderLayout.CENTER);
        cuerpo.add(crearBotones(), BorderLayout.SOUTH);
        add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel crearBanner() {
        JPanel banner = Estilo.panelDegradado(Estilo.PRIMARIO, Estilo.AZUL, 24);
        banner.setLayout(new GridLayout(2, 1, 0, 4));
        banner.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        banner.add(Estilo.texto("Mi agenda", Font.BOLD, 26, Color.WHITE));
        banner.add(Estilo.texto(etiquetaMedico, Font.PLAIN, 14, Estilo.TEXTO_CLARO));
        return banner;
    }

    /** Fila de controles de fecha y, debajo, la fecha en letras con el resumen del día. */
    private JPanel crearCabeceraFecha() {
        JButton btnAnterior = Estilo.botonSecundario("‹");
        btnAnterior.addActionListener(e -> moverFecha(-1));
        JButton btnSiguiente = Estilo.botonSecundario("›");
        btnSiguiente.addActionListener(e -> moverFecha(1));
        JButton btnHoy = Estilo.botonSecundario("Hoy");
        btnHoy.addActionListener(e -> irAFecha(LocalDate.now()));

        spnFecha = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy");
        editor.getTextField().setFont(Estilo.fuente(Font.PLAIN, 14));
        spnFecha.setEditor(editor);
        spnFecha.setPreferredSize(new Dimension(140, 40));
        spnFecha.addChangeListener(e -> cargarAgenda());

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controles.setOpaque(false);
        controles.add(btnAnterior);
        controles.add(spnFecha);
        controles.add(btnSiguiente);
        controles.add(btnHoy);

        lblFechaLarga = Estilo.texto(" ", Font.BOLD, 15, Estilo.TEXTO);
        lblResumen = Estilo.texto(" ", Font.PLAIN, 13, Estilo.TEXTO_SUAVE);
        JPanel info = new JPanel(new BorderLayout(16, 0));
        info.setOpaque(false);
        info.add(lblFechaLarga, BorderLayout.WEST);
        info.add(lblResumen, BorderLayout.EAST);

        JPanel cabecera = new JPanel(new GridLayout(2, 1, 0, 10));
        cabecera.setOpaque(false);
        cabecera.add(controles);
        cabecera.add(info);
        return cabecera;
    }

    private JScrollPane crearTabla() {
        tblCitas = new JTable(modelo);
        estilizarTabla(tblCitas);
        tblCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCitas.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblCitas.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblCitas.getColumnModel().getColumn(2).setPreferredWidth(95);
        tblCitas.getColumnModel().getColumn(3).setPreferredWidth(195);
        tblCitas.getColumnModel().getColumn(COL_ESTADO).setPreferredWidth(120);
        tblCitas.getColumnModel().getColumn(COL_ESTADO).setCellRenderer(new RenderEstado());

        // Doble clic en una cita = atenderla
        tblCitas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    atenderSeleccionada();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblCitas);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel crearBotones() {
        JButton btnActualizar = Estilo.botonSecundario("Actualizar");
        btnActualizar.addActionListener(e -> cargarAgenda());

        JButton btnAtender = Estilo.botonPrimario("Atender cita seleccionada");
        btnAtender.addActionListener(e -> atenderSeleccionada());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        botones.add(btnActualizar);
        botones.add(btnAtender);
        return botones;
    }

    /** Mismo estilo de tabla que FrmCitas (encabezado oscuro, filas altas, sin líneas verticales). */
    static void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setForeground(new Color(51, 65, 85));
        tabla.setRowHeight(32);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(226, 232, 240));
        tabla.setSelectionBackground(new Color(232, 240, 254));
        tabla.setSelectionForeground(new Color(15, 23, 42));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Un poco de aire a los lados del texto de cada celda (y del encabezado, para que queden alineados)
        tabla.setDefaultRenderer(Object.class, new RenderCelda());
        if (tabla.getTableHeader().getDefaultRenderer() instanceof JLabel etiquetaEncabezado) {
            etiquetaEncabezado.setBorder(MARGEN_CELDA);
        }

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        encabezado.setBackground(new Color(15, 23, 42));
        encabezado.setForeground(Color.WHITE);
        encabezado.setPreferredSize(new Dimension(0, 36));
        encabezado.setReorderingAllowed(false);
    }

    // =====================================================================
    //  FECHA
    // =====================================================================
    private LocalDate fechaSeleccionada() {
        Date elegida = (Date) spnFecha.getValue();
        return elegida.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void irAFecha(LocalDate fecha) {
        spnFecha.setValue(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private void moverFecha(int dias) {
        irAFecha(fechaSeleccionada().plusDays(dias));
    }

    // =====================================================================
    //  DATOS
    // =====================================================================

    /** Carga en la tabla las citas del médico de la sesión para la fecha elegida. */
    public void cargarAgenda() {
        LocalDate fecha = fechaSeleccionada();
        String texto = FECHA_LARGA.format(fecha);
        lblFechaLarga.setText(texto.substring(0, 1).toUpperCase(ES) + texto.substring(1));

        modelo.setRowCount(0);
        citas.clear();
        try {
            citas.addAll(servicio.obtenerAgenda(fecha));
            for (CitaAgenda c : citas) {
                modelo.addRow(new Object[]{
                        c.getHora().format(HORA),
                        c.getPaciente(),
                        c.getDocumento(),
                        c.getMotivo(),
                        c.getEstado()
                });
            }
        } catch (ReglaNegocioException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Mi agenda", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al cargar la agenda del médico", e);
            JOptionPane.showMessageDialog(this, "No se pudo cargar la agenda:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
        lblResumen.setText(resumen());
    }

    /** "3 citas · 2 por atender" o "Sin citas para este día". */
    private String resumen() {
        if (citas.isEmpty()) {
            return "Sin citas para este día";
        }
        int porAtender = 0;
        for (CitaAgenda c : citas) {
            if (c.puedeAtenderse()) {
                porAtender++;
            }
        }
        return citas.size() + (citas.size() == 1 ? " cita" : " citas") + " · " + porAtender + " por atender";
    }

    // =====================================================================
    //  ATENDER UNA CITA
    // =====================================================================
    private void atenderSeleccionada() {
        int fila = tblCitas.getSelectedRow();
        if (fila < 0 || fila >= citas.size()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una cita de la tabla.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CitaAgenda cita = citas.get(fila);

        String noAtendible = CitaAgenda.motivoNoAtendible(cita.getEstado());
        if (noAtendible != null) {
            JOptionPane.showMessageDialog(this, noAtendible, "No se puede atender", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Medicamento> catalogo;
        try {
            catalogo = medicamentoDAO.listarMedicamentos();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al cargar el catálogo de medicamentos", e);
            JOptionPane.showMessageDialog(this, "No se pudo cargar el catálogo de medicamentos:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DlgAtencion dialogo = new DlgAtencion(SwingUtilities.getWindowAncestor(this), cita, catalogo, servicio);
        dialogo.setVisible(true);   // modal: sigue aquí cuando se cierra
        if (dialogo.seGuardo()) {
            cargarAgenda();
        }
    }

    /** "Dr(a). Nombres Apellidos · Especialidad" del médico de la sesión (o el usuario si no se encuentra). */
    private static String describirMedicoDeLaSesion() {
        try {
            for (Medico m : new MedicoDAO().listarMedicos()) {
                if (m.getIdMedico() == Sesion.getIdMedico()) {
                    return "Dr(a). " + m.getNombres() + " " + m.getApellidos() + " · " + m.getEspecialidad();
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "No se pudo leer el nombre del médico de la sesión", e);
        }
        return "Sesión: " + Sesion.getUsername();
    }

    // =====================================================================
    //  COLOR DEL ESTADO
    // =====================================================================
    /** Celda normal con margen a los lados (DefaultTableCellRenderer cambia el borde en cada pintado, por eso se fija aquí). */
    private static class RenderCelda extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                       boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
            setBorder(MARGEN_CELDA);
            return this;
        }
    }

    private static class RenderEstado extends RenderCelda {

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                       boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
            setFont(Estilo.fuente(Font.BOLD, 13));
            if (!seleccionada) {
                setForeground(colorDe(valor == null ? "" : valor.toString()));
            }
            return this;
        }

        private static Color colorDe(String estado) {
            switch (estado) {
                case "Programada":
                    return Estilo.PRIMARIO;
                case "En Espera":
                    return new Color(37, 99, 235);
                case "Pendiente":
                    return new Color(217, 119, 6);
                case "Atendida":
                    return new Color(22, 163, 74);
                case "Cancelada":
                    return Estilo.ERROR;
                default:
                    return Estilo.TEXTO;
            }
        }
    }
}
