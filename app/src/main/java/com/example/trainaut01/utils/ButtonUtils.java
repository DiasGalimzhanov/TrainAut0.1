package com.example.trainaut01.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import com.example.trainaut01.R;

public class ButtonUtils {

    public static void updateButtonState(Context context, Button btn, String text, int textColor, int backgroundResource, boolean isEnabled) {
        btn.setText(text);
        btn.setEnabled(isEnabled);
        btn.setTextColor(context.getResources().getColor(textColor));
        btn.setBackgroundResource(backgroundResource);
    }
}
