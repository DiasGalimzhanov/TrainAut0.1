package com.example.trainaut01.utils;

import android.content.Context;

import java.util.Calendar;

/**
 * Утилитный класс для управления прогрессом ребенка.
 */
public class ProgressUtils {

    /**
     * Сбрасывает ежедневный прогресс, если текущий день недели отличается от последнего сохраненного.
     *
     * @param context  контекст для доступа к SharedPreferences.
     * @param listener слушатель для обработки сброса прогресса (может быть null).
     */
    public static void resetDailyProgress(Context context, ProgressResetListener listener) {
        int lastSavedDay = SharedPreferencesUtils.getInt(context, "child_progress", "lastSavedDayOfWeek", -1);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        if (lastSavedDay != currentDay) {
            SharedPreferencesUtils.saveBoolean(context, "child_progress", "isCompletedTodayTraining", false);
            SharedPreferencesUtils.saveInt(context, "child_progress", "lastSavedDayOfWeek", currentDay);
            SharedPreferencesUtils.saveInt(context, "child_progress", "progressBar", 0);
            SharedPreferencesUtils.saveBoolean(context, "child_progress", "isProgressSavedToday", false);

            if (listener != null) {
                listener.onProgressReset();
            }
        }
    }

    /**
     * Полностью сбрасывает прогресс ребенка.
     *
     * @param context контекст для доступа к SharedPreferences.
     */
    public static void resetAllProgress(Context context) {
        SharedPreferencesUtils.saveBoolean(context, "child_progress", "isCompletedTodayTraining", false);
        SharedPreferencesUtils.saveInt(context, "child_progress", "progressBar", 0);
        SharedPreferencesUtils.saveInt(context, "child_progress", "currentExerciseIndex", 0);
        SharedPreferencesUtils.saveInt(context, "child_progress", "remainingPoints", 0);
    }
}



