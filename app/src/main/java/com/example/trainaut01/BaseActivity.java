package com.example.trainaut01;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.trainaut01.helper.LocaleHelper;
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
import com.example.trainaut01.training.TodayMusclePlanFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

/**
 * Базовая активность приложения, которая управляет фрагментами и нижней навигацией.
 * Реализует интерфейс {@link BottomNavigationUpdater} для синхронизации состояния
 * нижней панели навигации с текущим активным фрагментом.
 */
public class BaseActivity extends AppCompatActivity implements BottomNavigationUpdater {

    private BottomNavigationView _bottomNavigationView;
    private int _previousPosition = 0;

    @Inject
    ExerciseRepository exerciseRepository;

    @Inject
    DayPlanRepository dayPlanRepository;

    @Inject
    AppInitializer appInitializer;

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = LocaleHelper.getLanguage(newBase);
        Context context = LocaleHelper.setLocale(newBase, language);
        super.attachBaseContext(context);
    }

    /**
     * Вызывается при создании активности.
     * Инициализирует Dagger-компоненты, загружает стартовый фрагмент и настраивает нижнюю навигацию.
     *
     * @param savedInstanceState Сохраненное состояние активности, если оно существует.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppComponent _appComponent = DaggerAppComponent.create();
        _appComponent.inject(this);

        setContentView(R.layout.activity_base);

        loadFragment(new HomeFragment());

        setupBottomNavigationView();

        initializeData();
    }

    /**
     * Загружает фрагмент в контейнер активности.
     *
     * @param fragment Фрагмент для отображения.
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    /**
     * Настраивает нижнюю панель навигации и слушатель выбора элемента.
     */
    private void setupBottomNavigationView() {
        _bottomNavigationView = findViewById(R.id.bottom_navigation);
        _bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int newPosition = 0;

                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                    newPosition = 0;
                } else if (item.getItemId() == R.id.nav_training) {
                    selectedFragment = new TrainingDashboardFragment();
                    newPosition = 1;
                } else if (item.getItemId() == R.id.nav_profile) {
                    selectedFragment = new UserProfileFragment();
                    newPosition = 2;
                }

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (selectedFragment != null && (currentFragment == null || !selectedFragment.getClass().equals(currentFragment.getClass()))) {
                    animateFragmentTransition(selectedFragment, newPosition);
                }
                return true;
            }
        });

    }

    /**
     * Добавляет анимацию перехода между фрагментами.
     *
     * @param fragment    Новый фрагмент для отображения.
     * @param newPosition Позиция нового элемента в навигации.
     */
    private void animateFragmentTransition(Fragment fragment, int newPosition) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            if (newPosition > _previousPosition) {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            } else if (newPosition < _previousPosition) {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }

        _previousPosition = newPosition;

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        _bottomNavigationView.post(() -> updateBottomNavigationSelection(fragment));
    }

    /**
     * Обновляет выбранный элемент нижней навигации в зависимости от текущего фрагмента.
     *
     * @param fragment Текущий активный фрагмент.
     */
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
                return;
        }
        _bottomNavigationView.setSelectedItemId(itemId);
    }

    /**
     * Инициализирует данные приложения, включая упражнения и планы на день.
     */
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

    /**
     * Обрабатывает нажатие кнопки "Назад".
     * Если стек возврата пуст, возвращает пользователя на главный экран.
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (currentFragment != null) {
                updateBottomNavigationSelection(currentFragment);
                if (currentFragment instanceof HomeFragment) {
                    _previousPosition = 0;
                } else if (currentFragment instanceof TodayMusclePlanFragment) {
                    _previousPosition = 1;
                } else if (currentFragment instanceof UserProfileFragment) {
                    _previousPosition = 2;
                }
            }
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof HomeFragment) {
                super.onBackPressed();
            } else {
                loadFragment(new HomeFragment());
                _previousPosition = 0;
            }
        }
    }
}
