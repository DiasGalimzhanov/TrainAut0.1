package com.example.trainaut01.enums;

import lombok.Getter;

@Getter
public enum GrossMotorMuscleGroup {
    BICEPS("Бицепс"),
    PECTORAL_MUSCLES("Грудные мышцы"),
    TRICEPS("Трицепс"),
    DELTOID_MUSCLES("Дельтовидные мышцы"),
    PRESS("Пресс"),
    UPPER_BACK_MUSCLES("Верхняя часть спины"),
    QUADRICEPS("Квадрицепсы"),
    LOWER_BACK_MUSCLES("Нижняя часть спины"),
    FULL_BODY("Всё тело");

    private final String displayName;

    GrossMotorMuscleGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static GrossMotorMuscleGroup fromString(String value) {
        for (GrossMotorMuscleGroup group : values()) {
            if (group.name().equalsIgnoreCase(value)) {
                return group;
            }
        }
        throw new IllegalArgumentException("Invalid GrossMotorMuscleGroup: " + value);
    }
}

