package com.mycompany.clinica.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 * Ventana padre MDI para los módulos que son un JInternalFrame (Especialidades, Médicos...).
 *
 * Reemplaza al código que estaba repetido en las antiguas clases Especialidad y Medico:
 * crea el JFrame maximizado con su JDesktopPane, mete dentro el formulario con el borde azul
 * con título y lo centra en pantalla.
 *
 * Uso: VentanaMdi.abrir("Gestión de Médicos", "REGISTRO DE MÉDICOS", new FrmMedico(), null);
 */
public final class VentanaMdi {

    private VentanaMdi() {
    }

    /**
     * Crea y muestra la ventana MDI con el formulario dentro.
     *
     * @param tituloVentana título de la ventana principal
     * @param tituloBorde   texto que se incrusta en el borde azul del formulario
     * @param formulario    formulario hijo (JInternalFrame) que se muestra
     * @param tamano        tamaño del formulario; null para respetar el que define el propio formulario
     * @return la ventana principal creada, para que quien la abre pueda controlar su cierre
     */
    public static JFrame abrir(String tituloVentana, String tituloBorde,
                               JInternalFrame formulario, Dimension tamano) {
        // 1. Crear el JFrame principal (Formulario Padre MDI)
        JFrame ventana = new JFrame(tituloVentana);
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizado a pantalla completa
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 2. Instanciar el JDesktopPane contenedor de ventanas internas
        JDesktopPane escritorio = new JDesktopPane();
        ventana.setContentPane(escritorio);

        // 3. Hacer visible el contenedor principal primero para obtener dimensiones reales
        ventana.setVisible(true);

        // 4. Integrar el JInternalFrame (Formulario Hijo)
        escritorio.add(formulario);

        // 5. Ajustar propiedades del formulario para evitar deformaciones en el renderizado
        ajustarCamposDeTexto(formulario);

        // Aplicar el TitledBorder para incrustar el título en el contorno azul
        LineBorder bordeAzul = new LineBorder(new Color(120, 160, 220), 2);
        formulario.setBorder(new TitledBorder(
                bordeAzul,
                tituloBorde,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 12),
                Color.BLACK));

        if (tamano != null) {
            formulario.setSize(tamano);
        }

        // Centrar en pantalla con la fórmula de la guía de MDI
        int x = (escritorio.getWidth() - formulario.getWidth()) / 2;
        int y = (escritorio.getHeight() - formulario.getHeight()) / 2;
        formulario.setLocation(Math.max(0, x), Math.max(0, y)); // Evita coordenadas negativas

        // 6. Mostrar el formulario hijo
        formulario.setVisible(true);
        return ventana;
    }

    /**
     * Busca y estira los JTextField internos automáticamente.
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
