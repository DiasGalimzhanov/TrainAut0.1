package com.example.trainaut01.training;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.trainaut01.R;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
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

public class ExerciseDetailFragment extends Fragment {

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

    private TextView _tvSet, _tvName, _tvDescription, _tvRestTime, _tvRecommendations, _tvPoints;
    private TextView _tvProgressTraining, _tvAllPointsForDay, _tvTimer;
    private ImageView _ivImageUrl;
    private Button _btnStart, _btnPause, _btnMiss;
    private ProgressBar _pbTraining;

    private String _text;
    private List<Exercise> _exercises;
    private int _currentExerciseIndex = 0;
    private int _skippedExercises = 0;
    private int _remainingPoints;

    @Inject
    ChildRepository _childRepository;

    @Inject
    DayPlanRepository _dayPlanRepository;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_details, container, false);
        init(view);
        loadFragmentArguments();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupMissButton();
    }

    public void init(View view) {
        AppComponent _appComponent = DaggerAppComponent.create();
        _appComponent.inject(this);

        _tvSet = view.findViewById(R.id.tvSet);
        _tvName = view.findViewById(R.id.tvName);
        _tvDescription = view.findViewById(R.id.tvDescription);
        _tvRestTime = view.findViewById(R.id.tvRestTime);
        _tvPoints = view.findViewById(R.id.tvPoints);
        _tvRecommendations = view.findViewById(R.id.tvRecommendations);
        _ivImageUrl = view.findViewById(R.id.ivImageUrl);
        _btnStart = view.findViewById(R.id.btnStart);
        _btnPause = view.findViewById(R.id.btnPause);
        _btnMiss = view.findViewById(R.id.btnMiss);
        _tvTimer = view.findViewById(R.id.tvTimer);
        _tvAllPointsForDay = view.findViewById(R.id.tvAllPointsForDay);
        _pbTraining = view.findViewById(R.id.pbTraining);
        _tvProgressTraining = view.findViewById(R.id.tvProgressTraining);

        _text = getDefaultRecommendationText();
        setupListeners();
    }

    private void setupListeners() {
        _btnStart.setOnClickListener(view -> handleButtonAction());
        _btnPause.setOnClickListener(view -> handlePauseAction());
        setupMissButton();
    }

    private void setupMissButton() {
        if (_isGrossMotorSelected) {
            ButtonUtils.updateButtonState(requireContext(), _btnMiss, "Пропустить", R.color.white, R.drawable.btn1_login_back, true);
            _btnMiss.setOnClickListener(view1 -> handleMissAction());
        } else {
            ButtonUtils.updateButtonState(requireContext(), _btnMiss, "Пропустить", R.color.white, R.drawable.btn2inactive_login_back, false);
        }
    }

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

    private void loadExerciseProgress() {
        _currentExerciseIndex = SharedPreferencesUtils.getInt(requireContext(), "child_progress", "currentExerciseIndex", 0);
    }

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

    @SuppressLint("DefaultLocale")
    private void displayExerciseDetails(Exercise exercise) {
        _tvRecommendations.setText(_text);
        _tvName.setText(exercise.getName());
        _tvDescription.setText(exercise.getDescription());

        if (_isGrossMotorSelected) {
            configureTextView(_tvSet, getSetAndRepsText(exercise.getSets(), exercise.getReps(), exercise.getDuration()), true);
            configureTextView(_tvRestTime, String.format("Время отдыха между подходами %.0f минуты", exercise.getRestTime()), true);
            configureTextView(_tvPoints, String.format("За выполнение этого упражнения вы получите +%d EXP", exercise.getRewardPoints()), true);
        } else {
            configureTextView(_tvSet, "", false);
            configureTextView(_tvRestTime, "", false);
            configureTextView(_tvPoints, "", false);
        }

        if (!_isGrossMotorSelected) _remainingPoints = exercise.getRewardPoints();
        loadExerciseImage(exercise);
        updateAllPointsForDayTextView();
    }

    @SuppressLint("DefaultLocale")
    private void updateAllPointsForDayTextView() {
        if (_isGrossMotorSelected)
            _tvAllPointsForDay.setText(String.format("За выполнение всех упражнений вы получите +%d Exp", _remainingPoints));
        else
            _tvAllPointsForDay.setText(String.format("За выполнение этого бонусного упражнения вы получите +%d Exp", _remainingPoints));
    }

    private void configureTextView(TextView textView, String text, boolean visible) {
        textView.setText(text);
        textView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

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

    private void loadExerciseImage(Exercise exercise) {
        if (_isGrossMotorSelected) {
            ImageUtils.setImagePicasso(exercise.getImageUrl(), _ivImageUrl, R.drawable.default_image, R.drawable.default_image);
        } else {
            ImageUtils.loadImageFromFirebase(exercise.getImageUrl(), _ivImageUrl, R.drawable.default_image, R.drawable.default_image);
        }
    }

    private String getDefaultRecommendationText() {
        return "Поддерживайте друг друга, чтобы создать дружескую атмосферу и следите за их техникой, чтобы избежать травм.\n\n" +
                "Объясняйте ребенку каждое движение.\n\n" +
                "Выполняйте упражнения в спокойном темпе, делая акцент на правильную технику.\n\n" +
                "Ребенок всегда должен дышать ровно и не спешить.\n\n" +
                "Всегда подстраивайте нагрузку под возможности ребёнка.\n\n" +
                "Включайте разминку перед началом тренировки: несколько легких упражнений для разогрева мышц";
    }

    private void setupProgress(int totalExercises) {
        _pbTraining.setMax(totalExercises);

        if (totalExercises == 1) {
            configureProgress(0, totalExercises);
        } else {
            int savedProgress = SharedPreferencesUtils.getInt(requireContext(), "child_progress", "progressBar", 0);
            configureProgress(savedProgress, _exercises.size());
        }
    }

    private void updateProgress(int completedExercises) {
        if (_isGrossMotorSelected) {
            configureProgress(completedExercises, _exercises.size());
            SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "progressBar", completedExercises);
        }
    }

    @SuppressLint("DefaultLocale")
    private void configureProgress(int completed, int total) {
        _pbTraining.setProgress(completed);
        _tvProgressTraining.setText(String.format("%d/%d", completed, total));
    }

    private void handleButtonAction() {
        int totalRewardPoints = _currentDayPlan != null ? _currentDayPlan.getRewardPointsDay() : 0;

        switch (_btnStart.getText().toString()) {
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

    private void handlePauseAction() {
        if (_timer != null) {
            stopTimer();
            ButtonUtils.updateButtonState(requireContext(), _btnPause, "Продолжить", R.color.white, R.drawable.back_start_exercise, true);
            ButtonUtils.updateButtonState(requireContext(), _btnStart, "Завершить упражнение", R.color.white, R.drawable.btn2inactive_login_back, false);
            _btnPause.setOnClickListener(view -> handleResumeAction());
        }
    }

    private void handleResumeAction() {
        startTimer();
        ButtonUtils.updateButtonState(requireContext(), _btnPause, "Пауза", R.color.white, R.drawable.btn1_login_back, true);
        ButtonUtils.updateButtonState(requireContext(), _btnStart, "Завершить упражнение", R.color.white, R.drawable.finish_exercise_back, true);
        _btnPause.setOnClickListener(view -> handlePauseAction());
    }

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

        ButtonUtils.updateButtonState(requireContext(), _btnPause, "Пауза", R.color.white, R.drawable.btn2inactive_login_back, false);

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

    private void startExercise() {
        resetTimer();
        startTimer();

        ButtonUtils.updateButtonState(requireContext(), _btnStart, "Завершить упражнение", R.color.white, R.drawable.finish_exercise_back, true);
        ButtonUtils.updateButtonState(requireContext(), _btnPause, "Пауза", R.color.white, R.drawable.btn1_login_back, true);

    }

    private void nextExercise() {
        _currentExerciseIndex++;
        SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "currentExerciseIndex", _currentExerciseIndex);
        if (_currentExerciseIndex < _exercises.size()) {
            resetTimer();
            displayExerciseDetails(_exercises.get(_currentExerciseIndex));
            ButtonUtils.updateButtonState(requireContext(), _btnStart,"Начать упражнение", R.color.black, R.drawable.back_start_exercise, true);
        } else {
            int totalRewardPoints = _currentDayPlan != null ? _currentDayPlan.getRewardPointsDay() : 0;
            finalizeTraining(totalRewardPoints);
        }
    }

    private void completeExercise(Exercise exercise) {

        float timeElapsed = _timeElapsed;
        int rewardPoints = _currentDayPlan.getRewardPointsDay();

        updateExerciseTime(exercise, timeElapsed, () -> {
            updateExperienceAndProgress(exercise);
            updateProgress(_currentExerciseIndex + 1);
            handleCompletion(rewardPoints);
        });
    }


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


    private void updateExperienceAndProgress(Exercise exercise) {
        updateChildExperience(exercise.getRewardPoints());
        updateChildLvl();
        updateProgress(1);
    }

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

    private void updateChildCountDays() {
        int updatedCountDays = SharedPreferencesUtils.getInt(requireContext(), "child_data", "countDays", 0) + 1;
        SharedPreferencesUtils.saveInt(requireContext(), "child_data", "countDays", updatedCountDays);
        _childRepository.updateChildItem(_userId, _childId, "countDays", updatedCountDays, requireContext());
    }

    private void handleCompletion(int rewardPoints) {
        if (_currentExerciseIndex + 1 < _exercises.size()) {
            ButtonUtils.updateButtonState(requireContext(), _btnStart, "Далее", R.color.black, R.drawable.btn1_login_back, true);
            ButtonUtils.updateButtonState(requireContext(), _btnPause, "Пауза", R.color.white, R.drawable.btn2inactive_login_back, false);
        } else {
            finalizeTraining(rewardPoints);
        }
    }

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


    private void handleGrossMotorCompletion(int finalPoints) {
        updateChildExperience(finalPoints);
        updateChildLvl();
        updateChildCountDays();
    }

    private int calculateFinalPoints(int totalRewardPoints) {
        if (!_isGrossMotorSelected) {
            return _singleExercise != null ? _singleExercise.getRewardPoints() : 0;
        }

        int totalExercises = _exercises != null ? _exercises.size() : 1;
        int pointsPerExercise = totalExercises > 0 ? totalRewardPoints / totalExercises : 0;
        return Math.max(0, totalRewardPoints - (_skippedExercises * pointsPerExercise));
    }



    private void finalizeUI(int finalPoints) {
        ButtonUtils.updateButtonState(requireContext(), _btnStart, "Завершить", R.color.white, R.drawable.finish_exercise_back, true);
        ButtonUtils.updateButtonState(requireContext(), _btnPause, "Пауза", R.color.white, R.drawable.btn2inactive_login_back, false);
        if (_isGrossMotorSelected) {
            SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "currentExerciseIndex", 0);
            SharedPreferencesUtils.saveBoolean(requireContext(), "child_progress", "isCompletedTodayTraining", true);
            sendTrainingCompletionResult();
        }
        goToRewardFragment(finalPoints);
    }

    private void handleExerciseCompletion(int totalRewardPoints) {
        if (_isGrossMotorSelected) {
            stopTimer();
            completeExercise(_exercises.get(_currentExerciseIndex));
        } else {
            finalizeTraining(totalRewardPoints);
        }
    }

    private void sendTrainingCompletionResult() {
        Bundle result = new Bundle();
        result.putBoolean("isCompletedTodayTraining", true);
        getParentFragmentManager().setFragmentResult("trainingResult", result);
    }

    private void startTimer() {
        _timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                _timeElapsed += 1000;
                _tvTimer.setText(TimeUtils.formatElapsedTime(_timeElapsed));
            }

            @Override
            public void onFinish() {
            }
        };
        _timer.start();
    }

    private void stopTimer() {
        if (_timer != null) {
            _timer.cancel();
        }
    }

    private void resetTimer() {
        stopTimer();
        _timeElapsed = 0;
        _tvTimer.setText(TimeUtils.formatElapsedTime(_timeElapsed));
    }

    private void goToRewardFragment(Integer rewardPoints) {
        requireActivity().getSupportFragmentManager().popBackStack("TrainingDashboardFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        RewardFragment rewardFragment = RewardFragment.newInstance(rewardPoints);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainTraining, rewardFragment)
                .commit();
    }

    private void returnToPreviousPageWithoutCompletion() {
        ProgressUtils.resetAllProgress(requireContext());
        ToastUtils.showShortMessage(requireContext(), "Все упражнения пропущены. Прогресс сброшен.");
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private String getChildIdFromPreferences() {
        return SharedPreferencesUtils.getString(requireContext(), "child_data", "childId", "");
    }
}
