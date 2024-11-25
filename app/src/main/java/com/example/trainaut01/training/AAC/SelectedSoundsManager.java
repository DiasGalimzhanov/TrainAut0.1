package com.example.trainaut01.training.AAC;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SelectedSoundsManager {
    private final List<String> selectedSounds = new ArrayList<>();
    private final SoundPlayer soundPlayer;
    private int currentIndex;

    public SelectedSoundsManager(Context context) {
        this.soundPlayer = new SoundPlayer();
    }

    // Добавляем звук в список
    public void addSound(String soundFileName) {
        selectedSounds.add(soundFileName);
    }

    // Воспроизводим все выбранные звуки
    public void playAllSounds(Context context) {
        currentIndex = 0;
        playNextSound(context);
    }

    // Воспроизводим следующий звук
    private void playNextSound(Context context) {
        if (currentIndex < selectedSounds.size()) {
            String soundFileName = selectedSounds.get(currentIndex);
            currentIndex++; // Переходим к следующему звуку

            // Воспроизведение звука
            soundPlayer.playSound(soundFileName, context, () -> {
                // После завершения воспроизведения идем к следующему звуку
                playNextSound(context);
            });
        } else {
            // Все звуки воспроизведены
            Toast.makeText(context, "Все звуки воспроизведены", Toast.LENGTH_SHORT).show();
        }
    }

    // Удаление последнего добавленного звука
    public void removeLastSound() {
        if (!selectedSounds.isEmpty()) {
            selectedSounds.remove(selectedSounds.size() - 1);
        }
    }

    // Очистка всех звуков
    public void clearAllSounds() {
        selectedSounds.clear();
    }

    // Получение списка выбранных звуков
    public List<String> getSelectedSounds() {
        return selectedSounds;
    }
}
