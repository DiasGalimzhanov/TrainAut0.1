package com.example.trainaut01.training;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.BottomNavigationUpdater;
import com.example.trainaut01.R;
import com.example.trainaut01.enums.GrossMotorMuscleGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TodayMusclePlanFragment extends Fragment {

    private String userId;

    private TextView _tvTrainingSubTitle;

    private ImageButton _btnBiceps, _btnPectoralMuscles, _btnTriceps, _btnDeltoidMuscles;
    private ImageButton _btnPress, _btnUpperBackMuscles, _btnQuadriceps, _btnLowerBackMuscles;
    private Button _btnGoToTraining, _btnGrossMotor, _btnFineMotor;

    private ImageView _ivPerson;

    private int _dayOfWeek;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_week, container, false);

        init(view);

        onGrossMotorSelected();

        return view;
    }

    private void init(View view) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        _dayOfWeek = currentDayOfWeek();

        _tvTrainingSubTitle = view.findViewById(R.id.tvTrainingSubTitle);

        _btnBiceps = view.findViewById(R.id.btnBiceps);
        _btnPectoralMuscles = view.findViewById(R.id.btnPectoralMuscles);
        _btnTriceps = view.findViewById(R.id.btnTriceps);
        _btnDeltoidMuscles = view.findViewById(R.id.btnDeltoidMuscles);
        _btnPress = view.findViewById(R.id.btnPress);
        _btnUpperBackMuscles = view.findViewById(R.id.btnUpperBackMuscles);
        _btnQuadriceps = view.findViewById(R.id.btnQuadriceps);
        _btnLowerBackMuscles = view.findViewById(R.id.btnLowerBackMuscles);

        _btnGrossMotor = view.findViewById(R.id.btnGrossMotor);
        _btnFineMotor = view.findViewById(R.id.btnFineMotor);
        _btnGoToTraining = view.findViewById(R.id.btnGoToTraining);

        _ivPerson = view.findViewById(R.id.ivPerson);

        _btnGrossMotor.setOnClickListener(v -> onGrossMotorSelected());
        _btnFineMotor.setOnClickListener(v -> onFineMotorSelected());
    }

    private void onGrossMotorSelected() {
        updateTrainingDay();
        updateButtonState(_btnGrossMotor, _btnFineMotor);
        Toast.makeText(getContext(), "Выбрана крупная моторика", Toast.LENGTH_SHORT).show();
    }

    private void onFineMotorSelected() {
        updateButtonState(_btnFineMotor, _btnGrossMotor);
        Toast.makeText(getContext(), "Выбрана мелкая моторика", Toast.LENGTH_SHORT).show();
    }

    private void updateButtonState(Button activeButton, Button inactiveButton) {
        activeButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_variant));
        inactiveButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.B5B8D7Light));
    }

    private void updateTrainingDay() {
        List<GrossMotorMuscleGroup> todayGrossMotorMuscleGroups = getMuscleGroupsForToday();

        updateTrainingSubTitle(todayGrossMotorMuscleGroups);

        setMuscleGroupButtonAndImage(todayGrossMotorMuscleGroups, _btnBiceps, GrossMotorMuscleGroup.BICEPS);
        setMuscleGroupButtonAndImage(todayGrossMotorMuscleGroups, _btnPectoralMuscles, GrossMotorMuscleGroup.PECTORAL_MUSCLES);
        setMuscleGroupButtonAndImage(todayGrossMotorMuscleGroups, _btnTriceps, GrossMotorMuscleGroup.TRICEPS);
        setMuscleGroupButtonAndImage(todayGrossMotorMuscleGroups, _btnDeltoidMuscles, GrossMotorMuscleGroup.DELTOID_MUSCLES);
        setMuscleGroupButtonAndImage(todayGrossMotorMuscleGroups, _btnPress, GrossMotorMuscleGroup.PRESS);
        setMuscleGroupButtonAndImage(todayGrossMotorMuscleGroups, _btnUpperBackMuscles, GrossMotorMuscleGroup.UPPER_BACK_MUSCLES);
        setMuscleGroupButtonAndImage(todayGrossMotorMuscleGroups, _btnQuadriceps, GrossMotorMuscleGroup.QUADRICEPS);
        setMuscleGroupButtonAndImage(todayGrossMotorMuscleGroups, _btnLowerBackMuscles, GrossMotorMuscleGroup.LOWER_BACK_MUSCLES);
        setMuscleGroupButtonAndImage(todayGrossMotorMuscleGroups, null, GrossMotorMuscleGroup.FULL_BODY);

        _btnGoToTraining.setOnClickListener(v -> openExerciseDetailFragment(getDayOfWeekString(_dayOfWeek), userId));
    }

    private void setMuscleGroupButtonAndImage(List<GrossMotorMuscleGroup> todayGrossMotorMuscleGroups, ImageButton btnMuscle, GrossMotorMuscleGroup grossMotorMuscleGroup) {
        int boy_front = R.drawable.boy_front;
        int boy_back = R.drawable.boy_back;

        if (todayGrossMotorMuscleGroups.contains(grossMotorMuscleGroup)) {
            if (grossMotorMuscleGroup == GrossMotorMuscleGroup.BICEPS
                    || grossMotorMuscleGroup == GrossMotorMuscleGroup.PECTORAL_MUSCLES
                    || grossMotorMuscleGroup == GrossMotorMuscleGroup.FULL_BODY) {
                _ivPerson.setImageResource(boy_front);
            } else {
                _ivPerson.setImageResource(boy_back);
            }

            if (btnMuscle != null) {
                btnMuscle.setVisibility(View.VISIBLE);
            }
        } else {
            if (btnMuscle != null) {
                btnMuscle.setVisibility(View.GONE);
            }
        }
    }


    private List<GrossMotorMuscleGroup> getMuscleGroupsForToday() {
        switch (_dayOfWeek) {
            case Calendar.MONDAY:
                return Arrays.asList(GrossMotorMuscleGroup.BICEPS, GrossMotorMuscleGroup.PECTORAL_MUSCLES);
            case Calendar.TUESDAY:
                return Arrays.asList(GrossMotorMuscleGroup.TRICEPS, GrossMotorMuscleGroup.DELTOID_MUSCLES);
            case Calendar.WEDNESDAY:
                return Arrays.asList(GrossMotorMuscleGroup.PRESS, GrossMotorMuscleGroup.UPPER_BACK_MUSCLES);
            case Calendar.THURSDAY:
                return Arrays.asList(GrossMotorMuscleGroup.QUADRICEPS, GrossMotorMuscleGroup.LOWER_BACK_MUSCLES);
            case Calendar.FRIDAY:
                return Arrays.asList(GrossMotorMuscleGroup.FULL_BODY);
            default:
                return new ArrayList<>();
        }
    }

    private int currentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private void updateTrainingSubTitle(List<GrossMotorMuscleGroup> grossMotorMuscleGroups) {
        StringBuilder title = new StringBuilder("Сегодня тренировка на ");
        for (int i = 0; i < grossMotorMuscleGroups.size(); i++) {
            title.append(grossMotorMuscleGroups.get(i).getDisplayName());
            if (i < grossMotorMuscleGroups.size() - 1) {
                title.append(" и ");
            }
        }
        _tvTrainingSubTitle.setText(title.toString());
    }

    private String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
            default:
                return "Unknown Day";
        }
    }

//    // Метод для открытия фрагмента с деталями тренировки для выбранного дня
//    private void openTrainingListFragment(String day) {
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
//        String userId = sharedPreferences.getString("userId", null);
//
//        if (userId != null) {
//            TrainingListFragment listFragment = TrainingListFragment.newInstance(day, userId);
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_enter, R.anim.fragment_exit)
//                    .add(R.id.mainTraining, listFragment)
//                    .addToBackStack(null)
//                    .commit();
//        } else {
//            Toast.makeText(getContext(), "User ID не найден", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void openExerciseDetailFragment(String day, String userId) {
        ExerciseDetailFragment detailFragment = ExerciseDetailFragment.newInstance(day, userId);

        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_enter, R.anim.fragment_exit)
                .add(R.id.mainTraining, detailFragment)
                .addToBackStack(null)
                .commit();
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
