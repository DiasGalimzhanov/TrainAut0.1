package com.example.trainaut01;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.models.DayPlan;
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
import java.util.List;

import javax.inject.Inject;

public class RegisterActivity extends AppCompatActivity {
    private EditText _etFN, _etLN, _etPhone, _etCity, _etEmail, _etPasReg, _etPasConfirm;
    private Spinner _spGender;
    private Button _btnReg;
    private TextView _tvPasswordMatch, _tvLogin;
    private CheckBox _chbUserAgreement;

    private AppComponent _appComponent;

    @Inject
    UserRepository _userRepository;

    @Inject
    DayPlanRepository _dayPlanRepository;


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

        _tvLogin.setOnClickListener(new View.OnClickListener() {
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

        _etPasReg.addTextChangedListener(passwordTextWatcher);
        _etPasConfirm.addTextChangedListener(passwordTextWatcher);

        _btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = _etFN.getText().toString();
                String lastName = _etLN.getText().toString();
                String phone = _etPhone.getText().toString();
                String city = _etCity.getText().toString();
                String selectedGender = _spGender.getSelectedItem().toString();
                String email = _etEmail.getText().toString();
                String pas = _etPasReg.getText().toString();

                if (areFieldsValid(firstName, lastName, phone, city, selectedGender, email, pas)) {
                    if (isUserAgreementChecked()) {
                        registerUser(firstName, lastName, phone, city, selectedGender, email, pas);
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
        _appComponent = DaggerAppComponent.create();
        _appComponent.inject(this);

        _tvLogin = findViewById(R.id.tvLogin);
        _tvPasswordMatch = findViewById(R.id.tvPasswordMatch);

        _etFN = findViewById(R.id.etFirstName);
        _etLN = findViewById(R.id.etLastName);
        _etPhone = findViewById(R.id.etPhone);
        _etCity = findViewById(R.id.etCity);
        _etEmail = findViewById(R.id.etEmailReg);
        _etPasReg = findViewById(R.id.etPasReg);
        _etPasConfirm = findViewById(R.id.etPasConfirm);

        _chbUserAgreement = findViewById(R.id.chbUserAgreement);

        _spGender = findViewById(R.id.spGender);
        setupGenderAdapter(_spGender);

        _btnReg = findViewById(R.id.btnRegisterReg);
    }

    private boolean areFieldsValid(String firstName, String lastName, String phone, String city, String selectedGender, String email, String pas) {
        return !firstName.isEmpty() && !lastName.isEmpty() && !phone.isEmpty() && !city.isEmpty() && !selectedGender.isEmpty() && !email.isEmpty() && !pas.isEmpty();
    }

    private boolean isUserAgreementChecked() {
        return _chbUserAgreement.isChecked();
    }

    private void setupGenderAdapter(Spinner spGender) {
        String[] genderOptions = Gender.getGenderValues();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, genderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(adapter);
    }

    private void registerUser(String firstName, String lastName, String phone, String city, String selectedGender, String email, String password) {
        _userRepository.registerUser(email, password, this, new OnCompleteListener<AuthResult>() {
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
        String phone = _etPhone.getText().toString();

        if (phone.matches("^\\d{10,12}$")) {
            return true;
        } else {
            _etPhone.setError("Введите корректный номер телефона");
            return false;
        }
    }


    private void getWeekPlansFromFirestore(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        _dayPlanRepository.getWeekPlansCollection()
                .get()
                .addOnCompleteListener(onCompleteListener);
    }


    private void saveUserDayPlans(String userId, List<DayPlan> weekPlans) {
        _dayPlanRepository.saveUserDayPlans(userId, weekPlans, new OnSuccessListener<Void>() {
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
        _userRepository.saveUserData(userId, firstName, lastName, phone, email, new OnCompleteListener<Void>() {
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
        String password = _etPasReg.getText().toString();
        String confirmPassword = _etPasConfirm.getText().toString();

        if (password.equals(confirmPassword)) {
            _tvPasswordMatch.setText("Пароли совпадают");
            _tvPasswordMatch.setTextColor(Color.GREEN);
        } else {
            _tvPasswordMatch.setText("Пароли не совпадают");
            _tvPasswordMatch.setTextColor(Color.RED);
        }
    }
}