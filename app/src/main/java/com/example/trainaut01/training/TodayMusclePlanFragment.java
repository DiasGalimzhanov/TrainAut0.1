package com.example.trainaut01.training;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.BottomNavigationUpdater;
import com.example.trainaut01.R;
import com.example.trainaut01.adapter.FineMotorAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.enums.FineMotorMuscleGroup;
import com.example.trainaut01.enums.GrossMotorMuscleGroup;
import com.example.trainaut01.models.DayPlan;
import com.example.trainaut01.models.Exercise;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.utils.ButtonUtils;
import com.example.trainaut01.utils.DateUtils;
import com.example.trainaut01.utils.ProgressResetListener;
import com.example.trainaut01.utils.ProgressUtils;
import com.example.trainaut01.utils.SharedPreferencesUtils;
import com.example.trainaut01.utils.ToastUtils;
import com.example.trainaut01.utils.TrainingUtils;

import java.util.Calendar;

import javax.inject.Inject;

public class TodayMusclePlanFragment extends Fragment implements ProgressResetListener {

    private static final String TAG = "TodayMusclePlanFragment";

    private String userId;
    private DayPlan currentDayPlan;

    private boolean isGrossMotorSelected = true;
    private int dayOfWeek;

    private TextView tvTrainingSubTitle;
    private ImageView ivPerson;

    private Button btnGrossMotor, btnFineMotor, btnGoToTraining;
    private RecyclerView rvFineMotorTasks;

