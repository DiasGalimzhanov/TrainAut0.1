package com.example.trainaut01.utils;

import com.example.trainaut01.R;
import com.example.trainaut01.enums.FineMotorMuscleGroup;
import com.example.trainaut01.enums.GrossMotorMuscleGroup;
import com.example.trainaut01.models.Exercise;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class TrainingUtils {

    /**
     * Возвращает ресурс изображения для человека в зависимости от дня недели.
     *
     * @param dayOfWeek День недели из Calendar.
     * @return Идентификатор ресурса изображения.
     */
    public static int getPersonImageResource(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY: return R.drawable.boy_front_monday;
            case Calendar.TUESDAY: return R.drawable.boy_back_tuesday;
            case Calendar.WEDNESDAY: return R.drawable.boy_back_wednesday;
            case Calendar.THURSDAY: return R.drawable.boy_back_thursday;
            case Calendar.FRIDAY:
            case Calendar.SATURDAY:
            case Calendar.SUNDAY: return R.drawable.boy_front;
            default: return R.drawable.default_image;
        }
    }

    /**
     * Формирует подзаголовок для тренировки на основе упражнений и групп мышц.
     *
     * @param exercises Список упражнений.
     * @param noTrainingMessage Сообщение, если тренировок нет.
     * @param prefix Префикс для заголовка.
     * @param groupConverter Конвертер строки в группу мышц.
     * @param <T> Тип группы мышц (GrossMotorMuscleGroup или FineMotorMuscleGroup).
     * @return Сформированный подзаголовок.
     */
    public static <T extends Enum<T>> String getMuscleGroupSubtitle(
            List<Exercise> exercises,
            String noTrainingMessage,
            String prefix,
            Function<String, T> groupConverter
    ) {
        if (exercises == null || exercises.isEmpty()) {
            return noTrainingMessage;
        }

        Set<T> muscleGroups = new LinkedHashSet<>();
        for (Exercise exercise : exercises) {
            Object muscleGroup = exercise.getMuscleGroup();

            if (muscleGroup == null) continue;

            if (muscleGroup.getClass().isEnum()) {
                muscleGroups.add((T) muscleGroup);
            } else if (muscleGroup instanceof String) {
                try {
                    muscleGroups.add(groupConverter.apply(((String) muscleGroup).toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        if (muscleGroups.isEmpty()) {
            return noTrainingMessage;
        }

        StringBuilder subtitle = new StringBuilder(prefix);
        int index = 0;
        for (T group : muscleGroups) {
            subtitle.append(getDisplayName(group));
            if (index < muscleGroups.size() - 1) {
                subtitle.append(" и ");
            }
            index++;
        }
        return subtitle.toString();
    }

    private static <T extends Enum<T>> String getDisplayName(T group) {
        if (group instanceof GrossMotorMuscleGroup) {
            return ((GrossMotorMuscleGroup) group).getDisplayName();
        } else if (group instanceof FineMotorMuscleGroup) {
            return ((FineMotorMuscleGroup) group).getDisplayName();
        }
        return group.name();
    }
}
