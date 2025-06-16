package org.example.dominio;

import java.time.LocalDateTime; // Usaremos LocalDateTime para fechas y horas modernas

public class Pago {
    private int id;
    private String citaId;
    private float monto;
    private LocalDateTime fechaPago; // Usaremos LocalDateTime para la fecha de pago


    // Campos adicionales para mostrar nombres (opcional)Add commentMore actions
    private String nombrePaciente;
    private String nombreDoctor;

    // Constructor vacío (opcional, pero útil para frameworks)
    public Pago() {
    }

    // Constructor con todos los campos (excepto ID, que es autoincremental)
    public Pago(String citaId, float monto, LocalDateTime fechaPago, String nombrePaciente) {
        this.citaId = citaId;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.nombrePaciente = nombrePaciente;
    }

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCitaId() {
        return citaId;
    }

    public void setCitaId(String citaId) {
        this.citaId = citaId;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    // Nuevo Getter y Setter para nombrePaciente
    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }


}


