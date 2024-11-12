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

    public void removeLastSound() {
        if (!selectedSounds.isEmpty()) {
            selectedSounds.remove(selectedSounds.size() - 1);
        }
    }

    public void playAllSounds(Context context) {
        currentIndex = 0;
        playNextSound(context);
    }

    private void playNextSound(Context context) {
        for (int i = currentIndex; i < selectedSounds.size(); i++) {
            String soundFileName = selectedSounds.get(i);
            soundPlayer.playSound(soundFileName, context, null);
        }

        // После завершения проигрывания всех звуков обновляем индекс
        currentIndex = selectedSounds.size(); // или reset, если нужно начать заново
    }
}
