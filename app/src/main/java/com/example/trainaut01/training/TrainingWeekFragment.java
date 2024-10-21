package com.example.trainaut01.training;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.BottomNavigationUpdater;
import com.example.trainaut01.R;

import java.util.Calendar;

public class TrainingWeekFragment extends Fragment {

    // Переменные для каждого дня недели
    private LinearLayout _monday, _tuesday, _wednesday, _thursday, _friday, _saturday, _sunday;

    // Переменная текущего дня недели
    private int dayOfWeek;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Устанавливаем макет для этого фрагмента
        View view = inflater.inflate(R.layout.fragment_training_week, container, false);

        // Инициализируем элементы интерфейса и делаем все дни неактивными
        init(view);

        // Активируем текущий день недели
        activeDay();

        return view;
    }

    // Метод для инициализации LinearLayout для каждого дня недели
    private void init(View view){
        // Получаем текущий день недели
        dayOfWeek = currentDayOfWeek();

        // Инициализируем каждый LinearLayout, связанный с днями недели
        _monday = view.findViewById(R.id.mondayLayout);
        _tuesday = view.findViewById(R.id.tuesdayLayout);
        _wednesday = view.findViewById(R.id.wednesdayLayout);
        _thursday = view.findViewById(R.id.thursdayLayout);
        _friday = view.findViewById(R.id.fridayLayout);
        _saturday = view.findViewById(R.id.saturdayLayout);
        _sunday = view.findViewById(R.id.sundayLayout);

        // По умолчанию делаем все дни неактивными
//        inactiveDays();

        // Устанавливаем обработчики кликов для каждого дня недели
        setOnClickListeners();
    }

    // Метод для деактивации всех дней недели (чтобы пользователь не мог выбрать любой день)
    private void inactiveDays(){
        _monday.setEnabled(false);
        _tuesday.setEnabled(false);
        _wednesday.setEnabled(false);
        _thursday.setEnabled(false);
        _friday.setEnabled(false);
        _saturday.setEnabled(false);
        _sunday.setEnabled(false);
    }

    // Метод для активации только того дня недели, который соответствует текущему дню
    private void activeDay(){

        boolean enabled = true;
        int drawbleBackgroundResource = R.drawable.background_week_current_day;

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                setUpdates(_monday, enabled, drawbleBackgroundResource);
                break;
            case Calendar.TUESDAY:
                setUpdates(_tuesday, enabled, drawbleBackgroundResource);
                break;
            case Calendar.WEDNESDAY:
                setUpdates(_wednesday, enabled, drawbleBackgroundResource);
                break;
            case Calendar.THURSDAY:
                setUpdates(_thursday, enabled, drawbleBackgroundResource);
                break;
            case Calendar.FRIDAY:
                setUpdates(_friday, enabled, drawbleBackgroundResource);
                break;
            case Calendar.SATURDAY:
                setUpdates(_saturday, enabled, drawbleBackgroundResource);
                break;
            case Calendar.SUNDAY:
                setUpdates(_sunday, enabled, drawbleBackgroundResource);
                break;
        }
    }

    //Метод для установки изменений в LinearLayout
    private void setUpdates(LinearLayout layout, boolean enabled, int backgroundResource){
        layout.setEnabled(enabled);
        layout.setBackgroundResource(backgroundResource);
    }

    // Метод для получения текущего дня недели с помощью Calendar
    private int currentDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    // Метод для установки обработчиков кликов
    private void setOnClickListeners() {
        _monday.setOnClickListener(v -> openTrainingListFragment("Monday"));
        _tuesday.setOnClickListener(v -> openTrainingListFragment("Tuesday"));
        _wednesday.setOnClickListener(v -> openTrainingListFragment("Wednesday"));
        _thursday.setOnClickListener(v -> openTrainingListFragment("Thursday"));
        _friday.setOnClickListener(v -> openTrainingListFragment("Friday"));
        _saturday.setOnClickListener(v -> openTrainingListFragment("Saturday"));
        _sunday.setOnClickListener(v -> openTrainingListFragment("Sunday"));
    }

    // Метод для открытия фрагмента с деталями тренировки
    private void openTrainingListFragment(String day) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            TrainingListFragment listFragment = TrainingListFragment.newInstance(day, userId);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_enter, R.anim.fragment_exit)
                    .add(R.id.mainTraining, listFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(getContext(), "User ID не найден", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateBottomNavigation() {
        ((BottomNavigationUpdater) getActivity()).updateBottomNavigationSelection(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation();
    }

}
