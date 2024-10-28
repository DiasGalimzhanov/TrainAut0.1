package com.example.trainaut01.repository;

import com.example.trainaut01.models.TrainingPlan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

// Репозиторий для работы с тренировочными планами в Firebase Firestore
public class TrainingPlanRepository implements Repository<TrainingPlan> {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collection = db.collection("trainingPlans");

    // Метод для добавления нового тренировочного плана
    @Override
    public void add(TrainingPlan trainingPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        collection.add(trainingPlan)
                .addOnSuccessListener(docRef -> onSuccess.onSuccess(null))
                .addOnFailureListener(onFailure);
    }

    // Метод для обновления существующего тренировочного плана по ID
    @Override
    public void update(String id, TrainingPlan trainingPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        collection.document(id).set(trainingPlan)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // Метод для удаления тренировочного плана по ID
    @Override
    public void delete(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        collection.document(id).delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // Метод для получения одного тренировочного плана по ID
    @Override
    public void get(String id, OnSuccessListener<TrainingPlan> onSuccess, OnFailureListener onFailure) {
        collection.document(id).get()
                .addOnSuccessListener(doc -> {
                    TrainingPlan trainingPlan = doc.toObject(TrainingPlan.class);
                    onSuccess.onSuccess(trainingPlan);
                })
                .addOnFailureListener(onFailure);
    }

    // Метод для получения всех тренировочных планов из коллекции
    @Override
    public void getAll(OnSuccessListener<List<TrainingPlan>> onSuccess, OnFailureListener onFailure) {
        collection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<TrainingPlan> trainingPlans = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        trainingPlans.add(document.toObject(TrainingPlan.class));
                    }
                    onSuccess.onSuccess(trainingPlans);
                })
                .addOnFailureListener(onFailure);
    }
}
