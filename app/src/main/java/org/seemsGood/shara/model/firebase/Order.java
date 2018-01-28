package org.seemsGood.shara.model.firebase;

import java.io.Serializable;
import java.util.Map;

public class Order implements Serializable {

    private String orderDate;
    private String orderTime;
    private String date;

    private double fullCost;
    private int pointsSpent;
    private int pointsReceived;

    private String userId;

    private Map<String,Map<String,SimpleGood>> goods;

    private String address;

    private String status;

    public Order() {
    }

    public String getOrderDate() {
        return orderDate;
    }

    public Order setOrderDate(String orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public Order setOrderTime(String orderTime) {
        this.orderTime = orderTime;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Order setDate(String date) {
        this.date = date;
        return this;
    }

    public Map<String, Map<String, SimpleGood>> getGoods() {
        return goods;
    }

    public void setGoods(Map<String, Map<String, SimpleGood>> goods) {
        this.goods = goods;
    }

    public String getUserId() {
        return userId;
    }

    public Order setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public double getFullCost() {
        return fullCost;
    }

    public Order setFullCost(double fullCost) {
        this.fullCost = fullCost;
        return this;
    }

    public int getPointsSpent() {
        return pointsSpent;
    }

    public Order setPointsSpent(int pointsSpent) {
        this.pointsSpent = pointsSpent;
        return this;
    }

    public int getPointsReceived() {
        return pointsReceived;
    }

    public Order setPointsReceived(int pointsReceived) {
        this.pointsReceived = pointsReceived;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
