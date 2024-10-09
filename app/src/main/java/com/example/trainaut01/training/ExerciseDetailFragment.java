package com.example.trainaut01.training;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private Button _btnStart;

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

            _tvRecommendations.setText("Поддерживайте друг друга, чтобы создать дружескую атмосферу и следите за их техникой, чтобы избежать травм.\n\n" +
                    "Объясняйте ребенку каждое движение.\n\n" +
                    "Выполняйте упражнения в спокойном темпе, делая акцент на правильную технику.\n\n" +
                    "Ребенок всегда должен дышать ровно и не спешить.\n\n" +
                    "Всегда подстраивайте нагрузку под возможности ребёнка.\n\n" +
                    "Включайте разминку перед началом тренировки: несколько легких упражнений для разогрева мышц");

            _tvName.setText(exercise != null ? exercise.getName() : null);
            _tvDescription.setText(exercise != null ? exercise.getDescription() : null);

            if (exercise != null && exercise.getSets() == 1) {
                _tvSet.setText(exercise != null ? exercise.getSets() + " подход" : "Error");
            } else {
                _tvSet.setText(exercise != null ? exercise.getSets() + " подхода" : "Error");
            }

            if (!exercise.getDuration().equals("")) {
                _tvRep.setText(exercise != null ? " по " + exercise.getReps() + " " + exercise.getDuration() : "Error");
            } else {
                if (exercise.getReps() >= 2 && exercise.getReps() <= 4) {
                    _tvRep.setText(exercise != null ? " по " + exercise.getReps() + " раза" : "Error");
                } else {
                    _tvRep.setText(exercise != null ? " по " + exercise.getReps() + " раз" : "Error");
                }
            }

            _tvRestTime.setText(exercise != null ? String.format(Locale.getDefault(), "Время отдыха между подходами %.0f минуты", exercise.getRestTime()) : "0 минут");
            _tvPoints.setText("За выполнение этого упражнения вы получите +" + (exercise != null ? exercise.getRewardPoints() : "Error") + " FitCoins");

            Picasso.get().load(exercise != null ? exercise.getImageUrl() : null).into(_ivImageUrl);
        }

        _btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_btnStart.getText().toString().equals("Начать упражнение")){
                    _btnStart.setText("Завершить упражнение");
                    _btnStart.setTextColor(getResources().getColor(R.color.white));
                    _btnStart.setBackgroundResource(R.drawable.background_finish_exercise);
                }
                if (_btnStart.getText().toString().equals("Завершить упражнение")){

                }
            }
        });

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
        _btnStart = view.findViewById(R.id.btnStart);
    }
}