    @Inject
    DayPlanRepository dayPlanRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today_muscle_plan, container, false);
        init(view);

        if (DateUtils.isWeekend()) {
            disableTrainingForWeekend();
        } else {
            resetDailyProgressIfNeeded();
            loadDayPlans();
            handleTrainingTypeSelection();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void init(View view) {
        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        userId = SharedPreferencesUtils.getString(requireContext(), "user_data", "userId", null);
        dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        tvTrainingSubTitle = view.findViewById(R.id.tvTrainingSubTitle);
        ivPerson = view.findViewById(R.id.ivPerson);
        btnGoToTraining = view.findViewById(R.id.btnGoToTraining);
        btnGrossMotor = view.findViewById(R.id.btn_gross_motor);
        btnFineMotor = view.findViewById(R.id.btn_fine_motor);
        rvFineMotorTasks = view.findViewById(R.id.rvFineMotorTasks);
        rvFineMotorTasks.setLayoutManager(new LinearLayoutManager(requireContext()));

        btnGrossMotor.setOnClickListener(v -> switchToGrossMotor());
        btnFineMotor.setOnClickListener(v -> switchToFineMotor());
    }

    private void loadDayPlans() {
        String dayOfWeekString = DateUtils.getDayOfWeekString(dayOfWeek);

        dayPlanRepository.getDayPlanForUserAndDay(userId, dayOfWeekString, this::handleDayPlanLoaded, error -> {
            ToastUtils.showShortMessage(requireContext(), "Ошибка загрузки плана: " + error.getMessage());
        });
    }

    private void handleDayPlanLoaded(DayPlan dayPlan) {
        currentDayPlan = dayPlan;

        if (currentDayPlan == null) {
            ToastUtils.showShortMessage(requireContext(), "План на сегодня отсутствует.");
            return;
        }

        setupFineMotorAdapter();
        updateUI();
    }

    private void handleTrainingTypeSelection() {
        if (isGrossMotorSelected) {
            switchToGrossMotor();
        } else {
            switchToFineMotor();
        }
    }

    private void setupFineMotorAdapter() {
        if (currentDayPlan.getExercisesFineMotor() != null) {
            FineMotorAdapter fineMotorAdapter = new FineMotorAdapter(
                    requireContext(),
                    currentDayPlan.getExercisesFineMotor(),
                    this::openFineMotorTaskDetail,
                    DateUtils.getDayOfWeekString(dayOfWeek)
            );
            rvFineMotorTasks.setAdapter(fineMotorAdapter);
        }
    }

    private void disableTrainingForWeekend() {
        tvTrainingSubTitle.setText("Ты отлично потрудился на неделе! Сегодня ты можешь просто отдыхать.");
        btnGoToTraining.setVisibility(View.GONE);
        rvFineMotorTasks.setVisibility(View.GONE);
    }

    private void switchToGrossMotor() {
        isGrossMotorSelected = true;
        updateUI();
    }

    private void switchToFineMotor() {
        isGrossMotorSelected = false;
        updateUI();
    }

    private void updateUI() {
        updateButtonStyles();
        toggleButtonState();

        if (isGrossMotorSelected) {
            updateGrossMotorUI();
        } else {
            updateFineMotorUI();
        }
    }

    private void updateGrossMotorUI() {
        ivPerson.setVisibility(View.VISIBLE);
        rvFineMotorTasks.setVisibility(View.GONE);
        btnGoToTraining.setVisibility(View.VISIBLE);
        tvTrainingSubTitle.setText(getTrainingSubtitle());

        updatePersonImage();
    }

    private void updateFineMotorUI() {
        ivPerson.setVisibility(View.GONE);
        rvFineMotorTasks.setVisibility(View.VISIBLE);
        btnGoToTraining.setVisibility(View.GONE);
        tvTrainingSubTitle.setText(getTrainingSubtitle());
    }

    private void updateButtonStyles() {
        ButtonUtils.updateButtonState(requireContext(), btnGrossMotor, "Крупная моторика",
                isGrossMotorSelected ? R.color.white : R.color.indigo,
                isGrossMotorSelected ? R.drawable.btn_active_today_muscle_plan : R.drawable.back_violet_encircle,
                !isGrossMotorSelected);

        ButtonUtils.updateButtonState(requireContext(), btnFineMotor, "Мелкая моторика",
                !isGrossMotorSelected ? R.color.white : R.color.indigo,
                !isGrossMotorSelected ? R.drawable.btn_active_today_muscle_plan : R.drawable.back_violet_encircle,
                isGrossMotorSelected);
    }

    private String getTrainingSubtitle() {
        if (DateUtils.isWeekend()) {
            return "Ты отлично потрудился на неделе! Сегодня ты можешь просто отдыхать.";
        }

        if (currentDayPlan == null) {
            return "Сегодня нет доступного плана тренировок";
        }

        return isGrossMotorSelected
                ? TrainingUtils.getMuscleGroupSubtitle(currentDayPlan.getExercisesGrossMotor(),
                "Сегодня нет тренировки на крупную моторику",
                "Сегодня тренировка на ",
                GrossMotorMuscleGroup::fromString)
                : TrainingUtils.getMuscleGroupSubtitle(currentDayPlan.getExercisesFineMotor(),
                "Сегодня нет тренировки на мелкую моторику",
                "Сегодня тренировка на ",
                FineMotorMuscleGroup::fromString);
    }

    private void openExerciseDetailFragment() {
        if (currentDayPlan != null) {
            ExerciseDetailFragment detailFragment = ExerciseDetailFragment.newInstance(
                    DateUtils.getDayOfWeekString(dayOfWeek), userId, currentDayPlan, isGrossMotorSelected);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_enter, R.anim.fragment_exit)
                    .add(R.id.mainTraining, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void openFineMotorTaskDetail(Exercise exercise) {
        if (currentDayPlan != null) {
            ExerciseDetailFragment detailFragment = ExerciseDetailFragment.newInstance(
                    DateUtils.getDayOfWeekString(dayOfWeek), userId, exercise, false);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_enter, R.anim.fragment_exit)
                    .add(R.id.mainTraining, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void resetDailyProgressIfNeeded() {
        ProgressUtils.resetDailyProgress(requireContext(), this);
    }

    @Override
    public void onProgressReset() {
        toggleButtonState();
    }

    private void updatePersonImage() {
        int imageRes = TrainingUtils.getPersonImageResource(dayOfWeek);
        ivPerson.setImageResource(imageRes);
    }

    private void toggleButtonState() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("child_progress", Context.MODE_PRIVATE);
        boolean isCompleted = sharedPreferences.getBoolean("isCompletedTodayTraining", false);

        if (isCompleted || DateUtils.isWeekend()) {
            ButtonUtils.updateButtonState(requireContext(), btnGoToTraining, "Тренировка завершена",
                    R.color.light_amethyst, R.drawable.btn2inactive_login_back, false);
            btnGoToTraining.setOnClickListener(null);
        } else {
            ButtonUtils.updateButtonState(requireContext(), btnGoToTraining, "Начать тренировку",
                    R.color.white, R.drawable.btn_active_today_muscle_plan, true);

            btnGoToTraining.setOnClickListener(v -> {
                openExerciseDetailFragment();
                sharedPreferences.edit().putBoolean("isCompletedTodayTraining", false).apply();
            });
        }
    }

}
