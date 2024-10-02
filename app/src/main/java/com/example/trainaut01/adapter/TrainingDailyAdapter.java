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
    private List<Exercise> exerciseList; // Список упражнений
    private OnExerciseClickListener listener; // Слушатель кликов

    public TrainingDailyAdapter(List<Exercise> exerciseList, OnExerciseClickListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
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

        // Обработка нажатия на элемент списка
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExerciseClick(exercise);
            }
        });

        holder.chbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            exercise.setCompleted(isChecked);
            // Здесь можно добавить логику для сохранения состояния в базе данных
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size(); // Возвращаем количество упражнений
    }

    public static class TrainingViewHolder extends RecyclerView.ViewHolder {
        CheckBox chbCompleted;
        TextView tvTitle;

        public TrainingViewHolder(@NonNull View itemView) {
            super(itemView);
            chbCompleted = itemView.findViewById(R.id.chbCompleted);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }

    public void updateExerciseList(List<Exercise> newExerciseList) {
        this.exerciseList = newExerciseList;
        notifyDataSetChanged(); // Обновляем RecyclerView
    }

    // Интерфейс для обработки кликов на упражнения
    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }
}
