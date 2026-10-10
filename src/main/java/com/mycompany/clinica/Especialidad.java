package com.mycompany.clinica;

import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.Component;
import java.awt.Container;
import java.awt.Color;
import java.awt.Font;

/**
 * Clase Principal adaptada al formato de la Guía de MDI y CRUD de SENATI.
 * Nombre del archivo físico obligatorio: Especialidad.java
 */
public class Especialidad {

    public static void main(String[] args) {
        // 1. Crear el JFrame principal (Formulario Padre MDI)
        JFrame ventanaPrincipal = new JFrame("Gestión de Especialidades - Vista de Desarrollo");
        ventanaPrincipal.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizado a pantalla completa
        ventanaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 2. Instanciar el JDesktopPane contenedor de ventanas internas (dskPrincipal)
        JDesktopPane dskPrincipal = new JDesktopPane();
        ventanaPrincipal.setContentPane(dskPrincipal);

        // 3. Hacer visible el contenedor principal primero para obtener dimensiones reales en casa
        ventanaPrincipal.setVisible(true);

        // 4. Instanciar e integrar tu JInternalFrame (Formulario Hijo)
        FrmEspecialidad frm = new FrmEspecialidad();
        dskPrincipal.add(frm);
        
        // 5. Ajustar propiedades del formulario para evitar deformaciones en el renderizado
        ajustarCamposDeTexto(frm);
        
        // Aplicar el TitledBorder para incrustar el título "ESPECIALIDAD" en el contorno azul
        LineBorder bordeAzul = new LineBorder(new Color(120, 160, 220), 2);
        TitledBorder tituloBorde = new TitledBorder(
            bordeAzul, 
            "REGISTRO DE ESPECIALIDADES", 
            TitledBorder.CENTER, 
            TitledBorder.TOP, 
            new Font("Tahoma", Font.BOLD, 12), 
            Color.BLACK
        );
        frm.setBorder(tituloBorde);
        
        // Asignar tamaño cómodo y centrar en pantalla usando la fórmula exacta de la guía
        frm.setSize(600, 500); 
        int x = (dskPrincipal.getWidth() - frm.getWidth()) / 2;
        int y = (dskPrincipal.getHeight() - frm.getHeight()) / 2;
        frm.setLocation(Math.max(0, x), Math.max(0, y)); // Evita coordenadas negativas
        
        // 6. Mostrar el formulario final listo para interactuar con el CRUD
        frm.setVisible(true);
    }

    /**
     * Método auxiliar para buscar y estirar los JTextField internos automáticamente.
     * Esto previene que los campos colapsen al tamaño mínimo cuando están vacíos.
     */
    private static void ajustarCamposDeTexto(Container contenedor) {
        for (Component comp : contenedor.getComponents()) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setColumns(20);
            } else if (comp instanceof Container) {
                ajustarCamposDeTexto((Container) comp);
            }
        }
    }
}
