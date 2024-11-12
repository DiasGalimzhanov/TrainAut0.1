package com.example.trainaut01.models;

import com.example.trainaut01.enums.FineMotorMuscleGroup;
import com.example.trainaut01.enums.GrossMotorMuscleGroup;
import com.example.trainaut01.enums.MotorSkillGroup;

import java.io.Serializable;
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

    private void initializeFromMap(Map<String, Object> map) {
        this.id = (String) map.get("id");

        String motorSkillTypeStr = (String) map.get("motorSkillType");
        if (motorSkillTypeStr != null) {
            // Определяем тип моторики (крупная или мелкая)
            if (motorSkillTypeStr.equalsIgnoreCase("GROSS_MOTOR")) {
                this.motorSkillType = MotorSkillGroup.GROSS_MOTOR;
            } else if (motorSkillTypeStr.equalsIgnoreCase("FINE_MOTOR")) {
                this.motorSkillType = MotorSkillGroup.FINE_MOTOR;
            }
        }

        String muscleGroupStr = (String) map.get("muscleGroup");
        if (muscleGroupStr != null) {
            if (this.motorSkillType == MotorSkillGroup.GROSS_MOTOR) {
                this.muscleGroup = GrossMotorMuscleGroup.valueOf(muscleGroupStr.toUpperCase());
            } else if (this.motorSkillType == MotorSkillGroup.FINE_MOTOR) {
                this.muscleGroup = FineMotorMuscleGroup.valueOf(muscleGroupStr.toUpperCase());
            }
        }

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

    public void setRewardPoints(int rewardPoints) {
        if (rewardPoints > 0) {
            this.rewardPoints = rewardPoints;
        }
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

        String motorSkillTypeStr = (String) map.get("motorSkillType");
        if (motorSkillTypeStr != null) {
            if (motorSkillTypeStr.equalsIgnoreCase("GROSS_MOTOR")) {
                exercise.setMotorSkillType(MotorSkillGroup.GROSS_MOTOR);
            } else if (motorSkillTypeStr.equalsIgnoreCase("FINE_MOTOR")) {
                exercise.setMotorSkillType(MotorSkillGroup.FINE_MOTOR);
            }
        }

        String muscleGroupStr = (String) map.get("muscleGroup");
        if (muscleGroupStr != null) {
            if (exercise.getMotorSkillType() == MotorSkillGroup.GROSS_MOTOR) {
                exercise.setMuscleGroup(GrossMotorMuscleGroup.valueOf(muscleGroupStr.toUpperCase()));
            } else if (exercise.getMotorSkillType() == MotorSkillGroup.FINE_MOTOR) {
                exercise.setMuscleGroup(FineMotorMuscleGroup.valueOf(muscleGroupStr.toUpperCase()));
            }
        }

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

        exercise.setExercisePurpose((String) map.get("exercisePurpose"));

        return exercise;
    }

}
