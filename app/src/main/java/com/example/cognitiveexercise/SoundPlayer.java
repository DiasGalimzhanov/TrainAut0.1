package com.example.cognitiveexercise;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundPlayer {
    private FirebaseStorage storage;

    public SoundPlayer() {
        storage = FirebaseStorage.getInstance();
    }

    public void playSound(String fileName, Context context, Runnable onComplete) {
        // Получаем ссылку на файл в Firebase Storage
        StorageReference storageRef = storage.getReference().child("sounds/" + fileName);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Запускаем воспроизведение
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.prepare();
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(mp -> {
                    // После завершения воспроизведения вызываем onComplete
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(exception -> {
            // Обрабатываем ошибки
            Toast.makeText(context, "Не удалось загрузить звук", Toast.LENGTH_SHORT).show();
        });
    }

    // Метод для получения списка доступных звуков
    public void getAvailableSounds(OnSoundsLoadedListener listener) {
        StorageReference soundsRef = storage.getReference().child("sounds/");
        soundsRef.listAll().addOnSuccessListener(listResult -> {
            List<String> soundNames = new ArrayList<>();
            for (StorageReference item : listResult.getItems()) {
                soundNames.add(item.getName());
            }
            listener.onSoundsLoaded(soundNames);
        }).addOnFailureListener(e -> listener.onSoundsLoaded(new ArrayList<>()));
    }

    public interface OnSoundsLoadedListener {
        void onSoundsLoaded(List<String> soundNames);
    }
}
