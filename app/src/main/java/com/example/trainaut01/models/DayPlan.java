package com.example.trainaut01.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DayPlan {
    public enum WeekDay {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    }

    private String id;
    private WeekDay weekDay;
    private List<Exercise> exercises;
    private boolean isCompleted;

    public DayPlan() {}

    public DayPlan(Map<String, Object> map) {
        this.id = (String) map.get("id");

        String weekDayStr = (String) map.get("weekDay");
        this.weekDay = weekDayStr != null ? WeekDay.valueOf(weekDayStr.toUpperCase()) : null;

        List<Map<String, Object>> exercisesData = (List<Map<String, Object>>) map.get("exercises");
        this.exercises = parseExercises(exercisesData);

        this.isCompleted = map.get("isCompleted") != null && (boolean) map.get("isCompleted");
    }

    public DayPlan(String id, WeekDay weekDay, List<Exercise> exercises, boolean isCompleted) {
        this.id = id;
        this.weekDay = weekDay;
        this.exercises = exercises;
        this.isCompleted = isCompleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WeekDay getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(WeekDay weekDay) {
        this.weekDay = weekDay;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        if (exercises == null || exercises.isEmpty()) {
            throw new IllegalArgumentException("Day plan must have at least one exercise.");
        }
        this.exercises = exercises;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }

    public void removeExercise(Exercise exercise) {
        this.exercises.remove(exercise);
    }

    public static DayPlan fromMap(Map<String, Object> map) {
        Log.d("DayPlan", "Данные для создания DayPlan: " + map);
        if (map == null) return null;

        DayPlan dayPlan = new DayPlan();
        dayPlan.setId((String) map.get("id"));

        String weekDayStr = (String) map.get("weekDay");
        if (weekDayStr != null) {
            dayPlan.setWeekDay(DayPlan.WeekDay.valueOf(weekDayStr.toUpperCase()));
        } else {
            dayPlan.setWeekDay(null);
        }

        Boolean isCompletedValue = (Boolean) map.get("isCompleted");
        dayPlan.setCompleted(isCompletedValue != null ? isCompletedValue : false);

        List<Map<String, Object>> exercisesMap = (List<Map<String, Object>>) map.get("exercises");
        List<Exercise> exercises = new ArrayList<>();
        if (exercisesMap != null) {
            for (Map<String, Object> exerciseMap : exercisesMap) {
                Exercise exercise = Exercise.fromMap(exerciseMap);
                exercises.add(exercise);
            }
        }
        dayPlan.setExercises(exercises);

        return dayPlan;
    }

    private List<Exercise> parseExercises(List<Map<String, Object>> exercisesData) {
        List<Exercise> exercises = new ArrayList<>();
        if (exercisesData != null) {
            for (Map<String, Object> exerciseData : exercisesData) {
                exercises.add(new Exercise(exerciseData));
            }
        }
        return exercises;
    }

}
