package com.mycompany.clinica.controlador;

import com.mycompany.clinica.modelo.Paciente;
import com.mycompany.clinica.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    // Método actualizado con control de seguridad por Roles
    public javax.swing.table.DefaultTableModel consultarHistorialClinico(String dni, int idMedicoLogueado, String rolUsuario) {
        String[] titulos = {"Fecha/Hora", "Motivo Cita", "Síntomas", "Diagnóstico", "Observaciones"};
        javax.swing.table.DefaultTableModel modeloTabla = new javax.swing.table.DefaultTableModel(null, titulos);
        
        // Consulta SQL base uniendo Paciente, Cita y Atencion
        String sql = "SELECT c.fecha_hora_cita, c.motivo, a.sintomas, a.diagnostico, a.observaciones " +
                     "FROM PACIENTE p " +
                     "JOIN CITA c ON p.id_paciente = c.id_paciente " +
                     "JOIN ATENCION a ON c.id_cita = a.id_cita " +
                     "WHERE p.documento = ?";
        
        // REGLA DE NEGOCIO: Si el rol es Médico, filtramos estrictamente por su ID de médico
        if (rolUsuario.equalsIgnoreCase("Médico") || rolUsuario.equalsIgnoreCase("Medico")) {
            sql += " AND c.id_medico = ?";
        }
                     
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, dni);
            
            // Si es médico, inyectamos su ID en el segundo parámetro del WHERE
            if (rolUsuario.equalsIgnoreCase("Médico") || rolUsuario.equalsIgnoreCase("Medico")) {
                ps.setInt(2, idMedicoLogueado);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                Object[] fila = new Object[5];
                while (rs.next()) {
                    fila[0] = rs.getTimestamp("fecha_hora_cita");
                    fila[1] = rs.getString("motivo");
                    fila[2] = rs.getString("sintomas");
                    fila[3] = rs.getString("diagnostico");
                    fila[4] = rs.getString("observaciones");
                    modeloTabla.addRow(fila);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar el historial: " + e.getMessage());
        }
        return modeloTabla;
    }
}
