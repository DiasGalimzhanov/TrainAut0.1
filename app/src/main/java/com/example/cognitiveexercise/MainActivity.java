package com.example.cognitiveexercise;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Находим кнопку "Начать"
        Button startButton = findViewById(R.id.start_button);

        // Устанавливаем слушатель на кнопку
        startButton.setOnClickListener(v -> {
            // Скрываем кнопку "Начать" после нажатия
            findViewById(R.id.start_button).setVisibility(View.GONE);

            // Появляется контейнер для фрагмента
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

            // Динамически добавляем фрагмент в контейнер
            CognitiveExerciseFragment fragment = new CognitiveExerciseFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);  // Заменяем контейнер на фрагмент
            transaction.commit();
        });
    }
}
