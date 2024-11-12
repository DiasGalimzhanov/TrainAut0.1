package com.example.trainaut01.models;

import android.util.Log;

import com.example.trainaut01.enums.FineMotorMuscleGroup;
import com.example.trainaut01.enums.GrossMotorMuscleGroup;
import com.example.trainaut01.enums.WeekDay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayPlan {

    private String id;
    private WeekDay weekDay;
    private List<Exercise> exercisesGrossMotor;
    private List<Exercise> exercisesFineMotor;
    private boolean isCompleted;

    public DayPlan(Map<String, Object> map) {
        this.id = (String) map.get("id");

        String weekDayStr = (String) map.get("weekDay");
        this.weekDay = weekDayStr != null ? WeekDay.valueOf(weekDayStr.toUpperCase()) : null;

        List<Map<String, Object>> exercisesData = (List<Map<String, Object>>) map.get("exercises");
        this.exercisesGrossMotor = parseExercises(exercisesData, GrossMotorMuscleGroup.class);
        this.exercisesFineMotor = parseExercises(exercisesData, FineMotorMuscleGroup.class);

        this.isCompleted = map.get("isCompleted") != null && (boolean) map.get("isCompleted");
    }

    public static DayPlan fromMap(Map<String, Object> map) {
        Log.d("DayPlan", "Данные для создания DayPlan: " + map);
        if (map == null) return null;

        DayPlan dayPlan = new DayPlan();
        dayPlan.setId((String) map.get("id"));

        String weekDayStr = (String) map.get("weekDay");
        if (weekDayStr != null) {
            dayPlan.setWeekDay(WeekDay.valueOf(weekDayStr.toUpperCase()));
        } else {
            dayPlan.setWeekDay(null);
        }

        Boolean isCompletedValue = (Boolean) map.get("isCompleted");
        dayPlan.setCompleted(isCompletedValue != null ? isCompletedValue : false);

        List<Map<String, Object>> exercisesMap = (List<Map<String, Object>>) map.get("exercises");
        List<Exercise> exercisesGrossMotor = new ArrayList<>();
        List<Exercise> exercisesFineMotor = new ArrayList<>();
        if (exercisesMap != null) {
            for (Map<String, Object> exerciseMap : exercisesMap) {
                Exercise exercise = Exercise.fromMap(exerciseMap);

                if (exercise.getMuscleGroup() instanceof GrossMotorMuscleGroup) {
                    exercisesGrossMotor.add(exercise);
                } else if (exercise.getMuscleGroup() instanceof FineMotorMuscleGroup) {
                    exercisesFineMotor.add(exercise);
                }
            }
        }
        dayPlan.setExercisesGrossMotor(exercisesGrossMotor);
        dayPlan.setExercisesFineMotor(exercisesFineMotor);

        return dayPlan;
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
}
