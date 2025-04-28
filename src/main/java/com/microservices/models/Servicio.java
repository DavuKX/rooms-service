package com.microservices.models;

public class Servicio {
    private int id;
    private String nombre;
    private String tipoServicio;

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

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public Servicio(int id, String nombre, String tipoServicio) {
        this.id = id;
        this.nombre = nombre;
        this.tipoServicio = tipoServicio;
    }

    public Servicio() {
    }
}
