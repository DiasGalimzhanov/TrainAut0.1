package com.example.trainaut01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.ActivityLoginBinding;
import com.example.trainaut01.repository.ChildRepository;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.utils.ToastUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import javax.inject.Inject;

/**
 * Экран входа в приложение. Позволяет авторизоваться с помощью email/пароля.
 * Также можно сбросить пароль или перейти к регистрации.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Inject
    UserRepository userRepository;

    @Inject
    ChildRepository childRepository;

    /**
     * Вызывается при создании активности.
     * @param savedInstanceState сохраненное состояние.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        mAuth = FirebaseAuth.getInstance();
        setupGoogleSignInOptions();
        setupClickListeners();
    }

    /**
     * Обрабатывает результат выполнения другой активности, в частности авторизации через Google.
     * @param requestCode код запроса.
     * @param resultCode код результата.
     * @param data данные, возвращаемые другой активностью.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            handleGoogleSignInResult(data);
        }
    }

    /**
     * Настраивает параметры входа через Google.
     */
    private void setupGoogleSignInOptions() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Настраивает обработчики нажатий на элементы интерфейса.
     */
    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> attemptEmailLogin());
        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        binding.tvForgotPas.setOnClickListener(v -> attemptPasswordReset());
    }

    /**
     * Пытается выполнить вход по email и паролю, проверяя введенные данные.
     */
    private void attemptEmailLogin() {
        String log = binding.etLogin.getText().toString().trim();
        String pas = binding.etPassword.getText().toString().trim();

        if (!log.isEmpty() && !pas.isEmpty()) {
            loginUser(log, pas);
        } else {
            ToastUtils.showShortMessage(this, "Заполните все поля");
        }
    }

    /**
     * Пытается выполнить сброс пароля, отправляя письмо на указанный email.
     */
    private void attemptPasswordReset() {
        String email = binding.etLogin.getText().toString().trim();
        if (!email.isEmpty()) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this,
                                            "Ссылка для сброса пароля отправлена на " + email,
                                            Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(this,
                                            "Ошибка: " + (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        } else {
            Toast.makeText(this,
                            "Пожалуйста, введите свой адрес электронной почты",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Обрабатывает результат авторизации через Google.
     * @param data данные, возвращаемые GoogleSignInIntent.
     */
    private void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            } else {
                Toast.makeText(this, "Google Sign-In failed: Account is null", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Выполняет авторизацию в Firebase с помощью учетных данных Google.
     * @param idToken токен, полученный от Google.
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                        navigateToBaseActivity();
                    } else {
                        Toast.makeText(this,
                                "Google Sign-In failed: " + (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Выполняет вход в систему с помощью email и пароля.
     * @param email email пользователя.
     * @param password пароль пользователя.
     */
    private void loginUser(String email, String password) {
        userRepository.loginUser(email, password, this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Вход успешен", Toast.LENGTH_SHORT).show();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    ChildRepository childRepositoryLocal = new ChildRepository();
                    childRepositoryLocal.saveChildData( userId, this,
                            unused -> navigateToBaseActivity(),
                            e -> ToastUtils.showErrorMessage(LoginActivity.this, "Не удалось сохранить данные ребёнка: " + e.getMessage())
                    );

                }
            } else {
                Toast.makeText(this,
                        "Ошибка входа: " + (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Переход к главному экрану приложения после успешного входа.
     */
    private void navigateToBaseActivity() {
        Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
        startActivity(intent);
    }
}
