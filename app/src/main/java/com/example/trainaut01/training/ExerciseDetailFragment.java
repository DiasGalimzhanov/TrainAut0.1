package com.example.trainaut01.training;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.R;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.Exercise;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.repository.ExerciseRepository;
import com.example.trainaut01.repository.UserRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

public class ExerciseDetailFragment extends Fragment {

    private static final String ARG_EXERCISE = "exercise";
    private static final String ARG_DAY = "day";

    private TextView _tvTimer;
    private CountDownTimer _timer;
    private float _timeElapsed = 0;

    private TextView _tvSet, _tvRep, _tvName, _tvDescription, _tvRestTime, _tvRecommendations, _tvPoints;
    private ImageView _ivImageUrl;
    private Button _btnStart;

    private String _text;

    private AppComponent _appComponent;

    @Inject
    UserRepository userRepository;

    @Inject
    DayPlanRepository dayPlanRepository;

    @Inject
    ExerciseRepository exerciseRepository;

    // Создание нового экземпляра фрагмента с аргументами
    public static ExerciseDetailFragment newInstance(Exercise exercise, String day) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EXERCISE, exercise);
        args.putString(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_details, container, false);
        init(view);

        // Получение данных об упражнении и дне недели
        if (getArguments() != null) {
            Exercise exercise = (Exercise) getArguments().getSerializable(ARG_EXERCISE);
            String weekDay = getArguments().getString(ARG_DAY).toLowerCase();

            int currentDay = currentDayOfWeek();
            String[] daysOfWeek = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
            String today = daysOfWeek[currentDay - 1];

            if (!today.equals(weekDay)) {
                _btnStart.setVisibility(View.GONE);
            }

            if (exercise != null) {
                displayExerciseDetails(exercise);
                setupStartButton(exercise, weekDay);
            } else {
                _tvPoints.setText("Ошибка: упражнение не найдено.");
            }
        }
        return view;
    }

    // Метод для отображения деталей упражнения
    private void displayExerciseDetails(Exercise exercise) {
        _tvRecommendations.setText(_text);
        _tvName.setText(exercise.getName());
        _tvDescription.setText(exercise.getDescription());

        _tvSet.setText(exercise.getSets() == 1 ? exercise.getSets() + " подход" : exercise.getSets() + " подхода");

        String durationText = !exercise.getDuration().isEmpty() ? " по " + exercise.getReps() + " " + exercise.getDuration()
                : (exercise.getReps() >= 2 && exercise.getReps() <= 4) ? " по " + exercise.getReps() + " раза" : " по " + exercise.getReps() + " раз";
        _tvRep.setText(durationText);

        _tvRestTime.setText(String.format(Locale.getDefault(), "Время отдыха между подходами %.0f минуты", exercise.getRestTime()));

        int rewardPoints = exercise.getRewardPoints();
        Log.d("ExerciseDetailFragment", "Reward Points: " + rewardPoints);
        _tvPoints.setText("За выполнение этого упражнения вы получите +" + rewardPoints + " EXP");

        Picasso.get().load(exercise.getImageUrl()).into(_ivImageUrl);
    }

    // Метод для настройки кнопки начала упражнения
    private void setupStartButton(Exercise exercise, String weekDay) {
        _btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_btnStart.getText().toString().equals("Начать упражнение")) {
                    startTimer();
                    _btnStart.setText("Завершить упражнение");
                    _btnStart.setTextColor(getResources().getColor(R.color.white));
                    _btnStart.setBackgroundResource(R.drawable.background_finish_exercise);
                } else {
                    stopTimer();
                    completeExercise(exercise, weekDay);
                }
            }
        });
    }

    // Метод для завершения упражнения
    private void completeExercise(Exercise exercise, String weekDay) {
        int points = exercise.getRewardPoints();
        SharedPreferences sharedPref = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        int currentExp = sharedPref.getInt("exp", 0);
        currentExp += points;

        userRepository.updateUserItem("exp", currentExp, getContext());

        String userId = sharedPref.getString("userId", "");
        String exerciseId = exercise.getId();

        Log.d("ExerciseDetailFragment", "userId:" + userId + " dayPlanId: " + weekDay + ", exerciseId: " + exerciseId);

        dayPlanRepository.markExerciseAsCompleted(userId, weekDay, exerciseId, _timeElapsed, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Bundle result = new Bundle();
                result.putBoolean("exerciseCompleted", true);
                getParentFragmentManager().setFragmentResult("exerciseResult", result);
                getActivity().onBackPressed();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void completeExercise(Exercise exercise, String weekDay) {
//        int points = exercise.getRewardPoints();
//        Log.d("ExerciseDetailFragment", "Reward Points: " + points);
//
//        SharedPreferences sharedPref = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
//        int currentExp = sharedPref.getInt("exp", 0);
//        currentExp += points;
//
//        Log.d("ExerciseDetailFragment", "Current EXP before update: " + sharedPref.getInt("exp", 0) + ", After update: " + currentExp);
//
//        userRepository.updateUserItem("exp", currentExp, getContext());
//
//        String userId = sharedPref.getString("userId", "");
//        String exerciseId = exercise.getId();
//
//        Log.d("ExerciseDetailFragment", "userId: " + userId + ", dayPlanId: " + weekDay + ", exerciseId: " + exerciseId);
//
//        exerciseRepository.markExerciseAsCompleted(userId, weekDay, exerciseId, _timeElapsed, new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d("ExerciseDetailFragment", "Exercise marked as completed successfully.");
//                Bundle result = new Bundle();
//                result.putBoolean("exerciseCompleted", true);
//                getParentFragmentManager().setFragmentResult("exerciseResult", result);
//                getActivity().onBackPressed();
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("ExerciseDetailFragment", "Error marking exercise as completed: " + e.getMessage());
//                Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    // Метод для запуска таймера
    private void startTimer() {
        _timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                _timeElapsed += 1000;
                int seconds = (int) (_timeElapsed / 1000) % 60;
                int minutes = (int) (_timeElapsed / (1000 * 60)) % 60;
                _tvTimer.setText(String.format(Locale.getDefault(), "Time: %02d:%02d",  minutes, seconds));
            }

            @Override
            public void onFinish() {}
        };
        _timer.start();
    }

    // Метод для остановки таймера
    private void stopTimer() {
        if (_timer != null) {
            _timer.cancel();
        }
    }

    // Метод для инициализации переменных
    public void init(View view) {
        _appComponent = DaggerAppComponent.create();
        _appComponent.inject(this);

        _tvSet = view.findViewById(R.id.tvSet);
        _tvRep = view.findViewById(R.id.tvRep);
        _tvName = view.findViewById(R.id.tvName);
        _tvDescription = view.findViewById(R.id.tvDescription);
        _tvRestTime = view.findViewById(R.id.tvRestTime);
        _tvPoints = view.findViewById(R.id.tvPoints);
        _tvRecommendations = view.findViewById(R.id.tvRecommendations);
        _ivImageUrl = view.findViewById(R.id.ivImageUrl);
        _btnStart = view.findViewById(R.id.btnStart);
        _tvTimer = view.findViewById(R.id.tvTimer);

        _text = "Поддерживайте друг друга, чтобы создать дружескую атмосферу и следите за их техникой, чтобы избежать травм.\n\n" +
                "Объясняйте ребенку каждое движение.\n\n" +
                "Выполняйте упражнения в спокойном темпе, делая акцент на правильную технику.\n\n" +
                "Ребенок всегда должен дышать ровно и не спешить.\n\n" +
                "Всегда подстраивайте нагрузку под возможности ребёнка.\n\n" +
                "Включайте разминку перед началом тренировки: несколько легких упражнений для разогрева мышц";
    }

    // Метод для получения текущего дня недели с помощью Calendar
    private int currentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
}

