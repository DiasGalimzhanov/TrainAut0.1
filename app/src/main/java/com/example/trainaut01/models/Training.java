package com.example.trainaut01.models;

public class Training {
    private long trainingId;
    private String type;
    private String name;
    private String description;
    private String imageUrl;
    private boolean isCompleted;
    private int sets;
    private int reps;
    private float restTime;

    public Training(String type, String name, String description, String imageUrl, boolean isCompleted, int sets, int reps, float restTime) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isCompleted = isCompleted;
        this.sets = sets;
        this.reps = reps;
        this.restTime = restTime;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public long getTrainingId() {
        return trainingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public float getRestTime() {
        return restTime;
    }

    public void setRestTime(float restTime) {
        this.restTime = restTime;
    }
}

