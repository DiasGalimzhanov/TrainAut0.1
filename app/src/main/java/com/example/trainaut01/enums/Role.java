package com.example.trainaut01.enums;

import lombok.Getter;

/**
 * Перечисление для представления ролей пользователей в системе.
 * Содержит варианты пользователь и администратор.
 */
@Getter
public enum Role {
    USER("Пользователь"),
    ADMIN("Админ");

    private final String displayName;

    /**
     * Конструктор для задания локализованного названия.
     *
     * @param displayName локализованное название роли.
     */
    Role(String displayName) {
        this.displayName = displayName;
    }


    /**
     * Преобразует строку в объект Role.
     * Сравнивает строку с локализованным названием роли (displayName).
     *
     * @param text строка для преобразования.
     * @return соответствующий объект Role.
     * @throws IllegalArgumentException если текст не соответствует ни одной роли.
     */
    public static Role fromString(String text) {
        for (Role role : Role.values()) {
            if (role.displayName.equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown gender: " + text);
    }
}
