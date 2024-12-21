package com.example.trainaut01.training;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.trainaut01.R;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.FragmentExerciseDetailsBinding;
import com.example.trainaut01.models.DayPlan;
import com.example.trainaut01.models.Exercise;
import com.example.trainaut01.repository.ChildRepository;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.utils.ButtonUtils;
import com.example.trainaut01.utils.ImageUtils;
import com.example.trainaut01.utils.ProgressUtils;
import com.example.trainaut01.utils.SharedPreferencesUtils;
import com.example.trainaut01.utils.TimeUtils;
import com.example.trainaut01.utils.ToastUtils;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Фрагмент для отображения деталей упражнения.
 */
public class ExerciseDetailFragment extends Fragment {

    private FragmentExerciseDetailsBinding _binding;

    private static final String ARG_DAY = "day";
    private static final String USER_ID = "userId";
    private static final String ARG_DAY_PLAN = "dayPlan";
    private static final String ARG_IS_GROSS_MOTOR_SELECTED = "isGrossMotorSelected";
    private static final String ARG_SINGLE_EXERCISE = "singleExercise";

    private String _userId;
    private String _childId;

    private Exercise _singleExercise;
    private DayPlan _currentDayPlan;
    private boolean _isGrossMotorSelected;

    private CountDownTimer _timer;
    private float _timeElapsed = 0;

    private String _text;
    private List<Exercise> _exercises;
    private int _currentExerciseIndex = 0;
    private int _skippedExercises = 0;
    private int _remainingPoints;

    @Inject
    ChildRepository _childRepository;

    @Inject
    DayPlanRepository _dayPlanRepository;

