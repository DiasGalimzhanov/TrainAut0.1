package com.example.trainaut01.utils;

import android.annotation.SuppressLint;
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
                return true;
            }
        }
        return false;
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
     * @param passwordField        поле ввода пароля
     * @param confirmPasswordField поле ввода подтверждения пароля
     * @param resultTextView       TextView для отображения результата проверки
     * @return объект TextWatcher, привязанный к указанным полям
     */
    public static TextWatcher createPasswordTextWatcher(EditText passwordField, EditText confirmPasswordField, TextView resultTextView) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswords(passwordField, confirmPasswordField, resultTextView);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        passwordField.addTextChangedListener(watcher);
        confirmPasswordField.addTextChangedListener(watcher);

        return watcher;
    }

    /**
     * Проверяет совпадение паролей и обновляет текст и цвет TextView в зависимости от результата.
     *
     * @param passwordField        поле ввода пароля
     * @param confirmPasswordField поле ввода подтверждения пароля
     * @param resultTextView       TextView для отображения результата проверки
     */
    private static void validatePasswords(EditText passwordField, EditText confirmPasswordField, TextView resultTextView) {
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


    /**
     * Проверяет, достиг ли пользователь минимального возраста.
     *
     * @param birthDateString дата рождения в формате "dd.MM.yyyy"
     * @param minimumAge минимальный возраст
     * @return true, если возраст больше или равен минимальному, иначе false
     */
    public static boolean isAgeValid(String birthDateString, int minimumAge) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date birthDate = sdf.parse(birthDateString);
            Calendar birthCalendar = Calendar.getInstance();
            if (birthDate != null) {
                birthCalendar.setTime(birthDate);
            }

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

    /**
     * Проверка корректности Firebase Storage URL
     */
    public static boolean isValidFirebaseUrl(String firebaseUrl) {
        if (firebaseUrl == null || !firebaseUrl.startsWith("gs://")) {
            System.out.println("ImageUtils: Некорректный URL -> " + firebaseUrl);
            return false;
        }
        return true;
    }
}
