package org.seemsGood.shara.model.firebase;

import java.io.Serializable;

public class SimpleGood implements Serializable{

    private float price;
    private int count;

    public SimpleGood(float price, int count) {
        this.price = price;
        this.count = count;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
