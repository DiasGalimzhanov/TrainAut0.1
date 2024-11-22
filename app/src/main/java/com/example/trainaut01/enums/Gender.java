package com.example.trainaut01.enums;

public enum Gender {
    MALE("Мужской"),
    FEMALE("Женский"),
    OTHER("Другое");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String[] getGenderValues() {
        Gender[] values = Gender.values();
        String[] genderNames = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            genderNames[i] = values[i].getDisplayName();
        }
        return genderNames;
    }

    public static Gender fromString(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Gender string cannot be null");
        }

        for (Gender gender : Gender.values()) {
            if (gender.name().equalsIgnoreCase(text) || gender.displayName.equalsIgnoreCase(text)) {
                return gender;
            }
        }

        throw new IllegalArgumentException("Unknown gender: " + text);
    }

}
