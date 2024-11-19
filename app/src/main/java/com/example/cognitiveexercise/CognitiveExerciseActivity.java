package com.example.cognitiveexercise;

import android.os.Bundle;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        // Настройка кнопок, чтобы они добавляли звуки и отображали текст
        soundButton1.setOnClickListener(v -> playSoundAndAddText("privet", "Привет"));
        soundButton2.setOnClickListener(v -> playSoundAndAddText("ya", "Я"));
        soundButton3.setOnClickListener(v -> playSoundAndAddText("hochu", "Хочу"));
        soundButton4.setOnClickListener(v -> playSoundAndAddText("kushat", "Кушать"));
        soundButton5.setOnClickListener(v -> playSoundAndAddText("pit", "Пить"));
        soundButton6.setOnClickListener(v -> playSoundAndAddText("spat", "Спать"));
    }

    // Метод для воспроизведения звука и добавления текста в контейнер
    private void playSoundAndAddText(String soundFileName, String buttonText) {
        // Добавляем звук в список
        selectedSoundsManager.addSound(soundFileName);

        // Воспроизводим звук
        soundPlayer.playSound(soundFileName, this, () -> {
            // Можно выполнить дополнительные действия после воспроизведения
        });

        // Добавляем текст в контейнер
        Button selectedButton = new Button(this);
        selectedButton.setText(buttonText); // Добавляем текст на русском
        selectedSoundsLayout.addView(selectedButton);

        // Сохраняем добавленную кнопку
        selectedButtons.add(selectedButton);

        // Прокручиваем контейнер до конца после добавления нового элемента
        scrollToEnd();
    }

    // Метод для прокрутки контейнера до конца
    private void scrollToEnd() {
        // Используем post() для выполнения действия после добавления элемента
        selectedSoundsLayout.post(() -> {
            // Получаем высоту всего содержимого в контейнере
            int scrollWidth = selectedSoundsLayout.getWidth();
            selectedSoundsScrollView.smoothScrollTo(scrollWidth, 0); // Прокручиваем по оси X
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
