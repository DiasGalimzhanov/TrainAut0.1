package com.example.cognitiveexercise;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class CognitiveExerciseActivity extends AppCompatActivity {
    private SoundPlayer soundPlayer;
    private SelectedSoundsManager selectedSoundsManager;
    private LinearLayout selectedSoundsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализация Firebase
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_cognitive_exercise);

        soundPlayer = new SoundPlayer();
        selectedSoundsManager = new SelectedSoundsManager(this);

        selectedSoundsLayout = findViewById(R.id.selectedSoundsContainer);

        setupSoundButtons();
        setupSelectedSoundsContainer();
    }

    private void setupSoundButtons() {
        // Привязываем кнопки, определенные в XML
        Button soundButton1 = findViewById(R.id.soundButton1);
        Button soundButton2 = findViewById(R.id.soundButton2);
        Button soundButton3 = findViewById(R.id.soundButton3);
        Button soundButton4 = findViewById(R.id.soundButton4);
        Button soundButton5 = findViewById(R.id.soundButton5);
        Button soundButton6 = findViewById(R.id.soundButton6);

        // Добавьте остальные кнопки по аналогии

        // Настраиваем каждую кнопку для добавления в список выбранных и мгновенного воспроизведения
        soundButton1.setOnClickListener(v -> addSoundToSelected("privet.mp3"));
        soundButton2.setOnClickListener(v -> addSoundToSelected("ya.mp3"));
        soundButton3.setOnClickListener(v -> addSoundToSelected("hochu.mp3"));
        soundButton4.setOnClickListener(v -> addSoundToSelected("est.mp3"));
        soundButton5.setOnClickListener(v -> addSoundToSelected("pit.mp3"));
        soundButton6.setOnClickListener(v -> addSoundToSelected("spat.mp3"));
        // Аналогично для остальных кнопок
    }

    private void addSoundToSelected(String soundFileName) {
        // Добавляем звук в список выбранных
        selectedSoundsManager.addSound(soundFileName);

        // Создаем кнопку для отображения выбранного звука
        Button selectedButton = new Button(this);
        selectedButton.setText(soundFileName);
        selectedSoundsLayout.addView(selectedButton);

        // Воспроизводим звук сразу после добавления
        soundPlayer.playSound(soundFileName, this, () -> {
            Toast.makeText(this, "Звук завершен: " + soundFileName, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSelectedSoundsContainer() {
        // Убедитесь, что контейнер поддерживает клики
        selectedSoundsLayout.setClickable(true);

        // Добавьте отладочный лог или сообщение
        selectedSoundsLayout.setOnClickListener(v -> {
            // Проверка нажатия
            Toast.makeText(this, "Воспроизведение всех звуков...", Toast.LENGTH_SHORT).show();
            // Воспроизведение всех звуков
            selectedSoundsManager.playAllSounds(this);
        });
    }
}
