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
    private Button clearAllButton;
    private Button playSelectedSoundsButton;
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
        clearAllButton = findViewById(R.id.clearAllButton);
        playSelectedSoundsButton = findViewById(R.id.playSelectedSoundsButton);

        setupSoundButtons();
        setupSelectedSoundsContainer();
        setupDeleteButton();
        setupClearAllButton();
        setupPlaySelectedSoundsButton();

        // Убедитесь, что HorizontalScrollView может быть нажат
        selectedSoundsScrollView.setClickable(true);
        selectedSoundsScrollView.setFocusable(true);
        selectedSoundsScrollView.setFocusableInTouchMode(true);

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

        soundButton1.setOnClickListener(v -> addSoundToSelected("Привет", "privet.mp3"));
        soundButton2.setOnClickListener(v -> addSoundToSelected("Я", "ya.mp3"));
        soundButton3.setOnClickListener(v -> addSoundToSelected("Хочу", "hochu.mp3"));
        soundButton4.setOnClickListener(v -> addSoundToSelected("Кушать", "kushat.mp3"));
        soundButton5.setOnClickListener(v -> addSoundToSelected("Пить", "pit.mp3"));
        soundButton6.setOnClickListener(v -> addSoundToSelected("Спать", "spat.mp3"));
    }

    private void addSoundToSelected(String buttonText, String soundFileName) {
        selectedSoundsManager.addSound(soundFileName);

        Button selectedButton = new Button(this);
        selectedButton.setText(buttonText);
        selectedSoundsLayout.addView(selectedButton);

        selectedButtons.add(selectedButton);

        soundPlayer.playSound(soundFileName, this, () -> {
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

    private void setupClearAllButton() {
        clearAllButton.setOnClickListener(v -> {
            if (!selectedButtons.isEmpty()) {
                selectedButtons.clear();
                selectedSoundsLayout.removeAllViews();
                selectedSoundsManager.clearAllSounds();
                Toast.makeText(this, "Все звуки удалены", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Нет звуков для удаления", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPlaySelectedSoundsButton() {
        playSelectedSoundsButton.setOnClickListener(v -> {
            if (!selectedSoundsManager.getSelectedSounds().isEmpty()) {
                Toast.makeText(this, "Воспроизведение выбранных звуков...", Toast.LENGTH_SHORT).show();
                selectedSoundsManager.playAllSounds(this);
            } else {
                Toast.makeText(this, "Нет выбранных звуков для воспроизведения", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
