package com.example.trainaut01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.trainaut01.R;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.DayPlan;
import com.example.trainaut01.models.User;
import com.example.trainaut01.profile.UserProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class UserRepository {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
//    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    // Метод для регистрации пользователя
    public void registerUser(String email, String password, String firstName, String lastName, String phone, String bd, Context context, OnCompleteListener<AuthResult> onCompleteListener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Сохранение данных пользователя
                            getUserDataById(user.getUid(), new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            saveUserDataToPreferences(document, context);
                                        }
                                    }
                                }
                            });
                        }
                    }
                    onCompleteListener.onComplete(task);
                });
    }

    // Метод для сохранения данных пользователя
    public void saveUserData(String userId, String firstName, String lastName, String phone, String email, String bd, OnCompleteListener<Void> onCompleteListener, OnFailureListener onFailureListener) {

        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("phone", phone);
        user.put("email", email);
        user.put("birthDate", bd);
        user.put("lvl",0);
        user.put("exp",0);
        user.put("countDays", 0);

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(onFailureListener);
    }


    // Метод для получения данных пользователя по ID
    public void getUserDataById(String userId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

    // Метод для авторизации пользователя
    public void loginUser(String email, String password, Context context, OnCompleteListener<AuthResult> onCompleteListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Сохранение данных пользователя
                            getUserDataById(user.getUid(), new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            saveUserDataToPreferences(document, context);
                                        }
                                    }
                                }
                            });
                        }
                    }
                    onCompleteListener.onComplete(task);
                });
    }

    private void saveUserDataToPreferences(DocumentSnapshot document, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Получаем данные пользователя
        String userId = document.getString("userId");
        String firstName = document.getString("firstName");
        String lastName = document.getString("lastName");
        String email = document.getString("email");
        String phone = document.getString("phone");
        String birthDate = document.getString("birthDate");
        int lvl = document.getLong("lvl").intValue();
        int exp = document.getLong("exp").intValue();
        int countDays = document.getLong("countDays").intValue();

        // Сохраняем данные в SharedPreferences
        editor.putString("userId", userId);
        editor.putString("email", email);
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("phone", phone);
        editor.putString("birthDate", birthDate);
        editor.putInt("lvl", lvl);
        editor.putInt("exp", exp);
        editor.putInt("countDays", countDays);
        editor.apply();

        Toast.makeText(context, "Данные пользователя сохранены", Toast.LENGTH_SHORT).show();
    }


    // Метод для получения текущего пользователя
//    public FirebaseUser getCurrentUser() {
//        return mAuth.getCurrentUser();
//    }

    public void updateUser(Map<String, Object> updatedUserData, Context context) {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String newLog = (String) updatedUserData.get("email");
        String newPas = (String) updatedUserData.get("password");

        if (newLog != null && !newLog.isEmpty()) {
            user.updateEmail(newLog);
        }
        if (newPas != null && !newPas.isEmpty()) {
            user.updatePassword(newPas);
        }
        db.collection("users").document(userId)
                .update(updatedUserData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Сохранение данных в SharedPreferences после успешного обновления
                        SharedPreferences sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        // Обновляем поля, только если они не пусты
                        if (newLog != null && !newLog.isEmpty()) {
                            editor.putString("email", newLog);
                        }
                        if (newPas != null && !newPas.isEmpty()) {
                            editor.putString("password", newPas);
                        }
                        editor.putString("firstName", (String) updatedUserData.get("firstName"));
                        editor.putString("lastName", (String) updatedUserData.get("lastName"));
                        editor.putString("phone", (String) updatedUserData.get("phone"));
                        editor.putString("birthDate", (String) updatedUserData.get("birthDate"));
                        editor.apply();

                        Toast.makeText(context, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show();

                        FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, new UserProfileFragment());
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Ошибка при обновлении профиля", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    public void saveMessageToFirestore(String theme, String messege, Context context) {
        // Получаем ID пользователя
        String userId = mAuth.getCurrentUser().getUid();

        // Создаем объект для сохранения
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("userId", userId);
        messageData.put("theme", theme);
        messageData.put("message", messege);

        // Сохраняем в коллекцию "messege_users"
        db.collection("messege_users")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Сообщение отправленно", Toast.LENGTH_SHORT).show();

                    // Заменяем текущий фрагмент на новый после успешной отправки
                    FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, new UserProfileFragment());
                    ft.addToBackStack(null); // Добавляем в стек, если нужно вернуться назад
                    ft.commit();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Что-то не так, сообщение не отправленно", Toast.LENGTH_SHORT).show();
                });
    }
}
