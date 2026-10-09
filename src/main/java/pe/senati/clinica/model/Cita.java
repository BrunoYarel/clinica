package pe.senati.clinica.model;

import java.time.LocalDateTime;

public class Cita {
    private Long idCita;
    private Object paciente; // Se usa Object temporalmente hasta que creen Paciente
    private Object medico;   // Se usa Object temporalmente hasta que creen Medico
    private LocalDateTime fechaHoraCita;
    private EstadoCita estado;

    public Cita() {
    }

    public Long getIdCita() {
        return idCita;
    }

    public void setIdCita(Long idCita) {
        this.idCita = idCita;
    }

    public Object getPaciente() {
        return paciente;
    }

    public void setPaciente(Object paciente) {
        this.paciente = paciente;
    }

    public Object getMedico() {
        return medico;
    }

    public void setMedico(Object medico) {
        this.medico = medico;
    }

    public LocalDateTime getFechaHoraCita() {
        return fechaHoraCita;
    }

    public void setFechaHoraCita(LocalDateTime fechaHoraCita) {
        this.fechaHoraCita = fechaHoraCita;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }
}
