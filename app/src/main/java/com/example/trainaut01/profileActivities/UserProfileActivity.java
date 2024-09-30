package com.example.trainaut01.profileActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trainaut01.BaseActivity;
import com.example.trainaut01.LoginActivity;
import com.example.trainaut01.MainActivity;
import com.example.trainaut01.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;

public class UserProfileActivity extends AppCompatActivity {
    private TextView  _parentName, _tvEmail, _tvPhone, _tvBirthDate, _btnExit;
    private ImageView _userProfileImage;
    private BottomNavigationView bottomNavigationView;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        init();

        if (currentUser != null) {
            userId = currentUser.getUid();
            getUserDataById(userId);
        }


        _userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfileActivity.this, UpdateUserProfileActivity.class));
            }
        });


        _btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences("user_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();

                FirebaseAuth.getInstance().signOut();

                Toast.makeText(UserProfileActivity.this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                finish();
            }
        });

    }

    public void init(){
        _userProfileImage = findViewById(R.id.userProfileImage);
        _parentName = findViewById(R.id.parentName);
        _tvEmail = findViewById(R.id.tvEmail);
        _tvPhone = findViewById(R.id.tvPhone);
        _tvBirthDate = findViewById(R.id.tvBirthDate);
        _btnExit = findViewById(R.id.btnExit);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void getUserDataById(String userId) {
        // Получаем ссылку на документ с id userId из коллекции users
        DocumentReference docRef = db.collection("users").document(userId);

        // Выполняем запрос для получения данных
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Проверяем, существует ли документ
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        saveUserToSP(document);
                        printData();
                    } else {
                        Log.d("UserData", "Документ не найден.");
                    }
                } else {
                    Log.d("UserData", "Ошибка запроса: ", task.getException());
                    Toast.makeText(UserProfileActivity.this, "Ошибка при получении данных: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserToSP(DocumentSnapshot user) {
        SharedPreferences sharedPref = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Проверка на наличие уже сохранённых данных
        if (sharedPref.contains("userId")) {
            return;  // Если данные уже сохранены, выходим из метода
        }

        String firstName = user.getString("firstName");
        String lastName = user.getString("lastName");
        String email = user.getString("email");
        String phone = user.getString("phone");
        String birthDate = user.getString("birthDate");
        String role = user.getString("role");


        editor.putString("userId", userId);
        editor.putString("email", email);
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("phone", phone);
        editor.putString("birthDate", birthDate);
        editor.putString("role", role);
        editor.apply();


        Toast.makeText(this, "Данные пользователя сохранены", Toast.LENGTH_SHORT).show();
    }

    public void printData(){
        SharedPreferences sharedPref = getSharedPreferences("user_data", MODE_PRIVATE);
        String firstName = sharedPref.getString("firstName", null);
        String lastName = sharedPref.getString("lastName", null);
        String email = sharedPref.getString("email", null);
        String phone = sharedPref.getString("phone", null);
        String birthDate = sharedPref.getString("birthDate", null);
        String role = sharedPref.getString("role", null);

        if(firstName != null && lastName != null && email != null && phone != null && birthDate != null){
            _parentName.setText(firstName + " " + lastName);
            _tvEmail.setText(email);
            _tvPhone.setText(phone);
            _tvBirthDate.setText(birthDate);
        }
    }


}