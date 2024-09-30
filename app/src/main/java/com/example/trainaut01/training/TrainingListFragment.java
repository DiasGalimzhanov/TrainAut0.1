package com.example.trainaut01.training;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.R;

public class TrainingListFragment extends Fragment {

    private static final String ARG_DAY = "day";

    public static TrainingListFragment newInstance(String day) {
        TrainingListFragment fragment = new TrainingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_list, container, false);

        // Получаем день из аргументов
        String day = getArguments() != null ? getArguments().getString(ARG_DAY) : "";

        // Здесь вы можете настроить ваш UI с учетом дня недели
        // Например, отобразить соответствующую тренировку для дня

        return view;
    }
}