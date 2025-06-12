package org.example.dominio;

public class Doctor {
    private int id;
    private String nombre;
    private String especialidad;
    private float experiencia;
    private byte disponibilidad;

    public Doctor() {
    }

    public Doctor(int id, String nombre, String especialidad, float experiencia, byte disponibilidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.experiencia = experiencia;
        this.disponibilidad = disponibilidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public float getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(float experiencia) {
        this.experiencia = experiencia;
    }

    public byte getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(byte disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getStrDisponibilidad() {
        return (disponibilidad == 1) ? "Disponible" : "No disponible";
    }
}
