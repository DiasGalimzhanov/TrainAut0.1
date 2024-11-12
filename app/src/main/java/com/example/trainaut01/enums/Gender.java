package com.example.trainaut01.enums;

public enum Gender {
    MALE, FEMALE;

    public static String[] getGenderValues() {
        Gender[] values = Gender.values();
        String[] genderNames = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            genderNames[i] = values[i].name();
        }
        return genderNames;
    }
}
