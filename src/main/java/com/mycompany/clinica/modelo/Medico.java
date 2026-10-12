package com.mycompany.clinica.modelo;

public class Medico {
    private int idMedico;
    private String nombres;
    private String apellidos;
    private String cmp;
    private String telefono;
    private String email;
    private int idEspecialidad;
    private String especialidad; // nombre de la especialidad (solo se llena al listar, viene del JOIN)

    // Constructor vacío
    public Medico() {
    }

    // Constructor para registrar un médico nuevo
    public Medico(String nombres, String apellidos, String cmp, String telefono, String email, int idEspecialidad) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cmp = cmp;
        this.telefono = telefono;
        this.email = email;
        this.idEspecialidad = idEspecialidad;
    }

    // Getters y Setters
    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCmp() { return cmp; }
    public void setCmp(String cmp) { this.cmp = cmp; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}
