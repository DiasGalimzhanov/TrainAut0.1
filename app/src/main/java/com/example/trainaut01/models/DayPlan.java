package com.example.trainaut01.models;

import java.util.List;

public class DayPlan {
    public enum WeekDay {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    private String id;  // Добавлено поле id
    private WeekDay weekDay;
    private List<Exercise> exercises;

    public DayPlan() {}

    public DayPlan(String id, WeekDay weekDay, List<Exercise> exercises) {
        this.id = id;
        this.weekDay = weekDay;
        this.exercises = exercises;
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

    public void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }

    public void removeExercise(Exercise exercise) {
        this.exercises.remove(exercise);
    }
}
