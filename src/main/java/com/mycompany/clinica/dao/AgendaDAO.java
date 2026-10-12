package com.mycompany.clinica.dao;

import com.mycompany.clinica.Conexion;
import com.mycompany.clinica.modelo.Atencion;
import com.mycompany.clinica.modelo.CitaAgenda;
import com.mycompany.clinica.modelo.DetalleReceta;
import com.mycompany.clinica.modelo.EstadoCita;
import com.mycompany.clinica.servicio.ReglaNegocioException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la agenda del médico: sus citas de un día y el registro de la atención
 * (con su receta) de una cita.
 * Los errores técnicos se lanzan como SQLException; las reglas del negocio incumplidas,
 * como ReglaNegocioException (con un mensaje listo para mostrar).
 */
public class AgendaDAO {

    private static final String SQL_AGENDA =
            "SELECT c.id_cita, c.fecha_cita, c.hora_cita, c.motivo, c.estado, "
            + "p.id_paciente, p.nombres, p.apellidos, p.documento "
            + "FROM cita c JOIN paciente p ON p.id_paciente = c.id_paciente "
            + "WHERE c.id_medico = ? AND c.fecha_cita = ? "
            + "ORDER BY c.hora_cita";

    // FOR UPDATE bloquea la fila hasta el commit: dos atenciones simultáneas de la misma cita no se pisan
    private static final String SQL_BLOQUEAR_CITA =
            "SELECT id_medico, estado FROM cita WHERE id_cita = ? FOR UPDATE";

    private static final String SQL_INSERTAR_ATENCION =
            "INSERT INTO atencion (id_cita, sintomas, diagnostico, observaciones) VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERTAR_RECETA =
            "INSERT INTO receta (indicaciones, id_atencion) VALUES (?, ?)";

    private static final String SQL_INSERTAR_DETALLE =
            "INSERT INTO detalle_receta (id_receta, id_medicamento, dosis, unidad_medida, frecuencia, duracion) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_MARCAR_ATENDIDA = "UPDATE cita SET estado = ? WHERE id_cita = ?";

    /** Citas de un médico en una fecha, ordenadas por hora, con los datos del paciente. */
    public List<CitaAgenda> listarCitasDelDia(int idMedico, LocalDate fecha) throws SQLException {
        List<CitaAgenda> lista = new ArrayList<>();
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_AGENDA)) {

            ps.setInt(1, idMedico);
            ps.setDate(2, java.sql.Date.valueOf(fecha));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CitaAgenda cita = new CitaAgenda();
                    cita.setIdCita(rs.getInt("id_cita"));
                    cita.setFecha(rs.getDate("fecha_cita").toLocalDate());
                    cita.setHora(rs.getTime("hora_cita").toLocalTime());
                    cita.setMotivo(rs.getString("motivo"));
                    cita.setEstado(rs.getString("estado"));
                    cita.setIdPaciente(rs.getInt("id_paciente"));
                    cita.setPaciente(rs.getString("apellidos") + ", " + rs.getString("nombres"));
                    cita.setDocumento(rs.getString("documento"));
                    lista.add(cita);
                }
            }
        }
        return lista;
    }

    /**
     * Registra la atención de una cita y, si trae medicamentos, su receta. Todo en UNA transacción:
     * o se guarda todo (atención, receta, detalles y cita pasada a Atendida) o no se guarda nada.
     *
     * @param idMedico médico que atiende (el de la sesión): la cita debe ser suya
     * @throws ReglaNegocioException si la cita no existe, no es de ese médico o su estado no permite atenderla
     */
    public void registrarAtencion(int idMedico, Atencion atencion) throws SQLException, ReglaNegocioException {
        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);
            try {
                verificarCitaAtendible(con, idMedico, atencion.getIdCita());
                int idAtencion = insertarAtencion(con, atencion);

                if (!atencion.getDetalles().isEmpty()) {
                    int idReceta = insertarReceta(con, atencion.getIndicaciones(), idAtencion);
                    insertarDetalles(con, idReceta, atencion.getDetalles());
                }

                marcarCitaAtendida(con, atencion.getIdCita());
                con.commit();
            } catch (SQLException | ReglaNegocioException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // ---------------------------------------------------------------------
    //  PASOS DE LA TRANSACCIÓN
    // ---------------------------------------------------------------------

    private void verificarCitaAtendible(Connection con, int idMedico, int idCita)
            throws SQLException, ReglaNegocioException {
        try (PreparedStatement ps = con.prepareStatement(SQL_BLOQUEAR_CITA)) {
            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ReglaNegocioException("La cita ya no existe.");
                }
                if (rs.getInt("id_medico") != idMedico) {
                    throw new ReglaNegocioException("Esta cita no pertenece a su agenda.");
                }
                String motivo = CitaAgenda.motivoNoAtendible(rs.getString("estado"));
                if (motivo != null) {
                    throw new ReglaNegocioException(motivo);
                }
            }
        }
    }

    private int insertarAtencion(Connection con, Atencion atencion) throws SQLException, ReglaNegocioException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERTAR_ATENCION, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, atencion.getIdCita());
            ps.setString(2, atencion.getSintomas());
            ps.setString(3, atencion.getDiagnostico());
            ps.setString(4, atencion.getObservaciones());   // puede ser null: la columna lo admite
            ps.executeUpdate();
            return leerIdGenerado(ps);
        } catch (SQLIntegrityConstraintViolationException e) {
            // uq_atencion_cita: la cita ya tiene su atención
            throw new ReglaNegocioException("Esta cita ya tiene una atención registrada.");
        }
    }

    private int insertarReceta(Connection con, String indicaciones, int idAtencion) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERTAR_RECETA, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, indicaciones);
            ps.setInt(2, idAtencion);
            ps.executeUpdate();
            return leerIdGenerado(ps);
        }
    }

    private void insertarDetalles(Connection con, int idReceta, List<DetalleReceta> detalles) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERTAR_DETALLE)) {
            for (DetalleReceta d : detalles) {
                ps.setInt(1, idReceta);
                ps.setInt(2, d.getMedicamento().getIdMedicamento());
                ps.setBigDecimal(3, d.getDosis());
                ps.setString(4, d.getUnidadMedida());
                ps.setString(5, d.getFrecuencia());
                ps.setString(6, d.getDuracion());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void marcarCitaAtendida(Connection con, int idCita) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_MARCAR_ATENDIDA)) {
            ps.setString(1, EstadoCita.ATENDIDA.getValor());
            ps.setInt(2, idCita);
            ps.executeUpdate();
        }
    }

    private static int leerIdGenerado(PreparedStatement ps) throws SQLException {
        try (ResultSet claves = ps.getGeneratedKeys()) {
            if (claves.next()) {
                return claves.getInt(1);
            }
        }
        throw new SQLException("La base de datos no devolvió el id del registro creado.");
    }
}
