package com.mycompany.clinica.modelo;

/** Un medicamento del catálogo (tabla MEDICAMENTO). Se usa en FrmMedicamento y en las recetas. */
public class Medicamento {
    private int idMedicamento;
    private String nombre;
    private String presentacion;

    public Medicamento() {
    }

    // Constructor para registrar un medicamento nuevo
    public Medicamento(String nombre, String presentacion) {
        this.nombre = nombre;
        this.presentacion = presentacion;
    }

    // Constructor para cargar tablas y ComboBox desde la base de datos
    public Medicamento(int idMedicamento, String nombre, String presentacion) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.presentacion = presentacion;
    }

    public int getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(int idMedicamento) { this.idMedicamento = idMedicamento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }

    // Para que el JComboBox muestre el texto del medicamento
    @Override
    public String toString() {
        return nombre + " - " + presentacion;
    }
}
