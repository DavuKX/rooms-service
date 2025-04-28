package com.microservices.dtos;

import com.microservices.models.Servicio;

import java.util.List;

public class HabitacionDTO {
    private int id;
    private String ciudad;
    private String direccion;
    private int capacidad;
    private float precioNoche;
    private String descripcion;
    private List<Servicio> serviciosIncluidos;
    private List<Servicio> serviciosAdicionales;
    private List<String> imagenes;

    public boolean isVerificada() {
        return verificada;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public List<Servicio> getServiciosAdicionales() {
        return serviciosAdicionales;
    }

    public List<Servicio> getServiciosIncluidos() {
        return serviciosIncluidos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public float getPrecioNoche() {
        return precioNoche;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public int getId() {
        return id;
    }

    private boolean verificada;

    public HabitacionDTO(int id, String ciudad, String direccion, int capacidad, float precioNoche, String descripcion, List<Servicio> serviciosIncluidos, List<Servicio> serviciosAdicionales, List<String> imagenes, boolean verificada) {
        this.id = id;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.capacidad = capacidad;
        this.precioNoche = precioNoche;
        this.descripcion = descripcion;
        this.serviciosIncluidos = serviciosIncluidos;
        this.serviciosAdicionales = serviciosAdicionales;
        this.imagenes = imagenes;
        this.verificada = verificada;
    }
}

