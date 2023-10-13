package com.example.wisataciayumajakuning.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;

public class Geometry implements Serializable {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("coordinates")
    @Expose
    private List<List<List<List<Double>>>> coordinates;
    private final static long serialVersionUID = -3174768666351849254L;

    public Geometry() {
    }

    public Geometry(String type, List<List<List<List<Double>>>> coordinates) {
        super();
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geometry withType(String type) {
        this.type = type;
        return this;
    }

    public JSONArray getCoordinates() {
        return (JSONArray) coordinates;
    }

    public void setCoordinates(List<List<List<List<Double>>>> coordinates) {
        this.coordinates = coordinates;
    }

    public Geometry withCoordinates(List<List<List<List<Double>>>> coordinates) {
        this.coordinates = coordinates;
        return this;
    }
}
