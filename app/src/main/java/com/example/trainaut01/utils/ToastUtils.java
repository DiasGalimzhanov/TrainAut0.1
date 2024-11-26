package com.example.trainaut01.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Утилитный класс для отображения всплывающих сообщений (Toast).
 */
public class ToastUtils {

    /**
     * Отображает сообщение с длительным временем показа (LONG) для ошибок.
     *
     * @param context контекст, из которого вызывается метод
     * @param message текст сообщения, которое необходимо отобразить
     */
    public static void showErrorMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Отображает сообщение с коротким временем показа (SHORT).
     *
     * @param context контекст, из которого вызывается метод
     * @param message текст сообщения, которое необходимо отобразить
     */
    public static void showShortMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
