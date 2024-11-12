package com.example.trainaut01.enums;

public enum FineMotorMuscleGroup {
    HAND_CONTROL("Развитие пальцев и кистей"),
    FINGER_STRENGTH("Сила пальцев"),
    COORDINATION("Координация и точность"),
    SENSORY_PERCEPTION("Сенсорные упражнения и тактильное восприятие"),
    SKILL_REINFORCEMENT("Закрепление навыков");

    private final String displayName;

    FineMotorMuscleGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
