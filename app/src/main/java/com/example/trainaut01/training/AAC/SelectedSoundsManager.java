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

            soundPlayer.playSound(soundFileName, context, () -> {
                playNextSound(context);
            });
        } else {
            Toast.makeText(context, "Все звуки воспроизведены", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeLastSound() {
        if (!selectedSounds.isEmpty()) {
            selectedSounds.remove(selectedSounds.size() - 1);
        }
    }

    public void clearAllSounds() {
        selectedSounds.clear();
    }

    public List<String> getSelectedSounds() {
        return selectedSounds;
    }
}
