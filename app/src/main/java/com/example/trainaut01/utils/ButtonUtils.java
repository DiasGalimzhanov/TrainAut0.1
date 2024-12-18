package com.example.trainaut01.utils;

import android.content.Context;
import android.widget.Button;

/**
 * Утилитный класс для работы с кнопками.
 */
public class ButtonUtils {

    /**
     * Обновляет состояние кнопки, включая текст, цвет текста, фон и возможность нажатия.
     *
     * @param context           контекст, необходимый для доступа к ресурсам
     * @param btn               кнопка, состояние которой нужно обновить
     * @param text              текст, который нужно установить на кнопке
     * @param textColor         идентификатор ресурса цвета текста
     * @param backgroundResource идентификатор ресурса фона кнопки
     * @param isEnabled         состояние активности кнопки (true - активна, false - неактивна)
     */
    public static void updateButtonState(Context context, Button btn, String text, int textColor, int backgroundResource, boolean isEnabled) {
        btn.setText(text);
        btn.setEnabled(isEnabled);
        btn.setTextColor(context.getResources().getColor(textColor));
        btn.setBackgroundResource(backgroundResource);
    }
}
