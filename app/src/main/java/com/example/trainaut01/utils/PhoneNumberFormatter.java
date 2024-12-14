package com.example.trainaut01.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class PhoneNumberFormatter {
    public static void setupPhoneNumberFormatting(EditText editText, String countryCode) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        AsYouTypeFormatter formatter = phoneNumberUtil.getAsYouTypeFormatter(countryCode);

        editText.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
