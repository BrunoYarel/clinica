package com.mycompany.clinica.dao;

import com.mycompany.clinica.Conexion;
import com.mycompany.clinica.modelo.Medico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de médicos.
 * Los errores de la base de datos se lanzan como SQLException: la ventana decide qué mostrar.
 */
public class MedicoDAO {

    private static final String SQL_LISTAR =
            "SELECT m.id_medico, m.nombres, m.apellidos, m.cmp, m.telefono, m.email, "
            + "m.id_especialidad, e.nombre AS especialidad "
            + "FROM medico m INNER JOIN especialidad e ON m.id_especialidad = e.id_especialidad";

    private static final String SQL_INSERTAR =
            "INSERT INTO medico (nombres, apellidos, cmp, telefono, email, id_especialidad) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    /** Todos los médicos junto con el nombre de su especialidad. */
    public List<Medico> listarMedicos() throws SQLException {
        List<Medico> lista = new ArrayList<>();
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Medico medico = new Medico();
                medico.setIdMedico(rs.getInt("id_medico"));
                medico.setNombres(rs.getString("nombres"));
                medico.setApellidos(rs.getString("apellidos"));
                medico.setCmp(rs.getString("cmp"));
                medico.setTelefono(rs.getString("telefono"));
                medico.setEmail(rs.getString("email"));
                medico.setIdEspecialidad(rs.getInt("id_especialidad"));
                medico.setEspecialidad(rs.getString("especialidad"));
                lista.add(medico);
            }
        }
        return lista;
    }

    public boolean registrarMedico(Medico medico) throws SQLException {
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_INSERTAR)) {

            ps.setString(1, medico.getNombres());
            ps.setString(2, medico.getApellidos());
            ps.setString(3, medico.getCmp());
            ps.setString(4, medico.getTelefono());
            ps.setString(5, medico.getEmail());
            ps.setInt(6, medico.getIdEspecialidad());
            return ps.executeUpdate() > 0;
        }
    }
}
