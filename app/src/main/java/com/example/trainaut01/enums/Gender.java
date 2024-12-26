package com.example.trainaut01.enums;

import android.content.Context;

import com.example.trainaut01.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Перечисление для представления гендера.
 * Содержит варианты: мужской, женский и другое.
 */
public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    private static Map<Gender, String> localizedNames = new HashMap<>();

    /**
     * Инициализирует локализованные названия для гендеров.
     * Метод должен быть вызван до использования {@link #getDisplayName()}.
     *
     * @param context Контекст для доступа к ресурсам строк.
     */
    public static void initializeLocalizedNames(Context context) {
        localizedNames.put(MALE, context.getString(R.string.gender_male));
        localizedNames.put(FEMALE, context.getString(R.string.gender_female));
        localizedNames.put(OTHER, context.getString(R.string.gender_other));
    }

    /**
     * Возвращает локализованное название гендера.
     * Если локализованное название отсутствует, возвращается имя элемента перечисления.
     *
     * @return Локализованное название гендера или имя элемента перечисления.
     * @throws IllegalStateException Если локализованные названия не были инициализированы.
     */
    public String getDisplayName() {
        if (localizedNames.isEmpty()) {
            throw new IllegalStateException("Localized names are not initialized. Call initializeLocalizedNames() first.");
        }
        return localizedNames.getOrDefault(this, name());
    }

    /**
     * Возвращает массив локализованных названий гендеров.
     *
     * @return массив строк с названиями гендеров.
     */
    public static String[] getGenderValues() {
        if (localizedNames.isEmpty()) {
            throw new IllegalStateException("Localized names are not initialized. Call initializeLocalizedNames() first.");
        }
        Gender[] values = Gender.values();
        String[] genderNames = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            genderNames[i] = values[i].getDisplayName();
        }
        return genderNames;
    }

    /**
     * Преобразует строку в объект Gender.
     * Сравнивает строку с названием объекта (name) и локализованным названием.
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
            if (gender.name().equalsIgnoreCase(text) || gender.getDisplayName().equalsIgnoreCase(text)) {
                return gender;
            }
        }

        throw new IllegalArgumentException("Unknown gender: " + text);
    }
}
