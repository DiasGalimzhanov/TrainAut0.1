package com.example.trainaut01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.models.Exercise;

import java.util.List;

/**
 * Адаптер для отображения упражнений по мелкой моторике в списке RecyclerView.
 */
public class FineMotorAdapter extends RecyclerView.Adapter<FineMotorAdapter.FineMotorViewHolder> {

    private final Context _context;
    private final List<Exercise> _exerciseList;
    private final OnExerciseClickListener _listener;
    private final String _dayOfWeek;

    /**
     * Интерфейс слушателя клика по упражнению.
     */
    public interface OnExerciseClickListener {
        /**
         * Вызывается при клике на конкретное упражнение.
         *
         * @param exercise Объект упражнения, на которое кликнули.
         */
        void onExerciseClick(Exercise exercise);
    }

    /**
     * Конструктор адаптера.
     *
     * @param context       Контекст для доступа к ресурсам.
     * @param exerciseList  Список упражнений для отображения.
     * @param listener      Слушатель кликов по элементам списка.
     * @param dayOfWeek     День недели (строка), используемый для выбора иконки.
     */
    public FineMotorAdapter(Context context, List<Exercise> exerciseList, OnExerciseClickListener listener, String dayOfWeek) {
        this._context = context;
        this._exerciseList = exerciseList;
        this._listener = listener;
        this._dayOfWeek = dayOfWeek;
    }

    @NonNull
    @Override
    public FineMotorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(_context).inflate(R.layout.item_fine_motor_exercise, parent, false);
        return new FineMotorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FineMotorViewHolder holder, int position) {
        Exercise exercise = _exerciseList.get(position);
        holder.bind(exercise, _listener);
    }

    @Override
    public int getItemCount() {
        return _exerciseList.size();
    }

    /**
     * Внутренний класс ViewHolder для привязки данных к элементам макета.
     */
    public class FineMotorViewHolder extends RecyclerView.ViewHolder {
        private final TextView _nameTextView;
        private final TextView _pointsTextView;
        private final ImageView _ivExerciseIcon;

        /**
         * Конструктор ViewHolder.
         *
         * @param itemView Представление (View) для одного элемента списка.
         */
        public FineMotorViewHolder(@NonNull View itemView) {
            super(itemView);
            _nameTextView = itemView.findViewById(R.id.tvExerciseName);
            _pointsTextView = itemView.findViewById(R.id.tvExercisePoints);
            _ivExerciseIcon = itemView.findViewById(R.id.ivExerciseIcon);
        }

        /**
         * Привязка данных упражнения к элементам интерфейса.
         *
         * @param exercise Упражнение, данные которого нужно отобразить.
         * @param listener Слушатель для обработки кликов.
         */
        public void bind(Exercise exercise, OnExerciseClickListener listener) {
            _nameTextView.setText(exercise.getName());
            _pointsTextView.setText("Опыт: " + exercise.getRewardPoints());

            int dayImageRes = getDayOfWeekImage(_dayOfWeek);
            _ivExerciseIcon.setForeground(_context.getDrawable(dayImageRes));

            itemView.setOnClickListener(v -> listener.onExerciseClick(exercise));
        }

        /**
         * Возвращает ресурс иконки в зависимости от дня недели.
         *
         * @param dayOfWeek День недели в текстовом формате (например, "monday").
         * @return Идентификатор ресурса иконки.
         */
        private int getDayOfWeekImage(String dayOfWeek) {
            switch (dayOfWeek.toLowerCase()) {
                case "monday":
                    return R.drawable.ic_monday;
                case "tuesday":
                    return R.drawable.ic_tuesday;
                case "wednesday":
                    return R.drawable.ic_wednesday;
                case "thursday":
                    return R.drawable.ic_thursday;
                case "friday":
                    return R.drawable.ic_friday;
                default:
                    return R.drawable.default_image;
            }
        }
    }
}
