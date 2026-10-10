package com.mycompany.clinica.controlador;

import com.mycompany.clinica.modelo.Paciente;
import com.mycompany.clinica.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PacienteControlador {

    // Método para insertar un paciente en XAMPP
    public boolean registrarPaciente(Paciente paciente) {
        String sql = "INSERT INTO PACIENTE (nombres, apellidos, documento, fecha_nacimiento, telefono, email) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, paciente.getNombres());
            ps.setString(2, paciente.getApellidos());
            ps.setString(3, paciente.getDocumento());
            ps.setString(4, paciente.getFechaNacimiento());
            ps.setString(5, paciente.getTelefono());
            ps.setString(6, paciente.getEmail());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // Retorna true si se guardó con éxito

        } catch (SQLException e) {
            System.out.println("Error al registrar paciente: " + e.getMessage());
            return false;
        }
    }
    public javax.swing.table.DefaultTableModel consultarHistorialClinico(String dni) {
        // Configurar los títulos de la tabla en Java
        String[] titulos = {"Fecha/Hora", "Motivo Cita", "Síntomas", "Diagnóstico", "Observaciones"};
        javax.swing.table.DefaultTableModel modeloTabla = new javax.swing.table.DefaultTableModel(null, titulos);

        // Consulta SQL uniendo Paciente, Cita y Atencion usando el DNI
        String sql = "SELECT c.fecha_hora_cita, c.motivo, a.sintomas, a.diagnostico, a.observaciones " +
                "FROM PACIENTE p " +
                "JOIN CITA c ON p.id_paciente = c.id_paciente " +
                "JOIN ATENCION a ON c.id_cita = a.id_cita " +
                "WHERE p.documento = ?";

        try (java.sql.Connection con = com.mycompany.clinica.Conexion.conectar();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                Object[] fila = new Object[5];
                while (rs.next()) {
                    fila[0] = rs.getTimestamp("fecha_hora_cita");
                    fila[1] = rs.getString("motivo");
                    fila[2] = rs.getString("sintomas");
                    fila[3] = rs.getString("diagnostico");
                    fila[4] = rs.getString("observaciones");
                    modeloTabla.addRow(fila); // Agrega el registro médico encontrado a la tabla
                }
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Error al consultar el historial: " + e.getMessage());
        }
        return modeloTabla;
    }

}