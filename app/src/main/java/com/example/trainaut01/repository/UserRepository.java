package com.example.trainaut01.repository;

import android.content.Context;
import android.content.SharedPreferences;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.trainaut01.R;
import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.models.DayPlan;
import com.example.trainaut01.models.User;
import com.example.trainaut01.profile.UserProfileFragment;
import com.example.trainaut01.utils.SharedPreferencesUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Класс UserRepository предоставляет методы для работы с данными пользователя
 * в Firebase Firestore и Firebase Authentication.
 */
public class UserRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;

    /**
     * Конструктор по умолчанию.
     * Инициализирует объекты для работы с Firestore и Firebase Authentication.
     */
    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Регистрация нового пользователя в Firebase Authentication и сохранение его данных в Firestore.
     *
     * @param user                Объект User с данными пользователя.
     * @param context             Контекст для отображения Toast-сообщений.
     * @param onCompleteListener  Слушатель, который будет вызван после завершения операции регистрации.
     */
    public void addUser(User user,  Context context, OnCompleteListener<AuthResult> onCompleteListener) {
        String rawPassword = user.getPass();
//        user.setPass(rawPassword);

        mAuth.createUserWithEmailAndPassword(user.getEmail(), rawPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            user.setUserId(userId);

                            db.collection("users").document(userId)
                                    .set(user.toMap())
                                    .addOnSuccessListener(aVoid -> {
                                        saveUserDataToPreferences(user, context);
                                        Toast.makeText(context, "Пользователь успешно добавлен", Toast.LENGTH_SHORT).show();
                                        onCompleteListener.onComplete(task);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Ошибка сохранения данных в Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Ошибка регистрации: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Создаёт объект User из документа Firestore.
     *
     * @param document Документ Firestore с данными пользователя.
     * @return Объект User, созданный на основе документа.
     */
    private User createUserFromDocument(DocumentSnapshot document) {
        String userId = document.getString("userId");
        String fullName = document.getString("fullName");
        String phone = document.getString("phone");
        String birthDate = document.getString("birthDate");
        String city = document.getString("city");
        Gender gender = Gender.fromString(document.getString("gender"));;
        String email = document.getString("email");
        String pass = document.getString("pass");

        List<Map<String, Object>> dayPlansData = (List<Map<String, Object>>) document.get("dayPlans");
        List<DayPlan> dayPlans = new ArrayList<>();
        if (dayPlansData != null) {
            for (Map<String, Object> planData : dayPlansData) {
                DayPlan dayPlan = new DayPlan(planData);
                dayPlans.add(dayPlan);
            }
        }

        return new User(userId, fullName, phone, birthDate, city, gender, email, pass);
    }

    /**
     * Получение данных пользователя по его уникальному идентификатору userId.
     *
     * @param userId             Уникальный идентификатор пользователя в Firestore.
     * @param onCompleteListener Слушатель, вызываемый при завершении операции загрузки документа.
     */
    public void getUserDataById(String userId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

    /**
     * Авторизация пользователя по email и паролю.
     * При успешной авторизации данные пользователя загружаются из Firestore и сохраняются в SharedPreferences.
     *
     * @param email              Email пользователя.
     * @param password           Пароль пользователя.
     * @param context            Контекст для отображения Toast-сообщений.
     * @param onCompleteListener Слушатель, вызываемый при завершении операции авторизации.
     */
    public void loginUser(String email, String password, Context context, OnCompleteListener<AuthResult> onCompleteListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            getUserDataById(user.getUid(), new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            User user = createUserFromDocument(document);
                                            saveUserDataToPreferences(user, context);
                                        }
                                    }
                                }
                            });
                        }
                    }
                    onCompleteListener.onComplete(task);
                });
    }


    /**
     * Обновление данных пользователя в Firestore.
     *
     * @param updatedUser Обновлённый объект User.
     * @param context     Контекст для отображения Toast-сообщений.
     */
    public void updateUser(User updatedUser, Context context) {
        String userId = updatedUser.getUserId();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Map<String, Object> userMap = updatedUser.toMap();

            db.collection("users").document(userId)
                    .set(userMap)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Данные успешно обновлены", Toast.LENGTH_SHORT).show();
                        saveUserDataToPreferences(updatedUser, context);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Ошибка обновления данных в Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } else {
            Toast.makeText(context, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Сохранение сообщения от пользователя в коллекцию "message_users" в Firestore.
     * После успешной отправки — переход к фрагменту профиля пользователя.
     *
     * @param theme    Тема сообщения.
     * @param message  Текст сообщения.
     * @param context  Контекст для отображения Toast-сообщений и выполнения транзакции фрагмента.
     */
    public void saveMessageToFirestore(String theme, String message, Context context) {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("userId", userId);
        messageData.put("email", SharedPreferencesUtils.getString(context,"user_data", "email", ""));
        messageData.put("theme", theme);
        messageData.put("message", message);

        db.collection("messege_users")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Сообщение отправленно", Toast.LENGTH_SHORT).show();

                    FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, new UserProfileFragment());
                    ft.addToBackStack(null);
                    ft.commit();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Что-то не так, сообщение не отправленно", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Удаление аккаунта пользователя из Firestore и Firebase Authentication.
     *
     * @param userId   Уникальный идентификатор пользователя, аккаунт которого нужно удалить.
     * @param onSuccess Слушатель, вызываемый при успешном удалении аккаунта.
     * @param onFailure Слушатель, вызываемый при ошибке удаления аккаунта.
     */
    public void deleteUserAccount(String userId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        currentUser.delete()
                                .addOnSuccessListener(onSuccess)
                                .addOnFailureListener(onFailure);
                    } else {
                        onFailure.onFailure(new Exception("No currently signed-in user."));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Сохранение данных пользователя в SharedPreferences для локального кэша.
     *
     * @param user    Объект пользователя с актуальными данными.
     * @param context Контекст для доступа к SharedPreferences.
     */
    private void saveUserDataToPreferences(User user, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("userId", user.getUserId());
        editor.putString("fullName", user.getFullName());
        editor.putString("phone", user.getPhone());
        editor.putString("birthDate", user.getBirthDate());
        editor.putString("city", user.getCity());
        editor.putString("gender", user.getGender().toString());
        editor.putString("email", user.getEmail());
        editor.putString("role", user.getRole().toString());

        editor.apply();
    }

}
