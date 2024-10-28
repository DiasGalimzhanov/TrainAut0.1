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
        _collection.add(exercise)
                .addOnSuccessListener(docRef -> onSuccess.onSuccess(null))
                .addOnFailureListener(onFailure);
    }

    // Метод для обновления существующего упражнения по ID
    @Override
    public void update(String id, Exercise exercise, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).set(exercise)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // Метод для удаления упражнения по ID
    @Override
    public void delete(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // Метод для получения одного упражнения по ID
    @Override
    public void get(String id, OnSuccessListener<Exercise> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).get()
                .addOnSuccessListener(doc -> {
                    Exercise exercise = doc.toObject(Exercise.class);
                    onSuccess.onSuccess(exercise);
                })
                .addOnFailureListener(onFailure);
    }

    // Метод для получения всех упражнений из коллекции
    @Override
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
