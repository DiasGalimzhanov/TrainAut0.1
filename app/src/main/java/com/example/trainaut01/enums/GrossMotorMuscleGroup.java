package com.example.trainaut01.enums;

public enum GrossMotorMuscleGroup {
    BICEPS("Бицепс"),
    PECTORAL_MUSCLES("Грудные мышцы"),
    TRICEPS("Трицепс"),
    DELTOID_MUSCLES("Дельтовидные мышцы"),
    PRESS("Пресс"),
    UPPER_BACK_MUSCLES("Верхние мышцы спины"),
    QUADRICEPS("Квадрицепс"),
    LOWER_BACK_MUSCLES("Нижние мышцы спины"),
    FULL_BODY("Всё тело");

    private final String displayName;

    GrossMotorMuscleGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

