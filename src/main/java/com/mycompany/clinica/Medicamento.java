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

public class Medicamento {

    public static void main(String[] args) {
        // 1. Crear el JFrame principal (Formulario Padre MDI)
        JFrame ventanaPrincipal = new JFrame("Gestión de Medicamentos - Vista de Desarrollo");
        ventanaPrincipal.setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        ventanaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 2. Instanciar el JDesktopPane contenedor
        JDesktopPane dskPrincipal = new JDesktopPane();
        ventanaPrincipal.setContentPane(dskPrincipal);

        // 3. Hacer visible el contenedor principal
        ventanaPrincipal.setVisible(true);

        // 4. Instanciar e integrar tu JInternalFrame de Medicamentos
        FrmMedicamento frm = new FrmMedicamento();
        dskPrincipal.add(frm);
        
        // 5. Ajustar propiedades visuales automáticas
        ajustarCamposDeTexto(frm);
        
        // Aplicar el TitledBorder para incrustar el título "MEDICAMENTOS" en el contorno azul
        LineBorder bordeAzul = new LineBorder(new Color(120, 160, 220), 2);
        TitledBorder tituloBorde = new TitledBorder(
            bordeAzul, 
            "REGISTRO DE MEDICAMENTOS", 
            TitledBorder.CENTER, 
            TitledBorder.TOP, 
            new Font("Tahoma", Font.BOLD, 12), 
            Color.BLACK
        );
        frm.setBorder(tituloBorde);
        
        // Asignar tamaño cómodo y centrar en pantalla
        frm.setSize(550, 450); 
        int x = (dskPrincipal.getWidth() - frm.getWidth()) / 2;
        int y = (dskPrincipal.getHeight() - frm.getHeight()) / 2;
        frm.setLocation(Math.max(0, x), Math.max(0, y));
        
        // 6. Mostrar el formulario hijo
        frm.setVisible(true);
    }

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
