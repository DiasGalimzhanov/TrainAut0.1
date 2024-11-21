package com.example.cognitiveexercise;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

public class SoundPlayer {

    public SoundPlayer() {
        // Инициализация
    }

    // Воспроизведение звука из ресурсов raw
    public void playSound(String fileName, Context context, Runnable onComplete) {
        // Получаем идентификатор ресурса по имени файла
        int resId = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());

        if (resId == 0) {
            Toast.makeText(context, "Файл не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, resId);
            mediaPlayer.start();

            // После завершения воспроизведения вызываем onComplete
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                if (onComplete != null) {
                    onComplete.run();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Ошибка воспроизведения звука", Toast.LENGTH_SHORT).show();
        }
    }
}
