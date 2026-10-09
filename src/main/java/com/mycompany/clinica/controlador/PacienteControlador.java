package com.mycompany.clinica.controlador;

import com.mycompany.clinica.modelo.Paciente;
import com.mycompany.clinica.utilidades.Conexion;
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
}
