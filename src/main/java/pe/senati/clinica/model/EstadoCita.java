package pe.senati.clinica.model;

public enum EstadoCita {
    PENDIENTE("Pendiente"),
    PROGRAMADA("Programada"),
    ATENDIDA("Atendida"),
    CANCELADA("Cancelada");

    private final String texto;

    EstadoCita(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public static EstadoCita desdeTexto(String texto) {
        for (EstadoCita estado : EstadoCita.values()) {
            if (estado.getTexto().equalsIgnoreCase(texto)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado no válido: " + texto);
    }

    public boolean puedePasarA(EstadoCita nuevoEstado) {
        if (this == PENDIENTE) {
            return nuevoEstado == PROGRAMADA || nuevoEstado == CANCELADA;
        }
        if (this == PROGRAMADA) {
            return nuevoEstado == ATENDIDA || nuevoEstado == CANCELADA;
        }
        return false; // ATENDIDA y CANCELADA son estados finales
    }
}

