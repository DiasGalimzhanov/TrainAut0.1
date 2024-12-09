package com.example.trainaut01.training;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private String _userId;
    private DayPlan _currentDayPlan;

    private boolean _isGrossMotorSelected = true;
    private int _dayOfWeek;

    private TextView _tvTrainingSubTitle;
    private ImageView _ivPerson;

    private Button _btnGrossMotor, _btnFineMotor, _btnGoToTraining;
    private RecyclerView _rvFineMotorTasks;

    @Inject
    DayPlanRepository _dayPlanRepository;

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

        _userId = SharedPreferencesUtils.getString(requireContext(), "user_data", "userId", null);
        _dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        _tvTrainingSubTitle = view.findViewById(R.id.tvTrainingSubTitle);
        _ivPerson = view.findViewById(R.id.ivPerson);
        _btnGoToTraining = view.findViewById(R.id.btnGoToTraining);
        _btnGrossMotor = view.findViewById(R.id.btn_gross_motor);
        _btnFineMotor = view.findViewById(R.id.btn_fine_motor);
        _rvFineMotorTasks = view.findViewById(R.id.rvFineMotorTasks);
        _rvFineMotorTasks.setLayoutManager(new LinearLayoutManager(requireContext()));

        _btnGrossMotor.setOnClickListener(v -> switchToGrossMotor());
        _btnFineMotor.setOnClickListener(v -> switchToFineMotor());
    }

    private void loadDayPlans() {
        String dayOfWeekString = DateUtils.getDayOfWeekString(_dayOfWeek);

        _dayPlanRepository.getDayPlanForUserAndDay(_userId, dayOfWeekString, this::handleDayPlanLoaded, error -> {
            ToastUtils.showShortMessage(requireContext(), "Ошибка загрузки плана: " + error.getMessage());
        });
    }

    private void handleDayPlanLoaded(DayPlan dayPlan) {
        _currentDayPlan = dayPlan;

        if (_currentDayPlan == null) {
            ToastUtils.showShortMessage(requireContext(), "План на сегодня отсутствует.");
            return;
        }

        setupFineMotorAdapter();
        updateUI();
    }

    private void handleTrainingTypeSelection() {
        if (_isGrossMotorSelected) {
            switchToGrossMotor();
        } else {
            switchToFineMotor();
        }
    }

    private void setupFineMotorAdapter() {
        if (_currentDayPlan.getExercisesFineMotor() != null) {
            FineMotorAdapter fineMotorAdapter = new FineMotorAdapter(
                    requireContext(),
                    _currentDayPlan.getExercisesFineMotor(),
                    this::openFineMotorTaskDetail,
                    DateUtils.getDayOfWeekString(_dayOfWeek)
            );
            _rvFineMotorTasks.setAdapter(fineMotorAdapter);
        }
    }

    private void disableTrainingForWeekend() {
        _tvTrainingSubTitle.setText("Ты отлично потрудился на неделе! Сегодня ты можешь просто отдыхать.");
        _rvFineMotorTasks.setVisibility(View.GONE);
    }

    private void switchToGrossMotor() {
        _isGrossMotorSelected = true;
        updateUI();
    }

    private void switchToFineMotor() {
        _isGrossMotorSelected = false;
        updateUI();
    }

    private void updateUI() {
        updateButtonStyles();
        toggleButtonState();

        if (_isGrossMotorSelected) {
            updateGrossMotorUI();
        } else {
            updateFineMotorUI();
        }
    }

    private void updateGrossMotorUI() {
        _ivPerson.setVisibility(View.VISIBLE);
        _rvFineMotorTasks.setVisibility(View.GONE);
        _btnGoToTraining.setVisibility(View.VISIBLE);
        _tvTrainingSubTitle.setText(getTrainingSubtitle());

        updatePersonImage();
    }

    private void updateFineMotorUI() {
        _ivPerson.setVisibility(View.GONE);
        _rvFineMotorTasks.setVisibility(View.VISIBLE);
        _btnGoToTraining.setVisibility(View.GONE);
        _tvTrainingSubTitle.setText(getTrainingSubtitle());
    }

    private void updateButtonStyles() {
        ButtonUtils.updateButtonState(requireContext(), _btnGrossMotor, "Крупная моторика",
                _isGrossMotorSelected ? R.color.white : R.color.indigo,
                _isGrossMotorSelected ? R.drawable.btn_active_today_muscle_plan : R.drawable.back_violet_encircle,
                !_isGrossMotorSelected);

        ButtonUtils.updateButtonState(requireContext(), _btnFineMotor, "Мелкая моторика",
                !_isGrossMotorSelected ? R.color.white : R.color.indigo,
                !_isGrossMotorSelected ? R.drawable.btn_active_today_muscle_plan : R.drawable.back_violet_encircle,
                _isGrossMotorSelected);
    }

    private String getTrainingSubtitle() {
        if (_currentDayPlan == null) {
            return "Сегодня нет доступного плана тренировок";
        }

        return _isGrossMotorSelected
                ? TrainingUtils.getMuscleGroupSubtitle(_currentDayPlan.getExercisesGrossMotor(),
                "Сегодня нет тренировки на крупную моторику",
                "Сегодня тренировка на ",
                GrossMotorMuscleGroup::fromString)
                : TrainingUtils.getMuscleGroupSubtitle(_currentDayPlan.getExercisesFineMotor(),
                "Сегодня нет тренировки на мелкую моторику",
                "Сегодня тренировка на ",
                FineMotorMuscleGroup::fromString);
    }

    private void openExerciseDetailFragment() {
        if (_currentDayPlan != null) {
            ExerciseDetailFragment detailFragment = ExerciseDetailFragment.newInstance(
                    DateUtils.getDayOfWeekString(_dayOfWeek), _userId, _currentDayPlan, _isGrossMotorSelected);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_enter, R.anim.fragment_exit)
                    .add(R.id.mainTraining, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void openFineMotorTaskDetail(Exercise exercise) {
        if (_currentDayPlan != null) {
            ExerciseDetailFragment detailFragment = ExerciseDetailFragment.newInstance(
                    DateUtils.getDayOfWeekString(_dayOfWeek), _userId, exercise, false);
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
        int imageRes = TrainingUtils.getPersonImageResource(_dayOfWeek);
        _ivPerson.setImageResource(imageRes);
    }

    private void toggleButtonState() {
        boolean isCompleted = SharedPreferencesUtils.getBoolean(requireContext(), "child_progress", "isCompletedTodayTraining", false);

        if (isCompleted || DateUtils.isWeekend()) {
            ButtonUtils.updateButtonState(requireContext(), _btnGoToTraining, "Начать тренировку",
                    R.color.white, R.drawable.btn2inactive_login_back, false);
            _btnGoToTraining.setOnClickListener(null);
        } else {
            ButtonUtils.updateButtonState(requireContext(), _btnGoToTraining, "Начать тренировку",
                    R.color.white, R.drawable.btn_active_today_muscle_plan, true);

            _btnGoToTraining.setOnClickListener(v -> {
                openExerciseDetailFragment();
                SharedPreferencesUtils.saveBoolean(requireContext(), "child_progress", "isCompletedTodayTraining", false);
            });
        }
    }

}
