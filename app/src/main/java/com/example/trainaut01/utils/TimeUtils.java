package com.example.trainaut01.utils;

import java.util.Locale;

public class TimeUtils {

    public static String formatElapsedTime(float elapsedTimeMillis) {
        int seconds = (int) (elapsedTimeMillis / 1000) % 60;
        int minutes = (int) (elapsedTimeMillis / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "Time: %02d:%02d", minutes, seconds);
    }
}
