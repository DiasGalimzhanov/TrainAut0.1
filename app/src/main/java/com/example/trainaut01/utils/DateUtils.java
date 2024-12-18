package com.example.trainaut01.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {

    /**
     * Показывает диалог выбора даты и устанавливает выбранную дату в EditText.
     *
     * @param context Контекст активности или фрагмента.
     * @param editText Поле, в которое нужно установить выбранную дату.
     */
    public static void showDatePickerDialog(Context context, EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = formatToTwoDigits(selectedDay) + "." +
                            formatToTwoDigits(selectedMonth + 1) + "." + selectedYear;
                    editText.setText(formattedDate);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    /**
     * Проверяет, является ли текущий день выходным (суббота или воскресенье).
     *
     * @return true, если выходной, иначе false.
     */
    public static boolean isWeekend() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    /**
     * Форматирует число в двухзначный формат.
     *
     * @param value Число для форматирования.
     * @return Строка с двухзначным числом.
     */
    public static String formatToTwoDigits(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }

    /**
     * Возвращает строку, представляющую текущий день недели.
     *
     * @param dayOfWeek День недели из Calendar.
     * @return Строка с названием дня недели.
     */
    public static String getDayOfWeekString(int dayOfWeek) {
        String[] days = {"Unknown", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return dayOfWeek >= 1 && dayOfWeek <= 7 ? days[dayOfWeek] : "Unknown";
    }

    /**
     * Возвращает текущий год в виде строки.
     *
     * @return Текущий год в формате строки.
     */
    public static String getCurrentYear() {
        Calendar currentCalendar = Calendar.getInstance();
        return String.valueOf(currentCalendar.get(Calendar.YEAR));
    }

    /**
     * Возвращает текущее название месяца на английском языке.
     *
     * @return Название текущего месяца в длинном формате (например, "January").
     */
    public static String getCurrentMonth() {
        Calendar currentCalendar = Calendar.getInstance();
        return currentCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
    }

    /**
     * Возвращает локализованное название текущего месяца в именительном падеже.
     *
     * @return Локализованное название месяца (например, "Декабрь" для русской локали).
     */
    public static String getCurrentMonthLocalized() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG_STANDALONE, Locale.getDefault());
    }
}
