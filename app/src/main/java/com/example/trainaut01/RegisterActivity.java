package com.example.trainaut01;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail,etPasReg,etPasConfirm,etFN, etLN, etPhone ,etBirthDate;
    private Button btnReg;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvPasswordMatch;
//    private DatabaseReference mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmailReg);
        etFN = findViewById(R.id.etFirstName);
        etLN = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etPasReg = findViewById(R.id.etPasReg);
        btnReg = findViewById(R.id.btnRegisterReg);
        etBirthDate = findViewById(R.id.etBirthDate);
        etPasConfirm = findViewById(R.id.etPasConfirm);
        tvPasswordMatch = findViewById(R.id.tvPasswordMatch);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextWatcher passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkPasswordMatch();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
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
                String birthDate = etBirthDate.getText().toString();
                if(!log.isEmpty() && !pas.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !phone.isEmpty() && !birthDate.isEmpty()) {
                    registerUser(log, pas,firstName,lastName,phone, birthDate);
                }else{
                    Toast.makeText(RegisterActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                }
            }
        });

        etBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получение текущей даты
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Открытие DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                // Установка выбранной даты в EditText
                                etBirthDate.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void registerUser(String email, String password, String firstName, String lastName, String phone, String bd) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                saveUserData(firebaseUser.getUid(), firstName, lastName, phone, email, bd);
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserData(String userId, String firstName, String lastName, String phone, String email, String bd) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("phone", phone);
        user.put("email", email);
        user.put("birthDate", bd);
        user.put("role", "parent");  // По умолчанию можно назначить роль

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User data saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
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