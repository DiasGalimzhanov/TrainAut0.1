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

    // Переменные для LinearLayout, представляющие каждый день недели
    private LinearLayout _monday, _tuesday, _wednesday, _thursday, _friday, _saturday, _sunday;

    // Переменная для хранения текущего дня недели
    private int _dayOfWeek;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Устанавливаем макет для фрагмента
        View view = inflater.inflate(R.layout.fragment_training_week, container, false);

        // Инициализируем элементы интерфейса и активируем текущий день
        init(view);
        
        activeDay(); // Активация текущего дня недели

        return view;
    }

    // Метод для инициализации LinearLayout для каждого дня недели
    private void init(View view) {
        // Получаем текущий день недели
        _dayOfWeek = currentDayOfWeek();

        // Инициализация LinearLayout для каждого дня недели
        _monday = view.findViewById(R.id.mondayLayout);
        _tuesday = view.findViewById(R.id.tuesdayLayout);
        _wednesday = view.findViewById(R.id.wednesdayLayout);
        _thursday = view.findViewById(R.id.thursdayLayout);
        _friday = view.findViewById(R.id.fridayLayout);
        _saturday = view.findViewById(R.id.saturdayLayout);
        _sunday = view.findViewById(R.id.sundayLayout);

        // Устанавливаем обработчики кликов для каждого дня недели
        setOnClickListeners();
    }

    // Метод для активации только текущего дня недели
    private void activeDay() {
        boolean enabled = true; // Переменная, указывающая на возможность активации
        int drawableBackgroundResource = R.drawable.background_week_current_day; // Фоновый ресурс для активного дня

        // Активация текущего дня недели
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
        layout.setEnabled(enabled); // Установка активности
        layout.setBackgroundResource(backgroundResource); // Установка фона
    }

    // Метод для получения текущего дня недели с использованием Calendar
    private int currentDayOfWeek() {
        Calendar calendar = Calendar.getInstance(); // Получаем экземпляр календаря
        return calendar.get(Calendar.DAY_OF_WEEK); // Возвращаем текущий день недели
    }

    // Метод для установки обработчиков кликов на каждый день недели
    private void setOnClickListeners() {
        _monday.setOnClickListener(v -> openTrainingListFragment("Monday"));
        _tuesday.setOnClickListener(v -> openTrainingListFragment("Tuesday"));
        _wednesday.setOnClickListener(v -> openTrainingListFragment("Wednesday"));
        _thursday.setOnClickListener(v -> openTrainingListFragment("Thursday"));
        _friday.setOnClickListener(v -> openTrainingListFragment("Friday"));
        _saturday.setOnClickListener(v -> openTrainingListFragment("Saturday"));
        _sunday.setOnClickListener(v -> openTrainingListFragment("Sunday"));
    }

    // Метод для открытия фрагмента с деталями тренировки для выбранного дня
    private void openTrainingListFragment(String day) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null); // Получаем userId из SharedPreferences

        if (userId != null) {
            // Создаем новый экземпляр TrainingListFragment и передаем день и userId
            TrainingListFragment listFragment = TrainingListFragment.newInstance(day, userId);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_enter, R.anim.fragment_exit) // Установка анимации перехода
                    .add(R.id.mainTraining, listFragment) // Добавление нового фрагмента
                    .addToBackStack(null) // Добавление в стек возврата
                    .commit(); // Завершение транзакции
        } else {
            Toast.makeText(getContext(), "User ID не найден", Toast.LENGTH_SHORT).show(); // Сообщение об ошибке
        }
    }

    // Метод для обновления состояния нижней навигации
    public void updateBottomNavigation() {
        ((BottomNavigationUpdater) getActivity()).updateBottomNavigationSelection(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation(); // Обновление нижней навигации при возобновлении фрагмента
    }
}
