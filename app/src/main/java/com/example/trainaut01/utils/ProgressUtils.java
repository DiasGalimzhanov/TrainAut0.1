package com.example.trainaut01.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class ProgressUtils {
    public static void resetDailyProgress(Context context, ProgressResetListener listener) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("child_progress", Context.MODE_PRIVATE);
        int lastSavedDay = sharedPreferences.getInt("lastSavedDayOfWeek", -1);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        if (lastSavedDay != currentDay) {
            sharedPreferences.edit()
                    .putBoolean("isCompletedTodayTraining", false)
                    .putInt("lastSavedDayOfWeek", currentDay)
                    .apply();
            Log.d("ProgressUtils", "Daily progress reset.");

            if (listener != null) {
                listener.onProgressReset();
            }
        }
    }
}


