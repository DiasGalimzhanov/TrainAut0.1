package com.example.trainaut01.training;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.R;

import com.example.trainaut01.models.Exercise;

public class ExerciseDetailFragment extends Fragment {

    private static final String ARG_EXERCISE = "exercise";

    public static ExerciseDetailFragment newInstance(Exercise exercise) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EXERCISE, exercise);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_details, container, false);

        // Получаем данные об упражнении
        if (getArguments() != null) {
            Exercise exercise = (Exercise) getArguments().getSerializable(ARG_EXERCISE);
            // Здесь вы можете отобразить данные об упражнении в пользовательском интерфейсе
            // Например, если у вас есть TextView для названия упражнения:
//            TextView titleTextView = view.findViewById(R.id.tvExerciseTitle);
//            TextView descTextView = view.findViewById(R.id.tvExerciseDescription);
//            titleTextView.setText(exercise.getName());
//            descTextView.setText(exercise.getDescription());
            // Добавьте другие данные об упражнении, если необходимо
        }

        return view;
    }
}

