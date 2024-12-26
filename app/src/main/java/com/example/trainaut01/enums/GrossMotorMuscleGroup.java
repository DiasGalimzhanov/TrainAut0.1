package com.example.trainaut01.enums;

import android.content.Context;

import com.example.trainaut01.R;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * Перечисление GrossMotorMuscleGroup представляет различные крупные мышечные группы.
 * Оно используется для классификации упражнений на крупную моторику.
 */
@Getter
public enum GrossMotorMuscleGroup {
    BICEPS, PECTORAL_MUSCLES, TRICEPS, DELTOID_MUSCLES, PRESS,
    UPPER_BACK_MUSCLES, QUADRICEPS, LOWER_BACK_MUSCLES, FULL_BODY;

    private static Map<GrossMotorMuscleGroup, String> localizedNames = new HashMap<>();

    /**
     * Инициализирует локализованные названия для групп мышц.
     * Метод должен быть вызван до использования {@link #getDisplayName()}.
     *
     * @param context Контекст для доступа к ресурсам строк.
     */
    public static void initializeLocalizedNames(Context context) {
        localizedNames.put(BICEPS, context.getString(R.string.biceps));
        localizedNames.put(PECTORAL_MUSCLES, context.getString(R.string.pectoral_muscles));
        localizedNames.put(TRICEPS, context.getString(R.string.triceps));
        localizedNames.put(DELTOID_MUSCLES, context.getString(R.string.deltoid_muscles));
        localizedNames.put(PRESS, context.getString(R.string.press));
        localizedNames.put(UPPER_BACK_MUSCLES, context.getString(R.string.upper_back_muscles));
        localizedNames.put(QUADRICEPS, context.getString(R.string.quadriceps));
        localizedNames.put(LOWER_BACK_MUSCLES, context.getString(R.string.lower_back_muscles));
        localizedNames.put(FULL_BODY, context.getString(R.string.full_body));
    }

    /**
     * Возвращает локализованное название группы мышц.
     * Если локализованное название отсутствует, возвращается имя элемента перечисления.
     *
     * @return Локализованное название группы мышц или имя элемента перечисления.
     * @throws IllegalStateException Если локализованные названия не были инициализированы.
     */
    public String getDisplayName() {
        if (localizedNames.isEmpty()) {
            throw new IllegalStateException("Localized names are not initialized. Call initializeLocalizedNames() first.");
        }
        return localizedNames.getOrDefault(this, name());
    }

    /**
     * Преобразует строку в соответствующий элемент перечисления {@link GrossMotorMuscleGroup}.
     *
     * @param value Название группы мышц (например, "BICEPS").
     * @return Соответствующий элемент перечисления.
     * @throws IllegalArgumentException Если переданное значение не соответствует ни одной группе мышц.
     */
    public static GrossMotorMuscleGroup fromString(String value) {
        for (GrossMotorMuscleGroup group : values()) {
            if (group.name().equalsIgnoreCase(value)) {
                return group;
            }
        }
        throw new IllegalArgumentException("Invalid GrossMotorMuscleGroup: " + value);
    }
}

