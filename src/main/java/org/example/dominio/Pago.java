package org.example.dominio;

import java.time.LocalDateTime; // Usaremos LocalDateTime para fechas y horas modernas

public class Pago {
    private int id;
    private int citaId;
    private float monto;
    private LocalDateTime fechaPago; // Usaremos LocalDateTime para la fecha de pago

    // Constructor vacío (opcional, pero útil para frameworks)
    public Pago() {
    }

    // Constructor con todos los campos (excepto ID, que es autoincremental)
    public Pago(int citaId, float monto, LocalDateTime fechaPago) {
        this.citaId = citaId;
        this.monto = monto;
        this.fechaPago = fechaPago;
    }

    // --- Getters y Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCitaId() {
        return citaId;
    }

    public void setCitaId(int citaId) {
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

    // Opcional: Método toString para una fácil depuración
    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", citaId=" + citaId +
                ", monto=" + monto +
                ", fechaPago=" + fechaPago +
                '}';
    }
}


