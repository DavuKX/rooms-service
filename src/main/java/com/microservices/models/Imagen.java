package com.microservices.models;

public class Imagen {
    private int id;
    private int habitacionId;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHabitacionId() {
        return habitacionId;
    }

    public void setHabitacionId(int habitacionId) {
        this.habitacionId = habitacionId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Imagen(int id, int habitacionId, String url) {
        this.id = id;
        this.habitacionId = habitacionId;
        this.url = url;
    }

    public Imagen() {
    }
}
