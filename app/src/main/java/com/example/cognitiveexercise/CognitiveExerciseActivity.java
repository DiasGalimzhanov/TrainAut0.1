package com.example.cognitiveexercise;

import android.os.Bundle;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

public class CognitiveExerciseActivity extends AppCompatActivity {
    private SoundPlayer soundPlayer;
    private SelectedSoundsManager selectedSoundsManager;
    private LinearLayout selectedSoundsLayout;
    private Button deleteButton;
    private Button clearAllButton; // Новая кнопка для очистки
    private Button playSelectedSoundsButton; // Новая кнопка для воспроизведения выбранных звуков
    private List<Button> selectedButtons = new ArrayList<>();
    private HorizontalScrollView selectedSoundsScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_cognitive_exercise);

        soundPlayer = new SoundPlayer();
        selectedSoundsManager = new SelectedSoundsManager(this);

        selectedSoundsLayout = findViewById(R.id.selectedSoundsContainer);
        selectedSoundsScrollView = findViewById(R.id.selectedSoundsScrollView);
        deleteButton = findViewById(R.id.deleteLastButton);
        clearAllButton = findViewById(R.id.clearAllButton); // Инициализация кнопки "Очистить все"
        playSelectedSoundsButton = findViewById(R.id.playSelectedSoundsButton); // Инициализация кнопки "Озвучить выбранные"

        setupSoundButtons();
        setupSelectedSoundsContainer();
        setupDeleteButton();
        setupClearAllButton(); // Настройка кнопки "Очистить все"
        setupPlaySelectedSoundsButton(); // Настройка кнопки "Озвучить выбранные"

        // Убедитесь, что HorizontalScrollView может быть нажат
        selectedSoundsScrollView.setClickable(true);
        selectedSoundsScrollView.setFocusable(true);
        selectedSoundsScrollView.setFocusableInTouchMode(true);

        // Обработчик касания для HorizontalScrollView
        selectedSoundsScrollView.setOnClickListener(v -> {
            Toast.makeText(this, "Воспроизведение всех звуков...", Toast.LENGTH_SHORT).show();
            selectedSoundsManager.playAllSounds(this);
        });
    }

    private void setupSoundButtons() {
        Button soundButton1 = findViewById(R.id.soundButton1);
        Button soundButton2 = findViewById(R.id.soundButton2);
        Button soundButton3 = findViewById(R.id.soundButton3);
        Button soundButton4 = findViewById(R.id.soundButton4);
        Button soundButton5 = findViewById(R.id.soundButton5);
        Button soundButton6 = findViewById(R.id.soundButton6);

        soundButton1.setOnClickListener(v -> addSoundToSelected("privet.mp3"));
        soundButton2.setOnClickListener(v -> addSoundToSelected("ya.mp3"));
        soundButton3.setOnClickListener(v -> addSoundToSelected("hochu.mp3"));
        soundButton4.setOnClickListener(v -> addSoundToSelected("est.mp3"));
        soundButton5.setOnClickListener(v -> addSoundToSelected("pit.mp3"));
        soundButton6.setOnClickListener(v -> addSoundToSelected("spat.mp3"));
    }

    private void addSoundToSelected(String soundFileName) {
        selectedSoundsManager.addSound(soundFileName);

        Button selectedButton = new Button(this);
        selectedButton.setText(soundFileName);
        selectedSoundsLayout.addView(selectedButton);

        selectedButtons.add(selectedButton);

        soundPlayer.playSound(soundFileName, this, () -> {
            Toast.makeText(this, "Звук завершен: " + soundFileName, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSelectedSoundsContainer() {
        selectedSoundsLayout.setClickable(true);

        selectedSoundsLayout.setOnClickListener(v -> {
            if (!selectedSoundsManager.getSelectedSounds().isEmpty()) {
                Toast.makeText(this, "Воспроизведение всех звуков...", Toast.LENGTH_SHORT).show();
                selectedSoundsManager.playAllSounds(this);
            } else {
                Toast.makeText(this, "Нет звуков для воспроизведения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDeleteButton() {
        deleteButton.setOnClickListener(v -> {
            if (!selectedButtons.isEmpty()) {
                Button buttonToRemove = selectedButtons.remove(selectedButtons.size() - 1);
                selectedSoundsLayout.removeView(buttonToRemove);
                selectedSoundsManager.removeLastSound();
            } else {
                Toast.makeText(this, "Нет кнопок для удаления", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Настройка кнопки "Очистить все"
    private void setupClearAllButton() {
        clearAllButton.setOnClickListener(v -> {
            if (!selectedButtons.isEmpty()) {
                selectedButtons.clear(); // Очистить список кнопок
                selectedSoundsLayout.removeAllViews(); // Удалить все кнопки из контейнера
                selectedSoundsManager.clearAllSounds(); // Очистить список выбранных звуков
                Toast.makeText(this, "Все звуки удалены", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Нет звуков для удаления", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Настройка кнопки "Озвучить выбранные"
    private void setupPlaySelectedSoundsButton() {
        playSelectedSoundsButton.setOnClickListener(v -> {
            if (!selectedSoundsManager.getSelectedSounds().isEmpty()) {
                Toast.makeText(this, "Воспроизведение выбранных звуков...", Toast.LENGTH_SHORT).show();
                selectedSoundsManager.playAllSounds(this); // Воспроизведение всех выбранных звуков
            } else {
                Toast.makeText(this, "Нет выбранных звуков для воспроизведения", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
