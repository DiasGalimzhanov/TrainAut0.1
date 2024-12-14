package com.example.trainaut01.training.AAC;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.trainaut01.R;

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
    private GridLayout soundGrid;
    private EditText searchEditText;
    private HorizontalScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundPlayer = new SoundPlayer();
        selectedSoundsManager = new SelectedSoundsManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cognitive_exercise, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedSoundsLayout = view.findViewById(R.id.selectedSoundsContainer);
        deleteButton = view.findViewById(R.id.deleteLastButton);
        clearAllButton = view.findViewById(R.id.clearAllButton);
        playSelectedSoundsButton = view.findViewById(R.id.playSelectedSoundsButton);
        searchEditText = view.findViewById(R.id.searchEditText);
        soundGrid = view.findViewById(R.id.soundGrid);
        scrollView = view.findViewById(R.id.selectedSoundsScrollView);

        setupSoundButtons();
        setupDeleteButton();
        setupClearAllButton();
        setupPlaySelectedSoundsButton();
        setupSearchButton(view);
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
        Button soundButton9 = getView().findViewById(R.id.soundButton9);
        Button soundButton10 = getView().findViewById(R.id.soundButton10);
        Button soundButton11 = getView().findViewById(R.id.soundButton11);
        Button soundButton12 = getView().findViewById(R.id.soundButton12);

        soundButton1.setOnClickListener(v -> playSoundAndAddText("privet", "Привет"));
        soundButton2.setOnClickListener(v -> playSoundAndAddText("ya", "Я"));
        soundButton3.setOnClickListener(v -> playSoundAndAddText("hochu", "Хочу"));
        soundButton4.setOnClickListener(v -> playSoundAndAddText("kushat", "Кушать"));
        soundButton5.setOnClickListener(v -> playSoundAndAddText("pit", "Пить"));
        soundButton6.setOnClickListener(v -> playSoundAndAddText("spat", "Спать"));
        soundButton7.setOnClickListener(v -> playSoundAndAddText("da", "Да"));
        soundButton8.setOnClickListener(v -> playSoundAndAddText("net", "Нет"));

        soundButton1.setVisibility(View.VISIBLE);
        soundButton2.setVisibility(View.VISIBLE);
        soundButton3.setVisibility(View.VISIBLE);
        soundButton4.setVisibility(View.VISIBLE);
        soundButton5.setVisibility(View.VISIBLE);
        soundButton6.setVisibility(View.VISIBLE);
        soundButton7.setVisibility(View.VISIBLE);
        soundButton8.setVisibility(View.VISIBLE);
        soundButton9.setVisibility(View.VISIBLE);
        soundButton10.setVisibility(View.VISIBLE);
        soundButton11.setVisibility(View.VISIBLE);
        soundButton12.setVisibility(View.VISIBLE);
    }

    private void playSoundAndAddText(String soundFileName, String buttonText) {
        selectedSoundsManager.addSound(soundFileName);
        soundPlayer.playSound(soundFileName, getContext(), () -> {});

        Button selectedButton = new Button(getContext());
        selectedButton.setBackgroundResource(R.drawable.back_violet_encircle);
        selectedButton.setTextColor(getResources().getColor(R.color.bright_dodger_blue));
        selectedButton.setText(buttonText);
        selectedSoundsLayout.addView(selectedButton);
        selectedButtons.add(selectedButton);

        scrollToEnd();
    }

    private void scrollToEnd() {
        selectedSoundsLayout.post(() -> {
            int scrollWidth = selectedSoundsLayout.getWidth();
            scrollView.smoothScrollTo(scrollWidth, 0);
        });
    }

    private void setupSearchButton(View view) {
        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            if (searchEditText.getVisibility() == View.GONE) {
                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
            } else {
                filterButtons(searchEditText.getText().toString());
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterButtons(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void filterButtons(String query) {
        for (int i = 0; i < soundGrid.getChildCount(); i++) {
            Button button = (Button) soundGrid.getChildAt(i);

            if (button.getContentDescription() != null && button.getContentDescription().toString().toLowerCase().contains(query.toLowerCase())) {
                button.setVisibility(View.VISIBLE);
            } else {
                button.setVisibility(View.GONE);
            }
        }
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

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }
}
