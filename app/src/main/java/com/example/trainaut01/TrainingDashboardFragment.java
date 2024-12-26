package com.example.trainaut01;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.trainaut01.adapter.CalendarAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.FragmentTrainingDashboardBinding;
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

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Фрагмент для отображения панели управления тренировками.
 * Обеспечивает доступ к календарю, уровням, упражнениям и прогрессу.
 */
public class TrainingDashboardFragment extends Fragment implements ProgressResetListener {

    private FragmentTrainingDashboardBinding _binding;

    private CalendarAdapter _calendarAdapter;
    private List<CalendarDay> _calendarDays;
    private int _currentDay;

    private String _currentYear;
    private String _currentMonth;

    @Inject
    ChildProgressRepository _childProgressRepository;

    @Inject
    DayPlanRepository _dayPlanRepository;

    /**
     * Создает и возвращает представление фрагмента.
     *
     * @param inflater           объект LayoutInflater для создания представлений
     * @param container          контейнер для представления (может быть null)
     * @param savedInstanceState сохраненное состояние (может быть null)
     * @return корневое представление фрагмента
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _binding = FragmentTrainingDashboardBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    /**
     * Вызывается после создания представления фрагмента.
     *
     * @param view               корневое представление фрагмента
     * @param savedInstanceState сохраненное состояние (может быть null)
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        ProgressUtils.resetDailyProgress(requireContext(), this);
        setButtonListenerToOpenFragment(_binding.btnExercisesMotor, new TodayMusclePlanFragment());
        setButtonListenerToOpenFragment(_binding.btnAAC, new CognitiveExerciseFragment());
        loadChildProgress();
        processCompletedExercises();
        loadLevel();

        _binding.layoutProgress.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ProgressFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });
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
     * Инициализирует компоненты и настраивает календарь.
     */
    private void init() {
        AppComponent _appComponent = DaggerAppComponent.create();
        _appComponent.inject(this);

        _currentYear = DateUtils.getCurrentYear();
        _currentMonth = DateUtils.getCurrentMonth();
        _currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        setupCalendar();
    }

    /**
     * Реализация интерфейса ProgressResetListener. Выполняется при сбросе прогресса.
     */
    @Override
    public void onProgressReset() {
    }

