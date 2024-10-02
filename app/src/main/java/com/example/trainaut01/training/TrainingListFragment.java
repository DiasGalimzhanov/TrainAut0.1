package com.example.trainaut01.training;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.adapter.TrainingDailyAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.DayPlan;
import com.example.trainaut01.models.Exercise;
import com.example.trainaut01.repository.DayPlanRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class TrainingListFragment extends Fragment implements TrainingDailyAdapter.OnExerciseClickListener {

    private static final String ARG_DAY = "day";
    private RecyclerView recyclerView;
    private TrainingDailyAdapter adapter;
    private AppComponent appComponent;

    @Inject
    DayPlanRepository dayPlanRepository;

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

        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        View view = inflater.inflate(R.layout.fragment_training_list, container, false);

        init(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Получаем день из аргументов
        String day = getArguments() != null ? getArguments().getString(ARG_DAY) : "";

        loadDayPlan(DayPlan.WeekDay.valueOf("MONDAY"));

        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.rvDailyList);
    }

    private void loadDayPlan(DayPlan.WeekDay weekDay) {
        dayPlanRepository.getAll(new OnSuccessListener<List<DayPlan>>() {
            @Override
            public void onSuccess(List<DayPlan> dayPlans) {
                List<DayPlan> filteredPlans = dayPlans.stream()
                        .filter(dayPlan -> dayPlan.getWeekDay() == weekDay)
                        .collect(Collectors.toList());

                if (!filteredPlans.isEmpty()) {
                    DayPlan dayPlan = filteredPlans.get(0);
                    List<Exercise> exercises = dayPlan.getExercises();

                    // Передача слушателя в адаптер
                    adapter = new TrainingDailyAdapter(exercises, TrainingListFragment.this);
                    recyclerView.setAdapter(adapter);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
        // Создаем новый фрагмент для деталей упражнения
        Fragment detailFragment = ExerciseDetailFragment.newInstance(exercise);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, detailFragment)
                .addToBackStack(null) // Добавляем в стек возврата
                .commit();
    }
}
