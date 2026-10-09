package pe.senati.clinica.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CitaDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/clinica";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 

    private Connection getConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Error de conexión en CitaDAO: " + e.getMessage());
            return null;
        }
    }

    public boolean registrarCita(int idPaciente, int idMedico, String fecha, String hora, String motivo) {
        String sql = "INSERT INTO CITA (fecha_cita, hora_cita, motivo, estado, id_paciente, id_medico) VALUES (?, ?, ?, 'Pendiente', ?, ?)";

        try (Connection con = getConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fecha);      
            ps.setString(2, hora);        
            ps.setString(3, motivo);      
            ps.setInt(4, idPaciente);
            ps.setInt(5, idMedico);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar cita en BD: " + e.getMessage());
            return false;
        }
    }
}



