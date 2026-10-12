package com.mycompany.clinica.modelo;

import java.math.BigDecimal;

/**
 * Una línea de la receta (tabla DETALLE_RECETA): un medicamento con su dosis, frecuencia y duración.
 * La dosis es BigDecimal porque la columna es DECIMAL(6,2).
 */
public class DetalleReceta {
    private final Medicamento medicamento;
    private final BigDecimal dosis;
    private final String unidadMedida;
    private final String frecuencia;
    private final String duracion;

    public DetalleReceta(Medicamento medicamento, BigDecimal dosis, String unidadMedida,
                         String frecuencia, String duracion) {
        this.medicamento = medicamento;
        this.dosis = dosis;
        this.unidadMedida = unidadMedida;
        this.frecuencia = frecuencia;
        this.duracion = duracion;
    }

    public Medicamento getMedicamento() { return medicamento; }
    public BigDecimal getDosis() { return dosis; }
    public String getUnidadMedida() { return unidadMedida; }
    public String getFrecuencia() { return frecuencia; }
    public String getDuracion() { return duracion; }

    /** "1 Tableta", "2.5 ml" (sin ceros sobrantes). */
    public String getDosisTexto() {
        return dosis.stripTrailingZeros().toPlainString() + " " + unidadMedida;
    }
}
