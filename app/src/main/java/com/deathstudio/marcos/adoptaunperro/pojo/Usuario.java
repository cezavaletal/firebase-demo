package com.deathstudio.marcos.adoptaunperro.pojo;

public class Usuario {
    private String correo;
    private String foto;
    private String nombre;
    private String proveedor;

    public Usuario(){}

    public Usuario(String correo, String foto, String nombre, String proveedor) {
        this.correo = correo;
        this.foto = foto;
        this.nombre = nombre;
        this.proveedor = proveedor;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }
}
