package com.example.trainaut01;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.trainaut01.repository.ChildProgressRepository;
import com.example.trainaut01.training.AAC.CognitiveExerciseFragment;
import com.example.trainaut01.training.ProgressFragment;
import com.example.trainaut01.training.TodayMusclePlanFragment;
import com.example.trainaut01.utils.ProgressResetListener;
import com.example.trainaut01.utils.ProgressUtils;
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

    private AppComponent _appComponent;

    @Inject
    ChildProgressRepository _childProgressRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_dashboard, container, false);
        init(view);

        ProgressUtils.resetDailyProgress(requireContext(), this);
        setButtonListenerToOpenFragment(_btnExercisesMotor, new TodayMusclePlanFragment());
        setButtonListenerToOpenFragment(_btnAAC, new CognitiveExerciseFragment());

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
        _appComponent = DaggerAppComponent.create();
        _appComponent.inject(this);

        _tvExpDashboard = view.findViewById(R.id.tvExpDashboard);
        _tvLevelDashboard = view.findViewById(R.id.tvLvlDashboard);
        _btnExercisesMotor = view.findViewById(R.id.btnExercisesMotor);
        _btnAAC = view.findViewById(R.id.btnAAC);
        _layoutProgress = view.findViewById(R.id.layoutProgress);
        _calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        _currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        setupCalendar();
        Log.d("TrainingDashboardFragment", "Initialized components");
    }

    @Override
    public void onProgressReset() {
        Log.d("TrainingDashboardFragment", "Progress was reset. Performing additional actions.");
    }

    private void setButtonListenerToOpenFragment(Button button, Fragment fragment) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TrainingDashboardFragment", "Button clicked to open fragment");
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
        Log.d("TrainingDashboardFragment", "Calendar setup complete");
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
        Log.d("TrainingDashboardFragment", "Generated calendar days: " + _calendarDays.size());
    }

    public void markDayAsCompleted(int day) {
        int offset = getFirstDayOffset();
        int index = day + offset - 1;

        if (day > 0 && index < _calendarDays.size()) {
            CalendarDay calendarDay = _calendarDays.get(index);
            if (!calendarDay.isCompleted()) {
                calendarDay.setCompleted(true);
                _calendarAdapter.notifyItemChanged(index);

                Log.d("TrainingDashboardFragment", "Marked day as completed: " + day);
                saveUserProgress();
            } else {
                Log.d("TrainingDashboardFragment", "Day " + day + " is already marked as completed.");
            }
        }
        Log.d("TrainingDashboardFragment", "Offset: " + offset + ", Index: " + index + ", _calendarDays.size(): " + _calendarDays.size());
    }

    private void loadChildProgress() {
        String userId = getUserId();
        if (userId == null) return;

        Log.d("TrainingDashboardFragment", "Loading user progress for user: " + userId);
        loadUserProgressFromRepository(userId);
        checkTodayTrainingCompletion();
    }

    private void checkTodayTrainingCompletion() {
        SharedPreferences progressPref = requireContext().getSharedPreferences("child_progress", Context.MODE_PRIVATE);
        boolean isCompletedTodayTraining = progressPref.getBoolean("isCompletedTodayTraining", false);

        if (isCompletedTodayTraining) {
            Log.d("TrainingDashboardFragment", "Today training is already completed");
            markDayAsCompleted(_currentDay);
        }
    }

    private void loadUserProgressFromRepository(String userId) {
        _childProgressRepository.loadUserProgress(userId,
                new OnSuccessListener<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Log.d("TrainingDashboardFragment", "Successfully loaded user progress: " + jsonObject.toString());
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

            String currentYear = getCurrentYear();
            String currentMonth = getCurrentMonth().toLowerCase(Locale.ENGLISH);

            int offset = getFirstDayOffset();
            Log.d("TrainingDashboardFragment", "First day offset: " + getFirstDayOffset());

            for (int i = 0; i < progressArray.length(); i++) {
                JSONObject monthProgress = progressArray.getJSONObject(i);
                String year = monthProgress.getString("year");
                String month = monthProgress.getString("month").toLowerCase(Locale.ENGLISH);

                if (year.equals(currentYear) && month.equals(currentMonth)) {
                    Log.d("TrainingDashboardFragment", "Found matching progress for year: " + year + " month: " + month);
                    loadCompletedDays(monthProgress, offset);
                }
            }
        } catch (Exception e) {
            Log.e("TrainingDashboardFragment", "Failed to parse progress: " + e.getMessage(), e);
        }
    }

    private void loadCompletedDays(JSONObject monthProgress, int offset) {
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
                Log.d("saveUserProgress", "No completed days to save.");
                return;
            }

            String year = getCurrentYear();
            String month = getCurrentMonth();

            Log.d("saveUserProgress", "Saving progress for user: " + userId + " Year: " + year + " Month: " + month + " Completed Days: " + completedDays);
            saveProgressToRepository(userId, year, month, completedDays);
        }

        private String getUserId() {
            SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            String userId = sharedPref.getString("userId", null);

            if (userId == null) {
                Log.d("saveUserProgress", "User ID is null, cannot save progress.");
            }
            return userId;
        }

        private List<Integer> getCompletedDays() {
            List<Integer> completedDays = new ArrayList<>();

            for (CalendarDay day : _calendarDays) {
                if (day.getDay() > 0 && day.isCompleted()) {
                    completedDays.add(day.getDay());
                }
            }
            Log.d("saveUserProgress", "Completed days: " + completedDays);
            return completedDays;
        }

        private String getCurrentYear() {
            Calendar currentCalendar = Calendar.getInstance();
            return String.valueOf(currentCalendar.get(Calendar.YEAR));
        }

        private String getCurrentMonth() {
            Calendar currentCalendar = Calendar.getInstance();
            return currentCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        }

        private void saveProgressToRepository(String userId, String year, String month, List<Integer> completedDays) {
            _childProgressRepository.saveUserProgress(userId, year, month, completedDays,
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("saveUserProgress", "Progress successfully saved.");
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


    public void loadLevel(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("child_data", getActivity().MODE_PRIVATE);
        int exp = sharedPreferences.getInt("exp", 0);
        int level = exp / 5000;
        int expForNextLevel = 5000;

        _tvLevelDashboard.setText("Уровень: " + level);
        _tvExpDashboard.setText(exp + " / " + (level + 1) * expForNextLevel + " опыта");
    }
}
