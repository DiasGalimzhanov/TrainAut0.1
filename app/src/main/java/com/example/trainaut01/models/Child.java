package com.example.trainaut01.models;

import com.example.trainaut01.enums.Gender;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Child {
    private String childId = UUID.randomUUID().toString();;
    private String fullName;
    private String birthDate;
    private Gender gender;
    private String diagnosis;
    private float height;
    private float weight;
    private int exp = 0;
    private int lvl = 0;
    private int countDays = 0;

    public Child(String fullName, String birthDate, Gender gender, String diagnosis, float height, float weight) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.diagnosis = diagnosis;
        this.height = height;
        this.weight = weight;
    }

    /**
     * Преобразует объект Child в Map<String, Object> для сохранения в Firestore.
     * @return Map с данными ребенка.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> childMap = new HashMap<>();
        childMap.put("childId", childId);
        childMap.put("fullName", fullName);
        childMap.put("birthDate", birthDate);
        childMap.put("gender", gender != null ? gender.toString() : null);
        childMap.put("diagnosis", diagnosis);
        childMap.put("height", height);
        childMap.put("weight", weight);
        childMap.put("exp", exp);
        childMap.put("lvl", lvl);
        childMap.put("countDays", countDays);
        return childMap;
    }
}
