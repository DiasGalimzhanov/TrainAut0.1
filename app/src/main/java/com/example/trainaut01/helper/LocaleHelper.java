package com.example.trainaut01.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    /**
     * Устанавливает локаль приложения на указанный язык и обновляет конфигурацию ресурсов.
     *
     * @param context  Контекст приложения или активности.
     * @param language Код языка в формате ISO 639-1 (например, "en" для английского).
     * @return Обновлённый {@link Context} с новой локалью.
     */
    public static Context setLocale(Context context, String language) {
        persist(context, language);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        config.setLocale(locale);
        config.setLayoutDirection(locale);
        return context.createConfigurationContext(config);
    }

    /**
     * Получает текущий выбранный язык из {@link SharedPreferences}.
     * Если язык не был ранее установлен, возвращает системный язык по умолчанию.
     *
     * @param context Контекст приложения или активности.
     * @return Код текущего языка в формате ISO 639-1.
     */
    public static String getLanguage(Context context) {
        return getPersistedData(context, "ru");
    }

    /**
     * Сохраняет выбранный язык в {@link SharedPreferences}.
     *
     * @param context  Контекст приложения или активности.
     * @param language Код языка в формате ISO 639-1.
     */
    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    /**
     * Получает сохранённый язык из {@link SharedPreferences}.
     * Если язык не был сохранён, возвращает язык по умолчанию.
     *
     * @param context          Контекст приложения или активности.
     * @param defaultLanguage  Язык по умолчанию, который будет возвращён, если сохранённый язык отсутствует.
     * @return Код сохранённого языка или язык по умолчанию.
     */
    private static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }
}
