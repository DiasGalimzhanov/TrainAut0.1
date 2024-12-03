package com.example.trainaut01.training;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.trainaut01.repository.ExerciseRepository;
import com.example.trainaut01.utils.ImageUtils;
import com.example.trainaut01.utils.SharedPreferencesUtils;
import com.example.trainaut01.utils.TimeUtils;
import com.example.trainaut01.utils.ToastUtils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

public class ExerciseDetailFragment extends Fragment {

    private static final String ARG_DAY = "day";
    private static final String USER_ID = "userId";
    private static final String ARG_DAY_PLAN = "dayPlan";
    private static final String ARG_IS_GROSS_MOTOR_SELECTED = "isGrossMotorSelected";
    private static final String ARG_SINGLE_EXERCISE = "singleExercise";

    private Exercise singleExercise;
    private DayPlan _currentDayPlan;
    private boolean isGrossMotorSelected;

    private TextView _tvTimer;
    private CountDownTimer _timer;
    private float _timeElapsed = 0;

    private TextView _tvSet, _tvRep, _tvName, _tvDescription, _tvRestTime, _tvRecommendations, _tvPoints;
    private TextView _tvProgressTraining, _tvAllPointsForDay;
    private ImageView _ivImageUrl;
    private Button _btnStart;
    private ProgressBar _pbTraining;

    private String _text;
    private List<Exercise> exercises;
    private int _currentExerciseIndex = 0;

    private AppComponent _appComponent;

    @Inject
    ChildRepository _childRepository;

    @Inject
    DayPlanRepository dayPlanRepository;

    @Inject
    ExerciseRepository exerciseRepository;


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
        _tvAllPointsForDay = view.findViewById(R.id.tvAllPointsForDay);
        _pbTraining = view.findViewById(R.id.pbTraining);
        _tvProgressTraining = view.findViewById(R.id.tvProgressTraining);

        _btnStart.setOnClickListener(view1 -> handleButtonAction());

