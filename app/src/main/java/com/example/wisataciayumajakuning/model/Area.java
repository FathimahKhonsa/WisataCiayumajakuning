package com.example.wisataciayumajakuning.model;

import com.google.firebase.firestore.GeoPoint;

public class Area {
    Double lat, lng;

    public Area(){

    }

    public Area(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
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
