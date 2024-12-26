package com.example.trainaut01.training.AAC;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

public class SoundPlayer {

    public SoundPlayer() {
    }

    public void playSound(String fileName, Context context, Runnable onComplete) {
        int resId = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());

        if (resId == 0) {
            Toast.makeText(context, "Файл не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, resId);
            mediaPlayer.start();

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
