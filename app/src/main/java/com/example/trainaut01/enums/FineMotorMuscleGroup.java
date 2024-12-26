package com.example.trainaut01.enums;


import android.content.Context;

import com.example.trainaut01.R;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * Перечисление FineMotorMuscleGroup представляет различные группы мышц и навыков,
 * связанных с мелкой моторикой. Каждая группа имеет своё описательное название.
 */
@Getter
public enum FineMotorMuscleGroup {
    HAND_CONTROL,
    FINGER_STRENGTH,
    COORDINATION,
    SENSORY_PERCEPTION;

    private static Map<FineMotorMuscleGroup, String> localizedNames = new HashMap<>();

    /**
     * Инициализирует локализованные названия для групп мышц.
     * Метод должен быть вызван до использования {@link #getDisplayName()}.
     *
     * @param context Контекст для доступа к ресурсам строк.
     */
    public static void initializeLocalizedNames(Context context) {
        localizedNames.put(HAND_CONTROL, context.getString(R.string.hand_control));
        localizedNames.put(FINGER_STRENGTH, context.getString(R.string.finger_strength));
        localizedNames.put(COORDINATION, context.getString(R.string.coordination));
        localizedNames.put(SENSORY_PERCEPTION, context.getString(R.string.sensory_perception));
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
     * Преобразует строку в соответствующее перечисление FineMotorMuscleGroup.
     *
     * @param value Строковое представление группы.
     * @return Соответствующее значение перечисления FineMotorMuscleGroup.
     * @throws IllegalArgumentException Если передано недопустимое значение.
     */
    public static FineMotorMuscleGroup fromString(String value) {
        for (FineMotorMuscleGroup group : values()) {
            if (group.name().equalsIgnoreCase(value)) {
                return group;
            }
        }
        throw new IllegalArgumentException("Invalid FineMotorMuscleGroup: " + value);
    }
}
