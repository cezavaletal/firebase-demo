package com.deathstudio.marcos.adoptaunperro.pojo;

public class ChatConsulta {

    private String mensaje;
    private String nombre;

    public ChatConsulta(){}

    public ChatConsulta(String mensaje, String nombre) {
        this.mensaje = mensaje;
        this.nombre = nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}