    /**
     * Устанавливает слушатель нажатия для кнопки, чтобы открыть указанный фрагмент.
     *
     * @param button   кнопка (анимация Lottie)
     * @param fragment фрагмент для открытия
     */
    private void setButtonListenerToOpenFragment(LottieAnimationView button, Fragment fragment) {
        button.setOnClickListener(view -> requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_enter, R.anim.fragment_exit)
                .add(R.id.mainTraining, fragment)
                .addToBackStack(null)
                .commit());
    }

    /**
     * Настраивает календарь с использованием адаптера.
     */
    private void setupCalendar() {
        _calendarDays = new ArrayList<>();
        generateCalendarDays();

        String localizedMonth = DateUtils.getCurrentMonthLocalized();

        _calendarAdapter = new CalendarAdapter(requireContext(), _calendarDays, _currentDay, localizedMonth.toUpperCase());

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 7);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 7 : 1;
            }
        });

        _binding.calendarRecyclerView.setLayoutManager(layoutManager);
        _binding.calendarRecyclerView.setAdapter(_calendarAdapter);
    }

    /**
     * Генерирует список дней для календаря.
     */
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

    /**
     * Помечает день как завершенный в календаре.
     *
     * @param day номер дня
     */
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

    /**
     * Загружает прогресс ребенка.
     */
    private void loadChildProgress() {
        String userId = getUserId();
        if (userId == null) return;

        loadUserProgressFromRepository(userId);
        checkTodayTrainingCompletion();
    }

    /**
     * Проверяет, завершена ли сегодняшняя тренировка.
     */
    private void checkTodayTrainingCompletion() {
        boolean isCompletedTodayTraining = SharedPreferencesUtils.getBoolean(requireContext(), "child_progress", "isCompletedTodayTraining", false);

        if (isCompletedTodayTraining) {
            markDayAsCompleted(_currentDay);
        }
    }

    /**
     * Загружает данные прогресса пользователя из репозитория.
     *
     * @param userId идентификатор пользователя
     */
    private void loadUserProgressFromRepository(String userId) {
        _childProgressRepository.loadChildProgress(userId,
                jsonObject -> parseAndLoadUserProgress(jsonObject),
                e -> Log.e("TrainingDashboardFragment", String.format(getString(R.string.progress_load_failed), e.getMessage()), e)
        );
    }

    /**
     * Разбирает и загружает данные прогресса пользователя.
     *
     * @param jsonObject объект JSON с данными прогресса
     */
    private void parseAndLoadUserProgress(JSONObject jsonObject) {
        try {
            JSONArray progressArray = jsonObject.getJSONArray("progress");

            for (int i = 0; i < progressArray.length(); i++) {
                JSONObject monthProgress = progressArray.getJSONObject(i);
                String year = monthProgress.getString("year");
                String month = monthProgress.getString("month").toLowerCase(Locale.ENGLISH);

                if (year.equals(_currentYear) && month.equals(_currentMonth.toLowerCase(Locale.ENGLISH))) {
                    loadCompletedDays(monthProgress);
                }
            }
        } catch (Exception e) {
            Log.e("TrainingDashboardFragment", String.format(getString(R.string.progress_parse_failed), e.getMessage()), e);
        }
    }

    /**
     * Загружает завершенные дни из объекта месяца.
     *
     * @param monthProgress объект JSON с данными месяца
     */
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
            Log.e("TrainingDashboardFragment", String.format(getString(R.string.completed_days_load_failed), e.getMessage()), e);
        }
    }

    /**
     * Возвращает смещение первого дня месяца в календаре.
     *
     * @return смещение в днях
     */
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

    /**
     * Сохраняет прогресс пользователя.
     */
    private void saveUserProgress() {
        String userId = getUserId();
        if (userId == null) return;

        List<Integer> completedDays = getCompletedDays();
        if (completedDays.isEmpty()) {
            return;
        }

        saveProgressToRepository(userId, _currentYear, _currentMonth, completedDays);
    }

    /**
     * Получает идентификатор пользователя из SharedPreferences.
     *
     * @return идентификатор пользователя или null
     */
    private String getUserId() {
        return SharedPreferencesUtils.getString(requireContext(), "user_data", "userId", null);
    }

    /**
     * Возвращает список завершенных дней.
     *
     * @return список завершенных дней
     */
    private List<Integer> getCompletedDays() {
        List<Integer> completedDays = new ArrayList<>();

        for (CalendarDay day : _calendarDays) {
            if (day.getDay() > 0 && day.isCompleted()) {
                completedDays.add(day.getDay());
            }
        }
        return completedDays;
    }

    /**
     * Сохраняет прогресс пользователя в репозиторий.
     *
     * @param userId        идентификатор пользователя
     * @param year          текущий год
     * @param month         текущий месяц
     * @param completedDays список завершенных дней
     */
    private void saveProgressToRepository(String userId, String year, String month, List<Integer> completedDays) {
        _childProgressRepository.saveChildProgress(userId, year, month, completedDays,
                unused -> Log.d("saveUserProgress", getString(R.string.progress_save_success)),
                e -> Log.e("saveUserProgress", String.format(getString(R.string.progress_save_failed), e.getMessage()), e)
        );
    }

    /**
     * Загружает уровень ребенка и отображает его на экране.
     */
    @SuppressLint("SetTextI18n")
    public void loadLevel() {
        int exp = SharedPreferencesUtils.getInt(requireContext(), "child_data", "exp", 0);
        int level = SharedPreferencesUtils.getInt(requireContext(), "child_data", "lvl", 1);
        int expForNextLevel = 5000;

        _binding.tvLvlDashboard.setText(String.format(getString(R.string.level_text), level));
        _binding.tvExpDashboard.setText(String.format(getString(R.string.experience_text), exp, level * expForNextLevel));
    }

    /**
     * Обрабатывает завершенные упражнения и сохраняет прогресс.
     */
    private void processCompletedExercises() {
        boolean isCompleted = SharedPreferencesUtils.getBoolean(requireContext(), "child_progress", "isCompletedTodayTraining", false);
        boolean isProgressAlreadySaved = SharedPreferencesUtils.getBoolean(requireContext(), "child_progress", "isProgressSavedToday", false);
        if (!isCompleted || isProgressAlreadySaved) {
            return;
        }

        new Thread(() -> {
            String userId = getUserId();
            if (userId == null) {
                Log.e("processCompletedExercises", getString(R.string.user_not_found));
                return;
            }

            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            String dayOfWeekString = DateUtils.getDayOfWeekString(dayOfWeek);

            _dayPlanRepository.getDayPlanForUserAndDay(userId, dayOfWeekString,
                    dayPlan -> new Handler(Looper.getMainLooper()).post(() -> saveCompletedExercisesToStorage(userId, dayPlan.getExercisesGrossMotor())),
                    e -> Log.e("processCompletedExercises", String.format(getString(R.string.day_plan_load_failed), e.getMessage()), e)
            );
        }).start();
    }

    /**
     * Сохраняет завершенные упражнения в хранилище прогресса.
     *
     * @param userId    Идентификатор пользователя.
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
                        Log.e("saveCompletedExercises", String.format(getString(R.string.exercises_process_failed), e.getMessage()), e);
                    }
                },
                e -> Log.e("saveCompletedExercises", String.format(getString(R.string.exercise_save_failed), e.getMessage()), e));
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
     * @param userId       Идентификатор пользователя.
     * @param progressData Данные прогресса.
     */
    private void saveProgressDataToStorage(String userId, JSONObject progressData) {
        _childProgressRepository.saveToStorage(userId, progressData,
                unused -> {
                    SharedPreferencesUtils.saveBoolean(requireContext(), "child_progress", "isProgressSavedToday", true);
                },
                e -> Log.e("saveCompletedExercises", "Ошибка при сохранении данных: " + e.getMessage(), e));
    }

    /**
     * Обновляет состояние нижней навигации, выделяя текущий фрагмент.
     */
    public void updateBottomNavigation() {
        ((BottomNavigationUpdater) requireActivity()).updateBottomNavigationSelection(this);
    }

    /**
     * Вызывается, когда фрагмент становится видимым для пользователя.
     * Обновляет состояние нижней навигации.
     */
    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation();
    }
}
