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

    private BottomNavigationView _bottomNavigationView; // BottomNavigationView для навигации
    private AppComponent _appComponent; // Компонент Dagger для внедрения зависимостей
    private int _previousPosition = 0; // Переменная для отслеживания предыдущей выбранной позиции

    // Внедренные репозитории для управления упражнениями и планами на день
    @Inject
    ExerciseRepository exerciseRepository;

    @Inject
    DayPlanRepository dayPlanRepository;

    @Inject
    AppInitializer appInitializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация компонентов Dagger
        _appComponent = DaggerAppComponent.create();
        _appComponent.inject(this);

        setContentView(R.layout.activity_base);

        // Устанавливаем фрагмент по умолчанию
        loadFragment(new HomeFragment());

        // Настраиваем BottomNavigationView и слушатели для его элементов
        setupBottomNavigationView();

        // Инициализация упражнений и планов на день
        initializeData();
    }

    // Метод для загрузки указанного фрагмента в контейнер
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Метод для настройки BottomNavigationView с слушателями
    private void setupBottomNavigationView() {
        _bottomNavigationView = findViewById(R.id.bottom_navigation);
        _bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int newPosition = 0;

                // Определяем, какой фрагмент выбрать в зависимости от нажатого элемента
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

    }

    // Метод для анимации перехода между фрагментами
    private void animateFragmentTransition(Fragment fragment, int newPosition) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Определяем направление анимации
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            if (newPosition > _previousPosition) {
                // Переход вправо
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            } else if (newPosition < _previousPosition) {
                // Переход влево
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }

        // Обновляем предыдущую позицию
        _previousPosition = newPosition;

        // Заменяем фрагмент и добавляем его в стек
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null); // Добавляем в стек при каждом переходе
        transaction.commit(); // Выполняем транзакцию

        // Используем post для обновления навигации
        _bottomNavigationView.post(() -> updateBottomNavigationSelection(fragment));
    }

    // Обновление выделенного элемента в BottomNavigationView в зависимости от текущего фрагмента
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
        _bottomNavigationView.setSelectedItemId(itemId);
    }

    // Инициализация данных (упражнений и планов на день)
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

    // Обработка нажатия кнопки "Назад"
    @Override
    public void onBackPressed() {
        // Проверяем, есть ли фрагменты в стеке
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(); // Возвращаемся к предыдущему фрагменту
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            // Обновляем навигацию
            if (currentFragment != null) {
                updateBottomNavigationSelection(currentFragment);
                // Обновляем предыдущую позицию на основе текущего фрагмента
                if (currentFragment instanceof HomeFragment) {
                    _previousPosition = 0;
                } else if (currentFragment instanceof TrainingWeekFragment) {
                    _previousPosition = 1;
                } else if (currentFragment instanceof UserProfileFragment) {
                    _previousPosition = 2;
                }
            }
        } else {
            // Если текущий фрагмент - HomeFragment, завершаем активность
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof HomeFragment) {
                super.onBackPressed(); // Завершение активности
            } else {
                // Возврат к HomeFragment
                loadFragment(new HomeFragment());
                _previousPosition = 0; // Обновляем предыдущую позицию
            }
        }
    }
}
