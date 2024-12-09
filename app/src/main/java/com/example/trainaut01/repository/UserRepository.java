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

public class UserRepository {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void addUser(User user, Context context, OnCompleteListener<AuthResult> onCompleteListener) {
        String rawPassword = user.getPass();
        user.setPass(rawPassword);

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

    public void getUserDataById(String userId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(onCompleteListener);
    }

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
        editor.putString("pass", user.getPass());
        editor.putString("role", user.getRole().toString());

        editor.apply();
    }


    public void updateUser(User updatedUser, Context context) {
        String userId = updatedUser.getUserId();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("firstName", updatedUser.getFullName());
            userMap.put("phone", updatedUser.getPhone());

            db.collection("users").document(userId)
                    .update(userMap)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Данные успешно обновлены", Toast.LENGTH_SHORT).show();
                        saveUserDataToPreferences(updatedUser, context);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Ошибка обновления данных в Firestore", Toast.LENGTH_SHORT).show();
                    });

        }
    }
//
//
//    public void updateUserItem(String fieldName, Object fieldValue, Context context) {
//        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//
//        db.collection("users").document(userId)
//                .update(fieldName, fieldValue)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        SharedPreferences sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPref.edit();
//
//                        if (fieldValue instanceof String) {
//                            editor.putString(fieldName, (String) fieldValue);
//                        } else if (fieldValue instanceof Integer) {
//                            editor.putInt(fieldName, (Integer) fieldValue);
//                        } else if (fieldValue instanceof Boolean) {
//                            editor.putBoolean(fieldName, (Boolean) fieldValue);
//                        } else if (fieldValue instanceof Float) {
//                            editor.putFloat(fieldName, (Float) fieldValue);
//                        } else if (fieldValue instanceof Long) {
//                            editor.putLong(fieldName, (Long) fieldValue);
//                        } else {
//                            Toast.makeText(context, "Неподдерживаемый тип данных", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        editor.apply();
//                        Toast.makeText(context, "Поле успешно обновлено", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context, "Ошибка при обновлении поля", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }


    public void saveMessageToFirestore(String theme, String messege, Context context) {
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("userId", userId);
        messageData.put("theme", theme);
        messageData.put("message", messege);

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
}
