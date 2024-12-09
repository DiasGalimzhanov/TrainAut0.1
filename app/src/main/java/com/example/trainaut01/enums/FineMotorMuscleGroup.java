package com.example.trainaut01.enums;


import lombok.Getter;

@Getter
public enum FineMotorMuscleGroup {
    HAND_CONTROL("Развитие пальцев и кистей"),
    FINGER_STRENGTH("Сила пальцев"),
    COORDINATION("Координация и точность"),
    SENSORY_PERCEPTION("Сенсорные упражнения и тактильное восприятие");

    private final String displayName;

    FineMotorMuscleGroup(String displayName) {
        this.displayName = displayName;
    }

    public static FineMotorMuscleGroup fromString(String value) {
        for (FineMotorMuscleGroup group : values()) {
            if (group.name().equalsIgnoreCase(value)) {
                return group;
            }
        }
        throw new IllegalArgumentException("Invalid FineMotorMuscleGroup: " + value);
    }
}
