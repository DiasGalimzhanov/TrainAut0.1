package com.example.trainaut01.profileActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trainaut01.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;

public class UserProfileActivity extends AppCompatActivity {
    private TextView _tvChildProfile, _parentName;
    private ImageView _userProfileImage;
    private Button btnAddChild;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        _tvChildProfile = findViewById(R.id.tvChildProfile);
        _userProfileImage = findViewById(R.id.userProfileImage);
        _parentName = findViewById(R.id.parentName);
        btnAddChild = findViewById(R.id.btnAddChild);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser!=null){
            userId = currentUser.getUid();
            getUserDataById(userId);
            printData();
        }


        _tvChildProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, ChildProfileActivity.class);
                startActivity(intent);
            }
        });

        _userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, UpdateUserProfileActivity.class);
                startActivity(intent);
            }
        });

        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(UserProfileActivity.this,)
            }
        });
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

        if(firstName != null && lastName != null){
            _parentName.setText(firstName + " " + lastName);
        }



    }


}