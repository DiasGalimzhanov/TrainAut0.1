package com.example.cognitiveexercise;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CognitiveExerciseActivity extends AppCompatActivity {
    private SoundPlayer soundPlayer;
    private SelectedSoundsManager selectedSoundsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cognitive_exercise);

        soundPlayer = new SoundPlayer();
        selectedSoundsManager = new SelectedSoundsManager( this);

        setupSoundButtons();
    }

    private void setupSoundButtons() {
        // Ссылаемся на кнопки, которые уже определены в XML
        Button soundButton1 = findViewById(R.id.soundButton1);
        Button soundButton2 = findViewById(R.id.soundButton2);
        // Прочие кнопки...

        // Загружаем звуки из Firebase
        soundPlayer.getAvailableSounds(soundNames -> {
            // Проверяем количество звуков и сопоставляем с кнопками
            if (soundNames.size() > 0) {
                soundButton1.setText(soundNames.get(0));
                soundButton1.setOnClickListener(v -> {
                    soundPlayer.playSound(soundNames.get(0), this); // Воспроизведение первого звука
                });
            }
            if (soundNames.size() > 1) {
                soundButton2.setText(soundNames.get(1));
                soundButton2.setOnClickListener(v -> {
                    soundPlayer.playSound(soundNames.get(1), this); // Воспроизведение второго звука
                });
            }
            // Привязка других кнопок аналогично
        });
    }


}
