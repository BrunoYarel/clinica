package com.mycompany.clinica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {
    

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    // Reemplaza 'nombre_de_tu_base_de_datos' por el nombre real en phpMyAdmin
    private static final String URL = "jdbc:mysql://localhost:3306/clinica?serverTimezone=UTC";
    private static final String USER = "root"; 
    private static final String PASSWORD = "";

    public static Connection conectar() {
        Connection cn = null;
        try {
            Class.forName(DRIVER);
            cn = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error: No se encontró el driver de MySQL. " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar a la base de datos: " + e.getMessage());
        }
        return cn;
    }
}
