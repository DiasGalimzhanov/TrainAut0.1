package com.example.trainaut01.utils;

import java.util.Locale;

/**
 * Утилитный класс для работы с временем.
 */
public class TimeUtils {

    /**
     * Форматирует время в миллисекундах в строку формата "MM:SS".
     *
     * @param elapsedTimeMillis время в миллисекундах.
     * @return строка, представляющая прошедшее время в формате "MM:SS".
     */
    public static String formatElapsedTime(float elapsedTimeMillis) {
        int seconds = (int) (elapsedTimeMillis / 1000) % 60;
        int minutes = (int) (elapsedTimeMillis / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "Time: %02d:%02d", minutes, seconds);
    }
}
