package com.mycompany.clinica.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lo que el médico registra al atender una cita: síntomas, diagnóstico y observaciones (tabla ATENCION)
 * y, opcionalmente, la receta con uno o más medicamentos (tablas RECETA y DETALLE_RECETA).
 * Si la lista de detalles está vacía no se emite receta.
 */
public class Atencion {
    private int idCita;
    private String sintomas;
    private String diagnostico;
    private String observaciones;
    private String indicaciones;   // indicaciones generales de la receta (RECETA.indicaciones)
    private final List<DetalleReceta> detalles = new ArrayList<>();

    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }

    public List<DetalleReceta> getDetalles() { return Collections.unmodifiableList(detalles); }

    public void setDetalles(List<DetalleReceta> nuevos) {
        detalles.clear();
        if (nuevos != null) {
            detalles.addAll(nuevos);
        }
    }
}
