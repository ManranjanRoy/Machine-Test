package com.ventrux.androidtest.Model;

public class PlaceModel {
    String id,place;

    public PlaceModel() {
    }

    public PlaceModel(String id, String place) {
        this.id = id;
        this.place = place;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
