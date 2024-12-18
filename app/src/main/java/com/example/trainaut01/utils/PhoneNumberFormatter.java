package com.example.trainaut01.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

/**
 * Утилитный класс для форматирования номеров телефона в режиме реального времени.
 */
public class PhoneNumberFormatter {

    /**
     * Настраивает форматирование номера телефона для указанного поля ввода.
     *
     * @param editText   поле ввода, для которого требуется форматирование номера телефона.
     * @param countryCode код страны, используемый для форматирования номера телефона.
     */
    public static void setupPhoneNumberFormatting(EditText editText, String countryCode) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        AsYouTypeFormatter formatter = phoneNumberUtil.getAsYouTypeFormatter(countryCode);

        editText.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;

            /**
             * Вызывается перед изменением текста. В данном случае не используется.
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            /**
             * Вызывается при изменении текста. В данном случае не используется.
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            /**
             * Вызывается после изменения текста. Форматирует введенный номер телефона.
             *
             * @param s текст, введенный пользователем.
             */
            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;

                isEditing = true;

                String input = s.toString().replaceAll("[^\\d]", "");

                String formattedNumber = "";
                formatter.clear();

                for (char c : input.toCharArray()) {
                    formattedNumber = formatter.inputDigit(c);
                }

                editText.setText(formattedNumber);
                editText.setSelection(formattedNumber.length());
                isEditing = false;
            }
        });
    }


}
