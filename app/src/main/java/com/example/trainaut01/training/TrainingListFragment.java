    package com.example.trainaut01.training;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentResultListener;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.trainaut01.R;
    import com.example.trainaut01.adapter.TrainingDailyAdapter;
    import com.example.trainaut01.component.AppComponent;
    import com.example.trainaut01.component.DaggerAppComponent;
    import com.example.trainaut01.models.DayPlan;
    import com.example.trainaut01.models.Exercise;
    import com.example.trainaut01.repository.DayPlanRepository;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;

    import java.util.List;
    import java.util.stream.Collectors;

    import javax.inject.Inject;

    public class TrainingListFragment extends Fragment implements TrainingDailyAdapter.OnExerciseClickListener {

        private static final String ARG_DAY = "day";

        private RecyclerView recyclerView;
        private TrainingDailyAdapter adapter;

        private TextView _tvTitleTrainingList, _tvTitleDayOfWeek;

        private AppComponent appComponent;

        private String[] daysOfWeek = {
                "Понедельник",
                "Вторник",
                "Среда",
                "Четверг",
                "Пятница",
                "Суббота",
                "Воскресенье"
        };

        @Inject
        DayPlanRepository dayPlanRepository;

        public static TrainingListFragment newInstance(String day, String userId) {
            TrainingListFragment fragment = new TrainingListFragment();
            Bundle args = new Bundle();
            args.putString(ARG_DAY, day);
            args.putString("userId", userId);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_training_list, container, false);

            init(view);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Получаем день и userId из аргументов
            String day = getArguments() != null ? getArguments().getString(ARG_DAY).toUpperCase() : "";

            if (day.equals("SUNDAY")) {
                String text = "На сегодня упражнений нет\n\n" +
                        "Воскресенье — это время для отдыха и восстановления после активной недели. \n\n" +
                        "Для детей и их родителей это может быть день, посвящённый расслаблению, эмоциональной гармонии и приятным семейным занятиям.";
                _tvTitleTrainingList.setText(text);
            } else {
                // Передаем userId в метод для загрузки данных
                loadDayPlan(DayPlan.WeekDay.valueOf(day));
            }

            // Слушатель для обновления данных при возврате из фрагмента с упражнением
            getParentFragmentManager().setFragmentResultListener("exerciseResult", this, new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                    boolean exerciseCompleted = result.getBoolean("exerciseCompleted", false);
                    if (exerciseCompleted) {
                        // Обновляем данные, если упражнение завершено
                        loadDayPlan(DayPlan.WeekDay.valueOf(day));
                    }
                }
            });

            return view;
        }



        private void init(View view) {
            appComponent = DaggerAppComponent.create();
            appComponent.inject(this);

            recyclerView = view.findViewById(R.id.rvDailyList);
            _tvTitleTrainingList = view.findViewById(R.id.tvTitleTrainingList);
            _tvTitleDayOfWeek = view.findViewById(R.id.tvTitleDayOfWeek);
        }

        // Метод для загрузки данных по планам тренировок конкретного пользователя
        private void loadDayPlan(DayPlan.WeekDay weekDay) {
            String userId = getArguments().getString("userId");
            int dayIndex = weekDay.ordinal(); // Получаем индекс дня недели
            _tvTitleDayOfWeek.setText(daysOfWeek[dayIndex]); // Устанавливаем день недели на русском

            dayPlanRepository.getUserDayPlans(userId, weekDay, new OnSuccessListener<List<DayPlan>>() {
                @Override
                public void onSuccess(List<DayPlan> dayPlans) {
                    if (!dayPlans.isEmpty()) {
                        DayPlan dayPlan = dayPlans.get(0);
                        List<Exercise> exercises = dayPlan.getExercises();

                        adapter = new TrainingDailyAdapter(exercises, TrainingListFragment.this, weekDay.name());
                        recyclerView.setAdapter(adapter);
                    } else {
                        _tvTitleTrainingList.setText("На сегодня нет упражнений.");
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }


        @Override
        public void onExerciseClick(Exercise exercise, String day) {
            // Создаем новый фрагмент для деталей упражнения
            Fragment detailFragment = ExerciseDetailFragment.newInstance(exercise, day);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, detailFragment)
                    .addToBackStack(null) // Добавляем в стек возврата
                    .commit();
        }
    }
