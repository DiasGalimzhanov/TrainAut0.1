package com.example.trainaut01.training;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.R;

import java.util.Calendar;

public class TrainingWeekFragment extends Fragment {

    // Объявляем переменные для каждого дня недели
    private LinearLayout _monday, _tuesday, _wednesday, _thursday, _friday, _saturday, _sunday;
    // Переменная для хранения текущего дня недели
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
        inactiveDays();

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
        // Используем переменную dayOfWeek для определения текущего дня недели и активации соответствующего LinearLayout
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                _monday.setEnabled(true);// Активируем понедельник
                _monday.setBackgroundResource(R.drawable.background_week_current_day);
                break;
            case Calendar.TUESDAY:
                _tuesday.setEnabled(true); // Активируем вторник
                _tuesday.setBackgroundResource(R.drawable.background_week_current_day);
                break;
            case Calendar.WEDNESDAY:
                _wednesday.setEnabled(true); // Активируем среду
                _wednesday.setBackgroundResource(R.drawable.background_week_current_day);
                break;
            case Calendar.THURSDAY:
                _thursday.setEnabled(true); // Активируем четверг
                _thursday.setBackgroundResource(R.drawable.background_week_current_day);
                break;
            case Calendar.FRIDAY:
                _friday.setEnabled(true); // Активируем пятницу
                _friday.setBackgroundResource(R.drawable.background_week_current_day);
                break;
            case Calendar.SATURDAY:
                _saturday.setEnabled(true); // Активируем субботу
                _saturday.setBackgroundResource(R.drawable.background_week_current_day);
                break;
            case Calendar.SUNDAY:
                _sunday.setEnabled(true); // Активируем воскресенье
                _sunday.setBackgroundResource(R.drawable.background_week_current_day);
                break;
        }
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
        TrainingListFragment listFragment = TrainingListFragment.newInstance(day);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.main, listFragment)
                .addToBackStack(null)
                .commit();
    }
}
