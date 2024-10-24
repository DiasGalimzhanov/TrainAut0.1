package com.example.trainaut01.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.models.Exercise;

import java.util.List;

// Адаптер для отображения списка ежедневных тренировок
public class TrainingDailyAdapter extends RecyclerView.Adapter<TrainingDailyAdapter.TrainingViewHolder> {
    private List<Exercise> exerciseList; // Список упражнений
    private OnExerciseClickListener listener; // Слушатель кликов на упражнения
    private String day; // День, к которому относится список упражнений

    // Конструктор адаптера
    public TrainingDailyAdapter(List<Exercise> exerciseList, OnExerciseClickListener listener, String day) {
        this.exerciseList = exerciseList;
        this.listener = listener;
        this.day = day;
    }

    @NonNull
    @Override
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание нового представления из разметки item_training_daily
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training_daily, parent, false);
        return new TrainingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingViewHolder holder, int position) {
        // Получение упражнения по позиции
        Exercise exercise = exerciseList.get(position);
        holder.tvTitle.setText(exercise.getName()); // Устанавливаем название упражнения
        holder.chbCompleted.setChecked(exercise.isCompleted()); // Устанавливаем состояние чекбокса
        holder.chbCompleted.setEnabled(false); // Отключение возможности изменения чекбокса
        holder.tvRewardPoints.setText("+" + exercise.getRewardPoints()); // Отображение очков за выполнение

        // Проверка на наличие времени выполнения и установка значения
        if (exercise.getCompletedTime() > 0) {
            float timeInSeconds = exercise.getCompletedTime();
            int minutes = (int) (timeInSeconds / (1000 * 60)) % 60; // Вычисляем минуты
            int seconds = (int) (timeInSeconds / 1000) % 60; // Вычисляем секунды
            holder.tvTime.setText(String.format("%02d:%02d", minutes, seconds)); // Устанавливаем время в формате MM:SS
        }

        // Отключение возможности клика, если упражнение выполнено
        if (exercise.isCompleted()) {
            holder.itemView.setEnabled(false); // Отключение кликов на элемент
            holder.itemView.setAlpha(0.5f); // Устанавливка полупрозрачности для визуального обозначения недоступности
        } else {
            holder.itemView.setAlpha(1.0f); // Восстановление прозрачности
        }

        // Обработка нажатия на элемент списка
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExerciseClick(exercise, day); // Вызов метода слушателя
            }
        });

        // Обработка изменения состояния чекбокса
        holder.chbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            exercise.setCompleted(isChecked); // Устанавливаем состояние выполненного упражнения
            notifyItemChanged(position); // Уведомляем адаптер об изменении элемента
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size(); // Возвращаем количество элементов в списке
    }

    // Внутренний класс ViewHolder для хранения ссылок на представления
    public static class TrainingViewHolder extends RecyclerView.ViewHolder {
        CheckBox chbCompleted; // Чекбокс для отображения состояния выполнения
        TextView tvTitle, tvRewardPoints, tvTime; // Переменные для названия, очков и времени

        public TrainingViewHolder(@NonNull View itemView) {
            super(itemView);
            // Инициализация переменных
            chbCompleted = itemView.findViewById(R.id.chbCompleted);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRewardPoints = itemView.findViewById(R.id.tvRewardPoints);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

    // Метод для обновления списка упражнений
    public void updateExerciseList(List<Exercise> newExerciseList) {
        this.exerciseList = newExerciseList; // Обновляем список упражнений
        notifyDataSetChanged(); // Уведомляем адаптер об изменении данных
    }

    // Интерфейс для обработки кликов на упражнения
    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise, String day); // Метод, вызываемый при клике на упражнение
    }
}
