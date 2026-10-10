package com.mycompany.clinica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Única conexión a la base de datos para todo el proyecto (login, pacientes, especialidades...).
 *
 * Prueba las configuraciones locales más comunes (casa / instituto) en este orden:
 *     3306 + clave vacía  ->  3306 + "root"  ->  3307 + clave vacía  ->  3307 + "root"
 * La primera es la configuración estándar de XAMPP, así que en ese caso se conecta al primer intento.
 */
public class Conexion {
    private static final String BASE_DATOS = "clinica"; // Reemplaza por el nombre real de tu BD
    private static final String USUARIO = "root";
    private static final String[] PUERTOS = {"3306", "3307"}; // estándar y alterno de XAMPP
    private static final String[] CLAVES = {"", "root"};      // vacía y 'root'

    public static Connection conectar() {
        try {
            // Registrar el driver de Maven
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el driver de MySQL: " + e.getMessage());
            return null;
        }

        SQLException primerError = null; // el de la configuración estándar, el más útil para diagnosticar
        for (String puerto : PUERTOS) {
            for (String clave : CLAVES) {
                String url = "jdbc:mysql://localhost:" + puerto + "/" + BASE_DATOS;
                try {
                    Connection conexion = DriverManager.getConnection(url, USUARIO, clave);
                    System.out.println("¡Conexión establecida con éxito en XAMPP!");
                    return conexion;
                } catch (SQLException e) {
                    if (primerError == null) {
                        primerError = e;
                    }
                    // sigue con la siguiente combinación
                }
            }
        }
        System.out.println("Error de conexión: " + (primerError != null ? primerError.getMessage() : "sin detalle"));
        return null;
    }

    /**
     * Mismo método que conectar(), con el nombre que usan los formularios de Oscar Ticona
     * (FrmPaciente y FrmEspecialidad). Así los tres equipos comparten una sola conexión.
     */
    public static Connection getConexion() {
        return conectar();
    }
}
