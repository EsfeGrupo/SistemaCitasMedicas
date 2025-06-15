package org.example.dominio;
import java.time.LocalDateTime;

public class Citas {
    private int id;
    private LocalDateTime fechaHora;
    private String pacienteId;
    private String doctorId;
    private String estado;

    public Citas(int id, LocalDateTime fechaHora, String pacienteId, String doctorId, String estado) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.pacienteId = pacienteId;
        this.doctorId = doctorId;
        this.estado = estado;
    }

    public Citas() {

    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return this.fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(String pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
