package com.example.wisataciayumajakuning.model;

import com.google.firebase.firestore.GeoPoint;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.List;

public class Area implements Serializable {
    @SerializedName("features")
    @Expose
    private List<Feature> features;
    private final static long serialVersionUID = 4497006938644946260L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Area() {
    }

    /**
     *
     * @param features
     */
    public Area(List<Feature> features) {
        super();
        this.features = features;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public Area withFeatures(List<Feature> features) {
        this.features = features;
        return this;
    }

}
