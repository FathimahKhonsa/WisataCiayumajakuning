package com.example.wisataciayumajakuning.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.data.Geometry;

import java.io.Serializable;

public class Feature implements Serializable {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    private final static long serialVersionUID = -8111139454765368863L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Feature() {
    }

    /**
     *
     * @param geometry
     * @param id
     * @param type
     */
    public Feature(String type, Integer id, Geometry geometry) {
        super();
        this.type = type;
        this.id = id;
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Feature withType(String type) {
        this.type = type;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Feature withId(Integer id) {
        this.id = id;
        return this;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Feature withGeometry(Geometry geometry) {
        this.geometry = geometry;
        return this;
    }
}