    /**
     * Создает новый экземпляр фрагмента с переданными аргументами.
     *
     * @param day                  день недели.
     * @param userId               идентификатор пользователя.
     * @param data                 данные для тренировки (DayPlan или Exercise).
     * @param isGrossMotorSelected true, если выбрана тренировка на крупную моторику.
     * @return экземпляр ExerciseDetailFragment.
     */
    public static ExerciseDetailFragment newInstance(String day, String userId, Object data, boolean isGrossMotorSelected) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, day);
        args.putString(USER_ID, userId);
        args.putBoolean(ARG_IS_GROSS_MOTOR_SELECTED, isGrossMotorSelected);

        if (isGrossMotorSelected && data instanceof DayPlan) {
            args.putSerializable(ARG_DAY_PLAN, (DayPlan) data);
        } else if (!isGrossMotorSelected && data instanceof Exercise) {
            args.putSerializable(ARG_SINGLE_EXERCISE, (Exercise) data);
        }

        fragment.setArguments(args);
        return fragment;
    }

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _binding = FragmentExerciseDetailsBinding.inflate(inflater, container, false);
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
        loadFragmentArguments();
        setupListeners();
    }

    /**
     * Вызывается при возобновлении работы фрагмента.
     * Настраивает кнопку "Пропустить".
     */
    @Override
    public void onResume() {
        super.onResume();
        setupMissButton();
    }

    /**
     * Вызывается при уничтожении представления фрагмента.
     * Очищает объект binding для предотвращения утечек памяти.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
        _timer = null;
        _binding = null;
    }

    /**
     * Инициализирует компоненты фрагмента.
     */

    public void init() {
        AppComponent _appComponent = DaggerAppComponent.create();
        _appComponent.inject(this);

        _text = getDefaultRecommendationText();

        setupListeners();
    }

    /**
     * Настраивает слушатели для кнопок.
     */
    private void setupListeners() {
        _binding.btnStart.setOnClickListener(view -> handleButtonAction());
        _binding.btnPause.setOnClickListener(view -> handlePauseAction());
        setupMissButton();
    }

    /**
     * Настраивает кнопку "Пропустить" в зависимости от типа тренировки.
     */
    private void setupMissButton() {
        if (_isGrossMotorSelected) {
            ButtonUtils.updateButtonState(requireContext(), _binding.btnMiss, "Пропустить", R.color.white, R.drawable.btn1_login_back, true);
            _binding.btnMiss.setOnClickListener(view1 -> handleMissAction());
        } else {
            ButtonUtils.updateButtonState(requireContext(), _binding.btnMiss, "Пропустить", R.color.white, R.drawable.btn2inactive_login_back, false);
        }
    }

    /**
     * Загружает аргументы, переданные фрагменту, и подготавливает данные для отображения.
     */
    private void loadFragmentArguments() {
        if (getArguments() == null) {
            ToastUtils.showErrorMessage(ExerciseDetailFragment.this.getContext(), "Ошибка: аргументы не переданы.");
            return;
        }

        _userId = getArguments().getString(USER_ID);
        _childId = getChildIdFromPreferences();
        _isGrossMotorSelected = getArguments().getBoolean(ARG_IS_GROSS_MOTOR_SELECTED);
        loadExerciseProgress();

        if (_isGrossMotorSelected) {
            loadGrossMotorExercises();
        } else {
            loadSingleExercise();
        }
    }

    /**
     * Загружает прогресс текущего упражнения из SharedPreferences.
     */
    private void loadExerciseProgress() {
        _currentExerciseIndex = SharedPreferencesUtils.getInt(requireContext(), "child_progress", "currentExerciseIndex", 0);
    }

    /**
     * Загружает упражнения для тренировки на крупную моторику.
     */
    private void loadGrossMotorExercises() {
        _currentDayPlan = (DayPlan) requireArguments().getSerializable(ARG_DAY_PLAN);

        if (_currentDayPlan == null || (_exercises = _currentDayPlan.getExercisesGrossMotor()) == null || _exercises.isEmpty()) {
            ToastUtils.showShortMessage(requireContext(), "Нет упражнений для данного дня.");
            return;
        }

        _remainingPoints = SharedPreferencesUtils.getInt(requireContext(), "child_progress", "remainingPoints", _currentDayPlan.getRewardPointsDay());
        setupProgress(_exercises.size());
        displayExerciseDetails(_exercises.get(_currentExerciseIndex));
    }

    /**
     * Загружает одно упражнение для тренировки на мелкую моторику.
     */
    private void loadSingleExercise() {
        _singleExercise = (Exercise) requireArguments().getSerializable(ARG_SINGLE_EXERCISE);

        if (_singleExercise == null) {
            ToastUtils.showErrorMessage(getContext(), "Упражнение не найдено.");
            return;
        }

        _exercises = Collections.singletonList(_singleExercise);
        setupProgress(1);
        displayExerciseDetails(_singleExercise);
    }


    /**
     * Отображает детали упражнения на экране.
     *
     * @param exercise упражнение для отображения.
     */
    @SuppressLint("DefaultLocale")
    private void displayExerciseDetails(Exercise exercise) {
        _binding.tvRecommendations.setText(_text);
        _binding.tvName.setText(exercise.getName());
        _binding.tvDescription.setText(exercise.getDescription());

        if (_isGrossMotorSelected) {
            configureTextView(_binding.tvSet, getSetAndRepsText(exercise.getSets(), exercise.getReps(), exercise.getDuration()), true);
            configureTextView(_binding.tvRestTime, String.format("Время отдыха между подходами %.0f минуты", exercise.getRestTime()), true);
            configureTextView(_binding.tvPoints, String.format("За выполнение этого упражнения вы получите +%d EXP", exercise.getRewardPoints()), true);
        } else {
            configureTextView(_binding.tvSet, "", false);
            configureTextView(_binding.tvRestTime, "", false);
            configureTextView(_binding.tvPoints, "", false);
        }

        if (!_isGrossMotorSelected) _remainingPoints = exercise.getRewardPoints();
        loadExerciseImage(exercise);
        updateAllPointsForDayTextView();
    }

    /**
     * Обновляет отображение общего количества очков за день.
     */
    @SuppressLint("DefaultLocale")
    private void updateAllPointsForDayTextView() {
        if (_isGrossMotorSelected)
            _binding.tvAllPointsForDay.setText(String.format("За выполнение всех упражнений вы получите +%d Exp", _remainingPoints));
        else
            _binding.tvAllPointsForDay.setText(String.format("За выполнение этого бонусного упражнения вы получите +%d Exp", _remainingPoints));
    }

    /**
     * Настраивает видимость и текст TextView.
     *
     * @param textView TextView для настройки.
     * @param text     текст для отображения.
     * @param visible  true, если TextView должен быть видимым; иначе false.
     */
    private void configureTextView(TextView textView, String text, boolean visible) {
        textView.setText(text);
        textView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Возвращает текст с количеством подходов и повторений.
     *
     * @param sets     количество подходов.
     * @param reps     количество повторений.
     * @param duration длительность упражнения.
     * @return строка с текстом подходов и повторений.
     */
    private String getSetAndRepsText(int sets, int reps, String duration) {
        String setsText = sets == 1 ? sets + " подход" : sets + " подхода";
        String repsText;
        if (!duration.isEmpty()) {
            repsText = "по " + reps + " " + duration;
        } else {
            repsText = (reps >= 2 && reps <= 4) ? "по " + reps + " раза" : "по " + reps + " раз";
        }
        return setsText + " " + repsText;
    }

    /**
     * Загружает изображение упражнения.
     *
     * @param exercise упражнение, для которого нужно загрузить изображение.
     */
    private void loadExerciseImage(Exercise exercise) {
        if (_isGrossMotorSelected) {
            ImageUtils.loadGifFromFirebase(exercise.getImageUrl(), _binding.ivImageUrl);
        } else {
            ImageUtils.loadImageFromFirebase(exercise.getImageUrl(), _binding.ivImageUrl);
        }
    }

    /**
     * Возвращает текст с рекомендациями по выполнению упражнений.
     *
     * @return строка с рекомендациями.
     */
    private String getDefaultRecommendationText() {
        return "Поддерживайте друг друга, чтобы создать дружескую атмосферу и следите за их техникой, чтобы избежать травм.\n\n" +
                "Объясняйте ребенку каждое движение.\n\n" +
                "Выполняйте упражнения в спокойном темпе, делая акцент на правильную технику.\n\n" +
                "Ребенок всегда должен дышать ровно и не спешить.\n\n" +
                "Всегда подстраивайте нагрузку под возможности ребёнка.\n\n" +
                "Включайте разминку перед началом тренировки: несколько легких упражнений для разогрева мышц";
    }

    /**
     * Настраивает прогресс выполнения упражнений.
     *
     * @param totalExercises общее количество упражнений.
     */
    private void setupProgress(int totalExercises) {
        _binding.pbTraining.setMax(totalExercises);

        if (totalExercises == 1) {
            configureProgress(0, totalExercises);
        } else {
            int savedProgress = SharedPreferencesUtils.getInt(requireContext(), "child_progress", "progressBar", 0);
            configureProgress(savedProgress, _exercises.size());
        }
    }

    /**
     * Обновляет прогресс выполнения упражнений.
     *
     * @param completedExercises количество завершенных упражнений.
     */
    private void updateProgress(int completedExercises) {
        if (_isGrossMotorSelected) {
            configureProgress(completedExercises, _exercises.size());
            SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "progressBar", completedExercises);
        }
    }

    /**
     * Настраивает отображение прогресса.
     *
     * @param completed количество завершенных упражнений.
     * @param total     общее количество упражнений.
     */
    @SuppressLint("DefaultLocale")
    private void configureProgress(int completed, int total) {
        _binding.pbTraining.setProgress(completed);
        _binding.tvProgressTraining.setText(String.format("%d/%d", completed, total));
    }

    /**
     * Обрабатывает действия кнопок на экране.
     */
    private void handleButtonAction() {
        int totalRewardPoints = _currentDayPlan != null ? _currentDayPlan.getRewardPointsDay() : 0;

        switch (_binding.btnStart.getText().toString()) {
            case "Начать упражнение":
                startExercise();
                break;
            case "Завершить упражнение":
                handleExerciseCompletion(totalRewardPoints);
                break;
            case "Далее":
                nextExercise();
                break;
        }
    }

    /**
     * Обрабатывает действие при паузе.
     */
    private void handlePauseAction() {
        if (_timer != null) {
            stopTimer();
            ButtonUtils.updateButtonState(requireContext(), _binding.btnPause, "Продолжить", R.color.white, R.drawable.back_start_exercise, true);
            ButtonUtils.updateButtonState(requireContext(), _binding.btnStart, "Завершить упражнение", R.color.white, R.drawable.btn2inactive_login_back, false);
            _binding.btnPause.setOnClickListener(view -> handleResumeAction());
        }
    }

    /**
     * Обрабатывает действие при возобновлении упражнения.
     */
    private void handleResumeAction() {
        startTimer();
        ButtonUtils.updateButtonState(requireContext(), _binding.btnPause, "Пауза", R.color.white, R.drawable.btn1_login_back, true);
        ButtonUtils.updateButtonState(requireContext(), _binding.btnStart, "Завершить упражнение", R.color.white, R.drawable.finish_exercise_back, true);
        _binding.btnPause.setOnClickListener(view -> handlePauseAction());
    }

    /**
     * Обрабатывает действие при пропуске упражнения.
     */
    private void handleMissAction() {
        if (_currentExerciseIndex < _exercises.size()) {
            Exercise missedExercise = _exercises.get(_currentExerciseIndex);

            updateExerciseTime(missedExercise, 0, () -> {
                updateProgress(_currentExerciseIndex + 1);
            });
        }

        _skippedExercises++;
        _remainingPoints = calculateFinalPoints(_currentDayPlan.getRewardPointsDay());

        updateAllPointsForDayTextView();
        SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "remainingPoints", _remainingPoints);

        ButtonUtils.updateButtonState(requireContext(), _binding.btnPause, "Пауза", R.color.white, R.drawable.btn2inactive_login_back, false);

        if (_currentExerciseIndex >= _exercises.size() - 1) {
            if (_skippedExercises == _exercises.size()) {
                returnToPreviousPageWithoutCompletion();
            } else {
                finalizeTraining(_remainingPoints);
            }
        } else {
            nextExercise();
        }
    }

    /**
     * Запускает выполнение упражнения.
     */
    private void startExercise() {
        resetTimer();
        startTimer();

        ButtonUtils.updateButtonState(requireContext(), _binding.btnStart, "Завершить упражнение", R.color.white, R.drawable.finish_exercise_back, true);
        ButtonUtils.updateButtonState(requireContext(), _binding.btnPause, "Пауза", R.color.white, R.drawable.btn1_login_back, true);

    }

    /**
     * Переходит к следующему упражнению.
     */
    private void nextExercise() {
        _currentExerciseIndex++;
        SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "currentExerciseIndex", _currentExerciseIndex);
        if (_currentExerciseIndex < _exercises.size()) {
            resetTimer();
            displayExerciseDetails(_exercises.get(_currentExerciseIndex));
            ButtonUtils.updateButtonState(requireContext(), _binding.btnStart,"Начать упражнение", R.color.black, R.drawable.back_start_exercise, true);
        } else {
            int totalRewardPoints = _currentDayPlan != null ? _currentDayPlan.getRewardPointsDay() : 0;
            finalizeTraining(totalRewardPoints);
        }
    }

    /**
     * Завершает выполнение текущего упражнения.
     *
     * @param exercise упражнение для завершения.
     */
    private void completeExercise(Exercise exercise) {

        float timeElapsed = _timeElapsed;
        int rewardPoints = _currentDayPlan.getRewardPointsDay();

        updateExerciseTime(exercise, timeElapsed, () -> {
            updateExperienceAndProgress(exercise);
            updateProgress(_currentExerciseIndex + 1);
            handleCompletion(rewardPoints);
        });
    }

    /**
     * Обновляет время выполнения упражнения.
     *
     * @param exercise        упражнение, для которого обновляется время.
     * @param timeElapsed     затраченное время.
     * @param onSuccessAction действие при успешном обновлении.
     */
    private void updateExerciseTime(Exercise exercise, float timeElapsed, Runnable onSuccessAction) {
        if (_currentDayPlan == null) {
            ToastUtils.showErrorMessage(getContext(), "План дня отсутствует.");
            return;
        }

        String dayPlanId = requireArguments().getString(ARG_DAY).toLowerCase();
        String exerciseId = exercise.getId();

        _dayPlanRepository.updateExerciseCompletedTime(_userId, dayPlanId, exerciseId, timeElapsed,
                aVoid -> {
                    if (onSuccessAction != null) {
                        onSuccessAction.run();
                    }
                },
                e -> ToastUtils.showErrorMessage(getContext(), "Ошибка обновления времени выполнения упражнения.")
        );
    }

    /**
     * Обновляет опыт и прогресс ребенка после выполнения упражнения.
     *
     * @param exercise текущее упражнение для обновления данных.
     */
    private void updateExperienceAndProgress(Exercise exercise) {
        updateChildExperience(exercise.getRewardPoints());
        updateChildLvl();
        updateProgress(1);
    }

    /**
     * Обновляет опыт ребенка.
     *
     * @param newPoints количество добавляемых очков опыта.
     */
    private void updateChildExperience(int newPoints) {
        int updatedExp = SharedPreferencesUtils.getInt(requireContext(), "child_data", "exp", 0) + newPoints;
        SharedPreferencesUtils.saveInt(requireContext(), "child_data", "exp", updatedExp);
        _childRepository.updateChildItem(_userId, _childId, "exp", updatedExp, requireContext());
    }

    private void updateChildLvl(){
        int exp = SharedPreferencesUtils.getInt(requireContext(), "child_data", "exp", 0);
        int updateLvl = (exp / 5000) + 1;
        SharedPreferencesUtils.saveInt(requireContext(), "child_data", "lvl", updateLvl);
        _childRepository.updateChildItem(_userId, _childId, "lvl", updateLvl, requireContext());
    }

    /**
     * Увеличивает счетчик дней тренировок для ребенка.
     */
    private void updateChildCountDays() {
        int updatedCountDays = SharedPreferencesUtils.getInt(requireContext(), "child_data", "countDays", 0) + 1;
        SharedPreferencesUtils.saveInt(requireContext(), "child_data", "countDays", updatedCountDays);
        _childRepository.updateChildItem(_userId, _childId, "countDays", updatedCountDays, requireContext());
    }

    /**
     * Обрабатывает завершение текущего упражнения или тренировочного дня.
     *
     * @param rewardPoints общее количество очков за тренировку.
     */
    private void handleCompletion(int rewardPoints) {
        if (_currentExerciseIndex + 1 < _exercises.size()) {
            ButtonUtils.updateButtonState(requireContext(), _binding.btnStart, "Далее", R.color.black, R.drawable.btn1_login_back, true);
            ButtonUtils.updateButtonState(requireContext(), _binding.btnPause, "Пауза", R.color.white, R.drawable.btn2inactive_login_back, false);
        } else {
            finalizeTraining(rewardPoints);
        }
    }

    /**
     * Завершает тренировку и рассчитывает финальные очки.
     *
     * @param totalRewardPoints общее количество очков опыта за тренировку.
     */
    private void finalizeTraining(int totalRewardPoints) {
        if (_isGrossMotorSelected && _currentDayPlan == null) {
            ToastUtils.showErrorMessage(getContext(), "Ошибка: План дня отсутствует.");
            return;
        }

        if (_exercises == null || _exercises.isEmpty()) {
            ToastUtils.showErrorMessage(getContext(), "Нет упражнений для завершения.");
            return;
        }

        if (_skippedExercises == _exercises.size()) {
            returnToPreviousPageWithoutCompletion();
            return;
        }

        int finalPoints = calculateFinalPoints(totalRewardPoints);

        if (_isGrossMotorSelected) {
            handleGrossMotorCompletion(finalPoints);
        } else {
            updateExperienceAndProgress(_singleExercise);
        }

        if (_currentDayPlan != null) {
            SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "remainingPoints", _currentDayPlan.getRewardPointsDay());
        }

        finalizeUI(finalPoints);
    }

    /**
     * Обрабатывает завершение тренировок на крупную моторику.
     *
     * @param finalPoints финальное количество очков за тренировку.
     */
    private void handleGrossMotorCompletion(int finalPoints) {
        updateChildExperience(finalPoints);
        updateChildLvl();
        updateChildCountDays();
    }

    /**
     * Рассчитывает финальные очки с учетом пропущенных упражнений.
     *
     * @param totalRewardPoints общее количество очков за тренировку.
     * @return финальное количество очков.
     */
    private int calculateFinalPoints(int totalRewardPoints) {
        if (!_isGrossMotorSelected) {
            return _singleExercise != null ? _singleExercise.getRewardPoints() : 0;
        }

        int totalExercises = _exercises != null ? _exercises.size() : 1;
        int pointsPerExercise = totalExercises > 0 ? totalRewardPoints / totalExercises : 0;
        return Math.max(0, totalRewardPoints - (_skippedExercises * pointsPerExercise));
    }

    /**
     * Завершает тренировку и обновляет интерфейс.
     *
     * @param finalPoints финальное количество очков за тренировку.
     */
    private void finalizeUI(int finalPoints) {
        ButtonUtils.updateButtonState(requireContext(), _binding.btnStart, "Завершить", R.color.white, R.drawable.finish_exercise_back, true);
        ButtonUtils.updateButtonState(requireContext(), _binding.btnPause, "Пауза", R.color.white, R.drawable.btn2inactive_login_back, false);
        if (_isGrossMotorSelected) {
            SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "currentExerciseIndex", 0);
            SharedPreferencesUtils.saveBoolean(requireContext(), "child_progress", "isCompletedTodayTraining", true);
            sendTrainingCompletionResult();
        }
        goToRewardFragment(finalPoints);
    }

    /**
     * Завершает упражнение или тренировку в зависимости от текущего контекста.
     *
     * @param totalRewardPoints общее количество очков за тренировку.
     */    private void handleExerciseCompletion(int totalRewardPoints) {
        if (_isGrossMotorSelected) {
            stopTimer();
            completeExercise(_exercises.get(_currentExerciseIndex));
        } else {
            finalizeTraining(totalRewardPoints);
        }
    }

    /**
     * Отправляет результат завершения тренировки.
     */
    private void sendTrainingCompletionResult() {
        Bundle result = new Bundle();
        result.putBoolean("isCompletedTodayTraining", true);
        getParentFragmentManager().setFragmentResult("trainingResult", result);
    }

    /**
     * Запускает таймер для упражнения.
     */
    private void startTimer() {
        _timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                _timeElapsed += 1000;
                _binding.tvTimer.setText(TimeUtils.formatElapsedTime(_timeElapsed));
            }

            @Override
            public void onFinish() {
            }
        };
        _timer.start();
    }

    /**
     * Останавливает таймер упражнения.
     */
    private void stopTimer() {
        if (_timer != null) {
            _timer.cancel();
        }
    }

    /**
     * Сбрасывает таймер упражнения.
     */
    private void resetTimer() {
        stopTimer();
        _timeElapsed = 0;
        _binding.tvTimer.setText(TimeUtils.formatElapsedTime(_timeElapsed));
    }

    /**
     * Переходит на экран награды после завершения тренировки.
     *
     * @param rewardPoints количество очков награды.
     */
    private void goToRewardFragment(Integer rewardPoints) {
        requireActivity().getSupportFragmentManager().popBackStack("TrainingDashboardFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        RewardFragment rewardFragment = RewardFragment.newInstance(rewardPoints);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainTraining, rewardFragment)
                .commit();
    }

    /**
     * Возвращает пользователя на предыдущую страницу без сохранения прогресса.
     */
    private void returnToPreviousPageWithoutCompletion() {
        ProgressUtils.resetAllProgress(requireContext());
        ToastUtils.showShortMessage(requireContext(), "Все упражнения пропущены. Прогресс сброшен.");
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * Получает идентификатор ребенка из настроек.
     *
     * @return идентификатор ребенка.
     */
    private String getChildIdFromPreferences() {
        return SharedPreferencesUtils.getString(requireContext(), "child_data", "childId", "");
    }
}
