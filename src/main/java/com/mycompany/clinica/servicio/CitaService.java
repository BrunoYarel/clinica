package com.mycompany.clinica.servicio;

import com.mycompany.clinica.dao.CitaDAO;
import com.mycompany.clinica.modelo.Cita;
import com.mycompany.clinica.modelo.EstadoCita;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class CitaService {

    private static final Logger LOGGER = Logger.getLogger(CitaService.class.getName());

    private final CitaDAO citaDAO = new CitaDAO();

    /**
     * Regla de negocio para registrar una cita.
     * Valida que los campos esenciales no estén vacíos antes de enviarlos a la BD.
     *
     * @return false si los datos están incompletos o son inválidos (no se toca la BD)
     * @throws SQLException si la base de datos rechaza el registro
     * @throws ReglaNegocioException si el rol de la sesión no puede gestionar citas
     */
    public boolean agendarNuevaCita(Cita cita) throws SQLException, ReglaNegocioException {
        if (!Permisos.puedeGestionarCitas()) {
            throw new ReglaNegocioException("No tiene permiso para gestionar citas.");
        }
        if (cita.getFechaCita() == null || cita.getFechaCita().trim().isEmpty()
                || cita.getHoraCita() == null || cita.getHoraCita().trim().isEmpty()
                || cita.getIdPaciente() <= 0 || cita.getIdMedico() <= 0) {

            LOGGER.warning("Datos de la cita incompletos o inválidos.");
            return false;
        }

        return citaDAO.registrarCita(cita);
    }

    public List<Cita> obtenerTodasLasCitas() throws SQLException {
        return citaDAO.listarCitas();
    }

    /**
     * Confirma (Programada) o cancela (Cancelada) una cita. Confirmar solo se puede desde Pendiente;
     * cancelar, desde Pendiente, Programada o En Espera (nunca una cita Atendida o ya Cancelada).
     *
     * @return false si el ID o el estado no son válidos, o si no existe esa cita
     * @throws ReglaNegocioException si no hay permiso, el destino no es Programada ni Cancelada,
     *         o el estado actual de la cita no permite el cambio
     */
    public boolean actualizarEstado(int idCita, EstadoCita nuevoEstado) throws SQLException, ReglaNegocioException {
        if (!Permisos.puedeGestionarCitas()) {
            throw new ReglaNegocioException("No tiene permiso para gestionar citas.");
        }
        if (idCita <= 0 || nuevoEstado == null) {
            LOGGER.warning("ID o estado de la cita inválido.");
            return false;
        }

        EstadoCita[] estadosOrigen;
        if (nuevoEstado == EstadoCita.PROGRAMADA) {
            estadosOrigen = new EstadoCita[]{EstadoCita.PENDIENTE};
        } else if (nuevoEstado == EstadoCita.CANCELADA) {
            estadosOrigen = new EstadoCita[]{EstadoCita.PENDIENTE, EstadoCita.PROGRAMADA, EstadoCita.EN_ESPERA};
        } else {
            throw new ReglaNegocioException("Solo se puede confirmar o cancelar una cita.");
        }

        if (citaDAO.modificarEstadoCita(idCita, nuevoEstado, estadosOrigen)) {
            return true;
        }

        String estadoActual = citaDAO.obtenerEstadoCita(idCita);
        if (estadoActual == null) {
            return false;
        }
        throw new ReglaNegocioException(motivoCambioNoPermitido(estadoActual, nuevoEstado));
    }

    /** Mensaje para el usuario cuando el estado actual de la cita no permite pasar a nuevoEstado. */
    private static String motivoCambioNoPermitido(String estadoActual, EstadoCita nuevoEstado) {
        if (nuevoEstado == EstadoCita.PROGRAMADA) {
            return "Solo se puede confirmar una cita pendiente.";
        }
        if (EstadoCita.ATENDIDA.getValor().equals(estadoActual)) {
            return "Esta cita ya fue atendida y no se puede cancelar.";
        }
        if (EstadoCita.CANCELADA.getValor().equals(estadoActual)) {
            return "Esta cita ya está cancelada.";
        }
        return "El estado de la cita (" + estadoActual + ") no permite cancelarla.";
    }
}