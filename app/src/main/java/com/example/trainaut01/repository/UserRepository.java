package com.example.trainaut01.repository;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
//    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    // Метод для сохранения данных пользователя
    public void saveUserData(String userId, String firstName, String lastName, String phone, String email, String role, OnCompleteListener<Void> onCompleteListener) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("phone", phone);
        user.put("email", email);
        user.put("role", role);

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(onCompleteListener);
    }

    // Метод для получения данных пользователя по ID
    public void getUserDataById(String userId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

    // Метод для авторизации пользователя
    public void loginUser(String email, String password, OnCompleteListener<AuthResult> onCompleteListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(onCompleteListener);
    }

    // Метод для получения текущего пользователя
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void updateUser(String userId, Map<String, Object> updatedUserData, Context context){
        db.collection("users").document(userId)
                .update(updatedUserData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Ошибка при обновлении профиля", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
