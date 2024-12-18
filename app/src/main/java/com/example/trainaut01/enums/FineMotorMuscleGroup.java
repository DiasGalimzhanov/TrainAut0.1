package com.example.trainaut01.enums;


import lombok.Getter;

/**
 * Перечисление FineMotorMuscleGroup представляет различные группы мышц и навыков,
 * связанных с мелкой моторикой. Каждая группа имеет своё описательное название.
 */
@Getter
public enum FineMotorMuscleGroup {
    HAND_CONTROL("Развитие пальцев и кистей"),
    FINGER_STRENGTH("Сила пальцев"),
    COORDINATION("Координация и точность"),
    SENSORY_PERCEPTION("Сенсорные упражнения и тактильное восприятие");

    private final String displayName;

    /**
     * Конструктор перечисления для задания человекочитаемого названия.
     *
     * @param displayName Человекочитаемое название группы мелкой моторики.
     */
    FineMotorMuscleGroup(String displayName) {
        this.displayName = displayName;
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
