package com.microservices.dto;

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
    private boolean verificada;

    public HabitacionDTO() {
    }

    public HabitacionDTO(int id, String ciudad, String direccion, int capacidad,
                         float precioNoche, String descripcion,
                         List<Servicio> serviciosIncluidos,
                         List<Servicio> serviciosAdicionales,
                         List<String> imagenes, boolean verificada) {
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

    public int getId() { return id; }
    public String getCiudad() { return ciudad; }
    public String getDireccion() { return direccion; }
    public int getCapacidad() { return capacidad; }
    public float getPrecioNoche() { return precioNoche; }
    public String getDescripcion() { return descripcion; }
    public List<Servicio> getServiciosIncluidos() { return serviciosIncluidos; }
    public List<Servicio> getServiciosAdicionales() { return serviciosAdicionales; }
    public List<String> getImagenes() { return imagenes; }
    public boolean isVerificada() { return verificada; }

    public void setId(int id) { this.id = id; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public void setPrecioNoche(float precioNoche) { this.precioNoche = precioNoche; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setServiciosIncluidos(List<Servicio> serviciosIncluidos) {
        this.serviciosIncluidos = serviciosIncluidos;
    }
    public void setServiciosAdicionales(List<Servicio> serviciosAdicionales) {
        this.serviciosAdicionales = serviciosAdicionales;
    }
    public void setImagenes(List<String> imagenes) { this.imagenes = imagenes; }
    public void setVerificada(boolean verificada) { this.verificada = verificada; }
}