package com.example.trainaut01;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.trainaut01.home.HomeFragment;
import com.example.trainaut01.profile.UserProfileFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.repository.AppInitializer;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.repository.ExerciseRepository;
import com.example.trainaut01.training.TrainingWeekFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

public class BaseActivity extends AppCompatActivity implements BottomNavigationUpdater {
    private BottomNavigationView bottomNavigationView;
    private AppComponent appComponent;
    private int previousPosition = 0;

    @Inject
    ExerciseRepository exerciseRepository;

    @Inject
    DayPlanRepository dayPlanRepository;

    @Inject
    AppInitializer appInitializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        setContentView(R.layout.activity_base);

        // Фрагмент по умолчанию
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        // Настройка BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int newPosition = 0;

                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                    newPosition = 0;
                } else if (item.getItemId() == R.id.nav_training) {
                    selectedFragment = new TrainingWeekFragment();
                    newPosition = 1;
                } else if (item.getItemId() == R.id.nav_profile) {
                    selectedFragment = new UserProfileFragment();
                    newPosition = 2;
                }

                // Проверяем, не является ли выбранный фрагмент текущим
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (selectedFragment != null && (currentFragment == null || !selectedFragment.getClass().equals(currentFragment.getClass()))) {
                    animateFragmentTransition(selectedFragment, newPosition);
                }
                return true;
            }
        });

        // Инициализация упражнений и планов на день
        initializeData();
    }

    private void animateFragmentTransition(Fragment fragment, int newPosition) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Определяем направление анимации
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            if (newPosition > previousPosition) {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            } else if (newPosition < previousPosition) {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }

        // Обновляем предыдущую позицию
        previousPosition = newPosition;

        // Заменяем фрагмент и добавляем его в стек
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null); // Добавляем в стек при каждом переходе
        transaction.commit(); // Выполняем транзакцию

        // Используем post для обновления навигации
        bottomNavigationView.post(() -> updateBottomNavigationSelection(fragment));
    }



    @Override
    public void updateBottomNavigationSelection(Fragment fragment) {
        int itemId;
        switch (fragment.getClass().getSimpleName()) {
            case "HomeFragment":
                itemId = R.id.nav_home;
                break;
            case "TrainingWeekFragment":
                itemId = R.id.nav_training;
                break;
            case "UserProfileFragment":
                itemId = R.id.nav_profile;
                break;
            default:
                return; // Неизвестный фрагмент
        }
        bottomNavigationView.setSelectedItemId(itemId);
    }



    private void initializeData() {
        appInitializer.initializeExercises(
                unused -> Log.d("BaseActivity", "Упражнения инициализированы успешно"),
                e -> Log.e("BaseActivity", "Не удалось инициализировать упражнения: " + e.getMessage())
        );

        appInitializer.initializeDayPlans(
                unused -> Log.d("BaseActivity", "Планы на день инициализированы успешно"),
                e -> Log.e("BaseActivity", "Не удалось инициализировать планы на день: " + e.getMessage())
        );
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            // Обновляем навигацию
            if (currentFragment != null) {
                updateBottomNavigationSelection(currentFragment);
                // Обновляем предыдущую позицию на основе текущего фрагмента
                if (currentFragment instanceof HomeFragment) {
                    previousPosition = 0;
                } else if (currentFragment instanceof TrainingWeekFragment) {
                    previousPosition = 1;
                } else if (currentFragment instanceof UserProfileFragment) {
                    previousPosition = 2;
                }
            }
        } else {
            // Проверка, является ли текущий фрагмент HomeFragment
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof HomeFragment) {
                super.onBackPressed(); // Завершение активности
            } else {
                // Возврат к HomeFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                previousPosition = 0; // Обновляем предыдущую позицию
            }
        }
    }


}
