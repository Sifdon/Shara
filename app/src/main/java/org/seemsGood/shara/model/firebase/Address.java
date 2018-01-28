package org.seemsGood.shara.model.firebase;


import java.io.Serializable;

public abstract class Address implements Serializable{

    private String place;
    private Coordinates coordinates;

    Address() {}

    Address(String place, Coordinates coordinates) {
        this.place = place;
        this.coordinates = coordinates;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

}
