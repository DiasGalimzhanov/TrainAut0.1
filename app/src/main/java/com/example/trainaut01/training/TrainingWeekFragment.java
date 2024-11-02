package com.example.trainaut01.training;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.BottomNavigationUpdater;
import com.example.trainaut01.R;

import java.util.Calendar;

public class TrainingWeekFragment extends Fragment {

    private LinearLayout _monday, _tuesday, _wednesday, _thursday, _friday, _saturday, _sunday;

    private int _dayOfWeek;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_week, container, false);

        init(view);
        
        activeDay();

        return view;
    }

    // Метод для инициализации LinearLayout для каждого дня недели
    private void init(View view) {
        _dayOfWeek = currentDayOfWeek();

        _monday = view.findViewById(R.id.mondayLayout);
        _tuesday = view.findViewById(R.id.tuesdayLayout);
        _wednesday = view.findViewById(R.id.wednesdayLayout);
        _thursday = view.findViewById(R.id.thursdayLayout);
        _friday = view.findViewById(R.id.fridayLayout);
        _saturday = view.findViewById(R.id.saturdayLayout);

        setOnClickListeners();
    }

    // Метод для активации только текущего дня недели
    private void activeDay() {
        boolean enabled = true;
        int drawableBackgroundResource = R.drawable.background_week_current_day;

        switch (_dayOfWeek) {
            case Calendar.MONDAY:
                setUpdates(_monday, enabled, drawableBackgroundResource);
                break;
            case Calendar.TUESDAY:
                setUpdates(_tuesday, enabled, drawableBackgroundResource);
                break;
            case Calendar.WEDNESDAY:
                setUpdates(_wednesday, enabled, drawableBackgroundResource);
                break;
            case Calendar.THURSDAY:
                setUpdates(_thursday, enabled, drawableBackgroundResource);
                break;
            case Calendar.FRIDAY:
                setUpdates(_friday, enabled, drawableBackgroundResource);
                break;
            case Calendar.SATURDAY:
                setUpdates(_saturday, enabled, drawableBackgroundResource);
                break;
            case Calendar.SUNDAY:
                setUpdates(_sunday, enabled, drawableBackgroundResource);
                break;
        }
    }

    // Метод для установки активного состояния и фона для LinearLayout
    private void setUpdates(LinearLayout layout, boolean enabled, int backgroundResource) {
        layout.setEnabled(enabled);
        layout.setBackgroundResource(backgroundResource);
    }

    // Метод для получения текущего дня недели с использованием Calendar
    private int currentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    // Метод для установки обработчиков кликов на каждый день недели
    private void setOnClickListeners() {
        _monday.setOnClickListener(v -> openTrainingListFragment("Monday"));
        _tuesday.setOnClickListener(v -> openTrainingListFragment("Tuesday"));
        _wednesday.setOnClickListener(v -> openTrainingListFragment("Wednesday"));
        _thursday.setOnClickListener(v -> openTrainingListFragment("Thursday"));
        _friday.setOnClickListener(v -> openTrainingListFragment("Friday"));
        _saturday.setOnClickListener(v -> openTrainingListFragment("Saturday"));
    }

    // Метод для открытия фрагмента с деталями тренировки для выбранного дня
    private void openTrainingListFragment(String day) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            // Создаем новый экземпляр TrainingListFragment и передаем день и userId
            TrainingListFragment listFragment = TrainingListFragment.newInstance(day, userId);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_enter, R.anim.fragment_exit) // Установка анимации перехода
                    .add(R.id.mainTraining, listFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(getContext(), "User ID не найден", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для обновления состояния нижней навигации
    public void updateBottomNavigation() {
        ((BottomNavigationUpdater) getActivity()).updateBottomNavigationSelection(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation();
    }
}
