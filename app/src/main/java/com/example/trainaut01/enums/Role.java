package com.example.trainaut01.enums;

import lombok.Getter;

@Getter
public enum Role {
    USER("Пользователь"),
    ADMIN("Админ");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public static Role fromString(String text) {
        for (Role role : Role.values()) {
            if (role.displayName.equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown gender: " + text);
    }
}
