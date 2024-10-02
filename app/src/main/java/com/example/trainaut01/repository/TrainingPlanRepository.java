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
    // Инициализация Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Ссылка на коллекцию "trainingPlans" в Firestore
    private final CollectionReference collection = db.collection("trainingPlans");

    // Метод для добавления нового тренировочного плана
    @Override
    public void add(TrainingPlan trainingPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        collection.add(trainingPlan) // Добавляем тренировочный план в коллекцию
                .addOnSuccessListener(docRef -> onSuccess.onSuccess(null)) // Успешное добавление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для обновления существующего тренировочного плана по ID
    @Override
    public void update(String id, TrainingPlan trainingPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        collection.document(id).set(trainingPlan) // Обновляем документ с указанным ID
                .addOnSuccessListener(onSuccess) // Успешное обновление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для удаления тренировочного плана по ID
    @Override
    public void delete(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        collection.document(id).delete() // Удаляем документ с указанным ID
                .addOnSuccessListener(onSuccess) // Успешное удаление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для получения одного тренировочного плана по ID
    @Override
    public void get(String id, OnSuccessListener<TrainingPlan> onSuccess, OnFailureListener onFailure) {
        collection.document(id).get() // Получаем документ с указанным ID
                .addOnSuccessListener(doc -> {
                    // Преобразуем документ в объект TrainingPlan
                    TrainingPlan trainingPlan = doc.toObject(TrainingPlan.class);
                    onSuccess.onSuccess(trainingPlan); // Успешное получение
                })
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для получения всех тренировочных планов из коллекции
    @Override
    public void getAll(OnSuccessListener<List<TrainingPlan>> onSuccess, OnFailureListener onFailure) {
        collection.get() // Получаем все документы из коллекции
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<TrainingPlan> trainingPlans = new ArrayList<>(); // Список для хранения полученных тренировочных планов
                    // Итерация по всем документам в коллекции
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        // Преобразуем каждый документ в объект TrainingPlan и добавляем в список
                        trainingPlans.add(document.toObject(TrainingPlan.class));
                    }
                    onSuccess.onSuccess(trainingPlans); // Успешное получение всех тренировочных планов
                })
                .addOnFailureListener(onFailure); // Обработка ошибки
    }
}
