package org.example.dominio;
import java.time.LocalDate;

public class Paciente {
    private int id;
    private String nombre;
    private String direccion;
    private String telefono;
    private LocalDate fechaNacimiento;
    private byte genero;

    public Paciente() {
    }

    public Paciente(int id, String nombre, String direccion, String telefono, LocalDate fechaNacimiento, byte genero) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public byte getGenero() {
        return genero;
    }

    public void setGenero(byte genero) {
        this.genero = genero;
    }

    public String getStrGenero() {
        String str = "";
        switch (genero) {
            case 1:
                str = "Masculino";
                break;
            case 2:
                str = "Femenino";
                break;
            case 3:
                str = "Otro";
                break;
            default:
                str = "Desconocido";
        }
        return str;
    }
}
