package com.example.trainaut01;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.adapter.CalendarAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.CalendarDay;
import com.example.trainaut01.models.Exercise;
import com.example.trainaut01.repository.ChildProgressRepository;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.training.AAC.CognitiveExerciseFragment;
import com.example.trainaut01.training.ProgressFragment;
import com.example.trainaut01.training.TodayMusclePlanFragment;
import com.example.trainaut01.utils.DateUtils;
import com.example.trainaut01.utils.ProgressResetListener;
import com.example.trainaut01.utils.ProgressUtils;
import com.example.trainaut01.utils.SharedPreferencesUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class TrainingDashboardFragment extends Fragment implements ProgressResetListener {

    private TextView _tvLevelDashboard, _tvExpDashboard;
    private Button _btnExercisesMotor, _btnAAC;
    private RecyclerView _calendarRecyclerView;
    private CalendarAdapter _calendarAdapter;
    private List<CalendarDay> _calendarDays;
    private LinearLayout _layoutProgress;
    private int _currentDay;

    private String _currentYear;
    private String _currentMonth;

    @Inject
    ChildProgressRepository _childProgressRepository;

    @Inject
    DayPlanRepository _dayPlanRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_dashboard, container, false);
        init(view);

        ProgressUtils.resetDailyProgress(requireContext(), this);
        setButtonListenerToOpenFragment(_btnExercisesMotor, new TodayMusclePlanFragment());
        setButtonListenerToOpenFragment(_btnAAC, new CognitiveExerciseFragment());

        processCompletedExercises();

        loadChildProgress();
        loadLevel();

        _layoutProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new ProgressFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void init(View view) {
        AppComponent _appComponent = DaggerAppComponent.create();
        _appComponent.inject(this);

        _currentYear = DateUtils.getCurrentYear();
        _currentMonth = DateUtils.getCurrentMonth();

        _tvExpDashboard = view.findViewById(R.id.tvExpDashboard);
        _tvLevelDashboard = view.findViewById(R.id.tvLvlDashboard);
        _btnExercisesMotor = view.findViewById(R.id.btnExercisesMotor);
        _btnAAC = view.findViewById(R.id.btnAAC);
        _layoutProgress = view.findViewById(R.id.layoutProgress);
        _calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        _currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        setupCalendar();
    }

    @Override
    public void onProgressReset() {}

    private void setButtonListenerToOpenFragment(Button button, Fragment fragment) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_enter, R.anim.fragment_exit)
                        .add(R.id.mainTraining, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setupCalendar() {
        _calendarDays = new ArrayList<>();
        generateCalendarDays();

        _calendarAdapter = new CalendarAdapter(_calendarDays, _currentDay);
        _calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        _calendarRecyclerView.setAdapter(_calendarAdapter);
    }

    private void generateCalendarDays() {
        _calendarDays.clear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (firstDayOfWeek == 0) {
            firstDayOfWeek = 7;
        }

        for (int i = 1; i < firstDayOfWeek; i++) {
            _calendarDays.add(new CalendarDay(0, false));
        }

        int maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= maxDaysInMonth; i++) {
            _calendarDays.add(new CalendarDay(i, false));
        }
    }

    public void markDayAsCompleted(int day) {
        int offset = getFirstDayOffset();
        int index = day + offset - 1;

        if (day > 0 && index < _calendarDays.size()) {
            CalendarDay calendarDay = _calendarDays.get(index);
            if (!calendarDay.isCompleted()) {
                calendarDay.setCompleted(true);
                _calendarAdapter.notifyItemChanged(index);

                saveUserProgress();
            }
        }
    }

    private void loadChildProgress() {
        String userId = getUserId();
        if (userId == null) return;

        loadUserProgressFromRepository(userId);
        checkTodayTrainingCompletion();
    }

    private void checkTodayTrainingCompletion() {
        boolean isCompletedTodayTraining = SharedPreferencesUtils.getBoolean(requireContext(), "child_progress", "isCompletedTodayTraining", false);

        if (isCompletedTodayTraining) {
            markDayAsCompleted(_currentDay);
        }
    }

    private void loadUserProgressFromRepository(String userId) {
        _childProgressRepository.loadChildProgress(userId,
                new OnSuccessListener<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        parseAndLoadUserProgress(jsonObject);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TrainingDashboardFragment", "Failed to load progress: " + e.getMessage(), e);
                    }
                }
        );
    }

    private void parseAndLoadUserProgress(JSONObject jsonObject) {
        try {
            JSONArray progressArray = jsonObject.getJSONArray("progress");

//            String currentYear = DateUtils.getCurrentYear();
//            String currentMonth = DateUtils.getCurrentMonth().toLowerCase(Locale.ENGLISH);


            for (int i = 0; i < progressArray.length(); i++) {
                JSONObject monthProgress = progressArray.getJSONObject(i);
                String year = monthProgress.getString("year");
                String month = monthProgress.getString("month").toLowerCase(Locale.ENGLISH);

                if (year.equals(_currentYear) && month.equals(_currentMonth.toLowerCase(Locale.ENGLISH))) {
                    loadCompletedDays(monthProgress);
                }
            }
        } catch (Exception e) {
            Log.e("TrainingDashboardFragment", "Failed to parse progress: " + e.getMessage(), e);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadCompletedDays(JSONObject monthProgress) {
        try {
            JSONArray completedDaysArray = monthProgress.getJSONArray("completedDays");

            for (int j = 0; j < completedDaysArray.length(); j++) {
                int day = completedDaysArray.getInt(j);
                if (day > 0) {
                    markDayAsCompleted(day);
                }
            }
            _calendarAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("TrainingDashboardFragment", "Failed to load completed days: " + e.getMessage(), e);
        }
    }

    private int getFirstDayOffset() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (firstDayOfWeek == Calendar.SUNDAY) {
            return 6;
        } else {
            return firstDayOfWeek - 2;
        }
    }

    private void saveUserProgress() {
            String userId = getUserId();
            if (userId == null) return;

            List<Integer> completedDays = getCompletedDays();
            if (completedDays.isEmpty()) {
                return;
            }

//            String year = DateUtils.getCurrentYear();;
//            String month = DateUtils.getCurrentMonth();

            saveProgressToRepository(userId, _currentYear, _currentMonth, completedDays);
        }

        private String getUserId() {
            return SharedPreferencesUtils.getString(requireContext(), "user_data", "userId", null);
        }

        private List<Integer> getCompletedDays() {
            List<Integer> completedDays = new ArrayList<>();

            for (CalendarDay day : _calendarDays) {
                if (day.getDay() > 0 && day.isCompleted()) {
                    completedDays.add(day.getDay());
                }
            }
            return completedDays;
        }

        private void saveProgressToRepository(String userId, String year, String month, List<Integer> completedDays) {
            _childProgressRepository.saveChildProgress(userId, year, month, completedDays,
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                        }
                    },
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("saveUserProgress", "Failed to save progress: " + e.getMessage(), e);
                        }
                    }
            );
        }


    @SuppressLint("SetTextI18n")
    public void loadLevel(){
        int exp = SharedPreferencesUtils.getInt(requireContext(), "child_data", "exp", 0);
        int level = exp / 5000;
        int expForNextLevel = 5000;

        _tvLevelDashboard.setText("Уровень: " + level);
        _tvExpDashboard.setText(exp + " / " + (level + 1) * expForNextLevel + " опыта");
    }

    private void processCompletedExercises() {
            boolean isCompleted = SharedPreferencesUtils.getBoolean(requireContext(), "child_progress", "isCompletedTodayTraining", false);
            boolean isProgressAlreadySaved = SharedPreferencesUtils.getBoolean(requireContext(), "child_progress", "isProgressSavedToday", false);
            if (!isCompleted || isProgressAlreadySaved) {
                return;
            }

        new Thread(() -> {
            String userId = getUserId();
            if (userId == null) {
                Log.e("processCompletedExercises", "User ID не найден, невозможно обработать завершенные упражнения.");
                return;
            }

            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            String dayOfWeekString = DateUtils.getDayOfWeekString(dayOfWeek);

            _dayPlanRepository.getDayPlanForUserAndDay(userId, dayOfWeekString,
                    dayPlan -> {
                        new Handler(Looper.getMainLooper()).post(() -> saveCompletedExercisesToStorage(userId, dayPlan.getExercisesGrossMotor()));
                    },
                    e -> Log.e("processCompletedExercises", "Ошибка при загрузке дневного плана: " + e.getMessage(), e)
            );
        }).start();
    }

    /**
     * Сохраняет завершенные упражнения в хранилище прогресса.
     *
     * @param userId   Идентификатор пользователя.
     * @param exercises Список завершенных упражнений.
     */
    private void saveCompletedExercisesToStorage(String userId, List<Exercise> exercises) {
        if (exercises == null || exercises.isEmpty()) {
            return;
        }

        _childProgressRepository.loadChildProgressDetails(userId,
                existingData -> {
                    try {
                        JSONObject progressData = prepareProgressData(existingData, exercises);
                        saveProgressDataToStorage(userId, progressData);
                    } catch (Exception e) {
                        Log.e("saveCompletedExercises", "Ошибка при обработке данных прогресса: " + e.getMessage(), e);
                    }
                },
                e -> Log.e("saveCompletedExercises", "Ошибка загрузки существующих данных: " + e.getMessage(), e));
    }

    /**
     * Подготавливает объект данных прогресса.
     *
     * @param existingData Существующие данные прогресса или null.
     * @param exercises    Список завершенных упражнений.
     * @return Обновленный объект данных прогресса.
     * @throws Exception Исключение при подготовке данных.
     */
    private JSONObject prepareProgressData(JSONObject existingData, List<Exercise> exercises) throws Exception {
        JSONObject progressData = existingData != null ? existingData : new JSONObject();
        JSONArray progressArray = progressData.optJSONArray("progress");
        if (progressArray == null) {
            progressArray = new JSONArray();
        }

        JSONObject dayObject = createDayObject(exercises);

        boolean monthFound = updateExistingMonth(progressArray, _currentYear, _currentMonth, dayObject);
        if (!monthFound) {
            addNewMonth(progressArray, _currentYear, _currentMonth, dayObject);
        }

        progressData.put("progress", progressArray);
        return progressData;
    }

    /**
     * Создает объект дня с завершенными упражнениями.
     *
     * @param exercises Список завершенных упражнений.
     * @return Объект JSON, представляющий завершенный день.
     * @throws Exception Исключение при создании объекта.
     */
    private JSONObject createDayObject(List<Exercise> exercises) throws Exception {
        JSONObject dayObject = new JSONObject();
        JSONArray completedExercises = new JSONArray();

        for (Exercise exercise : exercises) {
            completedExercises.put(exercise.toJsonObject());
        }

        dayObject.put(String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), completedExercises);
        return dayObject;
    }

    /**
     * Обновляет существующий месяц прогресса.
     *
     * @param progressArray Массив прогресса.
     * @param year          Текущий год.
     * @param month         Текущий месяц.
     * @param dayObject     Объект дня.
     * @return true, если месяц был найден и обновлен; false в противном случае.
     * @throws Exception Исключение при обновлении данных.
     */
    private boolean updateExistingMonth(JSONArray progressArray, String year, String month, JSONObject dayObject) throws Exception {
        for (int i = 0; i < progressArray.length(); i++) {
            JSONObject monthProgress = progressArray.getJSONObject(i);
            if (monthProgress.getString("year").equals(year) &&
                    monthProgress.getString("month").equalsIgnoreCase(month)) {
                monthProgress.getJSONArray("completedDays").put(dayObject);
                return true;
            }
        }
        return false;
    }

    /**
     * Добавляет новый месяц прогресса.
     *
     * @param progressArray Массив прогресса.
     * @param year          Текущий год.
     * @param month         Текущий месяц.
     * @param dayObject     Объект дня.
     * @throws Exception Исключение при добавлении данных.
     */
    private void addNewMonth(JSONArray progressArray, String year, String month, JSONObject dayObject) throws Exception {
        JSONObject newMonthProgress = new JSONObject();
        newMonthProgress.put("year", year);
        newMonthProgress.put("month", month);
        newMonthProgress.put("completedDays", new JSONArray().put(dayObject));
        progressArray.put(newMonthProgress);
    }

    /**
     * Сохраняет данные прогресса в хранилище.
     *
     * @param userId      Идентификатор пользователя.
     * @param progressData Данные прогресса.
     */
    private void saveProgressDataToStorage(String userId, JSONObject progressData) {
        _childProgressRepository.saveToStorage(userId, progressData,
                unused -> {
                    Log.d("saveCompletedExercises", "Данные успешно сохранены в Firestore Storage.");
                    SharedPreferencesUtils.saveBoolean(requireContext(), "child_progress", "isProgressSavedToday", true);
                },
                e -> Log.e("saveCompletedExercises", "Ошибка при сохранении данных: " + e.getMessage(), e));
    }


    public void updateBottomNavigation() {
        ((BottomNavigationUpdater) requireActivity()).updateBottomNavigationSelection(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation();
    }

}
