package com.example.trainaut01.repository;

import com.example.trainaut01.models.Child;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class ChildRepository{

    private final FirebaseFirestore firestore;

    public ChildRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Добавить ребенка в коллекцию `child` для конкретного пользователя.
     */
    public void addChild(String userId, Child child, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> childData = child.toMap();

        getChildCollection(userId)
                .document(child.getChildId())
                .set(childData)
                .addOnSuccessListener(onSuccess)
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
        return firestore.collection("users").document(userId).collection("child");
    }
}
