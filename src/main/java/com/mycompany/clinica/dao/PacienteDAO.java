package com.mycompany.clinica.dao;

import com.mycompany.clinica.Conexion;
import com.mycompany.clinica.modelo.Paciente;
import com.mycompany.clinica.modelo.RegistroHistorial;
import com.mycompany.clinica.servicio.Sesion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de pacientes y de su historial clínico.
 * Los errores de la base de datos se lanzan como SQLException: la ventana decide qué mostrar.
 */
public class PacienteDAO {

    private static final String SQL_INSERTAR =
            "INSERT INTO paciente (nombres, apellidos, documento, fecha_nacimiento, telefono, email) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

    // Búsqueda: base + filtro opcional + orden
    private static final String SQL_BUSCAR =
            "SELECT id_paciente, nombres, apellidos, documento, fecha_nacimiento, telefono, email FROM paciente";
    private static final String SQL_BUSCAR_FILTRO =
            " WHERE documento LIKE ? OR nombres LIKE ? OR apellidos LIKE ?";
    private static final String SQL_BUSCAR_ORDEN = " ORDER BY apellidos, nombres";

    private static final String SQL_ACTUALIZAR =
            "UPDATE paciente SET nombres = ?, apellidos = ?, documento = ?, fecha_nacimiento = ?, "
                    + "telefono = ?, email = ? WHERE id_paciente = ?";

    // Un paciente tiene historial si alguna de sus citas tiene atención
    private static final String SQL_TIENE_HISTORIAL =
            "SELECT 1 FROM cita c JOIN atencion a ON a.id_cita = c.id_cita WHERE c.id_paciente = ? LIMIT 1";
    private static final String SQL_TIENE_CITAS = "SELECT 1 FROM cita WHERE id_paciente = ? LIMIT 1";

    private static final String SQL_ELIMINAR = "DELETE FROM paciente WHERE id_paciente = ?";

    // Consulta base uniendo Paciente, Cita y Atencion
    private static final String SQL_HISTORIAL =
            "SELECT c.fecha_cita, c.hora_cita, c.motivo, a.sintomas, a.diagnostico, a.observaciones, "
                    // Receta de la atención: una línea por medicamento (NULL si no se emitió receta).
                    // El doble TRIM quita los ceros sobrantes de la dosis: 1.00 -> 1, 2.50 -> 2.5
                    + "(SELECT GROUP_CONCAT(CONCAT(m.nombre, ' ', m.presentacion, ' - ', "
                    + "TRIM(TRAILING '.' FROM TRIM(TRAILING '0' FROM d.dosis)), ' ', d.unidad_medida, ', ', "
                    + "d.frecuencia, ', ', d.duracion) ORDER BY d.id_detalle_receta SEPARATOR '\n') "
                    + "FROM receta r JOIN detalle_receta d ON d.id_receta = r.id_receta "
                    + "JOIN medicamento m ON m.id_medicamento = d.id_medicamento "
                    + "WHERE r.id_atencion = a.id_atencion) AS receta "
                    + "FROM paciente p "
                    + "JOIN cita c ON p.id_paciente = c.id_paciente "
                    + "JOIN atencion a ON c.id_cita = a.id_cita "
                    + "WHERE p.documento = ?";

    // Se agrega a la consulta base cuando el usuario es Médico
    private static final String FILTRO_MEDICO = " AND c.id_medico = ?";

