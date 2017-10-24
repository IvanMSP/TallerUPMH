package com.example.bawbam.blogupmh;

/**
 * Created by Bawbam on 24/10/2017.
 */

public class Blog {

    private String titulo;
    private String descripcion;
    private String image;

    public Blog(){

    }

    public Blog(String titulo, String descripcion, String image){
        this.titulo = titulo;
        this.descripcion = descripcion;
        //Constructor
        this.image = image;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
