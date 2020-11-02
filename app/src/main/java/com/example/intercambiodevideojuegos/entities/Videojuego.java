package com.example.intercambiodevideojuegos.entities;

import android.net.Uri;

import java.io.Serializable;

public class Videojuego implements Serializable {
    private String titulo;
    private String consola;
    private Uri foto;
    private String recojo;
    private Usuario dueñoOriginal;
    private String estado;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConsola() {
        return consola;
    }

    public void setConsola(String consola) {
        this.consola = consola;
    }

    public Uri getFoto() {
        return foto;
    }

    public void setFoto(Uri foto) {
        this.foto = foto;
    }

    public String getRecojo() {
        return recojo;
    }

    public void setRecojo(String recojo) {
        this.recojo = recojo;
    }

    public Usuario getDueñoOriginal() {
        return dueñoOriginal;
    }

    public void setDueñoOriginal(Usuario dueñoOriginal) {
        this.dueñoOriginal = dueñoOriginal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
