package org.seemsGood.shara.model.firebase;

import java.io.Serializable;

public class ShopMain implements Serializable {

    private String key;
    private String name;
    private double rating;
    private ShopAddress address = null;

    public ShopMain() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ShopAddress getAddress() {
        return address;
    }

    public void setAddress(ShopAddress address) {
        this.address = address;
    }

}
