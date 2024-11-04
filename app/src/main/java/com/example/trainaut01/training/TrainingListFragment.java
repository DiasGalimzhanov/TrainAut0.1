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

    private boolean isDayPlanUpdated = false;

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
        Log.d("TrainingListFragment", "onCreateView called");

        View view = inflater.inflate(R.layout.fragment_training_list, container, false);
        init(view);

        _recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String day = getArguments() != null ? getArguments().getString(ARG_DAY).toUpperCase() : "";
        Log.d("TrainingListFragment", "Day argument: " + day);

        if (day.equals("SUNDAY")) {
            String text = "На сегодня упражнений нет\n\n" +
                    "Воскресенье — это время для отдыха и восстановления после активной недели. \n\n" +
                    "Для детей и их родителей это может быть день, посвящённый расслаблению, эмоциональной гармонии и приятным семейным занятиям.";
            _tvTitleTrainingList.setText(text);
        } else {
            loadDayPlan(DayPlan.WeekDay.valueOf(day));
        }

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

    private void init(View view) {
        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        _recyclerView = view.findViewById(R.id.rvDailyList);
        _tvTitleTrainingList = view.findViewById(R.id.tvTitleTrainingList);
        _tvTitleDayOfWeek = view.findViewById(R.id.tvTitleDayOfWeek);
    }

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
        Log.d("TrainingListFragment", "All exercises completed: " + allCompleted);

        // Проверяем, что все упражнения завершены и обновление не было выполнено ранее
        if (allCompleted && !dayPlan.isCompleted() && !isDayPlanUpdated) {
            dayPlan.setCompleted(true);
            isDayPlanUpdated = true; // Устанавливаем флаг, чтобы предотвратить повторное обновление
            updateDayPlanCompletion(dayPlan.getWeekDay(), dayPlan);
        }
    }

    private void updateDayPlanCompletion(DayPlan.WeekDay weekDay, DayPlan dayPlan) {
        String userId = getArguments().getString(USER_ID);
        Log.d("TrainingListFragment", "Updating day plan completion for weekDay: " + weekDay);

        dayPlanRepository.updateDayPlanCompletion(userId, weekDay, true, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dayPlan.setCompleted(true);
                Log.d("TrainingListFragment", "Day plan updated successfully");

                Integer currentCountDays = getCountDays();
                currentCountDays++;

                saveCountDays(currentCountDays);
                userRepository.updateUserItem("countDays", currentCountDays, getContext());

                Toast.makeText(getContext(), "Все упражнения выполнены!", Toast.LENGTH_SHORT).show();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TrainingListFragment", "Error updating day plan completion", e);
                Toast.makeText(getContext(), "Ошибка при обновлении статуса", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCountDays(Integer count) {
        Log.d("TrainingListFragment", "Saving countDays: " + count);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_COUNT_DAYS, count);
        editor.apply();
    }

    private Integer getCountDays() {
        if (getActivity() == null) {
            Log.e("TrainingListFragment", "getActivity() returned null, unable to get SharedPreferences");
            return 0; // Default value
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int countDays = sharedPreferences.getInt(KEY_COUNT_DAYS, 0);
        Log.d("TrainingListFragment", "Retrieved countDays: " + countDays);
        return countDays;
    }

    @Override
    public void onExerciseClick(Exercise exercise, String day) {
        Log.d("TrainingListFragment", "Exercise clicked: " + exercise.getName());
        Fragment detailFragment = ExerciseDetailFragment.newInstance(exercise, day);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

}
