package com.mycompany.clinica.dao;

import com.mycompany.clinica.Conexion;
import com.mycompany.clinica.modelo.Cita;
import com.mycompany.clinica.modelo.EstadoCita;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de citas.
 * Los errores de la base de datos se lanzan como SQLException: la ventana decide qué mostrar.
 */
public class CitaDAO {

    private static final String SQL_INSERTAR =
            "INSERT INTO cita (fecha_cita, hora_cita, motivo, estado, id_paciente, id_medico) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_LISTAR =
            "SELECT id_cita, fecha_cita, hora_cita, motivo, estado, id_paciente, id_medico FROM cita";

    // Le faltan los "?" del IN (...): se agregan uno por cada estado de origen permitido
    private static final String SQL_CAMBIAR_ESTADO = "UPDATE cita SET estado = ? WHERE id_cita = ? AND estado IN (";

    private static final String SQL_ESTADO_ACTUAL = "SELECT estado FROM cita WHERE id_cita = ?";

    /** Guarda una cita nueva; siempre nace con el estado Pendiente. */
    public boolean registrarCita(Cita cita) throws SQLException {
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_INSERTAR)) {

            ps.setString(1, cita.getFechaCita());
            ps.setString(2, cita.getHoraCita());
            ps.setString(3, cita.getMotivo());
            ps.setString(4, EstadoCita.PENDIENTE.getValor());
            ps.setInt(5, cita.getIdPaciente());
            ps.setInt(6, cita.getIdMedico());

            return ps.executeUpdate() > 0;
        }
    }

    public List<Cita> listarCitas() throws SQLException {
        List<Cita> lista = new ArrayList<>();
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cita cita = new Cita();
                cita.setIdCita(rs.getInt("id_cita"));
                cita.setFechaCita(rs.getString("fecha_cita"));
                cita.setHoraCita(rs.getString("hora_cita"));
                cita.setMotivo(rs.getString("motivo"));
                cita.setEstado(rs.getString("estado"));
                cita.setIdPaciente(rs.getInt("id_paciente"));
                cita.setIdMedico(rs.getInt("id_medico"));
                lista.add(cita);
            }
        }
        return lista;
    }

    /**
     * Cambia el estado de la cita solo si su estado actual es uno de estadosOrigen. La comprobación y el
     * cambio van en un solo UPDATE, así dos usuarios a la vez no se pisan.
     *
     * @return false si no se cambió nada (la cita no existe o su estado actual no está en estadosOrigen);
     *         obtenerEstadoCita() permite distinguir ambos casos
     */
    public boolean modificarEstadoCita(int idCita, EstadoCita nuevoEstado, EstadoCita... estadosOrigen)
            throws SQLException {
        if (estadosOrigen.length == 0) {
            return false;
        }
        StringBuilder sql = new StringBuilder(SQL_CAMBIAR_ESTADO);
        for (int i = 0; i < estadosOrigen.length; i++) {
            sql.append(i == 0 ? "?" : ", ?");
        }
        sql.append(")");

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            ps.setString(1, nuevoEstado.getValor());
            ps.setInt(2, idCita);
            for (int i = 0; i < estadosOrigen.length; i++) {
                ps.setString(3 + i, estadosOrigen[i].getValor());
            }

            return ps.executeUpdate() > 0;
        }
    }

    /** Estado actual de la cita, o null si ya no existe. */
    public String obtenerEstadoCita(int idCita) throws SQLException {
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_ESTADO_ACTUAL)) {

            ps.setInt(1, idCita);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("estado") : null;
            }
        }
    }
}