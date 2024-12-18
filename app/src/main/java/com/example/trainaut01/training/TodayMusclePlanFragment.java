package com.example.trainaut01.training;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.trainaut01.R;
import com.example.trainaut01.adapter.FineMotorAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.FragmentTodayMusclePlanBinding;
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


/**
 * Фрагмент для отображения плана тренировок на текущий день.
 */
public class TodayMusclePlanFragment extends Fragment implements ProgressResetListener {

    private FragmentTodayMusclePlanBinding _binding;

    private String _userId;
    private DayPlan _currentDayPlan;
    private boolean _isGrossMotorSelected = true;
    private int _dayOfWeek;

    @Inject
    DayPlanRepository _dayPlanRepository;

    /**
     * Создает и возвращает представление фрагмента.
     *
     * @param inflater  объект LayoutInflater для создания представления.
     * @param container контейнер для представления (может быть null).
     * @param savedInstanceState сохраненное состояние (может быть null).
     * @return корневое представление фрагмента.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = FragmentTodayMusclePlanBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    /**
     * Вызывается после создания представления фрагмента.
     *
     * @param view корневое представление фрагмента.
     * @param savedInstanceState сохраненное состояние (может быть null).
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        if (!DateUtils.isWeekend()) {
            resetDailyProgressIfNeeded();
            loadDayPlans();
            handleTrainingTypeSelection();
        }
    }

    /**
     * Вызывается при возобновлении работы фрагмента.
     * Обновляет пользовательский интерфейс.
     */
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * Вызывается при уничтожении представления фрагмента.
     * Очищает объект binding для предотвращения утечек памяти.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    /**
     * Инициализирует компоненты фрагмента.
     */
    private void init() {
        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        _userId = SharedPreferencesUtils.getString(requireContext(), "user_data", "userId", null);
        _dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        _binding.btnGrossMotor.setOnClickListener(v -> switchToGrossMotor());
        _binding.btnFineMotor.setOnClickListener(v -> switchToFineMotor());
        _binding.rvFineMotorTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    /**
     * Загружает план тренировок на текущий день.
     */
    private void loadDayPlans() {
        String dayOfWeekString = DateUtils.getDayOfWeekString(_dayOfWeek);

        _dayPlanRepository.getDayPlanForUserAndDay(_userId, dayOfWeekString, this::handleDayPlanLoaded, error -> {
            ToastUtils.showShortMessage(requireContext(), "Ошибка загрузки плана: " + error.getMessage());
        });
    }

    /**
     * Обрабатывает успешную загрузку плана тренировок.
     *
     * @param dayPlan объект DayPlan, представляющий план на текущий день.
     */
    private void handleDayPlanLoaded(DayPlan dayPlan) {
        _currentDayPlan = dayPlan;

        if (_currentDayPlan == null) {
            ToastUtils.showShortMessage(requireContext(), "План на сегодня отсутствует.");
            return;
        }

        setupFineMotorAdapter();
        updateUI();
    }

    /**
     * Управляет выбором типа тренировки (крупная или мелкая моторика).
     */
    private void handleTrainingTypeSelection() {
        if (_isGrossMotorSelected) {
            switchToGrossMotor();
        } else {
            switchToFineMotor();
        }
    }

    /**
     * Настраивает адаптер для отображения задач на мелкую моторику.
     */
    private void setupFineMotorAdapter() {
        if (_currentDayPlan.getExercisesFineMotor() != null) {
            FineMotorAdapter fineMotorAdapter = new FineMotorAdapter(
                    requireContext(),
                    _currentDayPlan.getExercisesFineMotor(),
                    this::openFineMotorTaskDetail,
                    DateUtils.getDayOfWeekString(_dayOfWeek)
            );
            _binding.rvFineMotorTasks.setAdapter(fineMotorAdapter);
        }
    }

    /**
     * Переключается на отображение упражнений для крупной моторики.
     */
    private void switchToGrossMotor() {
        _isGrossMotorSelected = true;
        updateUI();
    }

    /**
     * Переключается на отображение упражнений для мелкой моторики.
     */
    private void switchToFineMotor() {
        _isGrossMotorSelected = false;
        updateUI();
    }

    /**
     * Обновляет пользовательский интерфейс в зависимости от текущего состояния.
     */
    private void updateUI() {
        updateButtonStyles();
        toggleButtonState();

        if (_isGrossMotorSelected) {
            updateGrossMotorUI();
        } else {
            updateFineMotorUI();
        }

        _binding.tvTrainingSubTitle.setText(getTrainingSubtitle());
    }

    /**
     * Обновляет интерфейс для крупной моторики.
     */
    private void updateGrossMotorUI() {
        _binding.lottieCatPlaying.setVisibility(View.GONE);
        _binding.lottieCatPlaying.playAnimation();

        _binding.ivPerson.setVisibility(View.VISIBLE);
        _binding.rvFineMotorTasks.setVisibility(View.GONE);
        _binding.btnGoToTraining.setVisibility(View.VISIBLE);

        updatePersonImage();
    }

    /**
     * Обновляет интерфейс для мелкой моторики.
     */
    private void updateFineMotorUI() {
        if (DateUtils.isWeekend()) {
            _binding.lottieCatPlaying.setVisibility(View.VISIBLE);
            _binding.lottieCatPlaying.playAnimation();
        } else {
            _binding.lottieCatPlaying.setVisibility(View.GONE);
            _binding.lottieCatPlaying.playAnimation();
        }

        _binding.ivPerson.setVisibility(View.GONE);
        _binding.rvFineMotorTasks.setVisibility(View.VISIBLE);
        _binding.btnGoToTraining.setVisibility(View.GONE);
    }

    /**
     * Обновляет стили кнопок в зависимости от выбранного типа тренировки.
     */
    private void updateButtonStyles() {
        ButtonUtils.updateButtonState(requireContext(), _binding.btnGrossMotor, "Крупная моторика",
                _isGrossMotorSelected ? R.color.white : R.color.indigo,
                _isGrossMotorSelected ? R.drawable.btn_active_today_muscle_plan : R.drawable.back_violet_encircle,
                !_isGrossMotorSelected);

        ButtonUtils.updateButtonState(requireContext(), _binding.btnFineMotor, "Мелкая моторика",
                !_isGrossMotorSelected ? R.color.white : R.color.indigo,
                !_isGrossMotorSelected ? R.drawable.btn_active_today_muscle_plan : R.drawable.back_violet_encircle,
                _isGrossMotorSelected);
    }

    /**
     * Возвращает подзаголовок для текущей тренировки.
     *
     * @return строка с подзаголовком тренировки.
     */
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

    /**
     * Открывает фрагмент с деталями упражнения.
     */
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

    /**
     * Открывает детали упражнения для мелкой моторики.
     *
     * @param exercise объект упражнения.
     */
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

    /**
     * Сбрасывает ежедневный прогресс, если это необходимо.
     */
    private void resetDailyProgressIfNeeded() {
        ProgressUtils.resetDailyProgress(requireContext(), this);
    }

    /**
     * Вызывается при сбросе прогресса.
     */
    @Override
    public void onProgressReset() {
        toggleButtonState();
    }

    /**
     * Обновляет изображение человека в зависимости от дня недели.
     */
    private void updatePersonImage() {
        int imageRes = TrainingUtils.getPersonImageResource(_dayOfWeek);
        _binding.ivPerson.setImageResource(imageRes);
    }

    /**
     * Переключает состояние кнопки "Начать тренировку" в зависимости от прогресса.
     */
    private void toggleButtonState() {
        boolean isCompleted = SharedPreferencesUtils.getBoolean(requireContext(), "child_progress", "isCompletedTodayTraining", false);

        if (isCompleted || DateUtils.isWeekend()) {
            ButtonUtils.updateButtonState(requireContext(), _binding.btnGoToTraining, "Начать тренировку",
                    R.color.white, R.drawable.btn2inactive_login_back, false);
            _binding.btnGoToTraining.setOnClickListener(null);
        } else {
            ButtonUtils.updateButtonState(requireContext(), _binding.btnGoToTraining, "Начать тренировку",
                    R.color.white, R.drawable.btn_active_today_muscle_plan, true);

            _binding.btnGoToTraining.setOnClickListener(v -> {
                openExerciseDetailFragment();
                SharedPreferencesUtils.saveBoolean(requireContext(), "child_progress", "isCompletedTodayTraining", false);
            });
        }
    }

}