package com.example.trainaut01.utils;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ValidationUtils {

    /**
     * Проверяет, что все переданные строки не пустые.
     *
     * @param fields строки для проверки
     * @return true, если все строки заполнены, иначе false
     */
    public static boolean areFieldsFilled(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет совпадение пароля и подтверждения пароля.
     *
     * @param password1         пароль
     * @param password2  подтверждение пароля
     * @return true, если пароли совпадают, иначе false
     */
    public static boolean doPasswordsMatch(String password1, String password2) {
        return password1 != null && password1.equals(password2);
    }

    /**
     * Проверяет, что длина номера телефона соответствует требованиям.
     *
     * @param phoneNumber номер телефона
     * @param maxLength   максимальная длина номера телефона
     * @param minLength   минимальная длина номера телефона
     * @return true, если длина номера телефона соответствует, иначе false
     */
    public static boolean isPhoneNumberLengthValid(String phoneNumber, int maxLength, int minLength) {
        if (phoneNumber == null) return false;

        int length = phoneNumber.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Создает TextWatcher для проверки совпадения паролей и обновления TextView.
     *
     * @param passwordField       поле ввода пароля
     * @param confirmPasswordField поле ввода подтверждения пароля
     * @param resultTextView       TextView для отображения результата
     * @return объект TextWatcher
     */
    public static TextWatcher createPasswordTextWatcher(EditText passwordField, EditText confirmPasswordField, TextView resultTextView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = passwordField.getText().toString();
                String confirmPassword = confirmPasswordField.getText().toString();

                if (!doPasswordsMatch(password, confirmPassword)) {
                    resultTextView.setText("Пароли не совпадают");
                    resultTextView.setTextColor(Color.RED);
                } else {
                    resultTextView.setText("Пароли совпадают");
                    resultTextView.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    /**
     * Проверяет, достиг ли пользователь минимального возраста.
     *
     * @param birthDateString дата рождения в формате "dd.MM.yyyy"
     * @param minimumAge минимальный возраст
     * @return true, если возраст больше или равен минимальному, иначе false
     */
    public static boolean isAgeValid(String birthDateString, int minimumAge) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date birthDate = sdf.parse(birthDateString);
            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(birthDate);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age >= minimumAge;
        } catch (ParseException e) {
            return false;
        }
    }
}
