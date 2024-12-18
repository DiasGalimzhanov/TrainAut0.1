package com.example.trainaut01.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;

import java.util.List;

/**
 * Адаптер для отображения списка упражнений в RecyclerView.
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    /**
     * Список названий упражнений.
     */
    private final List<String> exerciseList;

    /**
     * Конструктор адаптера.
     *
     * @param exerciseList список названий упражнений.
     */
    public ExerciseAdapter(List<String> exerciseList) {
        this.exerciseList = exerciseList;
    }

    /**
     * Создает ViewHolder для элемента списка.
     *
     * @param parent   родительский ViewGroup.
     * @param viewType тип View.
     * @return объект {@link ExerciseViewHolder}.
     */
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    /**
     * Связывает данные с элементом списка.
     *
     * @param holder   объект {@link ExerciseViewHolder}.
     * @param position позиция элемента в списке.
     */
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        String exercise = exerciseList.get(position);
        holder.tvExerciseName.setText(exercise);
    }

    /**
     * Возвращает количество элементов в списке.
     *
     * @return размер списка упражнений.
     */
    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    /**
     * ViewHolder для отображения одного элемента упражнения.
     */
    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        /**
         * TextView для отображения названия упражнения.
         */
        TextView tvExerciseName;

        /**
         * Конструктор ViewHolder.
         *
         * @param itemView представление элемента списка.
         */
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
        }
    }
}
