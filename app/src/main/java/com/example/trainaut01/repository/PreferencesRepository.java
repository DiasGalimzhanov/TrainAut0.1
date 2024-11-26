package com.example.trainaut01.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.trainaut01.models.User;
import com.google.gson.Gson;

public class PreferencesRepository {

    private static final String PREFS_NAME = "app_preferences";

    private final SharedPreferences sharedPreferences;

    public PreferencesRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void saveInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void saveBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public void saveUserInPreferences(User user) {
        String userJson = new Gson().toJson(user);

        sharedPreferences.edit().putString("user_data", userJson).apply();
    }

    public User getUserFromPreferences() {
        String userJson = sharedPreferences.getString("user_data", null);

        if (userJson != null) {
            return new Gson().fromJson(userJson, User.class);
        }

        return null;
    }


}
