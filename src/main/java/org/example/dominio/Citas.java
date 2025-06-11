package org.example.dominio;

public class Citas {
    private int id;
    private String fechaHora;
    private String pacienteId;
    private String doctorId;
    private String estado;

    public Citas(int id, String fechaHora, String pacienteId, String doctorId, String estado) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.pacienteId = pacienteId;
        this.doctorId = doctorId;
        this.estado = estado;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fecha) {
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
