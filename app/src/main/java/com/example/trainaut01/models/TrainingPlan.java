package com.example.trainaut01.models;

import java.util.List;

public class TrainingPlan {
    private String id;
    private String title;
    private List<DayPlan> days;

    public TrainingPlan() {}

    public TrainingPlan(String id, String title, List<DayPlan> days) {
        this.id = id;
        setTitle(title);
        setDays(days);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title;
    }

    public List<DayPlan> getDays() {
        return days;
    }

    public void setDays(List<DayPlan> days) {
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("Training plan must have at least one day");
        }
        this.days = days;
    }

    public void addDay(DayPlan dayPlan) {
        this.days.add(dayPlan);
    }

    public void removeDay(DayPlan dayPlan) {
        this.days.remove(dayPlan);
    }
}
