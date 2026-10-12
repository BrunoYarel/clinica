package com.mycompany.clinica.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Una fila de la agenda del médico: la cita junto con los datos del paciente.
 * Es lo que devuelve AgendaDAO.listarCitasDelDia() y lo que AgendaPanel muestra en su tabla.
 *
 * Aquí vive también la regla de qué estados se pueden atender, para que la pantalla
 * y la base de datos usen exactamente la misma.
 */
public class CitaAgenda {
    private int idCita;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private String estado;
    private int idPaciente;
    private String paciente;    // "Apellidos, Nombres"
    private String documento;

    public CitaAgenda() {
    }

    // =====================================================================
    //  REGLA: SOLO SE ATIENDEN CITAS CONFIRMADAS (Programada) O EN ESPERA
    // =====================================================================

    /** true si una cita en ese estado puede pasar a ser atendida. */
    public static boolean estadoPermiteAtender(String estado) {
        return EstadoCita.PROGRAMADA.getValor().equals(estado)
                || EstadoCita.EN_ESPERA.getValor().equals(estado);
    }

    /** Mensaje para el usuario si una cita en ese estado NO se puede atender; null si sí se puede. */
    public static String motivoNoAtendible(String estado) {
        if (estadoPermiteAtender(estado)) {
            return null;
        }
        if (EstadoCita.ATENDIDA.getValor().equals(estado)) {
            return "Esta cita ya fue atendida.";
        }
        if (EstadoCita.CANCELADA.getValor().equals(estado)) {
            return "Esta cita fue cancelada y no se puede atender.";
        }
        if (EstadoCita.PENDIENTE.getValor().equals(estado)) {
            return "Esta cita aún está pendiente de confirmación. Pida a Recepción que la confirme.";
        }
        return "El estado de la cita (" + estado + ") no permite atenderla.";
    }

    public boolean puedeAtenderse() {
        return estadoPermiteAtender(estado);
    }

    // Getters y Setters
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public String getPaciente() { return paciente; }
    public void setPaciente(String paciente) { this.paciente = paciente; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
}
