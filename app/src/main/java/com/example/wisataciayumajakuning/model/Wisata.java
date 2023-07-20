package com.example.wisataciayumajakuning.model;

public class Wisata {
    private String name, category, city, description, idWisata, image, type;

    public Wisata(){

    }

    public Wisata(String name, String category, String city, String description, String idWisata, String image, String type) {
        this.name = name;
        this.category = category;
        this.city = city;
        this.description = description;
        this.idWisata = idWisata;
        this.image = image;
        this.type = type;
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
}
