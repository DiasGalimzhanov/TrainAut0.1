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

// Репозиторий для работы с упражнениями в Firebase Firestore
public class ExerciseRepository implements Repository<Exercise> {
    private final FirebaseFirestore _db;
    private final CollectionReference _collection;

    @Inject
    public ExerciseRepository(FirebaseFirestore db) {
        this._db = db;
        this._collection = db.collection("exercises");
    }

    // Метод для добавления нового упражнения
    @Override
    public void add(Exercise exercise, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.add(exercise) // Добавляем упражнение в коллекцию
                .addOnSuccessListener(docRef -> onSuccess.onSuccess(null)) // Успешное добавление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для обновления существующего упражнения по ID
    @Override
    public void update(String id, Exercise exercise, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).set(exercise) // Обновляем документ с указанным ID
                .addOnSuccessListener(onSuccess) // Успешное обновление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для удаления упражнения по ID
    @Override
    public void delete(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).delete() // Удаляем документ с указанным ID
                .addOnSuccessListener(onSuccess) // Успешное удаление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для получения одного упражнения по ID
    @Override
    public void get(String id, OnSuccessListener<Exercise> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).get() // Получаем документ с указанным ID
                .addOnSuccessListener(doc -> {
                    // Преобразуем документ в объект Exercise
                    Exercise exercise = doc.toObject(Exercise.class);
                    onSuccess.onSuccess(exercise); // Успешное получение
                })
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для получения всех упражнений из коллекции
    @Override
    public void getAll(OnSuccessListener<List<Exercise>> onSuccess, OnFailureListener onFailure) {
        _collection.get() // Получаем все документы из коллекции
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Exercise> exercises = new ArrayList<>(); // Список для хранения полученных упражнений
                    // Итерация по всем документам в коллекции
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        // Преобразуем каждый документ в объект Exercise и добавляем в список
                        exercises.add(document.toObject(Exercise.class));
                    }
                    onSuccess.onSuccess(exercises); // Успешное получение всех упражнений
                })
                .addOnFailureListener(onFailure); // Обработка ошибки
    }
}
