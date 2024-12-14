package com.example.trainaut01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.repository.ChildRepository;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.utils.ToastUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import javax.inject.Inject;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    private EditText etLog;
    private EditText etPas;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Inject
    UserRepository userRepository;

    @Inject
    ChildRepository childRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);

        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        initUI();

        mAuth = FirebaseAuth.getInstance();
        setupGoogleSignInOptions();
        setupClickListeners();
    }

    /**
     * Обработка результата Activity, в частности, результата авторизации через Google.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            handleGoogleSignInResult(data);
        }
    }

    /**
     * Инициализация UI элементов.
     */
    private void initUI() {
        etLog = findViewById(R.id.etLogin);
        etPas = findViewById(R.id.etPassword);
    }

    /**
     * Настройка параметров входа через Google.
     */
    private void setupGoogleSignInOptions() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Установка обработчиков нажатий.
     */
    private void setupClickListeners() {
        Button btnLog = findViewById(R.id.btnLogin);
//        Button btnGoogleReg = findViewById(R.id.btnGoogleReg);
        TextView tvForgotPas = findViewById(R.id.tvForgotPas);
        TextView tvReg = findViewById(R.id.tvRegister);

//        btnGoogleReg.setOnClickListener(v -> signInWithGoogle());

        btnLog.setOnClickListener(v -> attemptEmailLogin());

        tvReg.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        tvForgotPas.setOnClickListener(v -> attemptPasswordReset());
    }

    /**
     * Попытка входа через email и пароль.
     */
    private void attemptEmailLogin() {
        String log = etLog.getText().toString().trim();
        String pas = etPas.getText().toString().trim();

        if (!log.isEmpty() && !pas.isEmpty()) {
            loginUser(log, pas);
        } else {
            ToastUtils.showShortMessage(this, "Заполните все поля");
        }
    }

    /**
     * Попытка сброса пароля.
     */
    private void attemptPasswordReset() {
        String email = etLog.getText().toString().trim();

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
     * Инициация входа через Google.
     */
//    private void signInWithGoogle() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    /**
     * Обработка результата авторизации через Google.
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
     * Авторизация через Firebase с полученным токеном Google.
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
     * Вход пользователя через email и пароль.
     */
    private void loginUser(String email, String password) {
        userRepository.loginUser(email, password, this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Вход успешен", Toast.LENGTH_SHORT).show();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    ChildRepository childRepositoryLocal = new ChildRepository();
                    childRepositoryLocal.saveChildData(userId, this);
                }

                navigateToBaseActivity();
            } else {
                Toast.makeText(this,
                        "Ошибка входа: " + (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Переход к главной Activity после успешного входа.
     */
    private void navigateToBaseActivity() {
        Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
        startActivity(intent);

    }
}
