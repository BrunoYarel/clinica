package com.mycompany.clinica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    // Intentar la conexión con los dos escenarios más comunes en entornos locales (Casa / Instituto)
    public static Connection getConexion() {
        String[] contrasenas = {"", "root"}; // Probar vacío y 'root' automáticamente
        String[] puertos = {"3306", "3307"}; // Probar puerto estándar y puerto alterno de XAMPP
        
        for (String puerto : puertos) {
            for (String clave : contrasenas) {
                String url = "jdbc:mysql://localhost:" + puerto + "/clinica?serverTimezone=UTC&useSSL=false";
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection cn = DriverManager.getConnection(url, "root", clave);
                    if (cn != null) {
                        return cn; // Si conecta con éxito, devuelve la conexión de inmediato
                    }
                } catch (Exception e) {
                    // Continúa intentando con la siguiente combinación si esta falla
                }
            }
        }
        System.out.println("Error crítico: No se pudo conectar a MySQL con ninguna configuración local.");
        return null;
    }
}
