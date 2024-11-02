package com.example.trainaut01.models;

import java.io.Serializable;
import java.util.Map;

import kotlin.jvm.internal.SerializedIr;

public class Exercise implements Serializable {

    public enum ExerciseType {
        BICEPS, PECTORAL_MUSCLES, TRICEPS, DELTOID_MUSCLES, PRESS, UPPER_BACK_MUSCLES,
        QUADRICEPS, LOWER_BACK_MUSCLES, OTHER, FAMILY_COMPETITION;
    }

    private String id;
    private ExerciseType type;
    private String name;
    private String description;
    private String imageUrl;
    private String duration;
    private boolean isCompleted;
    private int sets;
    private int reps;
    private int rewardPoints;
    private float completedTime;
    private float restTime;

    public Exercise() {
    }

    public Exercise(String id, ExerciseType type, String name, String description, String imageUrl, boolean isCompleted, int sets, int reps, String duration, int rewardPoints, float completedTime, float restTime) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isCompleted = isCompleted;
        this.completedTime = completedTime;
        this.duration = duration;
        setRewardPoints(rewardPoints);
        setSets(sets);
        setReps(reps);
        setRestTime(restTime);
    }

    public Exercise(Map<String, Object> map) {
        initializeFromMap(map);
    }

    private void initializeFromMap(Map<String, Object> map) {
        this.id = (String) map.get("id");

        // Преобразование строки в ExerciseType
        String typeStr = (String) map.get("type");
        this.type = typeStr != null ? ExerciseType.valueOf(typeStr.toUpperCase()) : null;

        this.name = (String) map.get("name");
        this.description = (String) map.get("description");
        this.imageUrl = (String) map.get("imageUrl");
        this.duration = (String) map.get("duration");
        this.isCompleted = map.get("isCompleted") != null && (boolean) map.get("isCompleted");

        this.sets = map.get("sets") != null ? ((Long) map.get("sets")).intValue() : 0;
        this.reps = map.get("reps") != null ? ((Long) map.get("reps")).intValue() : 0;
        this.rewardPoints = map.get("rewardPoints") != null ? ((Long) map.get("rewardPoints")).intValue() : 0;
        this.completedTime = map.get("completedTime") != null ? ((Double) map.get("completedTime")).floatValue() : 0f;
        this.restTime = map.get("restTime") != null ? ((Double) map.get("restTime")).floatValue() : 0f;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public float getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(float completedTime) {
        this.completedTime = completedTime;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        if (rewardPoints > 0) {
            this.rewardPoints = rewardPoints;
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

    public static Exercise fromMap(Map<String, Object> map) {
        if (map == null) return null;

        Exercise exercise = new Exercise();
        exercise.setId((String) map.get("id"));

        String typeStr = (String) map.get("type");
        exercise.setType(typeStr != null ? ExerciseType.valueOf(typeStr.toUpperCase()) : null);

        exercise.setName((String) map.get("name"));
        exercise.setDescription((String) map.get("description"));
        exercise.setImageUrl((String) map.get("imageUrl"));
        exercise.setDuration((String) map.get("duration"));
        exercise.setCompleted(map.get("isCompleted") != null && (Boolean) map.get("isCompleted"));

        exercise.setSets(map.get("sets") != null ? ((Long) map.get("sets")).intValue() : 0);
        exercise.setReps(map.get("reps") != null ? ((Long) map.get("reps")).intValue() : 0);
        exercise.setRewardPoints(map.get("rewardPoints") != null ? ((Long) map.get("rewardPoints")).intValue() : 0);
        exercise.setCompletedTime(map.get("completedTime") != null ? ((Number) map.get("completedTime")).floatValue() : 0f);
        exercise.setRestTime(map.get("restTime") != null ? ((Number) map.get("restTime")).floatValue() : 0f);

        return exercise;
    }

}
