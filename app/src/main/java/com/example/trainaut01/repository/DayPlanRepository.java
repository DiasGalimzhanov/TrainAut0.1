package com.example.trainaut01.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.trainaut01.models.DayPlan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

// Репозиторий для работы с планами дня в Firebase Firestore
public class DayPlanRepository implements Repository<DayPlan> {
    private final FirebaseFirestore _db;
    private final CollectionReference _collection;

    @Inject
    public DayPlanRepository(FirebaseFirestore db) {
        this._db = db;
        this._collection = db.collection("dayPlans");
    }

    // Метод для получения коллекции
    public CollectionReference getWeekPlansCollection() {
        return _db.collection("dayPlans");
    }

    // Метод для добавления нового плана дня
    @Override
    public void add(DayPlan dayPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.add(dayPlan)
                .addOnSuccessListener(docRef -> onSuccess.onSuccess(null))
                .addOnFailureListener(onFailure);
    }

    // Метод для обновления существующего плана дня по ID
    @Override
    public void update(String id, DayPlan dayPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).set(dayPlan)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // Метод для удаления плана дня по ID
    @Override
    public void delete(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // Метод для получения одного плана дня по ID
    @Override
    public void get(String id, OnSuccessListener<DayPlan> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).get()
                .addOnSuccessListener(doc -> {
                    DayPlan dayPlan = doc.toObject(DayPlan.class);
                    onSuccess.onSuccess(dayPlan);
                })
                .addOnFailureListener(onFailure);
    }

    // Метод для получения одного плана дня по дню недели и ID пользователя
    public void getDayPlanByWeekDay(String userId, DayPlan.WeekDay weekDay, OnSuccessListener<DayPlan> onSuccessListener, OnFailureListener onFailureListener) {
        CollectionReference userDayPlansCollection = _db.collection("users").document(userId).collection("dayPlans");

        userDayPlansCollection.whereEqualTo("weekDay", weekDay.toString())
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DayPlan dayPlan = queryDocumentSnapshots.getDocuments().get(0).toObject(DayPlan.class);
                        dayPlan.setId(queryDocumentSnapshots.getDocuments().get(0).getId());
                        onSuccessListener.onSuccess(dayPlan);
                    } else {
                        onSuccessListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    // Метод для получения всех планов дня из коллекции
    @Override
    public void getAll(OnSuccessListener<List<DayPlan>> onSuccess, OnFailureListener onFailure) {
        _collection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DayPlan> dayPlans = new ArrayList<>();
                    // Итерация по всем документам в коллекции
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        dayPlans.add(document.toObject(DayPlan.class));
                    }
                    onSuccess.onSuccess(dayPlans);
                })
                .addOnFailureListener(onFailure);
    }

    // Метод для сохранения планов дня пользователя
    public void saveUserDayPlans(String userId, List<DayPlan> weekPlans, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        CollectionReference userDayPlansCollection = _db.collection("users").document(userId).collection("dayPlans");
        CountDownLatch latch = new CountDownLatch(weekPlans.size());
        Handler handler = new Handler(Looper.getMainLooper());

        for (DayPlan dayPlan : weekPlans) {
            userDayPlansCollection.add(dayPlan)
                    .addOnSuccessListener(aVoid -> {
                        latch.countDown();
                    })
                    .addOnFailureListener(e -> {
                        latch.countDown();
                        handler.post(() -> onFailureListener.onFailure(e));
                    });
        }

        // Запуск потока для ожидания завершения добавления всех планов
        new Thread(() -> {
            try {
                latch.await();
                handler.post(() -> onSuccessListener.onSuccess(null));
            } catch (InterruptedException e) {
                handler.post(() -> onFailureListener.onFailure(e));
            }
        }).start();
    }

    // Метод для получения планов дня пользователя по дню недели
    public void getUserDayPlans(String userId, DayPlan.WeekDay weekDay, OnSuccessListener<List<DayPlan>> onSuccessListener, OnFailureListener onFailureListener) {
        CollectionReference userDayPlansCollection = _db.collection("users").document(userId).collection("dayPlans");

        userDayPlansCollection.whereEqualTo("weekDay", weekDay.toString())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DayPlan> dayPlans = queryDocumentSnapshots.toObjects(DayPlan.class);
                        onSuccessListener.onSuccess(dayPlans);
                    } else {
                        onSuccessListener.onSuccess(Collections.emptyList());
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    // Метод для обновления статуса завершенности плана дня
    public void updateDayPlanCompletion(String userId, DayPlan.WeekDay weekDay, boolean completed, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        getDayPlanByWeekDay(userId, weekDay, new OnSuccessListener<DayPlan>() {
            @Override
            public void onSuccess(DayPlan dayPlan) {
                if (dayPlan != null) {
                    _db.collection("users")
                            .document(userId)
                            .collection("dayPlans")
                            .document(dayPlan.getId())
                            .update("completed", completed)
                            .addOnSuccessListener(successListener)
                            .addOnFailureListener(e -> {
                                Log.e("FirestoreUpdate", "Ошибка при обновлении статуса completed: " + e.getMessage());
                                failureListener.onFailure(e);
                            });
                } else {
                    Log.e("FirestoreUpdate", "План дня не найден для указанного дня недели");
                    failureListener.onFailure(new Exception("No day plan found for the specified week day"));
                }
            }
        }, failureListener);
    }


    public void markExerciseAsCompleted(String userId, String weekDay, String
            exerciseId, float timeElapsed, OnSuccessListener<
            Void> onSuccessListener, OnFailureListener onFailureListener) {
        CollectionReference userDayPlansCollection = _db.collection("users").document(userId).collection("dayPlans");

        userDayPlansCollection.whereEqualTo("id", weekDay)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        List<Map<String, Object>> exercises = (List<Map<String, Object>>) document.get("exercises");
                        if (exercises != null) {
                            for (Map<String, Object> exercise : exercises) {
                                if (exercise.get("id").equals(exerciseId)) {
                                    exercise.put("completed", true);
                                    exercise.put("completedTime", timeElapsed);
                                    break;
                                }
                            }

                            document.getReference().update("exercises", exercises)
                                    .addOnSuccessListener(onSuccessListener)
                                    .addOnFailureListener(onFailureListener);
                        } else {
                            onFailureListener.onFailure(new Exception("Exercises not found"));
                        }
                    } else {
                        onFailureListener.onFailure(new Exception("No day plan found for the specified week day"));
                    }
                })
                .addOnFailureListener(onFailureListener);
    }


}
