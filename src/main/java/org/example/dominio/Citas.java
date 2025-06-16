package org.example.dominio;

import java.time.LocalDateTime;

public class Citas {
    private int id;
    private LocalDateTime fechaHora;
    private int pacienteId;  // Cambiado a int
    private int doctorId;    // Cambiado a int
    private String estado;

    // Campos adicionales para mostrar nombres (opcional)
    private String nombrePaciente;
    private String nombreDoctor;

    public Citas(int id, LocalDateTime fechaHora, int pacienteId, int doctorId, String estado) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.pacienteId = pacienteId;
        this.doctorId = doctorId;
        this.estado = estado;
    }

    // Constructor extendido si ya vienen los nombres
    public Citas(int id, LocalDateTime fechaHora, int pacienteId, int doctorId, String estado, String nombrePaciente, String nombreDoctor) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.pacienteId = pacienteId;
        this.doctorId = doctorId;
        this.estado = estado;
        this.nombrePaciente = nombrePaciente;
        this.nombreDoctor = nombreDoctor;
    }

    public Citas() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Getters y Setters para nombres (opcional)
    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getNombreDoctor() {
        return nombreDoctor;
    }

    public void setNombreDoctor(String nombreDoctor) {
        this.nombreDoctor = nombreDoctor;
    }
}
