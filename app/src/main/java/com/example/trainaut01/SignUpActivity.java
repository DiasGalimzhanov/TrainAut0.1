package com.example.trainaut01;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
import com.example.trainaut01.models.User;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.repository.PreferencesRepository;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.utils.DatePickerUtils;
import com.example.trainaut01.utils.SpinnerUtils;
import com.example.trainaut01.utils.ToastUtils;
import com.example.trainaut01.utils.ValidationUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class SignUpActivity extends AppCompatActivity {

    private EditText _etFullName, _etPhone, _etBirthDate, _etCity, _etEmail, _etPassReg, _etPasConfirm;
    private Spinner _spGender;
    private Button _btnContinue;
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

        initComponents();
        setupUI();
    }

    private void initComponents() {
        _appComponent = DaggerAppComponent.create();
        _appComponent.inject(SignUpActivity.this);

        _tvLogin = findViewById(R.id.tvLogin);
        _tvPasswordMatch = findViewById(R.id.tvPasswordMatch);

        _etFullName = findViewById(R.id.etFullName);
        _etPhone = findViewById(R.id.etPhone);
        _etBirthDate = findViewById(R.id.etBirthDate);
        _etCity = findViewById(R.id.etCity);
        _etEmail = findViewById(R.id.etEmailReg);
        _etPassReg = findViewById(R.id.etPasReg);
        _etPasConfirm = findViewById(R.id.etPasConfirm);

        _chbUserAgreement = findViewById(R.id.chbUserAgreement);

        _spGender = findViewById(R.id.spGender);
        SpinnerUtils.setupGenderAdapter(this, _spGender, Gender.getGenderValues());

        _btnContinue = findViewById(R.id.btnContinue);
    }

    private void setupUI() {
        setupLoginRedirect();
        setupBirthDatePicker();
        setupPasswordValidation();
        setupContinueButton();
    }

    private void setupLoginRedirect() {
        _tvLogin.setOnClickListener(view -> navigateToLogin());
    }

    private void setupBirthDatePicker() {
        _etBirthDate.setOnClickListener(v -> DatePickerUtils.showDatePickerDialog(this, _etBirthDate));
    }

    private void setupPasswordValidation() {
        TextWatcher passwordTextWatcher = createPasswordTextWatcher();
        _etPassReg.addTextChangedListener(passwordTextWatcher);
        _etPasConfirm.addTextChangedListener(passwordTextWatcher);
    }

    private void setupContinueButton() {
        _btnContinue.setOnClickListener(view -> {
            if (!validateForm()) return;

            User newUser = createNewUser();
            navigateToChildSignUpFragment(newUser);
        });
    }

    private boolean validateForm() {
        if (!ValidationUtils.areFieldsFilled(
                _etFullName.getText().toString().trim(),
                _etPhone.getText().toString().trim(),
                _etBirthDate.getText().toString().trim(),
                _etCity.getText().toString().trim(),
                _etEmail.getText().toString().trim(),
                _etPassReg.getText().toString().trim()
        )) {
            ToastUtils.showErrorMessage(this, "Заполните все поля");
            return false;
        }

        if (!isUserAgreementChecked()) {
            ToastUtils.showErrorMessage(this, "Примите пользовательское соглашение");
            return false;
        }

        return true;
    }

    private boolean isUserAgreementChecked() {
        return _chbUserAgreement.isChecked();
    }


    @SuppressLint("DefaultLocale")
    private DatePickerDialog.OnDateSetListener getDateSetListener() {
        return (view, year, month, day) -> _etBirthDate.setText(String.format("%02d.%02d.%d", day, month + 1, year));
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private TextWatcher createPasswordTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkPasswordMatch();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };
    }

    private void checkPasswordMatch() {
        if (!ValidationUtils.doPasswordsMatch(_etPassReg.getText().toString(),_etPasConfirm.getText().toString())) {
            _tvPasswordMatch.setText("Пароли не совпадают");
            _tvPasswordMatch.setTextColor(Color.RED);
        }else{
            _tvPasswordMatch.setText("Пароли совпадают");
            _tvPasswordMatch.setTextColor(Color.GREEN);
        }
    }

    private User createNewUser() {
        String fullName = _etFullName.getText().toString();
        String phone = _etPhone.getText().toString();
        String birthDate = _etBirthDate.getText().toString();
        String city = _etCity.getText().toString();
        String selectedGender = _spGender.getSelectedItem().toString();
        String email = _etEmail.getText().toString();
        String pass = _etPassReg.getText().toString();

        Gender gender = Gender.fromString(selectedGender);

        return new User(fullName, phone, birthDate, city, gender, email, pass);
    }

    private void navigateToChildSignUpFragment(User user) {
        ChildSignUpFragment fragment = ChildSignUpFragment.newInstance(user);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.signUp_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}



//    private void registerUser(String fullName, String phone, String birthDate, String city, Gender gender, String email, String password) {
//        _userRepository.registerUser(email, password, this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                    if (firebaseUser != null) {
//                        saveUserData(firebaseUser.getUid(), fullName, phone, birthDate, city, gender, email, password);
//                        getWeekPlansFromFirestore(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    List<DayPlan> weekPlans = new ArrayList<>();
//                                    for (DocumentSnapshot document : task.getResult()) {
//                                        DayPlan dayPlan = document.toObject(DayPlan.class);
//                                        weekPlans.add(dayPlan);
//                                    }
//                                    saveUserDayPlans(firebaseUser.getUid(), weekPlans);
//                                } else {
//                                    Toast.makeText(SignUpActivity.this, "Не удалось загрузить планы на неделю", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                } else {
//                    Toast.makeText(SignUpActivity.this, "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }





//    private void getWeekPlansFromFirestore(OnCompleteListener<QuerySnapshot> onCompleteListener) {
//        _dayPlanRepository.getWeekPlansCollection()
//                .get()
//                .addOnCompleteListener(onCompleteListener);
//    }

//    private void saveDayPlans(String parentId, List<DayPlan> weekPlans) {
//        _dayPlanRepository.saveUserDayPlans(parentId, weekPlans, new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                runOnUiThread(() -> {
//                    Toast.makeText(SignUpActivity.this, "Планы на неделю сохранены", Toast.LENGTH_SHORT).show();
//                });
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                runOnUiThread(() -> {
//                    Toast.makeText(SignUpActivity.this, "Ошибка при сохранении планов: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//            }
//        });
//    }


//    private void saveUserDayPlans(String userId, List<DayPlan> weekPlans) {
//        _dayPlanRepository.saveUserDayPlans(userId, weekPlans, new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                runOnUiThread(() -> {
//                    Toast.makeText(SignUpActivity.this, "Планы на неделю сохранены", Toast.LENGTH_SHORT).show();
//                });
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                runOnUiThread(() -> {
//                    Toast.makeText(SignUpActivity.this, "Ошибка при сохранении планов: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//            }
//        });
//    }

//    private void saveUserData(String userId, String fullName, String phone, String birthDate, String city, Gender gender, String email, String pass) {
//        _userRepository.saveUserData(userId, fullName, phone, birthDate, city, gender, email, pass, new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
////                    todo перейти на регистацию ребенка, а не на главную
//                    Intent intent = new Intent(SignUpActivity.this, BaseActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(SignUpActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(SignUpActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
