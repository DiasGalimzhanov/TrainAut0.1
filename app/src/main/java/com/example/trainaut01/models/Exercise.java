package com.example.trainaut01.models;

import java.io.Serializable;

import kotlin.jvm.internal.SerializedIr;

public class Exercise implements Serializable {

    public enum ExerciseType {
        BICEPS, PECTORAL_MUSCLES, TRICEPS, DELTOID_MUSCLES, PRESS, UPPER_BACK_MUSCLES,
        QUADRICEPS, LOWER_BACK_MUSCLES, OTHER
    }

    private String id;
    private ExerciseType type;
    private String name;
    private String description;
    private String imageUrl;
    private boolean isCompleted;
    private int sets;
    private int reps;
    private float restTime;

    public Exercise() {
    }

    public Exercise(String id, ExerciseType type, String name, String description, String imageUrl, boolean isCompleted, int sets, int reps, float restTime) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isCompleted = isCompleted;
        setSets(sets);
        setReps(reps);
        setRestTime(restTime);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ExerciseType getType() {
        return type;
    }

    public void setType(ExerciseType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (sets > 0) {
            this.sets = sets;
        }
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        if (reps > 0) {
            this.reps = reps;
        }
    }

    public float getRestTime() {
        return restTime;
    }

    public void setRestTime(float restTime) {
        if (restTime >= 0) {
            this.restTime = restTime;
        }
    }
}
