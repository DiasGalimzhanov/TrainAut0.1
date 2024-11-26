package com.example.trainaut01.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.util.Calendar;

public class DatePickerUtils {

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
     * Форматирует число в двухзначный формат.
     *
     * @param value Число для форматирования.
     * @return Строка с двухзначным числом.
     */
    private static String formatToTwoDigits(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }
}
