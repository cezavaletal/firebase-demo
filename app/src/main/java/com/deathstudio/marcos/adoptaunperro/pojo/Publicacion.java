package com.deathstudio.marcos.adoptaunperro.pojo;

import android.net.Uri;

public class Publicacion {

    private Uri imagen;
    private String genero;
    private int edad;
    private String telefono;
    private String descripcion;

    public Publicacion(){}

    public Publicacion(Uri imagen, String genero, int edad, String telefono, String descripcion) {
        this.imagen = imagen;
        this.genero = genero;
        this.edad = edad;
        this.telefono = telefono;
        this.descripcion = descripcion;
    }

    public Uri getImagen() {
        return imagen;
    }

    public void setImagen(Uri imagen) {
        this.imagen = imagen;
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
}
