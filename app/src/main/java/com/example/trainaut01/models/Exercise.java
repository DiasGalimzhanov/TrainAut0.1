package com.example.trainaut01.models;

import android.util.Log;

import com.example.trainaut01.enums.FineMotorMuscleGroup;
import com.example.trainaut01.enums.GrossMotorMuscleGroup;
import com.example.trainaut01.enums.MotorSkillGroup;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exercise implements Serializable {

    private String id;
    private MotorSkillGroup motorSkillType;
    private Object muscleGroup;
    private String name;
    private String description;
    private String imageUrl;
    private String duration;
    private int sets;
    private int reps;
    private int rewardPoints;
    private float completedTime;
    private float restTime;

    public Exercise(Map<String, Object> map) {
        this.initializeFromMap(map);
    }

    /**
     * Инициализирует поля объекта на основе данных из переданной карты.
     * Предполагается, что карта содержит ключи, соответствующие полям класса.
     *
     * @param map Карта с данными упражнения.
     */
    public void initializeFromMap(Map<String, Object> map) {
        this.id = (String) map.get("id");

        String motorSkillTypeStr = (String) map.get("motorSkillType");
        if (motorSkillTypeStr != null) {
            if (motorSkillTypeStr.equalsIgnoreCase("GROSS_MOTOR")) {
                this.motorSkillType = MotorSkillGroup.GROSS_MOTOR;
            } else if (motorSkillTypeStr.equalsIgnoreCase("FINE_MOTOR")) {
                this.motorSkillType = MotorSkillGroup.FINE_MOTOR;
            }
        }

        String muscleGroupStr = (String) map.get("muscleGroup");
        try {
            if (this.motorSkillType == MotorSkillGroup.GROSS_MOTOR) {
                this.muscleGroup = GrossMotorMuscleGroup.valueOf(muscleGroupStr.toUpperCase());
            } else if (this.motorSkillType == MotorSkillGroup.FINE_MOTOR) {
                this.muscleGroup = FineMotorMuscleGroup.valueOf(muscleGroupStr.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            Log.e("initializeFromMap", "Invalid muscle group: " + muscleGroupStr, e);
            this.muscleGroup = null;
        }

        this.name = (String) map.get("name");
        this.description = (String) map.get("description");
        this.imageUrl = (String) map.get("imageUrl");
        this.duration = (String) map.get("duration");

        this.sets = map.get("sets") != null ? ((Long) map.get("sets")).intValue() : 0;
        this.reps = map.get("reps") != null ? ((Long) map.get("reps")).intValue() : 0;
        this.rewardPoints = map.get("rewardPoints") != null ? ((Long) map.get("rewardPoints")).intValue() : 0;

        this.completedTime = map.get("completedTime") != null ? ((Number) map.get("completedTime")).floatValue() : 0f;
        this.restTime = map.get("restTime") != null ? ((Number) map.get("restTime")).floatValue() : 0f;
    }

    /**
     * Преобразует объект Exercise в Map<String, Object> для удобной сериализации и хранения.
     *
     * @return Карта, представляющая объект Exercise.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> exerciseMap = new HashMap<>();
        exerciseMap.put("id", id);
        exerciseMap.put("motorSkillType", motorSkillType != null ? motorSkillType.toString() : null);
        exerciseMap.put("muscleGroup", muscleGroup != null ? muscleGroup.toString() : null);
        exerciseMap.put("name", name);
        exerciseMap.put("description", description);
        exerciseMap.put("imageUrl", imageUrl);
        exerciseMap.put("duration", duration);
        exerciseMap.put("sets", sets);
        exerciseMap.put("reps", reps);
        exerciseMap.put("rewardPoints", rewardPoints);
        exerciseMap.put("completedTime", completedTime);
        exerciseMap.put("restTime", restTime);
        return exerciseMap;
    }

    /**
     * Преобразует объекt Exercise в JSON-объект (JSONObject).
     * Используется для сериализации в формат JSON.
     *
     * @return JSONObject, содержащий название упражнения и прошедшее время.
     */
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", this.getName());
            jsonObject.put("timeElapsed", this.getCompletedTime());
        } catch (Exception e) {
            Log.e("Exercise", "Ошибка при преобразовании объекта в JSON: " + e.getMessage(), e);
        }
        return jsonObject;
    }

}
