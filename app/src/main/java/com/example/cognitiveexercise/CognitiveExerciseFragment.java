package com.example.cognitiveexercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class CognitiveExerciseFragment extends Fragment {
    private SoundPlayer soundPlayer;
    private SelectedSoundsManager selectedSoundsManager;
    private LinearLayout selectedSoundsLayout;
    private Button deleteButton;
    private Button clearAllButton;
    private Button playSelectedSoundsButton;
    private List<Button> selectedButtons = new ArrayList<>();
    private HorizontalScrollView selectedSoundsScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализация объектов, если нужно
        soundPlayer = new SoundPlayer();
        selectedSoundsManager = new SelectedSoundsManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Привязываем новый XML layout фрагмента
        return inflater.inflate(R.layout.fragment_cognitive_exercise, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Теперь здесь привязываем элементы UI
        selectedSoundsLayout = view.findViewById(R.id.selectedSoundsContainer);
        selectedSoundsScrollView = view.findViewById(R.id.selectedSoundsScrollView);
        deleteButton = view.findViewById(R.id.deleteLastButton);
        clearAllButton = view.findViewById(R.id.clearAllButton);
        playSelectedSoundsButton = view.findViewById(R.id.playSelectedSoundsButton);

        setupSoundButtons();
        setupSelectedSoundsContainer();
        setupDeleteButton();
        setupClearAllButton();
        setupPlaySelectedSoundsButton();
    }

    private void setupSoundButtons() {
        Button soundButton1 = getView().findViewById(R.id.soundButton1);
        Button soundButton2 = getView().findViewById(R.id.soundButton2);
        Button soundButton3 = getView().findViewById(R.id.soundButton3);
        Button soundButton4 = getView().findViewById(R.id.soundButton4);
        Button soundButton5 = getView().findViewById(R.id.soundButton5);
        Button soundButton6 = getView().findViewById(R.id.soundButton6);
        Button soundButton7 = getView().findViewById(R.id.soundButton7);
        Button soundButton8 = getView().findViewById(R.id.soundButton8);

        // Настройка кнопок, чтобы они добавляли звуки и отображали текст
        soundButton1.setOnClickListener(v -> playSoundAndAddText("privet", "Привет"));
        soundButton2.setOnClickListener(v -> playSoundAndAddText("ya", "Я"));
        soundButton3.setOnClickListener(v -> playSoundAndAddText("hochu", "Хочу"));
        soundButton4.setOnClickListener(v -> playSoundAndAddText("kushat", "Кушать"));
        soundButton5.setOnClickListener(v -> playSoundAndAddText("pit", "Пить"));
        soundButton6.setOnClickListener(v -> playSoundAndAddText("spat", "Спать"));
        soundButton7.setOnClickListener(v -> playSoundAndAddText("da", "Да"));
        soundButton8.setOnClickListener(v -> playSoundAndAddText("net", "Нет"));
    }

    private void playSoundAndAddText(String soundFileName, String buttonText) {
        // Добавляем звук в список
        selectedSoundsManager.addSound(soundFileName);

        // Воспроизводим звук
        soundPlayer.playSound(soundFileName, getContext(), () -> {
            // Можно выполнить дополнительные действия после воспроизведения
        });

        // Добавляем текст в контейнер
        Button selectedButton = new Button(getContext());
        selectedButton.setText(buttonText); // Добавляем текст на русском
        selectedSoundsLayout.addView(selectedButton);

        // Сохраняем добавленную кнопку
        selectedButtons.add(selectedButton);

        // Прокручиваем контейнер до конца после добавления нового элемента
        scrollToEnd();
    }

    // Метод для прокрутки контейнера до конца
    private void scrollToEnd() {
        selectedSoundsLayout.post(() -> {
            int scrollWidth = selectedSoundsLayout.getWidth();
            selectedSoundsScrollView.smoothScrollTo(scrollWidth, 0); // Прокручиваем по оси X
        });
    }

    private void setupSelectedSoundsContainer() {
        selectedSoundsLayout.setClickable(true);

        selectedSoundsLayout.setOnClickListener(v -> {
            if (!selectedSoundsManager.getSelectedSounds().isEmpty()) {
                Toast.makeText(getContext(), "Воспроизведение всех звуков...", Toast.LENGTH_SHORT).show();
                selectedSoundsManager.playAllSounds(getContext());
            } else {
                Toast.makeText(getContext(), "Нет звуков для воспроизведения", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Нет кнопок для удаления", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClearAllButton() {
        clearAllButton.setOnClickListener(v -> {
            if (!selectedButtons.isEmpty()) {
                selectedButtons.clear();
                selectedSoundsLayout.removeAllViews();
                selectedSoundsManager.clearAllSounds();
                Toast.makeText(getContext(), "Все звуки удалены", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Нет звуков для удаления", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPlaySelectedSoundsButton() {
        playSelectedSoundsButton.setOnClickListener(v -> {
            if (!selectedSoundsManager.getSelectedSounds().isEmpty()) {
                Toast.makeText(getContext(), "Воспроизведение выбранных звуков...", Toast.LENGTH_SHORT).show();
                selectedSoundsManager.playAllSounds(getContext());
            } else {
                Toast.makeText(getContext(), "Нет выбранных звуков для воспроизведения", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
