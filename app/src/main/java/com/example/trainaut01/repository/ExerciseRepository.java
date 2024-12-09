package com.example.trainaut01.repository;

import com.example.trainaut01.models.Exercise;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Репозиторий для управления упражнениями в базе данных Firebase Firestore.
 * Предоставляет методы для добавления, обновления, получения и списка упражнений.
 */
public class ExerciseRepository {

    private final CollectionReference _collection;

    /**
     * Конструктор, инициализирующий ссылку на коллекцию Firestore.
     *
     * @param db экземпляр базы данных Firestore
     */
    @Inject
    public ExerciseRepository(FirebaseFirestore db) {
        this._collection = db.collection("exercises");
    }

    /**
     * Добавляет новое упражнение в базу данных.
     *
     * @param exercise  объект упражнения для добавления
     * @param onSuccess обратный вызов при успешном добавлении
     * @param onFailure обратный вызов при ошибке добавления
     */
    public void add(Exercise exercise, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.add(exercise)
                .addOnSuccessListener(docRef -> onSuccess.onSuccess(null))
                .addOnFailureListener(onFailure);
    }

    /**
     * Обновляет существующее упражнение в базе данных.
     *
     * @param id        ID упражнения для обновления
     * @param exercise  обновлённый объект упражнения
     * @param onSuccess обратный вызов при успешном обновлении
     * @param onFailure обратный вызов при ошибке обновления
     */
    public void update(String id, Exercise exercise, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).set(exercise)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Получает упражнение по его ID.
     *
     * @param id        ID упражнения для получения
     * @param onSuccess обратный вызов с полученным упражнением
     * @param onFailure обратный вызов при ошибке получения
     */
    public void get(String id, OnSuccessListener<Exercise> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).get()
                .addOnSuccessListener(doc -> {
                    Exercise exercise = doc.toObject(Exercise.class);
                    onSuccess.onSuccess(exercise);
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Получает все упражнения из базы данных.
     *
     * @param onSuccess обратный вызов с списком всех упражнений
     * @param onFailure обратный вызов при ошибке получения
     */
    public void getAll(OnSuccessListener<List<Exercise>> onSuccess, OnFailureListener onFailure) {
        _collection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Exercise> exercises = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        exercises.add(document.toObject(Exercise.class));
                    }
                    onSuccess.onSuccess(exercises);
                })
                .addOnFailureListener(onFailure);
    }
}
