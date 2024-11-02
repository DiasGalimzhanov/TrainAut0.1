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

public class TrainingDailyAdapter extends RecyclerView.Adapter<TrainingDailyAdapter.TrainingViewHolder> {
    private List<Exercise> exerciseList;
    private OnExerciseClickListener listener;
    private String day;

    // Конструктор адаптера
    public TrainingDailyAdapter(List<Exercise> exerciseList, OnExerciseClickListener listener, String day) {
        this.exerciseList = exerciseList;
        this.listener = listener;
        this.day = day;
    }

    @NonNull
    @Override
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training_daily, parent, false);
        return new TrainingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.tvTitle.setText(exercise.getName());
        holder.chbCompleted.setChecked(exercise.isCompleted());
        holder.chbCompleted.setEnabled(false);
        holder.tvRewardPoints.setText("+" + exercise.getRewardPoints());

        // Проверка на наличие времени выполнения и установка значения
        if (exercise.getCompletedTime() > 0) {
            float timeInSeconds = exercise.getCompletedTime();
            int minutes = (int) (timeInSeconds / (1000 * 60)) % 60;
            int seconds = (int) (timeInSeconds / 1000) % 60;
            holder.tvTime.setText(String.format("%02d:%02d", minutes, seconds));
        }

        // Отключение возможности клика, если упражнение выполнено
        if (exercise.isCompleted()) {
            holder.itemView.setEnabled(false);
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.itemView.setAlpha(1.0f);
        }

        // Обработка нажатия на элемент списка
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExerciseClick(exercise, day);
            }
        });

        // Обработка изменения состояния чекбокса
        holder.chbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            exercise.setCompleted(isChecked);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    // Внутренний класс ViewHolder для хранения ссылок на представления
    public static class TrainingViewHolder extends RecyclerView.ViewHolder {
        CheckBox chbCompleted;
        TextView tvTitle, tvRewardPoints, tvTime;

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
        this.exerciseList = newExerciseList;
        notifyDataSetChanged();
    }


    // Интерфейс для обработки кликов на упражнения
    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise, String day);
    }
}
