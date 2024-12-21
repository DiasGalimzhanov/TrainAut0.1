package com.example.trainaut01.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Утилитный класс для работы с SharedPreferences.
 */
public class SharedPreferencesUtils {

    /**
     * Получает значение типа int из SharedPreferences.
     *
     * @param context      контекст приложения.
     * @param prefName     имя файла SharedPreferences.
     * @param key          ключ значения.
     * @param defaultValue значение по умолчанию, если ключ отсутствует.
     * @return значение типа int, сохраненное в SharedPreferences, или defaultValue, если ключ отсутствует.
     */
    public static int getInt(Context context, String prefName, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Получает значение типа float из SharedPreferences.
     */
    public static float getFloat(Context context, String prefName, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key, defaultValue);
    }

    /**
     * Сохраняет значение типа int в SharedPreferences.
     *
     * @param context  контекст приложения.
     * @param prefName имя файла SharedPreferences.
     * @param key      ключ значения.
     * @param value    сохраняемое значение.
     */
    public static void saveInt(Context context, String prefName, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Получает значение типа String из SharedPreferences.
     *
     * @param context      контекст приложения.
     * @param prefName     имя файла SharedPreferences.
     * @param key          ключ значения.
     * @param defaultValue значение по умолчанию, если ключ отсутствует.
     * @return значение типа String, сохраненное в SharedPreferences, или defaultValue, если ключ отсутствует.
     */
    public static String getString(Context context, String prefName, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * Сохраняет значение типа String в SharedPreferences.
     *
     * @param context  контекст приложения.
     * @param prefName имя файла SharedPreferences.
     * @param key      ключ значения.
     * @param value    сохраняемое значение.
     */
    public static void saveString(Context context, String prefName, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Получает значение типа boolean из SharedPreferences.
     *
     * @param context      контекст приложения.
     * @param prefName     имя файла SharedPreferences.
     * @param key          ключ значения.
     * @param defaultValue значение по умолчанию, если ключ отсутствует.
     * @return значение типа boolean, сохраненное в SharedPreferences, или defaultValue, если ключ отсутствует.
     */
    public static boolean getBoolean(Context context, String prefName, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Сохраняет значение типа boolean в SharedPreferences.
     *
     * @param context  контекст приложения.
     * @param prefName имя файла SharedPreferences.
     * @param key      ключ значения.
     * @param value    сохраняемое значение.
     */
    public static void saveBoolean(Context context, String prefName, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

}
