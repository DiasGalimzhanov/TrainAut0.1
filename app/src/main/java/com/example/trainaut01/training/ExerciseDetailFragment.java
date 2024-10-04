package com.example.trainaut01.training;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.R;

import com.example.trainaut01.models.Exercise;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ExerciseDetailFragment extends Fragment {

    private static final String ARG_EXERCISE = "exercise";

    private TextView _tvSet, _tvRep, _tvName, _tvDescription, _tvRestTime, _tvRecommendations, _tvPoints;
    private ImageView _ivImageUrl;

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
        init(view);

        // Получаем данные об упражнении
        if (getArguments() != null) {
            Exercise exercise = (Exercise) getArguments().getSerializable(ARG_EXERCISE);

            _tvRecommendations.setText("Поддерживайте друг друга, чтобы создать дружескую атмосферу.\n\n" +
                    "Объясняйте ребенку каждое движение.\n\n" +
                    "Важно следить за правильной техникой выполнения упражнений.\n\n" +
                    "Всегда подстраивайте нагрузку под возможности ребёнка.\n\n" +
                    "Включайте разминку перед началом тренировки: несколько легких упражнений для разогрева мышц и заминку после.");

            _tvName.setText(exercise != null ? exercise.getName() : null);
            _tvDescription.setText(exercise != null ? exercise.getDescription() : null);
            _tvSet.setText(exercise != null ? exercise.getSets() + " подхода" : "Error");
            _tvRep.setText(exercise != null ? "по " + exercise.getReps() + " раз" : "Error");
            _tvRestTime.setText(exercise != null ? String.format(Locale.getDefault(), "Время отдыха между подходами %.0f минуты", exercise.getRestTime()) : "0 минут");
            _tvPoints.setText("За выполнение этого упражнения вы получите +" + (exercise != null ? exercise.getRewardPoints() : "Error") + " FitCoins");

            Picasso.get().load(exercise != null ? exercise.getImageUrl() : null).into(_ivImageUrl);
        }

        return view;
    }

    public void init(View view) {
        _tvSet = view.findViewById(R.id.tvSet);
        _tvRep = view.findViewById(R.id.tvRep);
        _tvName = view.findViewById(R.id.tvName);
        _tvDescription = view.findViewById(R.id.tvDescription);
        _tvRestTime = view.findViewById(R.id.tvRestTime);
        _tvPoints = view.findViewById(R.id.tvPoints);
        _tvRecommendations = view.findViewById(R.id.tvRecommendations);
        _ivImageUrl = view.findViewById(R.id.ivImageUrl);
    }
}

