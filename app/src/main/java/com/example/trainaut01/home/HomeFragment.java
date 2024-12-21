package com.example.trainaut01.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.trainaut01.BottomNavigationUpdater;
import com.example.trainaut01.R;
import com.example.trainaut01.adapter.ExerciseAdapter;
import com.example.trainaut01.adapter.NewsAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.FragmentHomeBinding;
import com.example.trainaut01.models.Avatar;
import com.example.trainaut01.models.Exercise;
import com.example.trainaut01.models.News;
import com.example.trainaut01.repository.AvatarRepository;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.repository.NewsRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Главная страница приложения. Отображает приветствие для пользователя, список новостей,
 * текущие упражнения на день, а также уровень и соответствующий ему аватар.
 */
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    private SharedPreferences sharedPref;
    private NewsAdapter adapterNews;
    private final List<String> exerciseList = new ArrayList<>();
    private static final String TAG = "HomeFragment";

    @Inject
    NewsRepository newsRepository;

    @Inject
    DayPlanRepository dayPlanRepository;

    @Inject
    AvatarRepository avatarRepository;

    /**
     * Создает новый экземпляр фрагмента с передачей идентификатора пользователя.
     * @param userId Идентификатор пользователя.
     * @return Новый экземпляр HomeFragment.
     */
    public static HomeFragment newInstance(String userId) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Создает и инициализирует пользовательский интерфейс с помощью ViewBinding.
     * @param inflater Объект для "надувания" макета фрагмента.
     * @param container Родительский контейнер для макета.
     * @param savedInstanceState Предыдущее состояние фрагмента (если есть).
     * @return Корневой View фрагмента.
     */
    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          @Nullable android.view.ViewGroup container,
                                          @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Вызывается, когда View фрагмента создана. Здесь настраиваются слушатели,
     * RecyclerView и загружаются данные.
     * @param view Корневой вид фрагмента.
     * @param savedInstanceState Предыдущее состояние фрагмента (если есть).
     */
    @Override
    public void onViewCreated(@NonNull android.view.View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        loadUserData();
        fetchNews();
        setupListeners();
        loadExercises();
    }

    /**
     * Вызывается, когда фрагмент становится видимым для пользователя.
     * Обновляет выбранный элемент нижней навигации.
     */
    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation();
    }

    /**
     * Вызывается при уничтожении View. Освобождает ViewBinding.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Инициализирует SharedPreferences и настройку RecyclerView.
     */
    private void init() {
        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        sharedPref = requireContext().getSharedPreferences("child_data", Context.MODE_PRIVATE);

        binding.recyclerViewNews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Загружает данные пользователя (имя) и аватар.
     */
    private void loadUserData() {
        String fullName = sharedPref.getString("fullName", "Гость");
        binding.tvHello.setText(String.format("Привет, %s", fullName));
        loadAvatar();
    }

    /**
     * Загружает аватар в соответствии с уровнем пользователя.
     */
    private void loadAvatar() {
        avatarRepository.getAvatarByLevel(requireContext(), new AvatarRepository.AvatarCallback() {
            @Override
            public void onSuccess(List<Avatar> avatars) {
                if (!avatars.isEmpty()) {
                    Avatar avatar = avatars.get(0);
                    Picasso.get().load(avatar.getUrlAvatar()).into(binding.imgAvatar);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Не удалось загрузить аватар", e);
            }
        });
    }

    /**
     * Загружает новости и отображает их в RecyclerView.
     */
    private void fetchNews() {
        newsRepository.fetchNews(new NewsRepository.NewsFetchCallback() {
            @Override
            public void onNewsFetched(List<News> newsList) {
                adapterNews = new NewsAdapter(newsList, newsItem -> {});
                binding.recyclerViewNews.setAdapter(adapterNews);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Ошибка загрузки новостей", e);
            }
        });
    }

    /**
     * Настраивает обработчик нажатия на кнопку "Подробнее о новостях".
     */
    private void setupListeners() {
        binding.tvMoreNews.setOnClickListener(v -> openNewsFragment());
    }

    /**
     * Открывает фрагмент со списком новостей.
     */
    private void openNewsFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new NewsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Загружает упражнения для пользователя на текущий день.
     */
    private void loadExercises() {

        String userId = getUserId();
        if (userId.isEmpty()) {
            Log.e(TAG, "Не удалось получить userId. Пользователь не авторизован.");
            Toast.makeText(getContext(), "Ошибка: пользователь не авторизован.", Toast.LENGTH_SHORT).show();
            return;
        }

        String dayOfWeek = getCurrentDayOfWeek();
        if (dayOfWeek.equals("Sunday") || dayOfWeek.equals("Saturday")) {
            updateExerciseList(new ArrayList<>());
        } else {
            dayPlanRepository.getDayPlanForUserAndDay(userId, dayOfWeek.toLowerCase(), dayPlan -> {
                if (dayPlan != null && dayPlan.getExercisesGrossMotor() != null && !dayPlan.getExercisesGrossMotor().isEmpty()) {
                    updateExerciseList(dayPlan.getExercisesGrossMotor());
                } else {
                    updateExerciseList(null);
                }
            }, error -> Toast.makeText(getActivity(), "Не удалось загрузить упражнения", Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Обновляет список упражнений в RecyclerView.
     * @param exercises Список упражнений или null/пустой для отсутствия упражнений.
     */
    private void updateExerciseList(List<Exercise> exercises) {

        exerciseList.clear();
        if (exercises == null || exercises.isEmpty()) {
            exerciseList.add("На сегодня занятий нету");
        } else {
            for (Exercise exercise : exercises) {
                exerciseList.add(exercise.getName());
            }
        }
        ExerciseAdapter exerciseAdapter = new ExerciseAdapter(exerciseList);
        binding.recyclerViewExercises.setAdapter(exerciseAdapter);
    }

    /**
     * Получает идентификатор текущего пользователя из FirebaseAuth.
     * @return Идентификатор пользователя или пустая строка, если пользователь не авторизован.
     */
    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";
    }

    /**
     * Получает текущий день недели.
     * @return Строка с названием дня недели (на английском).
     */
    private String getCurrentDayOfWeek() {
        return new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date());
    }

    /**
     * Обновляет выбранный элемент нижней навигации.
     */
    private void updateBottomNavigation() {
        ((BottomNavigationUpdater) requireActivity()).updateBottomNavigationSelection(this);
    }
}
