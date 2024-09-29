package com.example.trainaut01.repository;

import com.google.android.gms.tasks.OnCompleteListener;
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
}
