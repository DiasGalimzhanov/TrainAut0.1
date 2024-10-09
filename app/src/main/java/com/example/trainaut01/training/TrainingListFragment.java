    package com.example.trainaut01.training;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
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
        private static final String ARG_USER_ID = "userId";

        private RecyclerView recyclerView;
        private TrainingDailyAdapter adapter;

        private TextView _tvTitleTrainingList;

        private AppComponent appComponent;

        @Inject
        DayPlanRepository dayPlanRepository;

        public static TrainingListFragment newInstance(String day, String userId) {
            TrainingListFragment fragment = new TrainingListFragment();
            Bundle args = new Bundle();
            args.putString(ARG_DAY, day);
            args.putString("userId", userId); // Передаем userId в аргументы фрагмента
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
            String userId = getArguments() != null ? getArguments().getString(ARG_USER_ID) : "";

            if (day.equals("SUNDAY")) {
                String text = "На сегодня упражнений нет\n\n" +
                        "Воскресенье — это время для отдыха и восстановления после активной недели. \n\n" +
                        "Для детей и их родителей это может быть день, посвящённый расслаблению, эмоциональной гармонии и приятным семейным занятиям.";
                _tvTitleTrainingList.setText(text);
            } else {
                // Передаем userId в метод для загрузки данных
                loadDayPlan(DayPlan.WeekDay.valueOf(day));
            }
            return view;
        }


        private void init(View view) {
            appComponent = DaggerAppComponent.create();
            appComponent.inject(this);

            recyclerView = view.findViewById(R.id.rvDailyList);
            _tvTitleTrainingList = view.findViewById(R.id.tvTitleTrainingList);
        }

        // Метод для загрузки данных по планам тренировок конкретного пользователя
        private void loadDayPlan(DayPlan.WeekDay weekDay) {
            String userId = getArguments().getString("userId"); // Получаем userId из аргументов

            dayPlanRepository.getUserDayPlans(userId, weekDay, new OnSuccessListener<List<DayPlan>>() {
                @Override
                public void onSuccess(List<DayPlan> dayPlans) {
                    if (!dayPlans.isEmpty()) {
                        DayPlan dayPlan = dayPlans.get(0);
                        List<Exercise> exercises = dayPlan.getExercises();

                        // Передача слушателя в адаптер
                        adapter = new TrainingDailyAdapter(exercises, TrainingListFragment.this);
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
        public void onExerciseClick(Exercise exercise) {
            // Создаем новый фрагмент для деталей упражнения
            Fragment detailFragment = ExerciseDetailFragment.newInstance(exercise);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, detailFragment)
                    .addToBackStack(null) // Добавляем в стек возврата
                    .commit();
        }
    }
