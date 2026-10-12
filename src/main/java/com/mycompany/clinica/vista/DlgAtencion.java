package com.mycompany.clinica.vista;

import com.mycompany.clinica.modelo.Atencion;
import com.mycompany.clinica.modelo.CitaAgenda;
import com.mycompany.clinica.modelo.DetalleReceta;
import com.mycompany.clinica.modelo.Medicamento;
import com.mycompany.clinica.servicio.AtencionService;
import com.mycompany.clinica.servicio.ReglaNegocioException;
import com.mycompany.clinica.utilidades.ModeloTabla;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 * ATENDER UNA CITA: el médico registra síntomas, diagnóstico y observaciones y, si lo necesita,
 * arma una receta con uno o más medicamentos del catálogo. Al guardar se llama a
 * AtencionService.registrarAtencion(), que lo guarda todo junto y deja la cita como Atendida.
 *
 * Se abre desde AgendaPanel. Si algo falla, el mensaje aparece aquí mismo y la ventana sigue abierta
 * para que no se pierda lo escrito.
 */
public class DlgAtencion extends JDialog {

    private static final Logger logger = Logger.getLogger(DlgAtencion.class.getName());

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    // Sugerencias de los combos editables (el médico también puede escribir otra cosa)
    private static final String[] UNIDADES = {"Tableta", "Cápsula", "ml", "mg", "Gotas", "Ampolla", "Sobre", "Unidad"};
    private static final String[] FRECUENCIAS = {"Cada 4 horas", "Cada 6 horas", "Cada 8 horas",
        "Cada 12 horas", "Cada 24 horas", "Dosis única"};

    // Límites de las columnas de DETALLE_RECETA
    private static final int MAX_UNIDAD = 20;
    private static final int MAX_FRECUENCIA = 100;
    private static final int MAX_DURACION = 50;
    private static final BigDecimal MAX_DOSIS = new BigDecimal("9999.99");   // DECIMAL(6,2)

    private final CitaAgenda cita;
    private final AtencionService servicio;
    private final List<Medicamento> catalogo;
    private final List<DetalleReceta> detalles = new ArrayList<>();   // lo que muestra la tabla de la receta
    private boolean guardada = false;

    private JTextArea txtSintomas;
    private JTextArea txtDiagnostico;
    private JTextArea txtObservaciones;
    private JTextArea txtIndicaciones;
    private JComboBox<Medicamento> cboMedicamento;
    private JTextField txtDosis;
    private JComboBox<String> cboUnidad;
    private JComboBox<String> cboFrecuencia;
    private JTextField txtDuracion;
    private JButton btnAgregar;
    private JTable tblReceta;
    private final ModeloTabla modeloReceta = new ModeloTabla("Medicamento", "Dosis", "Frecuencia", "Duración");
    private JLabel lblError;

