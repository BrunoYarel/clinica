package com.mycompany.clinica.dao;

import com.mycompany.clinica.Conexion;
import com.mycompany.clinica.modelo.Medicamento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos del catálogo de medicamentos.
 * Los errores de la base de datos se lanzan como SQLException: la ventana decide qué mostrar.
 */
public class MedicamentoDAO {

    private static final String SQL_LISTAR =
            "SELECT id_medicamento, nombre, presentacion FROM medicamento ORDER BY nombre, presentacion";

    private static final String SQL_INSERTAR =
            "INSERT INTO medicamento (nombre, presentacion) VALUES (?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE medicamento SET nombre = ?, presentacion = ? WHERE id_medicamento = ?";

    /** Todo el catálogo, ordenado por nombre (para la tabla de FrmMedicamento y el combo de recetas). */
    public List<Medicamento> listarMedicamentos() throws SQLException {
        List<Medicamento> lista = new ArrayList<>();
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Medicamento(
                        rs.getInt("id_medicamento"),
                        rs.getString("nombre"),
                        rs.getString("presentacion")));
            }
        }
        return lista;
    }

    public boolean registrarMedicamento(Medicamento medicamento) throws SQLException {
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_INSERTAR)) {

            ps.setString(1, medicamento.getNombre());
            ps.setString(2, medicamento.getPresentacion());
            return ps.executeUpdate() > 0;
        }
    }

    /** Cambia nombre y presentación de un medicamento existente (las recetas ya emitidas lo reflejan). */
    public boolean actualizarMedicamento(Medicamento medicamento) throws SQLException {
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setString(1, medicamento.getNombre());
            ps.setString(2, medicamento.getPresentacion());
            ps.setInt(3, medicamento.getIdMedicamento());
            return ps.executeUpdate() > 0;
        }
    }
}
