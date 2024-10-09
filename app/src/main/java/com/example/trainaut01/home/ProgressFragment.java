package com.example.trainaut01.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trainaut01.R;


public class ProgressFragment extends Fragment {
    private TextView levelTitle,levelProgressText,streakTitle, streakProgressText;
    private ProgressBar levelProgressBar,streakProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        int exp = sharedPreferences.getInt("exp", 0); // Получите опыт
//        int exp = 22350;
        int level = exp / 5000; // Определите уровень
        Log.d("PROG", String.valueOf(exp));
        int expForNextLevel = 5000; // Опыт для следующего уровня
        int progress = (int) ((exp % expForNextLevel) * 100 / expForNextLevel); // Процентный прогресс

        levelTitle = view.findViewById(R.id.tvLevel);
        levelProgressText = view.findViewById(R.id.levelProgressText);
        levelProgressBar = view.findViewById(R.id.progressBarLevel);

        levelTitle.setText("Уровень: " + level);
        levelProgressText.setText(exp + " / " + (level + 1) * expForNextLevel + " опыта");
        levelProgressBar.setProgress(progress);

        int streakDays = sharedPreferences.getInt("countDays", 0); // Получите количество дней стрика
        int maxStreakDays = 300; // Максимальное количество дней для стрика

        streakTitle = view.findViewById(R.id.tvDays);
        streakProgressText = view.findViewById(R.id.streakProgressText);
        streakProgressBar = view.findViewById(R.id.progressDays);

        // Установите текст для текстовых полей
        streakTitle.setText("Дней в ударе: " + streakDays);
        streakProgressText.setText(streakDays + " / " + maxStreakDays + " дней");

        // Установите прогресс в процентах
        int progresss = (int) ((float) streakDays / maxStreakDays * 100); // Преобразуем в проценты
        streakProgressBar.setProgress(progresss);
        return view;
    }
}