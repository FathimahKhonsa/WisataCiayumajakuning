package com.example.wisataciayumajakuning.model;

public class Marker {
    String name, city;
    Double lat, lng;

    public Marker(){

    }

    public Marker(String name, String city, Double lat, Double lng) {
        this.name = name;
        this.city = city;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
