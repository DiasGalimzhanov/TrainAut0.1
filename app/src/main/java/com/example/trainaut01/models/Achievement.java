package com.example.trainaut01.models;

public class Achievement {
    private int day;
    private String imageUrl;
    private String description;

    public Achievement(int day, String imageUrl, String description) {
        this.day = day;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public int getDay() {
        return day;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }
}

