package com.mycompany.clinica.utilidades;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

public class AjustarCelda extends JTextArea implements TableCellRenderer {

    public AjustarCelda() {
        setLineWrap(true);       // Activa el salto de línea automático
        setWrapStyleWord(true);   // Evita que las palabras se corten por la mitad
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        setText(value != null ? value.toString() : "");

        // Mantener el color de selección de la tabla original
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        // Ajustar automáticamente la altura de la fila según el tamaño del texto
        setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
        int alturaPreferida = getPreferredSize().height;
        if (table.getRowHeight(row) != alturaPreferida) {
            table.setRowHeight(row, Math.max(alturaPreferida, 24)); // Mínimo 24px de alto
        }

        return this;
    }
}
