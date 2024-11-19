package com.example.cognitiveexercise;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundPlayer {
    private final FirebaseStorage storage;
    private final Map<String, MediaPlayer> preloadedSounds = new HashMap<>();
    private MediaPlayer currentMediaPlayer;  // Для отслеживания текущего воспроизводимого звука
    private boolean isMediaPlayerPlaying = false; // Флаг для отслеживания, воспроизводится ли звук

    public SoundPlayer() {
        storage = FirebaseStorage.getInstance();
    }

    // Предзагрузка звуков
    public void preloadSound(String fileName, Context context) {
        if (preloadedSounds.containsKey(fileName)) {
            return; // Звук уже загружен
        }

        StorageReference storageRef = storage.getReference().child("sounds/" + fileName);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                // Используем Uri для установки источника данных
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.setOnPreparedListener(mp -> {
                    preloadedSounds.put(fileName, mediaPlayer); // Кэшируем звук после его подготовки
                });
                mediaPlayer.prepare(); // Синхронная подготовка
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Ошибка при предзагрузке звука", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(exception -> {
            // Обрабатываем ошибку
            Toast.makeText(context, "Не удалось загрузить звук", Toast.LENGTH_SHORT).show();
        });
    }

    // Воспроизведение предзагруженного звука
    public void playPreloadedSound(String fileName, Context context, Runnable onComplete) {
        MediaPlayer mediaPlayer = preloadedSounds.get(fileName);
        if (mediaPlayer == null) {
            playSound(fileName, context, onComplete);  // Если звук не предзагружен, загружаем и воспроизводим его
            return;
        }

        try {
            if (isMediaPlayerPlaying) {
                // Если звук уже воспроизводится, ничего не делаем
                return;
            }

            isMediaPlayerPlaying = true; // Устанавливаем флаг, что воспроизведение началось
            currentMediaPlayer = mediaPlayer;
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                isMediaPlayerPlaying = false; // Воспроизведение завершено
                mp.release();
                if (onComplete != null) {
                    onComplete.run();
                }
            });
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(context, "Ошибка воспроизведения звука", Toast.LENGTH_SHORT).show();
        }
    }

    // Воспроизведение звука из Firebase (если не предзагружен)
    public void playSound(String fileName, Context context, Runnable onComplete) {
        StorageReference storageRef = storage.getReference().child("sounds/" + fileName);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            currentMediaPlayer = new MediaPlayer();
            try {
                currentMediaPlayer.setDataSource(context, uri);
                currentMediaPlayer.setOnPreparedListener(mp -> {
                    currentMediaPlayer.start(); // Воспроизводим после подготовки
                    currentMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                        isMediaPlayerPlaying = false; // Воспроизведение завершено
                        mediaPlayer.release(); // Освобождаем ресурсы после завершения
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    });
                });
                currentMediaPlayer.prepare(); // Синхронная подготовка
                isMediaPlayerPlaying = true; // Устанавливаем флаг, что воспроизведение началось
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Ошибка при воспроизведении звука", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(exception -> {
            // Обрабатываем ошибки при загрузке
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
