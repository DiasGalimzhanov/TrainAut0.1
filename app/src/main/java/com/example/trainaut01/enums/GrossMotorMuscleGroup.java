package com.example.trainaut01.enums;

import lombok.Getter;

/**
 * Перечисление GrossMotorMuscleGroup представляет различные крупные мышечные группы.
 * Оно используется для классификации упражнений на крупную моторику.
 */
@Getter
public enum GrossMotorMuscleGroup {
    BICEPS("Бицепс"),
    PECTORAL_MUSCLES("Грудные мышцы"),
    TRICEPS("Трицепс"),
    DELTOID_MUSCLES("Дельтовидные мышцы"),
    PRESS("Пресс"),
    UPPER_BACK_MUSCLES("Верхняя часть спины"),
    QUADRICEPS("Квадрицепсы"),
    LOWER_BACK_MUSCLES("Нижняя часть спины"),
    FULL_BODY("Всё тело");

    private final String displayName;

    /**
     * Конструктор перечисления для присвоения читабельного названия мышечной группе.
     *
     * @param displayName Человекочитаемое название мышечной группы.
     */
    GrossMotorMuscleGroup(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Преобразует строку в соответствующий элемент перечисления GrossMotorMuscleGroup.
     *
     * @param value Строковое название группы.
     * @return Соответствующий элемент перечисления.
     * @throws IllegalArgumentException Если значение не соответствует ни одной группе.
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

