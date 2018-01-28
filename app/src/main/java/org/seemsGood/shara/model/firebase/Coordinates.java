package org.seemsGood.shara.model.firebase;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private float latitude;
    private float longitude;

    public Coordinates() {}

    public Coordinates(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }

}