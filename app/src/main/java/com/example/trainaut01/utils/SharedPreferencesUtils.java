package com.example.trainaut01.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    public static int getInt(Context context, String prefName, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void saveInt(Context context, String prefName, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getString(Context context, String prefName, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void saveString(Context context, String prefName, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String prefName, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void saveBoolean(Context context, String prefName, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
