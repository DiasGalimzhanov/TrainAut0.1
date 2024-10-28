package com.example.trainaut01.training;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.adapter.TrainingDailyAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.DayPlan;
import com.example.trainaut01.models.Exercise;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.repository.UserRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import javax.inject.Inject;

public class TrainingListFragment extends Fragment implements TrainingDailyAdapter.OnExerciseClickListener {

     private static final String ARG_DAY = "day";
    private static final String USER_ID = "userId";
    private static final String PREFS_NAME = "TrainingPrefs";
    private static final String KEY_COUNT_DAYS = "countDays";

    private RecyclerView _recyclerView;
    private TrainingDailyAdapter _adapter;
    private TextView _tvTitleTrainingList, _tvTitleDayOfWeek;

    private AppComponent appComponent;

    private String[] daysOfWeek = {
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота",
            "Воскресенье"
    };

    @Inject
    DayPlanRepository dayPlanRepository;

    @Inject
    UserRepository userRepository;

    public static TrainingListFragment newInstance(String day, String userId) {
        TrainingListFragment fragment = new TrainingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, day);
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_training_list, container, false);

        init(view);

        _recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String day = getArguments() != null ? getArguments().getString(ARG_DAY).toUpperCase() : "";

        if (day.equals("SUNDAY")) {
            String text = "На сегодня упражнений нет\n\n" +
                    "Воскресенье — это время для отдыха и восстановления после активной недели. \n\n" +
                    "Для детей и их родителей это может быть день, посвящённый расслаблению, эмоциональной гармонии и приятным семейным занятиям.";
            _tvTitleTrainingList.setText(text);
        } else {
            loadDayPlan(DayPlan.WeekDay.valueOf(day));
        }

        // Слушатель для обновления данных при возврате из фрагмента с упражнением
        getParentFragmentManager().setFragmentResultListener("exerciseResult", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                boolean exerciseCompleted = result.getBoolean("exerciseCompleted", false);
                if (exerciseCompleted) {
                    loadDayPlan(DayPlan.WeekDay.valueOf(day));
                }
            }
        });

        return view;
    }

    // Инициализация компонентов пользовательского интерфейса
    private void init(View view) {
        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        _recyclerView = view.findViewById(R.id.rvDailyList);
        _tvTitleTrainingList = view.findViewById(R.id.tvTitleTrainingList);
        _tvTitleDayOfWeek = view.findViewById(R.id.tvTitleDayOfWeek);
    }

    // Метод для загрузки данных по планам тренировок конкретного пользователя
    private void loadDayPlan(DayPlan.WeekDay weekDay) {

        String userId = getArguments().getString(USER_ID);
        int dayIndex = weekDay.ordinal();

        _tvTitleDayOfWeek.setText(daysOfWeek[dayIndex]);

        dayPlanRepository.getUserDayPlans(userId, weekDay, new OnSuccessListener<List<DayPlan>>() {
            @Override
            public void onSuccess(List<DayPlan> dayPlans) {
                if (!dayPlans.isEmpty()) {

                    DayPlan dayPlan = dayPlans.get(0);
                    List<Exercise> exercises = dayPlan.getExercises();

                    allExerciseIsCompleted(exercises, dayPlan);

                    _adapter = new TrainingDailyAdapter(exercises, TrainingListFragment.this, weekDay.name());
                    _recyclerView.setAdapter(_adapter);
                } else {
                    _tvTitleTrainingList.setText("На сегодня нет упражнений.");
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void allExerciseIsCompleted(List<Exercise> exercises, DayPlan dayPlan) {
        boolean allCompleted = exercises.stream().allMatch(Exercise::isCompleted);

        if (allCompleted && !dayPlan.isCompleted()) {
            DayPlan.WeekDay weekDay = dayPlan.getWeekDay();
            updateDayPlanCompletion(weekDay);
        }
    }

    private void updateDayPlanCompletion(DayPlan.WeekDay weekDay) {
        String userId = getArguments().getString(USER_ID);

        Log.d("TrainingList", weekDay.toString());
        dayPlanRepository.updateDayPlanCompletion(userId, weekDay, true, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Integer currentCountDays = getCountDays();
                currentCountDays++;

                saveCountDays(currentCountDays);

                userRepository.updateUserItem("countDays", currentCountDays, getContext());

                Toast.makeText(getContext(), "Все упражнения выполнены!", Toast.LENGTH_SHORT).show();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Ошибка при обновлении статуса", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод для сохранения countDays в SharedPreferences
    private void saveCountDays(Integer count) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_COUNT_DAYS, count);
        editor.apply();
    }

    // Метод для получения значения countDays из SharedPreferences
    private Integer getCountDays() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_COUNT_DAYS, 0);
    }

    @Override
    public void onExerciseClick(Exercise exercise, String day) {
        Fragment detailFragment = ExerciseDetailFragment.newInstance(exercise, day);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}
