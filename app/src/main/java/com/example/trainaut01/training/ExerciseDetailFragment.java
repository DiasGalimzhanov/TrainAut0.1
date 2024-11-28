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
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class ExerciseDetailFragment extends Fragment {

    private static final String ARG_DAY = "day";
    private static final String USER_ID = "userId";

    private DayPlan _currentDayPlan;

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

    public static ExerciseDetailFragment newInstance(String day, String userId) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, day);
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_details, container, false);

        init(view);

        if (getArguments() != null) {
            String weekDay = getArguments().getString(ARG_DAY).toLowerCase();
            String userId = getArguments().getString(USER_ID);

            loadExerciseProgress();

            dayPlanRepository.getDayPlanForUserAndDay(userId, weekDay, dayPlan -> {
                if (dayPlan != null) {
                    _currentDayPlan = dayPlan;
                    exercises = dayPlan.getExercisesGrossMotor();
                    setupProgress(exercises != null ? exercises.size() : 0);
                    if (exercises != null && !exercises.isEmpty()) {
                        if (_currentExerciseIndex < exercises.size()) {
                            displayExerciseDetails(exercises.get(_currentExerciseIndex));
                            setupStartButton();
                        } else {
                            Toast.makeText(getContext(), "Все упражнения завершены", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Нет упражнений для этого дня", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "План дня не найден или пустой", Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                Toast.makeText(getContext(), "Ошибка загрузки плана дня", Toast.LENGTH_SHORT).show();
            });
        }

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

        _text = "Поддерживайте друг друга, чтобы создать дружескую атмосферу и следите за их техникой, чтобы избежать травм.\n\n" +
                "Объясняйте ребенку каждое движение.\n\n" +
                "Выполняйте упражнения в спокойном темпе, делая акцент на правильную технику.\n\n" +
                "Ребенок всегда должен дышать ровно и не спешить.\n\n" +
                "Всегда подстраивайте нагрузку под возможности ребёнка.\n\n" +
                "Включайте разминку перед началом тренировки: несколько легких упражнений для разогрева мышц";
    }


    private void saveInSharedPreference(String key, Object value) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("child_progress", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }

        editor.apply();
    }


    private void loadExerciseProgress() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("child_progress", Context.MODE_PRIVATE);
        _currentExerciseIndex = sharedPreferences.getInt("currentExerciseIndex", 0);
        _timeElapsed = sharedPreferences.getFloat("timeElapsed", 0);
    }

    private void displayExerciseDetails(Exercise exercise) {
        Log.d("ExerciseDetailFragment", "Отображение упражнения: " + exercise.getName());

        _tvRecommendations.setText(_text);
        _tvName.setText(exercise.getName());
        _tvDescription.setText(exercise.getDescription());

        _tvSet.setText(exercise.getSets() == 1 ? exercise.getSets() + " подход" : exercise.getSets() + " подхода");

        String durationText = !exercise.getDuration().isEmpty() ? "по " + exercise.getReps() + " " + exercise.getDuration()
                : (exercise.getReps() >= 2 && exercise.getReps() <= 4) ? "по " + exercise.getReps() + " раза" : "по " + exercise.getReps() + " раз";
        _tvRep.setText(durationText);

        _tvRestTime.setText(String.format(Locale.getDefault(), "Время отдыха между подходами %.0f минуты", exercise.getRestTime()));

        int rewardPoints = exercise.getRewardPoints();
        _tvPoints.setText("За выполнение этого упражнения вы получите +" + rewardPoints + " EXP");

        _tvAllPointsForDay.setText("За выполнение всех упражнений вы получите +" + _currentDayPlan.getRewardPointsDay() + "EXP");

        Picasso.get().load(exercise.getImageUrl()).into(_ivImageUrl);
    }

    private void setupProgress(int totalExercises) {
        _pbTraining.setMax(totalExercises);
        _pbTraining.setProgress(0);
        _tvProgressTraining.setText(String.format(Locale.getDefault(), "%d/%d", 0, exercises.size()));
    }


    private void updateProgress(int completedExercises) {
        _pbTraining.setProgress(completedExercises);
        _tvProgressTraining.setText(String.format(Locale.getDefault(), "%d/%d", completedExercises, exercises.size()));
    }


    private void setupStartButton() {
        _btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleButtonAction();
            }
        });
    }

    private void handleButtonAction() {
        String buttonText = _btnStart.getText().toString();
        if (buttonText.equals("Начать упражнение")) {
            startExercise();
        } else if (buttonText.equals("Завершить упражнение")) {
            finishExercise();
        } else if (buttonText.equals("Далее")) {
            nextExercise();
        }
    }

    private void startExercise() {
        resetTimer();
        startTimer();
        updateButtonState("Завершить упражнение", R.color.white, R.drawable.background_finish_exercise);
    }

    private void finishExercise() {
        Log.d("ExerciseDetailFragment", "Упражнение завершено. Индекс: " + _currentExerciseIndex);
        stopTimer();
        completeExercise(exercises.get(_currentExerciseIndex));
    }

    private void nextExercise() {
        Log.d("ExerciseDetailFragment", "Переход к следующему упражнению. Индекс: " + _currentExerciseIndex);

        _currentExerciseIndex++;
        saveInSharedPreference("currentExerciseIndex", _currentExerciseIndex);

        if (_currentExerciseIndex < exercises.size()) {
            Log.d("ExerciseDetailFragment", "Отображение следующего упражнения.");
            resetTimer();
            displayExerciseDetails(exercises.get(_currentExerciseIndex));
            updateButtonState("Начать упражнение", R.color.black, R.drawable.back_start_exercise);
        } else {
            Log.d("ExerciseDetailFragment", "Все упражнения завершены.");
            Toast.makeText(getContext(), "Вы завершили все упражнения на сегодня!", Toast.LENGTH_SHORT).show();
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

        String userId = getArguments().getString(USER_ID);
        String childId = getChildIdFromPreferences();
        String dayPlanId = getArguments().getString(ARG_DAY).toLowerCase();
        String exerciseId = exercise.getId();
        float timeElapsed = _timeElapsed;
        int rewardPoints = _currentDayPlan.getRewardPointsDay();

        saveInSharedPreference("currentExerciseIndex", _currentExerciseIndex);

        dayPlanRepository.updateExerciseCompletedTime(userId, dayPlanId, exerciseId, timeElapsed, aVoid -> {
            Log.d("ExerciseDetailFragment", "Упражнение завершено и время выполнения обновлено.");
            updateProgress(_currentExerciseIndex + 1);

            if (_currentExerciseIndex + 1 < exercises.size()) {
                updateButtonState("Далее", R.color.black, R.drawable.back_start_exercise);
            } else {
                // Обновляем опыт ребенка
                updateChildExperience(userId, childId, rewardPoints, requireContext());

                // Обновляем количество дней
                updateChildCountDays(userId, childId, requireContext());

                updateButtonState("Завершить", R.color.white, R.drawable.background_finish_exercise);

                saveInSharedPreference("currentExerciseIndex", 0);
                saveInSharedPreference("isCompletedTodayTraining", true);

                Bundle result = new Bundle();
                result.putBoolean("isCompletedTodayTraining", true);
                getParentFragmentManager().setFragmentResult("trainingResult", result);

                goToRewardFragment();
            }
        }, e -> {
            Log.e("ExerciseDetailFragment", "Ошибка завершения упражнения: ", e);
            Toast.makeText(getContext(), "Ошибка завершения упражнения.", Toast.LENGTH_SHORT).show();
        });
    }



    /**
     * Обновляет опыт ребенка, добавляя новые очки, и сохраняет изменения в Firestore и SharedPreferences.
     *
     * @param userId    идентификатор пользователя.
     * @param childId   идентификатор ребенка.
     * @param newPoints количество новых очков опыта.
     * @param context   контекст для доступа к SharedPreferences.
     */
    private void updateChildExperience(String userId, String childId, int newPoints, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("child_data", Context.MODE_PRIVATE);

        int currentExp = sharedPref.getInt("exp", 0);
        int updatedExp = currentExp + newPoints;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("exp", updatedExp);
        editor.apply();

        _childRepository.updateChildItem(userId, childId, "exp", updatedExp, context);
    }



    private void updateChildCountDays(String userId, String childId, Context context) {
        SharedPreferences sharedPref = getContext().getSharedPreferences("child_data", Context.MODE_PRIVATE);
        int currentCountDays = sharedPref.getInt("countDays", 0);
        currentCountDays++;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("countDays", currentCountDays).apply();

        _childRepository.updateChildItem(userId, childId, "countDays", currentCountDays, context);
    }

    private void startTimer() {
        _timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                _timeElapsed += 1000;
                int seconds = (int) (_timeElapsed / 1000) % 60;
                int minutes = (int) (_timeElapsed / (1000 * 60)) % 60;
                _tvTimer.setText(String.format(Locale.getDefault(), "Time: %02d:%02d", minutes, seconds));
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
        if (_timer != null) {
            _timer.cancel();
        }
        _timeElapsed = 0;
        _tvTimer.setText("Time: 00:00");
    }

    private void goToRewardFragment() {
        requireActivity().getSupportFragmentManager().popBackStack("TrainingDashboardFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        RewardFragment rewardFragment = RewardFragment.newInstance();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainTraining, rewardFragment)
                .commit();
    }


    /**
     * Извлекает идентификатор ребенка из SharedPreferences.
     *
     * @return идентификатор ребенка (childId), или пустую строку, если идентификатор не найден.
     */
    private String getChildIdFromPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("child_data", Context.MODE_PRIVATE);
        return sharedPreferences.getString("childId", "");
    }



}
