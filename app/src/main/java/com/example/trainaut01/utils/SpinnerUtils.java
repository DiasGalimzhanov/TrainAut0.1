package com.example.trainaut01.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.trainaut01.R;

public class SpinnerUtils {

    /**
     * Настраивает адаптер для Spinner с переданными данными.
     *
     * @param context       Контекст активности или фрагмента.
     * @param spinner       Spinner, для которого настраивается адаптер.
     * @param options       Массив строк, который будет отображаться в Spinner.
     */
    public static void setupGenderAdapter(Context context, Spinner spinner, String[] options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_spinner, options);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapter);
    }
}
