package com.example.trainaut01.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class ProgressUtils {
    public static void resetDailyProgress(Context context, ProgressResetListener listener) {
        int lastSavedDay = SharedPreferencesUtils.getInt(context, "child_progress", "lastSavedDayOfWeek", -1);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        if (lastSavedDay != currentDay) {
            SharedPreferencesUtils.saveBoolean(context, "child_progress", "isCompletedTodayTraining", false);
            SharedPreferencesUtils.saveInt(context, "child_progress", "lastSavedDayOfWeek", currentDay);
            SharedPreferencesUtils.saveInt(context, "child_progress", "progressBar", 0);

            if (listener != null) {
                listener.onProgressReset();
            }
        }
    }

    public static void resetAllProgress(Context context) {
        SharedPreferencesUtils.saveBoolean(context, "child_progress", "isCompletedTodayTraining", false);
        SharedPreferencesUtils.saveInt(context, "child_progress", "progressBar", 0);
        SharedPreferencesUtils.saveInt(context, "child_progress", "currentExerciseIndex", 0);
        SharedPreferencesUtils.saveInt(context, "child_progress", "remainingPoints", 0);
    }
}



