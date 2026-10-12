package com.mycompany.clinica.modelo;

/**
 * Una fila del historial clínico de un paciente: la cita y lo que se registró en su atención.
 * Es lo que devuelve PacienteDAO.consultarHistorialClinico() y lo que FrmHistorial muestra en su tabla.
 */
public class RegistroHistorial {
    private final String fechaHora;
    private final String motivo;
    private final String sintomas;
    private final String diagnostico;
    private final String observaciones;
    private final String receta;   // medicamentos recetados en esa atención, uno por línea (null si no hubo receta)

    public RegistroHistorial(String fechaHora, String motivo, String sintomas, String diagnostico, String observaciones) {
        this(fechaHora, motivo, sintomas, diagnostico, observaciones, null);
    }

    public RegistroHistorial(String fechaHora, String motivo, String sintomas, String diagnostico,
                             String observaciones, String receta) {
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.sintomas = sintomas;
        this.diagnostico = diagnostico;
        this.observaciones = observaciones;
        this.receta = receta;
    }

    public String getFechaHora() { return fechaHora; }
    public String getMotivo() { return motivo; }
    public String getSintomas() { return sintomas; }
    public String getDiagnostico() { return diagnostico; }
    public String getObservaciones() { return observaciones; }
    public String getReceta() { return receta; }
}
