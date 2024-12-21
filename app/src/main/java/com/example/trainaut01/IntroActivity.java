package com.example.trainaut01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Активность приветствия, отображаемая при запуске приложения.
 * Позволяет пользователю выбрать между авторизацией или регистрацией.
 * Если пользователь уже авторизован, автоматически переходит в основную активность.
 */
public class IntroActivity extends AppCompatActivity {
    Button _btnLogIn, _btnReg;

    /**
     * Вызывается при создании активности.
     * Проверяет, авторизован ли пользователь, и перенаправляет его в основную активность,
     * если пользователь уже зарегистрирован.
     *
     * @param savedInstanceState сохраненное состояние активности, если оно существует.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if (isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        setContentView(R.layout.activity_intro);
        init();
        setupListeners();
    }

    /**
     * Проверяет, авторизован ли пользователь.
     *
     * @return true, если пользователь авторизован, иначе false.
     */
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        return sharedPreferences.getString("userId", null) != null;
    }

    /**
     * Переход в основную активность приложения.
     */
    private void navigateToMainActivity() {
        startActivity(new Intent(this, BaseActivity.class));
        finish();
    }

    /**
     * Инициализирует элементы пользовательского интерфейса.
     */
    private void init() {
        _btnReg = findViewById(R.id.btnRegister);
        _btnLogIn = findViewById(R.id.btnLogIn);
    }

    /**
     * Настраивает обработчики событий для кнопок.
     */
    private void setupListeners() {
        _btnLogIn.setOnClickListener(view -> navigateTo(LoginActivity.class));
        _btnReg.setOnClickListener(view -> navigateTo(SignUpActivity.class));
    }

    /**
     * Переход на указанную активность.
     *
     * @param targetActivity класс активности, на которую нужно перейти.
     */
    private void navigateTo(Class<?> targetActivity) {
        startActivity(new Intent(this, targetActivity));
    }
}