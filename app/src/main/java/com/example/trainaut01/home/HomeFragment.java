package com.example.trainaut01.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.BottomNavigationUpdater;
import com.example.trainaut01.R;
import com.example.trainaut01.adapter.ExerciseAdapter;
import com.example.trainaut01.adapter.NewsAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.Avatar;
import com.example.trainaut01.models.Exercise;
import com.example.trainaut01.models.News;
import com.example.trainaut01.repository.AvatarRepository;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.repository.NewsRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class HomeFragment extends Fragment {

    private static final String USER_ID = "userId";
    private static final String SHARED_PREF_NAME = "child_data";

    private List<String> exerciseList;
    private NewsAdapter adapterNews;
    private ExerciseAdapter exerciseAdapter;
    private SharedPreferences sharedPref;

    private TextView tvHello, tvMoreNews;
    private ImageView imgAvatar;
    private RecyclerView recyclerViewNews, recyclerViewExercises;

    @Inject
    NewsRepository newsRepository;

    @Inject
    DayPlanRepository dayPlanRepository;

    @Inject
    AvatarRepository avatarRepository;

    /**
     * Создает новый экземпляр фрагмента с передачей идентификатора пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Новый экземпляр HomeFragment.
     */
    public static HomeFragment newInstance(String userId) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);
        loadUserData();
        fetchNews();
        setupListeners();
        loadExercises();

        return view;
    }

    /**
     * Инициализация компонентов пользовательского интерфейса и зависимостей.
     *
     * @param view Основной вид фрагмента.
     */
    private void init(View view) {
        sharedPref = requireActivity().getSharedPreferences(SHARED_PREF_NAME, getActivity().MODE_PRIVATE);

        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        tvHello = view.findViewById(R.id.tvHello);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvMoreNews = view.findViewById(R.id.tvMoreNews);
        recyclerViewNews = view.findViewById(R.id.recyclerViewNews);
        recyclerViewExercises = view.findViewById(R.id.recyclerViewExercises);

        exerciseList = new ArrayList<>();
        setupRecyclerViews();
    }

    /**
     * Настраивает RecyclerView для отображения новостей и упражнений.
     */
    private void setupRecyclerViews() {
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Загружает данные пользователя (имя, уровень и аватар) из SharedPreferences и Firebase.
     */
    private void loadUserData() {
        String fullName = sharedPref.getString("fullName", "Гость");
        tvHello.setText(String.format("Привет, %s", fullName));

        int exp = sharedPref.getInt("exp", 0);
        int lvl = exp / 5000;

        loadAvatar(lvl);
    }

    /**
     * Загружает аватар пользователя в зависимости от его уровня.
     *
     * @param level Уровень пользователя.
     */
    private void loadAvatar(int level) {
        avatarRepository.getAvatarByLevel(level, new AvatarRepository.AvatarCallback() {
            @Override
            public void onSuccess(List<Avatar> avatars) {
                if (!avatars.isEmpty()) {
                    Avatar avatar = avatars.get(0);
                    Picasso.get().load(avatar.getUrlAvatar()).into(imgAvatar);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HOME", "Не удалось загрузить аватар", e);
            }
        });
    }

    /**
     * Загружает новости из репозитория и отображает их в RecyclerView.
     */
    private void fetchNews() {
        newsRepository.fetchNews(new NewsRepository.NewsFetchCallback() {
            @Override
            public void onNewsFetched(List<News> newsList) {
                adapterNews = new NewsAdapter(newsList, newsItem -> {
                    // Обработка клика по новости
                });
                recyclerViewNews.setAdapter(adapterNews);
            }

            @Override
            public void onError(Exception e) {
                Log.e("HOME", "Ошибка загрузки новостей", e);
            }
        });
    }

    /**
     * Устанавливает обработчики событий для элементов пользовательского интерфейса.
     */
    private void setupListeners() {
        tvMoreNews.setOnClickListener(view -> openNewsFragment());
    }

    /**
     * Открывает фрагмент новостей.
     */
    private void openNewsFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new NewsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Загружает упражнения для текущего пользователя и отображает их в RecyclerView.
     */
    private void loadExercises() {
        String userId = getUserId();
        String dayOfWeek = getCurrentDayOfWeek();

        dayPlanRepository.getDayPlanForUserAndDay(userId, dayOfWeek.toLowerCase(), dayPlan -> {
            if (dayPlan != null && dayPlan.getExercisesGrossMotor() != null) {
                updateExerciseList(dayPlan.getExercisesGrossMotor());
            } else {
                Toast.makeText(getActivity(), "План дня отсутствует или пуст", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Log.e("HOME", "Ошибка загрузки плана дня: ", error);
            Toast.makeText(getActivity(), "Не удалось загрузить упражнения", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Обновляет список упражнений и отображает его в RecyclerView.
     *
     * @param exercises Список упражнений из DayPlan.
     */
    private void updateExerciseList(List<Exercise> exercises) {
        exerciseList.clear();
        for (Exercise exercise : exercises) {
            exerciseList.add(exercise.getName());
        }

        exerciseAdapter = new ExerciseAdapter(exerciseList);
        recyclerViewExercises.setAdapter(exerciseAdapter);
    }

    /**
     * Получает идентификатор текущего пользователя из FirebaseAuth.
     *
     * @return Идентификатор пользователя.
     */
    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";
    }

    /**
     * Получает текущий день недели.
     *
     * @return День недели в формате строки.
     */
    private String getCurrentDayOfWeek() {
        return new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation();
    }

    /**
     * Обновляет выбранный элемент нижней навигации.
     */
    private void updateBottomNavigation() {
        ((BottomNavigationUpdater) getActivity()).updateBottomNavigationSelection(this);
    }

}