    public DlgAtencion(Window padre, CitaAgenda cita, List<Medicamento> catalogo, AtencionService servicio) {
        super(padre, "Atender cita", ModalityType.APPLICATION_MODAL);
        this.cita = cita;
        this.catalogo = catalogo;
        this.servicio = servicio;
        initComponents();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // Esc = cancelar
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtSintomas.requestFocusInWindow();
            }
        });

        // Ancho fijo; el alto se recorta si la pantalla es pequeña (el cuerpo tiene scroll)
        int alto = Math.min(780, Toolkit.getDefaultToolkit().getScreenSize().height - 80);
        setSize(880, alto);
        setMinimumSize(new Dimension(760, 420));
        setLocationRelativeTo(padre);
    }

    /** true si la atención se guardó (AgendaPanel recarga la tabla). */
    public boolean seGuardo() {
        return guardada;
    }

    // =====================================================================
    //  ARMADO DE LA VENTANA
    // =====================================================================
    private void initComponents() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Color.WHITE);
        raiz.add(crearCabecera(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(crearCuerpo());
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        raiz.add(scroll, BorderLayout.CENTER);

        raiz.add(crearBarraBotones(), BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    private JPanel crearCabecera() {
        String paciente = cita.getPaciente() + "  ·  DNI " + cita.getDocumento()
                + "  ·  " + cita.getFecha().format(FECHA) + " " + cita.getHora().format(HORA);
        String motivo = "Motivo: " + (cita.getMotivo() == null || cita.getMotivo().isBlank() ? "—" : cita.getMotivo());

        JPanel textos = new JPanel(new GridLayout(3, 1, 0, 3));
        textos.setOpaque(false);
        textos.add(Estilo.texto("Atender cita", Font.BOLD, 22, Color.WHITE));
        textos.add(Estilo.texto(paciente, Font.BOLD, 13, Color.WHITE));
        textos.add(Estilo.texto(motivo, Font.PLAIN, 13, Estilo.TEXTO_CLARO));

        JPanel cabecera = Estilo.panelDegradado(Estilo.PRIMARIO_OSCURO, Estilo.AZUL, 0);
        cabecera.setLayout(new BorderLayout(14, 0));
        cabecera.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
        cabecera.add(Estilo.logo(46, Color.WHITE, Estilo.PRIMARIO_OSCURO), BorderLayout.WEST);
        cabecera.add(textos, BorderLayout.CENTER);
        return cabecera;
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setBackground(Color.WHITE);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        txtSintomas = new JTextArea();
        txtDiagnostico = new JTextArea();
        txtObservaciones = new JTextArea();
        txtIndicaciones = new JTextArea();

        int fila = 0;
        Estilo.agregar(cuerpo, Estilo.texto("Atención", Font.BOLD, 15, Estilo.TEXTO), fila++, 0);
        Estilo.agregar(cuerpo, Estilo.campo("Síntomas *", areaTexto(txtSintomas, 3)), fila++, 10);
        Estilo.agregar(cuerpo, Estilo.campo("Diagnóstico *", areaTexto(txtDiagnostico, 3)), fila++, 14);
        Estilo.agregar(cuerpo, Estilo.campo("Observaciones", areaTexto(txtObservaciones, 2)), fila++, 14);

        Estilo.agregar(cuerpo, Estilo.linea(), fila++, 22);
        Estilo.agregar(cuerpo, Estilo.texto("Receta (opcional)", Font.BOLD, 15, Estilo.TEXTO), fila++, 18);
        Estilo.agregar(cuerpo, Estilo.texto("Agregue uno o más medicamentos. Si no agrega ninguno, no se emite receta.",
                Font.PLAIN, 12, Estilo.TEXTO_SUAVE), fila++, 2);

        Estilo.agregar(cuerpo, crearFilaMedicamento(), fila++, 12);
        Estilo.agregar(cuerpo, crearTablaReceta(), fila++, 12);
        Estilo.agregar(cuerpo, crearBotonQuitar(), fila++, 8);
        Estilo.agregar(cuerpo, Estilo.campo("Indicaciones generales de la receta", areaTexto(txtIndicaciones, 2)),
                fila++, 14);

        lblError = Estilo.textoError();
        Estilo.agregar(cuerpo, lblError, fila, 12);
        return cuerpo;
    }

    /** Fila para armar una línea de la receta: medicamento, dosis, unidad, frecuencia, duración y botón Agregar. */
    private JPanel crearFilaMedicamento() {
        cboMedicamento = new JComboBox<>(catalogo.toArray(new Medicamento[0]));
        estilizarCombo(cboMedicamento);

        // columnas = 0: con columnas definidas JTextField ignora el ancho que se le fije con setPreferredSize
        txtDosis = Estilo.campoTexto();
        txtDosis.setColumns(0);
        txtDosis.setPreferredSize(new Dimension(70, 40));

        cboUnidad = Estilo.combo(UNIDADES);
        cboUnidad.setEditable(true);
        cboUnidad.setPreferredSize(new Dimension(105, 40));

        cboFrecuencia = Estilo.combo(FRECUENCIAS);
        cboFrecuencia.setEditable(true);
        cboFrecuencia.setPreferredSize(new Dimension(140, 40));

        txtDuracion = Estilo.campoTexto();
        txtDuracion.setColumns(0);
        txtDuracion.setPreferredSize(new Dimension(100, 40));

        btnAgregar = Estilo.botonPrimario("Agregar");
        btnAgregar.addActionListener(e -> agregarMedicamento());

        boolean hayCatalogo = !catalogo.isEmpty();
        if (!hayCatalogo) {
            for (Component c : new Component[]{cboMedicamento, txtDosis, cboUnidad, cboFrecuencia, txtDuracion, btnAgregar}) {
                c.setEnabled(false);
            }
        }

        JPanel fila = new JPanel(new GridBagLayout());
        fila.setOpaque(false);
        columna(fila, Estilo.campo("Medicamento", cboMedicamento), 0, 1.0);   // este se estira
        columna(fila, Estilo.campo("Dosis", txtDosis), 1, 0);
        columna(fila, Estilo.campo("Unidad", cboUnidad), 2, 0);
        columna(fila, Estilo.campo("Frecuencia", cboFrecuencia), 3, 0);
        columna(fila, Estilo.campo("Duración", txtDuracion), 4, 0);
        columna(fila, Estilo.campo(" ", btnAgregar), 5, 0);

        if (hayCatalogo) {
            return fila;
        }
        // Sin catálogo no se puede recetar: se avisa en lugar de dejar la fila muerta sin explicación
        JPanel conAviso = new JPanel(new BorderLayout(0, 8));
        conAviso.setOpaque(false);
        conAviso.add(fila, BorderLayout.CENTER);
        conAviso.add(Estilo.texto("El catálogo de medicamentos está vacío. Pida al administrador que los registre "
                + "para poder emitir recetas.", Font.BOLD, 12, Estilo.ERROR), BorderLayout.SOUTH);
        return conAviso;
    }

    private static void columna(JPanel fila, Component componente, int x, double pesoHorizontal) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = 0;
        gbc.weightx = pesoHorizontal;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, x == 0 ? 0 : 10, 0, 0);
        fila.add(componente, gbc);
    }

    private JScrollPane crearTablaReceta() {
        tblReceta = new JTable(modeloReceta);
        AgendaPanel.estilizarTabla(tblReceta);
        tblReceta.setRowHeight(28);
        tblReceta.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblReceta.getColumnModel().getColumn(0).setPreferredWidth(300);
        tblReceta.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblReceta.getColumnModel().getColumn(2).setPreferredWidth(130);
        tblReceta.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(tblReceta);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setPreferredSize(new Dimension(100, 130));
        return scroll;
    }

    private JPanel crearBotonQuitar() {
        JButton btnQuitar = Estilo.botonSecundario("Quitar seleccionado");
        btnQuitar.addActionListener(e -> quitarMedicamento());
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        fila.setOpaque(false);
        fila.add(btnQuitar);
        return fila;
    }

    private JPanel crearBarraBotones() {
        JButton btnCancelar = Estilo.botonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        JButton btnGuardar = Estilo.botonPrimario("Guardar atención");
        btnGuardar.addActionListener(e -> guardar());

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE));
        barra.add(btnCancelar);
        barra.add(btnGuardar);
        return barra;
    }

    // =====================================================================
    //  COMPONENTES CON EL ESTILO DEL GRUPO
    // =====================================================================

    /** Área de texto con el borde de Estilo; Tab pasa al siguiente campo en lugar de escribir una tabulación. */
    private static JScrollPane areaTexto(JTextArea area, int filas) {
        area.setRows(filas);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(Estilo.fuente(Font.PLAIN, 14));
        area.setForeground(Estilo.TEXTO);
        area.setCaretColor(Estilo.PRIMARIO);
        area.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        area.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                Set.of(KeyStroke.getKeyStroke("TAB")));
        area.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                Set.of(KeyStroke.getKeyStroke("shift TAB")));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 1));
        return scroll;
    }

    private static void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(Estilo.fuente(Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(Estilo.TEXTO);
        combo.setPreferredSize(new Dimension(180, 40));
    }

    /** Texto de un combo editable, aunque el usuario no haya confirmado con Enter lo que escribió. */
    private static String textoDe(JComboBox<String> combo) {
        Object item = combo.getEditor().getItem();
        return item == null ? "" : item.toString().trim();
    }

    // =====================================================================
    //  RECETA: AGREGAR / QUITAR
    // =====================================================================
    private void agregarMedicamento() {
        lblError.setText(" ");

        Medicamento medicamento = (Medicamento) cboMedicamento.getSelectedItem();
        if (medicamento == null) {
            lblError.setText("Seleccione un medicamento.");
            return;
        }

        BigDecimal dosis;
        try {
            dosis = new BigDecimal(txtDosis.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            lblError.setText("La dosis debe ser un número (por ejemplo 1 o 2.5).");
            txtDosis.requestFocusInWindow();
            return;
        }
        if (dosis.signum() <= 0) {
            lblError.setText("La dosis debe ser mayor que cero.");
            return;
        }
        if (dosis.scale() > 2) {
            lblError.setText("La dosis admite como máximo 2 decimales.");
            return;
        }
        if (dosis.compareTo(MAX_DOSIS) > 0) {
            lblError.setText("La dosis es demasiado grande (máximo 9999.99).");
            return;
        }

        String unidad = textoDe(cboUnidad);
        String frecuencia = textoDe(cboFrecuencia);
        String duracion = txtDuracion.getText().trim();
        if (unidad.isEmpty() || frecuencia.isEmpty() || duracion.isEmpty()) {
            lblError.setText("Complete la unidad, la frecuencia y la duración (por ejemplo \"7 días\").");
            return;
        }
        if (unidad.length() > MAX_UNIDAD || frecuencia.length() > MAX_FRECUENCIA || duracion.length() > MAX_DURACION) {
            lblError.setText("Unidad (máx. " + MAX_UNIDAD + "), frecuencia (máx. " + MAX_FRECUENCIA
                    + ") o duración (máx. " + MAX_DURACION + ") demasiado largas.");
            return;
        }
        for (DetalleReceta d : detalles) {
            if (d.getMedicamento().getIdMedicamento() == medicamento.getIdMedicamento()) {
                lblError.setText("Ese medicamento ya está en la receta. Quítelo si desea cambiarlo.");
                return;
            }
        }

        DetalleReceta detalle = new DetalleReceta(medicamento, dosis, unidad, frecuencia, duracion);
        detalles.add(detalle);
        modeloReceta.addRow(new Object[]{medicamento.toString(), detalle.getDosisTexto(), frecuencia, duracion});

        txtDosis.setText("");
        txtDuracion.setText("");
        cboMedicamento.requestFocusInWindow();
    }

    private void quitarMedicamento() {
        lblError.setText(" ");
        int fila = tblReceta.getSelectedRow();
        if (fila < 0 || fila >= detalles.size()) {
            lblError.setText("Seleccione de la tabla el medicamento que desea quitar.");
            return;
        }
        detalles.remove(fila);
        modeloReceta.removeRow(fila);
    }

    // =====================================================================
    //  GUARDAR
    // =====================================================================
    private void guardar() {
        lblError.setText(" ");

        String sintomas = txtSintomas.getText().trim();
        String diagnostico = txtDiagnostico.getText().trim();
        if (sintomas.isEmpty() || diagnostico.isEmpty()) {
            lblError.setText("Los síntomas y el diagnóstico son obligatorios.");
            return;
        }
        // Evita perder un medicamento que se escribió pero no se agregó a la receta
        if (!txtDosis.getText().trim().isEmpty() || !txtDuracion.getText().trim().isEmpty()) {
            lblError.setText("Hay un medicamento sin agregar. Pulse «Agregar» o vacíe esos campos.");
            return;
        }

        String resumenReceta = detalles.isEmpty()
                ? "sin receta"
                : "con receta de " + detalles.size() + (detalles.size() == 1 ? " medicamento" : " medicamentos");
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Guardar la atención (" + resumenReceta + ")?\nLa cita pasará a «Atendida» y no podrá modificarse.",
                "Confirmar atención", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        Atencion atencion = new Atencion();
        atencion.setIdCita(cita.getIdCita());
        atencion.setSintomas(sintomas);
        atencion.setDiagnostico(diagnostico);
        atencion.setObservaciones(txtObservaciones.getText().trim());
        atencion.setIndicaciones(txtIndicaciones.getText().trim());
        atencion.setDetalles(detalles);

        try {
            servicio.registrarAtencion(atencion);
            guardada = true;
            JOptionPane.showMessageDialog(this, "¡Atención registrada correctamente!",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (ReglaNegocioException e) {
            lblError.setText(e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al registrar la atención", e);
            JOptionPane.showMessageDialog(this, "Error de base de datos:\n" + e.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}