        _text = getDefaultRecommendationText();
    }

    private void loadFragmentArguments() {
        if (getArguments() == null) {
            ToastUtils.showErrorMessage(ExerciseDetailFragment.this.getContext(),"Ошибка: аргументы не переданы.");
            return;
        }

        isGrossMotorSelected = getArguments().getBoolean(ARG_IS_GROSS_MOTOR_SELECTED);
        loadExerciseProgress();

        if (isGrossMotorSelected) {
            loadGrossMotorExercises();
        } else {
            loadSingleExercise();
        }
    }

    private void loadGrossMotorExercises() {
        _currentDayPlan = (DayPlan) requireArguments().getSerializable(ARG_DAY_PLAN);

        if (_currentDayPlan == null) {
            ToastUtils.showErrorMessage(ExerciseDetailFragment.this.getContext(), "План дня не найден или пустой.");
            return;
        }

        exercises = _currentDayPlan.getExercisesGrossMotor();
        setupProgress(exercises != null ? exercises.size() : 0);

        if (exercises == null || exercises.isEmpty()) {
            ToastUtils.showShortMessage(ExerciseDetailFragment.this.getContext(), "Нет упражнений для данного дня.");
            return;
        }

        if (_currentExerciseIndex < exercises.size()) {
            displayExerciseDetails(exercises.get(_currentExerciseIndex));
        } else {
            ToastUtils.showShortMessage(ExerciseDetailFragment.this.getContext(), "Все упражнения завершены.");
        }
    }


    private void loadSingleExercise() {
        singleExercise = (Exercise) requireArguments().getSerializable(ARG_SINGLE_EXERCISE);
        if (singleExercise == null) {
            ToastUtils.showErrorMessage(ExerciseDetailFragment.this.getContext(), "Упражнение не найдено.");
            return;
        }
        setupProgress(1);
        displayExerciseDetails(singleExercise);
    }

    private String getDefaultRecommendationText() {
        return "Поддерживайте друг друга, чтобы создать дружескую атмосферу и следите за их техникой, чтобы избежать травм.\n\n" +
                "Объясняйте ребенку каждое движение.\n\n" +
                "Выполняйте упражнения в спокойном темпе, делая акцент на правильную технику.\n\n" +
                "Ребенок всегда должен дышать ровно и не спешить.\n\n" +
                "Всегда подстраивайте нагрузку под возможности ребёнка.\n\n" +
                "Включайте разминку перед началом тренировки: несколько легких упражнений для разогрева мышц";
    }

    private void loadExerciseProgress() {
        _currentExerciseIndex = SharedPreferencesUtils.getInt(requireContext(), "child_progress","currentExerciseIndex", 0);

        _timeElapsed = SharedPreferencesUtils.getInt( requireContext(), "child_progress","timeElapsed", 0);
    }


    @SuppressLint("DefaultLocale")
    private void displayExerciseDetails(Exercise exercise) {
        _tvRecommendations.setText(_text);
        _tvName.setText(exercise.getName());
        _tvDescription.setText(exercise.getDescription());
        _tvSet.setText(getSetText(exercise.getSets()));
        _tvRep.setText(getDurationText(exercise));
        _tvRestTime.setText(String.format(Locale.getDefault(), "Время отдыха между подходами %.0f минуты", exercise.getRestTime()));
        _tvPoints.setText(String.format("За выполнение этого упражнения вы получите +%d EXP", exercise.getRewardPoints()));

        if (_currentDayPlan != null) {
            _tvAllPointsForDay.setText(String.format("За выполнение всех упражнений вы получите +%d Exp", _currentDayPlan.getRewardPointsDay()));
        } else {
            _tvAllPointsForDay.setText(String.format("За выполнение бонусного упражнения вы получите: +%d Exp", exercise.getRewardPoints()));
        }

        loadExerciseImage(exercise);
    }

    private String getSetText(int sets) {
        return sets == 1 ? sets + " подход" : sets + " подхода";
    }

    private String getDurationText(Exercise exercise) {
        if (!exercise.getDuration().isEmpty()) {
            return "по " + exercise.getReps() + " " + exercise.getDuration();
        }
        return (exercise.getReps() >= 2 && exercise.getReps() <= 4) ? "по " + exercise.getReps() + " раза" : "по " + exercise.getReps() + " раз";
    }

    private void loadExerciseImage(Exercise exercise) {
        if (isGrossMotorSelected) {
            ImageUtils.setImagePicasso(exercise.getImageUrl(), _ivImageUrl, R.drawable.default_image_not_found, R.drawable.default_image_not_found);
        } else {
            ImageUtils.loadImageFromFirebase(exercise.getImageUrl(), _ivImageUrl,R.drawable.default_image_not_found, R.drawable.default_image_not_found);
        }
    }

    private void setupProgress(int totalExercises) {
        _pbTraining.setMax(totalExercises);

        if (totalExercises == 1) {
            _pbTraining.setProgress(0);
            _tvProgressTraining.setText(String.format(Locale.getDefault(), "%d/%d", 0, 1));
        } else {
            int savedProgress = SharedPreferencesUtils.getInt(requireContext(), "child_progress", "progressBar", 0);
            _pbTraining.setProgress(savedProgress);
            _tvProgressTraining.setText(String.format(Locale.getDefault(), "%d/%d", savedProgress, exercises.size()));
        }
    }

    private void updateProgress(int completedExercises) {
        if (exercises != null) {
            _pbTraining.setProgress(completedExercises);
            _tvProgressTraining.setText(String.format(Locale.getDefault(), "%d/%d", completedExercises, exercises.size()));
        } else {
            _pbTraining.setProgress(1);
            _tvProgressTraining.setText(String.format(Locale.getDefault(), "%d/%d", 1, 1));
        }

        SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "progressBar", completedExercises);
    }

    private void handleButtonAction() {
        switch (_btnStart.getText().toString()) {
            case "Начать упражнение":
                startExercise();
                break;
            case "Завершить упражнение":
                if (isGrossMotorSelected) {
                    finishExercise();
                } else {
                    finishSingleExercise();
                }
                break;
            case "Далее":
                nextExercise();
                break;
        }
    }

    private void finishSingleExercise() {
        stopTimer();
        updateExperienceAndProgress(singleExercise);
        showCompletionToast();
        sendTrainingCompletionResult();
        goToRewardFragment(singleExercise.getRewardPoints());
    }



    private void startExercise() {
        resetTimer();
        startTimer();
        updateButtonState("Завершить упражнение", R.color.white, R.drawable.background_finish_exercise);
    }

    private void finishExercise() {
            stopTimer();
        completeExercise(exercises.get(_currentExerciseIndex));
    }

    private void nextExercise() {
        _currentExerciseIndex++;
        SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "currentExerciseIndex", _currentExerciseIndex);
        if (_currentExerciseIndex < exercises.size()) {
            resetTimer();
            displayExerciseDetails(exercises.get(_currentExerciseIndex));
            updateButtonState("Начать упражнение", R.color.black, R.drawable.back_start_exercise);
        } else {
            showCompletionToast();
        }
    }

    private void updateButtonState(String text, int textColor, int backgroundResource) {
        _btnStart.setText(text);
        _btnStart.setTextColor(getResources().getColor(textColor));
        _btnStart.setBackgroundResource(backgroundResource);
    }

    private void completeExercise(Exercise exercise) {
        if (_currentDayPlan == null) {
            Toast.makeText(getContext(), "План дня отсутствует.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = requireArguments().getString(USER_ID);
        String childId = getChildIdFromPreferences();
        String dayPlanId = Objects.requireNonNull(requireArguments().getString(ARG_DAY)).toLowerCase();
        String exerciseId = exercise.getId();
        float timeElapsed = _timeElapsed;
        int rewardPoints = _currentDayPlan.getRewardPointsDay();

        SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "currentExerciseIndex", _currentExerciseIndex);

        dayPlanRepository.updateExerciseCompletedTime(userId, dayPlanId, exerciseId, timeElapsed, aVoid -> {
            updateProgress(_currentExerciseIndex + 1);
            handleCompletion(userId, childId, rewardPoints);
        }, e -> ToastUtils.showErrorMessage(ExerciseDetailFragment.this.getContext(),"Ошибка завершения упражнения."));
    }

    private void updateExperienceAndProgress(Exercise exercise) {
        String userId = requireArguments().getString(USER_ID);
        String childId = getChildIdFromPreferences();
        updateChildExperience(userId, childId, exercise.getRewardPoints(), requireContext());
        updateProgress(1);
    }

    private void updateChildExperience(String userId, String childId, int newPoints, Context context) {
        int updatedExp = SharedPreferencesUtils.getInt(requireContext(), "child_data", "exp", 0) + newPoints;
        SharedPreferencesUtils.saveInt(requireContext(), "child_data", "exp", updatedExp);
        _childRepository.updateChildItem(userId, childId, "exp", updatedExp, context);
    }

    private void updateChildCountDays(String userId, String childId, Context context) {
        int updatedCountDays = SharedPreferencesUtils.getInt(requireContext(), "child_data", "countDays", 0) + 1;
        SharedPreferencesUtils.saveInt(requireContext(), "child_data", "countDays", updatedCountDays);
        _childRepository.updateChildItem(userId, childId, "countDays", updatedCountDays, context);
    }

    private void handleCompletion(String userId, String childId, int rewardPoints) {
        if (_currentExerciseIndex + 1 < exercises.size()) {
            updateButtonState("Далее", R.color.black, R.drawable.back_start_exercise);
        } else {
            updateChildExperience(userId, childId, rewardPoints, requireContext());
            updateChildCountDays(userId, childId, requireContext());
            updateButtonState("Завершить", R.color.white, R.drawable.background_finish_exercise);
            SharedPreferencesUtils.saveInt(requireContext(), "child_progress", "currentExerciseIndex", 0);
            SharedPreferencesUtils.saveBoolean(requireContext(), "child_progress", "isCompletedTodayTraining", true);
            sendTrainingCompletionResult();
            goToRewardFragment(rewardPoints);
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

    private void showCompletionToast() {
        ToastUtils.showShortMessage(getContext(), "Вы завершили упражнение!");
    }

    private String getChildIdFromPreferences() {
        return SharedPreferencesUtils.getString(requireContext(), "child_data", "childId", "");
    }
}
