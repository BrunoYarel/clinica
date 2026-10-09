package com.mycompany.clinica;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/clinica";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Vacío por defecto en XAMPP

    public static Connection getConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
}

