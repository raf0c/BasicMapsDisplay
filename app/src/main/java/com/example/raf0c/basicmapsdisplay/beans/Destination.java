package com.example.raf0c.basicmapsdisplay.beans;

/**
 * Created by raf0c on 22/06/15.
 */
public class Destination {

    public String title;
    public Double latitud;
    public Double longitud;
    public int position;


    public Destination(String title, Double latitud, Double longitud, int position) {
        this.title = title;
        this.latitud = latitud;
        this.longitud = longitud;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
