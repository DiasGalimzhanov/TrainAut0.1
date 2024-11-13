package com.example.cognitiveexercise;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class SelectedSoundsManager {
    private final List<String> selectedSounds = new ArrayList<>();
    private final SoundPlayer soundPlayer;
    private int currentIndex;

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
            soundPlayer.playSound(soundFileName, context, () -> playNextSound(context));
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
}
