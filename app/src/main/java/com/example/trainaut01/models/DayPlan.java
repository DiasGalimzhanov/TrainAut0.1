package com.example.trainaut01.models;

import android.util.Log;

import com.example.trainaut01.enums.FineMotorMuscleGroup;
import com.example.trainaut01.enums.GrossMotorMuscleGroup;
import com.example.trainaut01.enums.WeekDay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayPlan implements Serializable {

    private String dayPlanId;
    private WeekDay weekDay;
    private List<Exercise> exercisesGrossMotor;
    private List<Exercise> exercisesFineMotor;
    private int rewardPointsDay;


    public DayPlan(Map<String, Object> map) {
        this.dayPlanId = (String) map.get("id");

        String weekDayStr = (String) map.get("weekDay");
        this.weekDay = weekDayStr != null ? WeekDay.valueOf(weekDayStr.toUpperCase()) : null;

        List<Map<String, Object>> exercisesData = (List<Map<String, Object>>) map.get("exercises");
        this.exercisesGrossMotor = parseExercises(exercisesData, GrossMotorMuscleGroup.class);
        this.exercisesFineMotor = parseExercises(exercisesData, FineMotorMuscleGroup.class);

    }

    private List<Exercise> parseExercises(List<Map<String, Object>> exercisesData, Class<?> muscleGroupEnum) {
        List<Exercise> exercises = new ArrayList<>();
        if (exercisesData != null) {
            for (Map<String, Object> exerciseData : exercisesData) {
                Exercise exercise = new Exercise(exerciseData);

                if (muscleGroupEnum.isAssignableFrom(exercise.getMuscleGroup().getClass())) {
                    exercises.add(exercise);
                }
            }
        }
        return exercises;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> dayPlanMap = new HashMap<>();

        dayPlanMap.put("dayPlanId", dayPlanId);
        dayPlanMap.put("weekDay", weekDay != null ? weekDay.toString() : null);
        dayPlanMap.put("exercisesGrossMotor", exercisesToMap(exercisesGrossMotor));
        dayPlanMap.put("exercisesFineMotor", exercisesToMap(exercisesFineMotor));
        dayPlanMap.put("rewardPointsDay", rewardPointsDay);

        return dayPlanMap;
    }

    private List<Map<String, Object>> exercisesToMap(List<Exercise> exercises) {
        List<Map<String, Object>> exercisesMap = new ArrayList<>();
        if (exercises != null) {
            for (Exercise exercise : exercises) {
                exercisesMap.add(exercise.toMap());
            }
        }
        return exercisesMap;
    }

}
