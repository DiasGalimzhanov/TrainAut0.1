package com.example.trainaut01;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.DayPlan;
import com.example.trainaut01.repository.AppInitializer;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class RegisterActivity extends AppCompatActivity {
    private EditText etFN, etLN, etPhone, etEmail, etPasReg, etPasConfirm;
    private Button btnReg;
    private TextView tvPasswordMatch, tvLogin;
    private CheckBox _chbUserAgreement;

    private AppComponent appComponent;
    @Inject
    UserRepository db;

    @Inject
    DayPlanRepository dayPlanRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        init();

//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();

//        etBirthDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Получение текущей даты
//                final Calendar calendar = Calendar.getInstance();
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//                // Открытие DatePickerDialog
//                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year, int month, int day) {
//                                // Установка выбранной даты в EditText
//                                etBirthDate.setText(day + "/" + (month + 1) + "/" + year);
//                            }
//                        }, year, month, day);
//                datePickerDialog.show();
//            }
//        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        TextWatcher passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkPasswordMatch();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };

        etPasReg.addTextChangedListener(passwordTextWatcher);
        etPasConfirm.addTextChangedListener(passwordTextWatcher);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String log = etEmail.getText().toString();
                String pas = etPasReg.getText().toString();
                String firstName = etFN.getText().toString();
                String lastName = etLN.getText().toString();
                String phone = etPhone.getText().toString();

                if (!log.isEmpty() && !pas.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !phone.isEmpty()) {
                    if (_chbUserAgreement.isChecked()) {
                        registerUser(log, pas, firstName, lastName, phone);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Примите пользовательское соглашение", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        etEmail = findViewById(R.id.etEmailReg);
        etFN = findViewById(R.id.etFirstName);
        etLN = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etPasReg = findViewById(R.id.etPasReg);
        btnReg = findViewById(R.id.btnRegisterReg);
        etPasConfirm = findViewById(R.id.etPasConfirm);
        tvPasswordMatch = findViewById(R.id.tvPasswordMatch);
        tvLogin = findViewById(R.id.tvLogin);
        _chbUserAgreement = findViewById(R.id.chbUserAgreement);
    }

    private void registerUser(String email, String password, String firstName, String lastName, String phone) {
        db.registerUser(email, password, this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser != null) {
                        saveUserData(firebaseUser.getUid(), firstName, lastName, phone, email);
                        getWeekPlansFromFirestore(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<DayPlan> weekPlans = new ArrayList<>();
                                    for (DocumentSnapshot document : task.getResult()) {
                                        DayPlan dayPlan = document.toObject(DayPlan.class);
                                        weekPlans.add(dayPlan);
                                    }
                                    saveUserDayPlans(firebaseUser.getUid(), weekPlans);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Не удалось загрузить планы на неделю", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean validatePhoneNumber() {
        String phone = etPhone.getText().toString();

        if (phone.matches("^\\d{10,12}$")) {
            return true;
        } else {
            etPhone.setError("Введите корректный номер телефона");
            return false;
        }
    }


    private void getWeekPlansFromFirestore(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        dayPlanRepository.getWeekPlansCollection()
                .get()
                .addOnCompleteListener(onCompleteListener);
    }


    private void saveUserDayPlans(String userId, List<DayPlan> weekPlans) {
        dayPlanRepository.saveUserDayPlans(userId, weekPlans, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Планы на неделю сохранены", Toast.LENGTH_SHORT).show();
                });
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Ошибка при сохранении планов: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void saveUserData(String userId, String firstName, String lastName, String phone, String email) {
        db.saveUserData(userId, firstName, lastName, phone, email, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User data saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, BaseActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkPasswordMatch() {
        String password = etPasReg.getText().toString();
        String confirmPassword = etPasConfirm.getText().toString();

        if (password.equals(confirmPassword)) {
            tvPasswordMatch.setText("Пароли совпадают");
            tvPasswordMatch.setTextColor(Color.GREEN);
        } else {
            tvPasswordMatch.setText("Пароли не совпадают");
            tvPasswordMatch.setTextColor(Color.RED);
        }
    }
}