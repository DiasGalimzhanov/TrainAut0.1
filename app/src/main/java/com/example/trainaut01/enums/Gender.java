package com.example.trainaut01.enums;

import lombok.Getter;

/**
 * Перечисление для представления гендера.
 * Содержит варианты мужской, женский и другое.
 */
@Getter
public enum Gender {
    MALE("Мужской"),
    FEMALE("Женский"),
    OTHER("Другое");

    private final String displayName;

    /**
     * Конструктор для задания локализованного названия.
     *
     * @param displayName локализованное название гендера.
     */
    Gender(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает массив локализованных названий гендеров.
     *
     * @return массив строк с названиями гендеров.
     */
    public static String[] getGenderValues() {
        Gender[] values = Gender.values();
        String[] genderNames = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            genderNames[i] = values[i].getDisplayName();
        }
        return genderNames;
    }

    /**
     * Преобразует строку в объект Gender.
     * Сравнивает строку с названием объекта (name) и локализованным названием (displayName).
     *
     * @param text строка для преобразования.
     * @return соответствующий объект Gender.
     * @throws IllegalArgumentException если текст не соответствует ни одному значению.
     */
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
