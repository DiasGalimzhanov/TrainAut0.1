package com.example.trainaut01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.trainaut01.models.Child;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class ChildRepository{

    private final FirebaseFirestore _db;

    public ChildRepository() {
        this._db = FirebaseFirestore.getInstance();
    }

    /**
     * Добавить ребенка в коллекцию `child` для конкретного пользователя.
     */
    public void addChild(String userId, Child child, Context context, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> childData = child.toMap();

        getChildCollection(userId)
                .document(child.getChildId())
                .set(childData)
                .addOnSuccessListener(aVoid -> {
                    saveChildDataToPreferences(child, context);
                    onSuccess.onSuccess(aVoid);
                })
                .addOnFailureListener(onFailure);
    }



    /**
     * Обновить данные ребенка в коллекции `child` для конкретного пользователя.
     */
    public void updateChild(String userId, Child updateChild, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> updatedChildData = updateChild.toMap();

        getChildCollection(userId)
                .document(updateChild.getChildId())
                .set(updatedChildData)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Удалить ребенка из коллекции `child` для конкретного пользователя.
     */
    public void deleteChild(String userId, String childId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        getChildCollection(userId)
                .document(childId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Получить данные ребенка из коллекции `child` для конкретного пользователя.
     */
    public void getChild(String userId, String childId,
                         OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        getChildCollection(userId)
                .document(childId)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


    /**
     * Получить ссылку на коллекцию `child` для конкретного пользователя.
     */
    private CollectionReference getChildCollection(String userId) {
        return _db.collection("users").document(userId).collection("child");
    }


    private void saveChildDataToPreferences(Child child, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("child_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("childId", child.getChildId());
        editor.putString("fullName", child.getFullName());
        editor.putString("birthDate", child.getBirthDate());
        editor.putString("gender", child.getGender().toString());
        editor.putString("diagnosis", child.getDiagnosis());
        editor.putFloat("height", child.getHeight());
        editor.putFloat("weight", child.getWeight());
        editor.putInt("exp", child.getExp());
        editor.putInt("lvl", child.getExp());
        editor.putInt("countDays", child.getExp());

        editor.apply();

        Toast.makeText(context, "Данные ребенка сохранены", Toast.LENGTH_SHORT).show();
    }

    /**
     * Обновляет определенное поле ребенка в коллекции `child` и SharedPreferences.
     *
     * @param userId     идентификатор пользователя.
     * @param childId    идентификатор ребенка.
     * @param fieldName  имя поля для обновления.
     * @param fieldValue новое значение поля.
     * @param context    контекст для доступа к SharedPreferences и Toast.
     */
    public void updateChildItem(String userId, String childId, String fieldName, Object fieldValue, Context context) {
        _db.collection("users").document(userId).collection("child").document(childId)
                .update(fieldName, fieldValue)
                .addOnSuccessListener(aVoid -> {
                    SharedPreferences sharedPref = context.getSharedPreferences("child_data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    if (fieldValue instanceof String) {
                        editor.putString(fieldName, (String) fieldValue);
                    } else if (fieldValue instanceof Integer) {
                        editor.putInt(fieldName, (Integer) fieldValue);
                    } else if (fieldValue instanceof Boolean) {
                        editor.putBoolean(fieldName, (Boolean) fieldValue);
                    } else if (fieldValue instanceof Float) {
                        editor.putFloat(fieldName, (Float) fieldValue);
                    } else if (fieldValue instanceof Long) {
                        editor.putLong(fieldName, (Long) fieldValue);
                    } else {
                        Toast.makeText(context, "Неподдерживаемый тип данных", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    editor.apply();
                    Toast.makeText(context, "Поле успешно обновлено", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Ошибка при обновлении поля: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
