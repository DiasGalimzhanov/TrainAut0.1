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
    private static final String ARG_DAY = "day";
    private static final String USER_ID = "userId";

    private List<Exercise> exercises;
    private NewsAdapter adapterNews;
    private TextView tvHello, tvMoreNews;
    private ImageView imgAvatar;
    private RecyclerView recyclerViewNews;
//    private AvatarRepository avatarRepository;
    private SharedPreferences sharedPref;
    private AppComponent appComponent;

    private RecyclerView recyclerViewExercises;
    private ExerciseAdapter adapter;
    private List<String> exerciseList;

    @Inject
    NewsRepository newsRepository;

    @Inject
    DayPlanRepository dayPlanRepository;

    @Inject
    AvatarRepository avatarRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);
        loadUserData();
        fetchNews();
        setupListeners();
        printExercise();

        return view;
    }

    private void init(View view) {
        sharedPref = requireActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);

        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        tvHello = view.findViewById(R.id.tvHello);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvMoreNews = view.findViewById(R.id.tvMoreNews);
        recyclerViewNews = view.findViewById(R.id.recyclerViewNews);

        avatarRepository = new AvatarRepository();
        recyclerViewExercises = view.findViewById(R.id.recyclerViewExercises);
        exerciseList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewNews.setLayoutManager(layoutManager);
        recyclerViewNews.setAdapter(adapterNews);
    }

    private void loadUserData() {
        String firstName = sharedPref.getString("firstName", "Гость");
        tvHello.setText("Привет, " + firstName);

        int exp = sharedPref.getInt("exp", 0);
        int lvl = exp / 5000;
        Log.d("HOME", "User experience: " + exp);

        avatarRepository.getAvatarByLevel(lvl, new AvatarRepository.AvatarCallback() {
            @Override
            public void onSuccess(List<Avatar> avatars) {
                if (!avatars.isEmpty()) {
                    Avatar avatar = avatars.get(0);
                    Picasso.get().load(avatar.getUrlAvatar()).into(imgAvatar);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HOME", "Failed to load avatar", e);
            }
        });
    }

    private void fetchNews() {
        newsRepository.fetchNews(new NewsRepository.NewsFetchCallback() {
            @Override
            public void onNewsFetched(List<News> newsList) {
                adapterNews = new NewsAdapter(newsList, new NewsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(News newsItem) {
                        // Handle news item click
                    }
                });
                recyclerViewNews.setAdapter(adapterNews);
            }

            @Override
            public void onError(Exception e) {
                Log.e("HOME", "Failed to fetch news", e);
            }
        });
    }

    private void setupListeners() {
        tvMoreNews.setOnClickListener(view -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new NewsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    public void updateBottomNavigation() {
        ((BottomNavigationUpdater) getActivity()).updateBottomNavigationSelection(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation();
    }

//    public void printExercise(){
//        exerciseList = new ArrayList<>();
//        exerciseList.add("Упражнение 1");
//        exerciseList.add("Упражнение 2");
//        exerciseList.add("Упражнение 3");
//        exerciseList.add("Упражнение 4");
//
//        adapter = new ExerciseAdapter(exerciseList);
//        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerViewExercises.setAdapter(adapter);
//
//
//    }

    public void printExercise() {
        // Инициализация списка упражнений
//        exerciseList = new ArrayList<>();
//        DayPlanRepository dayPlanRepository = new DayPlanRepository();

        // Получение текущего пользователя (замените на вашу реализацию)
//        String userId = getArguments().getString(USER_ID);
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userId = "vx6U63qDWCP4oKikCY6j9LFtXrz1";

        // Определяем текущий день недели
        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());

        // Запрос к Firebase
        dayPlanRepository.getExercisesForUserAndDay(
                userId,
                dayOfWeek,
                exercises -> {
                    // Успешное получение упражнений
                    Log.d("EXERCiSE", exercises.toString());
                    for (Exercise exercise : exercises) {
                        exerciseList.add(exercise.getName()); // Добавляем названия упражнений
                    }

                    // Настройка адаптера после загрузки данных
                    adapter = new ExerciseAdapter(exerciseList);
                    recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerViewExercises.setAdapter(adapter);
                },
                e -> {
                    // Ошибка загрузки
                    Log.e("printExercise", "Ошибка загрузки упражнений: ", e);
                    Toast.makeText(getActivity(), "Не удалось загрузить упражнения", Toast.LENGTH_SHORT).show();
                }
        );
    }

}
