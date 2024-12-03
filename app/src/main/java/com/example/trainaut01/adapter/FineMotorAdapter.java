package com.example.trainaut01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trainaut01.R;
import com.example.trainaut01.models.Exercise;

import java.util.List;

public class FineMotorAdapter extends RecyclerView.Adapter<FineMotorAdapter.FineMotorViewHolder> {
    private final Context context;
    private final List<Exercise> exerciseList;
    private final OnExerciseClickListener listener;
    private final String dayOfWeek;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    public FineMotorAdapter(Context context, List<Exercise> exerciseList, OnExerciseClickListener listener, String dayOfWeek) {
        this.context = context;
        this.exerciseList = exerciseList;
        this.listener = listener;
        this.dayOfWeek = dayOfWeek;
    }

    @NonNull
    @Override
    public FineMotorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fine_motor_exercise, parent, false);
        return new FineMotorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FineMotorViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.bind(exercise, listener);
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public class FineMotorViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView pointsTextView;
        private final ImageView iconImageView;

        public FineMotorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvExerciseName);
            pointsTextView = itemView.findViewById(R.id.tvExercisePoints);
            iconImageView = itemView.findViewById(R.id.ivExerciseIcon);
        }

        public void bind(Exercise exercise, OnExerciseClickListener listener) {
            nameTextView.setText(exercise.getName());
            pointsTextView.setText("Опыт: " + exercise.getRewardPoints());

            int dayImageRes = getDayOfWeekImage(dayOfWeek);

            iconImageView.setForeground(context.getDrawable(dayImageRes));

            itemView.setOnClickListener(v -> listener.onExerciseClick(exercise));
        }


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
                    return R.drawable.ic_weekend;
            }
        }
    }
}