    /** Inserta un paciente nuevo. Si el documento ya existe, MySQL lanza error 1062. */
    public boolean registrarPaciente(Paciente paciente) throws SQLException {
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_INSERTAR)) {

            ps.setString(1, paciente.getNombres());
            ps.setString(2, paciente.getApellidos());
            ps.setString(3, paciente.getDocumento());
            ps.setString(4, paciente.getFechaNacimiento());
            ps.setString(5, paciente.getTelefono());
            ps.setString(6, paciente.getEmail());

            return ps.executeUpdate() > 0; // true si se guardó con éxito
        }
    }

    /** Busca por documento, nombres o apellidos (texto vacío = todos). Cada paciente trae su idPaciente. */
    public List<Paciente> buscarPacientes(String texto) throws SQLException {
        String filtro = texto == null ? "" : texto.trim();
        boolean todos = filtro.isEmpty();
        String sql = SQL_BUSCAR + (todos ? "" : SQL_BUSCAR_FILTRO) + SQL_BUSCAR_ORDEN;

        List<Paciente> pacientes = new ArrayList<>();
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (!todos) {
                String patron = "%" + filtro + "%";
                ps.setString(1, patron);
                ps.setString(2, patron);
                ps.setString(3, patron);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Paciente p = new Paciente(
                            rs.getString("nombres"),
                            rs.getString("apellidos"),
                            rs.getString("documento"),
                            rs.getString("fecha_nacimiento"),
                            rs.getString("telefono"),
                            rs.getString("email"));
                    p.setIdPaciente(rs.getInt("id_paciente"));
                    pacientes.add(p);
                }
            }
        }
        return pacientes;
    }

    /** Actualiza los datos del paciente. Si el documento choca con otro paciente, MySQL lanza error 1062. */
    public boolean actualizarPaciente(Paciente p) throws SQLException {
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setString(1, p.getNombres());
            ps.setString(2, p.getApellidos());
            ps.setString(3, p.getDocumento());
            ps.setString(4, p.getFechaNacimiento());
            ps.setString(5, p.getTelefono());
            ps.setString(6, p.getEmail());
            ps.setInt(7, p.getIdPaciente());

            return ps.executeUpdate() > 0;
        }
    }

    /** true si el paciente tiene al menos una atención registrada. */
    public boolean tieneHistorial(int idPaciente) throws SQLException {
        try (Connection con = Conexion.conectar()) {
            return existe(con, SQL_TIENE_HISTORIAL, idPaciente);
        }
    }

    /** true si el paciente tiene al menos una cita. */
    public boolean tieneCitas(int idPaciente) throws SQLException {
        try (Connection con = Conexion.conectar()) {
            return existe(con, SQL_TIENE_CITAS, idPaciente);
        }
    }

    /**
     * Elimina al paciente solo si no tiene historial ni citas (se vuelve a verificar aquí mismo).
     * Devuelve false, sin borrar nada, si tiene alguno de los dos o si ya no existe.
     */
    public boolean eliminarPaciente(int idPaciente) throws SQLException {
        try (Connection con = Conexion.conectar()) {
            if (existe(con, SQL_TIENE_HISTORIAL, idPaciente) || existe(con, SQL_TIENE_CITAS, idPaciente)) {
                return false;
            }
            try (PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {
                ps.setInt(1, idPaciente);
                return ps.executeUpdate() > 0;
            }
        }
    }

    /** Ejecuta una consulta "SELECT 1 ... WHERE id_paciente = ?" y dice si devolvió alguna fila. */
    private static boolean existe(Connection con, String sql, int idPaciente) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Historial clínico de un paciente (por su documento), con control de seguridad por rol.
     *
     * REGLA DE NEGOCIO: si el rol es Médico, se filtra estrictamente por su ID de médico;
     * Admin y Recepcionista ven todas las atenciones del paciente.
     */
    public List<RegistroHistorial> consultarHistorialClinico(String dni, int idMedicoLogueado, String rolUsuario)
            throws SQLException {
        boolean soloSusCitas = esMedico(rolUsuario);
        String sql = soloSusCitas ? SQL_HISTORIAL + FILTRO_MEDICO : SQL_HISTORIAL;

        List<RegistroHistorial> historial = new ArrayList<>();
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);
            if (soloSusCitas) {
                ps.setInt(2, idMedicoLogueado);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    historial.add(new RegistroHistorial(
                            unirFechaYHora(rs.getString("fecha_cita"), rs.getString("hora_cita")),
                            rs.getString("motivo"),
                            rs.getString("sintomas"),
                            rs.getString("diagnostico"),
                            rs.getString("observaciones"),
                            rs.getString("receta")));
                }
            }
        }
        return historial;
    }

    /** Acepta el rol como "Medico" o como "Médico" (con tilde), igual que la clase Sesion. */
    private static boolean esMedico(String rol) {
        return rol != null && rol.replace('é', 'e').replace('É', 'E').trim().equalsIgnoreCase(Sesion.MEDICO);
    }

    /** "2026-10-11" + "07:40:00" -> "2026-10-11 07:40:00" (si falta alguna parte, devuelve la otra). */
    private static String unirFechaYHora(String fecha, String hora) {
        if (fecha == null) {
            return hora == null ? "" : hora;
        }
        return hora == null ? fecha : fecha + " " + hora;
    }
}