package com.example.cognitiveexercise;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectedSoundsManager {
    private final List<String> selectedSounds = new ArrayList<>();
    private final SoundPlayer soundPlayer;
    private int currentIndex;

    // Используем Executor для параллельного выполнения задач
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Используем Handler для пост-обработки
    private final Handler handler = new Handler(Looper.getMainLooper());

    public SelectedSoundsManager(Context context) {
        this.soundPlayer = new SoundPlayer();
    }

    public void addSound(String soundFileName) {
        selectedSounds.add(soundFileName);
    }

    public void playAllSounds(Context context) {
        currentIndex = 0;
        playNextSound(context);
    }

    private void playNextSound(Context context) {
        if (currentIndex < selectedSounds.size()) {
            String soundFileName = selectedSounds.get(currentIndex);
            currentIndex++;

            // Запускаем воспроизведение в отдельном потоке, чтобы минимизировать задержки
            executor.execute(() -> {
                soundPlayer.playSound(soundFileName, context, () -> {
                    // После завершения воспроизведения следующего звука продолжаем воспроизведение
                    handler.post(() -> playNextSound(context));
                });
            });
        }
    }

    public void removeLastSound() {
        if (!selectedSounds.isEmpty()) {
            selectedSounds.remove(selectedSounds.size() - 1);
        }
    }

    // Новый метод для очистки всех выбранных звуков
    public void clearAllSounds() {
        selectedSounds.clear();
    }

    public List<String> getSelectedSounds() {
        return selectedSounds;
    }

    // Закрытие Executor при завершении работы
    public void shutdown() {
        executor.shutdown();
    }
}
