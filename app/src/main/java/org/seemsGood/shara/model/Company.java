package org.seemsGood.shara.model;

public class Company {

    private String name;
    private String key;
    private String typeKey;
    private float rating;

    private String description;
    private float maxPointsPercentage;


    public Company() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getMaxPointsPercentage() {
        return maxPointsPercentage;
    }

    public void setMaxPointsPercentage(float maxPointsPercentage) {
        this.maxPointsPercentage = maxPointsPercentage;
    }
}
