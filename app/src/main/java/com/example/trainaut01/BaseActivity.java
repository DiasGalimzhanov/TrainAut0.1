package com.example.trainaut01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.trainaut01.home.HomeFragment;
//import com.example.trainaut01.profileActivities.UserProfileActivity;
import com.example.trainaut01.profile.UserProfileFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.home.HomeFragment;
import com.example.trainaut01.profile.UserProfileFragment;
import com.example.trainaut01.repository.AppInitializer;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.repository.ExerciseRepository;
import com.example.trainaut01.training.TrainingWeekFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

public class BaseActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private AppComponent appComponent;

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

                if(item.getItemId() == R.id.nav_home){
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_training) {
                    selectedFragment = new TrainingWeekFragment();
                } else if (item.getItemId() == R.id.nav_profile) {
                    selectedFragment = new UserProfileFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });

        // Инициализация упражнений и планов на день
        initializeData();
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
}
