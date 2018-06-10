package com.deathstudio.marcos.adoptaunperro.pojo;

import android.net.Uri;

public class Publicacion {

    private String uid;
    private String fotoPublicacion;
    private String genero;
    private int edad;
    private String telefono;
    private String descripcion;
    private String fecha;

    public Publicacion(){}

    public Publicacion(String uid, String fotoPublicacion, String genero, int edad, String telefono, String descripcion, String fecha) {
        this.uid = uid;
        this.fotoPublicacion = fotoPublicacion;
        this.genero = genero;
        this.edad = edad;
        this.telefono = telefono;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFotoPublicacion() {
        return fotoPublicacion;
    }

    public void setFotoPublicacion(String fotoPublicacion) {
        this.fotoPublicacion = fotoPublicacion;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
