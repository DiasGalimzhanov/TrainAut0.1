package com.example.trainaut01.models;

import com.example.trainaut01.enums.FineMotorMuscleGroup;
import com.example.trainaut01.enums.GrossMotorMuscleGroup;
import com.example.trainaut01.enums.MotorSkillGroup;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

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
    private String exercisePurpose ;
    private String description;
    private String imageUrl;
    private String duration;
    private boolean isCompleted;
    private int sets;
    private int reps;
    private int rewardPoints;
    private float completedTime;
    private float restTime;

    public Exercise(Map<String, Object> map) {
        initializeFromMap(map);
    }

    public static Exercise initializeFromMap(Map<String, Object> map) {
        Exercise exercise = new Exercise();

        exercise.id = (String) map.get("id");

        String motorSkillTypeStr = (String) map.get("motorSkillType");
        if (motorSkillTypeStr != null) {
            if (motorSkillTypeStr.equalsIgnoreCase("GROSS_MOTOR")) {
                exercise.motorSkillType = MotorSkillGroup.GROSS_MOTOR;
            } else if (motorSkillTypeStr.equalsIgnoreCase("FINE_MOTOR")) {
                exercise.motorSkillType = MotorSkillGroup.FINE_MOTOR;
            }
        }

        String muscleGroupStr = (String) map.get("muscleGroup");
        if (muscleGroupStr != null) {
            if (exercise.motorSkillType == MotorSkillGroup.GROSS_MOTOR) {
                exercise.muscleGroup = GrossMotorMuscleGroup.valueOf(muscleGroupStr.toUpperCase());
            } else if (exercise.motorSkillType == MotorSkillGroup.FINE_MOTOR) {
                exercise.muscleGroup = FineMotorMuscleGroup.valueOf(muscleGroupStr.toUpperCase());
            }
        }

        exercise.name = (String) map.get("name");
        exercise.description = (String) map.get("description");
        exercise.imageUrl = (String) map.get("imageUrl");
        exercise.duration = (String) map.get("duration");
        exercise.isCompleted = map.get("isCompleted") != null && (boolean) map.get("isCompleted");

        exercise.sets = map.get("sets") != null ? ((Long) map.get("sets")).intValue() : 0;
        exercise.reps = map.get("reps") != null ? ((Long) map.get("reps")).intValue() : 0;
        exercise.rewardPoints = map.get("rewardPoints") != null ? ((Long) map.get("rewardPoints")).intValue() : 0;

        exercise.completedTime = map.get("completedTime") != null ? ((Number) map.get("completedTime")).floatValue() : 0f;
        exercise.restTime = map.get("restTime") != null ? ((Number) map.get("restTime")).floatValue() : 0f;

        return exercise;
    }


    public void setSets(int sets) {
        if (sets > 0) {
            this.sets = sets;
        }
    }

    public void setReps(int reps) {
        if (reps > 0) {
            this.reps = reps;
        }
    }

    public void setRestTime(float restTime) {
        if (restTime >= 0) {
            this.restTime = restTime;
        }
    }

}
