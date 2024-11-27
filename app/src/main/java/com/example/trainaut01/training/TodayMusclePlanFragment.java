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
    import com.example.trainaut01.utils.ProgressResetListener;
    import com.example.trainaut01.utils.ProgressUtils;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Calendar;
    import java.util.Collections;
    import java.util.List;

    public class TodayMusclePlanFragment extends Fragment implements ProgressResetListener {

        private String userId;

        private TextView _tvTrainingSubTitle;

        private ImageButton _btnBiceps, _btnPectoralMuscles, _btnTriceps, _btnDeltoidMuscles;
        private ImageButton _btnPress, _btnUpperBackMuscles, _btnQuadriceps, _btnLowerBackMuscles;

        private Button _btnGrossMotor, _btnFineMotor;
        private boolean isGrossMotorSelected = true;

        private Button _btnGoToTraining;

        private ImageView _ivPerson;

        private int _dayOfWeek;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_today_muscle_plan, container, false);
            init(view);

            resetDailyProgressIfNeeded();
            toggleButtonState();

            updateGrossMotorTrainingDay();

            getParentFragmentManager().setFragmentResultListener("trainingResult", this, this::handleTrainingResult);

            return view;
        }

        private void handleTrainingResult(String requestKey, Bundle bundle) {
            boolean isCompletedTodayTraining = bundle.getBoolean("isCompletedTodayTraining");
            updateUIBasedOnCompletion(isCompletedTodayTraining);
        }

        private void updateUIBasedOnCompletion(boolean isCompletedTodayTraining) {
            if (isCompletedTodayTraining) {
                setButtonState(_btnGoToTraining, true);
                Toast.makeText(getContext(), "Тренировка на сегодня завершена", Toast.LENGTH_SHORT).show();
            } else {
                setButtonState(_btnGoToTraining, false);
            }
        }

        private void init(View view) {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
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

            _btnGoToTraining = view.findViewById(R.id.btnGoToTraining);

            _ivPerson = view.findViewById(R.id.ivPerson);

            _btnGrossMotor = view.findViewById(R.id.btn_gross_motor);
            _btnFineMotor = view.findViewById(R.id.btn_fine_motor);

            _btnGrossMotor.setOnClickListener(v -> switchToGrossMotor());
            _btnFineMotor.setOnClickListener(v -> switchToFineMotor());


        }

        private void switchToGrossMotor() {
            isGrossMotorSelected = true;
            updateButtonStyles();
            updateGrossMotorTrainingDay();
        }

        private void switchToFineMotor() {
            isGrossMotorSelected = false;
            updateButtonStyles();
            updateFineMotorTrainingDay();
        }

        private void updateButtonStyles() {
            if (isGrossMotorSelected) {
                _btnGrossMotor.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.btn_active_today_muscle_plan));
                _btnGrossMotor.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                _btnFineMotor.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_signup));
                _btnFineMotor.setTextColor(ContextCompat.getColor(requireContext(), R.color.indigo));
            } else {
                _btnFineMotor.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.btn_active_today_muscle_plan));
                _btnFineMotor.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                _btnGrossMotor.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_signup));
                _btnGrossMotor.setTextColor(ContextCompat.getColor(requireContext(), R.color.indigo));
            }
        }


        private void updateGrossMotorTrainingDay() {
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

            _btnGoToTraining.setOnClickListener(v -> {
                Log.d("TodayMusclePlanFragment", "Кнопка нажата, переход на ExerciseDetailFragment");
                openExerciseDetailFragment(getDayOfWeekString(_dayOfWeek), userId);
            });

        }

        private void updateFineMotorTrainingDay() {
            // Пример: данные для мелкой моторики (заполните своим списком групп или действиями)
            List<String> fineMotorTasks = Arrays.asList("Задание 1", "Задание 2", "Задание 3");

            StringBuilder title = new StringBuilder("Сегодня тренировка на мелкую моторику: ");
            for (int i = 0; i < fineMotorTasks.size(); i++) {
                title.append(fineMotorTasks.get(i));
                if (i < fineMotorTasks.size() - 1) title.append(", ");
            }
            _tvTrainingSubTitle.setText(title.toString());

            // Обновите UI или другие элементы для мелкой моторики
            // Например, скрытие кнопок для крупной моторики:
            _btnBiceps.setVisibility(View.GONE);
            _btnPectoralMuscles.setVisibility(View.GONE);

            // Добавьте видимость или обработку для мелкой моторики
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

        @Override
        public void onProgressReset() {
            toggleButtonState();
        }

        private void resetDailyProgressIfNeeded() {
            ProgressUtils.resetDailyProgress(requireContext(), this);
        }

        private void toggleButtonState() {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_progress", Context.MODE_PRIVATE);
            boolean isFinishDailyTraining = sharedPreferences.getBoolean("isCompletedTodayTraining", false);

            setButtonState(_btnGoToTraining, isFinishDailyTraining);

            _btnGoToTraining.setOnClickListener(v -> {
                if (!isFinishDailyTraining) {
                    sharedPreferences.edit().putBoolean("isCompletedTodayTraining", false).apply();
                    openExerciseDetailFragment(getDayOfWeekString(_dayOfWeek), userId);
                    setButtonState(_btnGoToTraining, false);
                } else {
                    Toast.makeText(getContext(), "Тренировка на сегодня уже завершена", Toast.LENGTH_SHORT).show();
                }
            });
        }


        private void setButtonState(Button button, boolean isLocked) {
            button.setEnabled(!isLocked);
            button.setBackgroundResource(isLocked ? R.drawable.btn2inactive_login_back : R.drawable.btn_active_today_muscle_plan );

        }

        private void openExerciseDetailFragment(String day, String userId) {
            ExerciseDetailFragment detailFragment = ExerciseDetailFragment.newInstance(day, userId);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_enter, R.anim.fragment_exit)
                    .add(R.id.mainTraining, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }

        private List<GrossMotorMuscleGroup> getMuscleGroupsForToday() {
            switch (_dayOfWeek) {
                case Calendar.MONDAY: return Arrays.asList(GrossMotorMuscleGroup.BICEPS, GrossMotorMuscleGroup.PECTORAL_MUSCLES);
                case Calendar.TUESDAY: return Arrays.asList(GrossMotorMuscleGroup.TRICEPS, GrossMotorMuscleGroup.DELTOID_MUSCLES);
                case Calendar.WEDNESDAY: return Arrays.asList(GrossMotorMuscleGroup.PRESS, GrossMotorMuscleGroup.UPPER_BACK_MUSCLES);
                case Calendar.THURSDAY: return Arrays.asList(GrossMotorMuscleGroup.QUADRICEPS, GrossMotorMuscleGroup.LOWER_BACK_MUSCLES);
                case Calendar.FRIDAY: return Collections.singletonList(GrossMotorMuscleGroup.FULL_BODY);
                default: return new ArrayList<>();
            }
        }

        private void updateTrainingSubTitle(List<GrossMotorMuscleGroup> muscleGroups) {
            StringBuilder title = new StringBuilder("Сегодня тренировка на ");
            for (int i = 0; i < muscleGroups.size(); i++) {
                title.append(muscleGroups.get(i).getDisplayName());
                if (i < muscleGroups.size() - 1) title.append(" и ");
            }
            _tvTrainingSubTitle.setText(title.toString());
        }

        private int currentDayOfWeek() {
            return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        }

        private String getDayOfWeekString(int dayOfWeek) {
            switch (dayOfWeek) {
                case Calendar.MONDAY: return "Monday";
                case Calendar.TUESDAY: return "Tuesday";
                case Calendar.WEDNESDAY: return "Wednesday";
                case Calendar.THURSDAY: return "Thursday";
                case Calendar.FRIDAY: return "Friday";
                case Calendar.SATURDAY: return "Saturday";
                case Calendar.SUNDAY: return "Sunday";
                default: return "Unknown Day";
            }
        }

        public void updateBottomNavigation() {
            ((BottomNavigationUpdater) requireActivity()).updateBottomNavigationSelection(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            updateBottomNavigation();
        }
    }
