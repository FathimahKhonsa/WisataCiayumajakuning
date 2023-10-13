package com.example.wisataciayumajakuning.model;

public class Wisata {
    private String name, category, city, description, idWisata, image, type, address, lat, lng;

    private String tiketUmum, tiketDewasa, tiketAnak, tiketPelajar, jamOperasional;

    public Wisata(){

    }

    public Wisata(String name, String category, String city, String description, String idWisata, String image, String type, String address, String lat, String lng
    , String tiketUmum, String tiketAnak, String tiketPelajar, String tiketDewasa, String jamOperasional) {
        this.name = name;
        this.category = category;
        this.city = city;
        this.description = description;
        this.idWisata = idWisata;
        this.image = image;
        this.type = type;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.tiketAnak = tiketAnak;
        this.tiketUmum = tiketUmum;
        this.tiketPelajar = tiketPelajar;
        this.tiketDewasa = tiketDewasa;
        this.jamOperasional = jamOperasional;
    }

    public String getTiketUmum() {
        return tiketUmum;
    }

    public void setTiketUmum(String tiketUmum) {
        this.tiketUmum = tiketUmum;
    }

    public String getTiketDewasa() {
        return tiketDewasa;
    }

    public void setTiketDewasa(String tiketDewasa) {
        this.tiketDewasa = tiketDewasa;
    }

    public String getTiketAnak() {
        return tiketAnak;
    }

    public void setTiketAnak(String tiketAnak) {
        this.tiketAnak = tiketAnak;
    }

    public String getTiketPelajar() {
        return tiketPelajar;
    }

    public void setTiketPelajar(String tiketPelajar) {
        this.tiketPelajar = tiketPelajar;
    }

    public String getJamOperasional() {
        return jamOperasional;
    }

    public void setJamOperasional(String jamOperasional) {
        this.jamOperasional = jamOperasional;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdWisata() {
        return idWisata;
    }

    public void setIdWisata(String idWisata) {
        this.idWisata = idWisata;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
