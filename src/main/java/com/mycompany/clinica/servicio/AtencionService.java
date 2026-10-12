package com.mycompany.clinica.servicio;

import com.mycompany.clinica.dao.AgendaDAO;
import com.mycompany.clinica.modelo.Atencion;
import com.mycompany.clinica.modelo.CitaAgenda;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Reglas de negocio de la agenda y las atenciones del médico.
 * Comprueba el rol y que la sesión esté vinculada a un médico ANTES de tocar la base de datos,
 * y toma siempre el médico de la sesión (nunca de un parámetro), así un médico solo ve y atiende lo suyo.
 */
public class AtencionService {

    /** Texto de RECETA.indicaciones (NOT NULL) cuando el médico no escribe indicaciones generales. */
    public static final String INDICACIONES_POR_DEFECTO = "Seguir las indicaciones de cada medicamento.";

    private final AgendaDAO agendaDAO = new AgendaDAO();

    /** Citas de la fecha indicada del médico que inició sesión. */
    public List<CitaAgenda> obtenerAgenda(LocalDate fecha) throws SQLException, ReglaNegocioException {
        int idMedico = medicoDeLaSesion();
        if (fecha == null) {
            throw new ReglaNegocioException("Seleccione una fecha.");
        }
        return agendaDAO.listarCitasDelDia(idMedico, fecha);
    }

    /**
     * Guarda la atención y, si tiene medicamentos, la receta. Lanza ReglaNegocioException con un
     * mensaje claro si no hay permiso, faltan datos o la cita no se puede atender.
     */
    public void registrarAtencion(Atencion atencion) throws SQLException, ReglaNegocioException {
        if (!Permisos.puedeRegistrarAtencion()) {
            throw new ReglaNegocioException("Solo un médico puede registrar atenciones.");
        }
        int idMedico = medicoDeLaSesion();

        if (atencion == null || atencion.getIdCita() <= 0) {
            throw new ReglaNegocioException("Seleccione la cita que desea atender.");
        }
        if (esVacio(atencion.getSintomas()) || esVacio(atencion.getDiagnostico())) {
            throw new ReglaNegocioException("Los síntomas y el diagnóstico son obligatorios.");
        }
        if (esVacio(atencion.getObservaciones())) {
            atencion.setObservaciones(null);
        }

        if (!atencion.getDetalles().isEmpty()) {
            if (!Permisos.puedeEmitirReceta()) {
                throw new ReglaNegocioException("Solo un médico puede emitir recetas.");
            }
            if (esVacio(atencion.getIndicaciones())) {
                atencion.setIndicaciones(INDICACIONES_POR_DEFECTO);
            }
        }

        agendaDAO.registrarAtencion(idMedico, atencion);
    }

    /** id_medico de la sesión; falla con un mensaje claro si no es médico o no está vinculado. */
    private int medicoDeLaSesion() throws ReglaNegocioException {
        if (!Permisos.puedeVer(Permisos.Pantalla.MI_AGENDA)) {
            throw new ReglaNegocioException("Solo un médico puede acceder a la agenda.");
        }
        if (Sesion.getIdMedico() <= 0) {
            throw new ReglaNegocioException(
                    "Su usuario no está vinculado a un médico. Pida al administrador que lo vincule.");
        }
        return Sesion.getIdMedico();
    }

    private static boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
