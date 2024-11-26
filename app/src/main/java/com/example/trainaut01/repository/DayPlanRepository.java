package com.example.trainaut01.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.trainaut01.enums.WeekDay;
import com.example.trainaut01.models.DayPlan;
import com.example.trainaut01.models.Exercise;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

public class DayPlanRepository{
    private final FirebaseFirestore _db;
    private final CollectionReference _collection;

    @Inject
    public DayPlanRepository(FirebaseFirestore db) {
        this._db = db;
        this._collection = db.collection("dayPlans");
    }

    public void add(DayPlan dayPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.document(dayPlan.getDayPlanId())
                .set(dayPlan)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void addAllToUser(String userId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("dayPlans")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        DayPlan dayPlan = document.toObject(DayPlan.class);
                        if (dayPlan != null) {
                            dayPlan.setDayPlanId(document.getId());

                            DocumentReference userDayPlanRef = db.collection("users")
                                    .document(userId)
                                    .collection("dayPlans")
                                    .document(dayPlan.getDayPlanId());
                            batch.set(userDayPlanRef, dayPlan.toMap());
                        }
                    }

                    batch.commit()
                            .addOnSuccessListener(onSuccess)
                            .addOnFailureListener(onFailure);
                })
                .addOnFailureListener(onFailure);
    }


    public void updateForUser(String userId, String dayPlanId, Map<String, Object> updatedFields,
                       OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("dayPlans")
                .document(dayPlanId)
                .update(updatedFields)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


    public void deleteForUser(String userId, String dayPlanId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("dayPlans")
                .document(dayPlanId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


    public void get(String userId, String dayPlanId, OnSuccessListener<DayPlan> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("dayPlans")
                .document(dayPlanId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        DayPlan dayPlan = documentSnapshot.toObject(DayPlan.class);
                        if (dayPlan != null) {
                            dayPlan.setDayPlanId(dayPlanId);
                            onSuccess.onSuccess(dayPlan);
                        } else {
                            onFailure.onFailure(new Exception("Документ не удалось преобразовать в DayPlan."));
                        }
                    } else {
                        onFailure.onFailure(new Exception("Документ не найден."));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public void getAll(OnSuccessListener<List<DayPlan>> onSuccess, OnFailureListener onFailure) {
        _collection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DayPlan> dayPlans = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        dayPlans.add(document.toObject(DayPlan.class));
                    }
                    onSuccess.onSuccess(dayPlans);
                })
                .addOnFailureListener(onFailure);
    }

    public void getAllForUser(String userId, OnSuccessListener<List<DayPlan>> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("dayPlans")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<DayPlan> dayPlans = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        DayPlan dayPlan = document.toObject(DayPlan.class);
                        if (dayPlan != null) {
                            dayPlan.setDayPlanId(document.getId());
                            dayPlans.add(dayPlan);
                        }
                    }
                    onSuccess.onSuccess(dayPlans);
                })
                .addOnFailureListener(onFailure);
    }


    public void updateExerciseCompletedTime(
            String userId,
            String dayPlanId,
            String exercisePath, // Путь до конкретного упражнения в DayPlan
            float completedTime,
            OnSuccessListener<Void> onSuccess,
            OnFailureListener onFailure) {

        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put(exercisePath + ".completedTime", completedTime); // Путь до поля completedTime

        updateForUser(userId, dayPlanId, updatedFields, onSuccess, onFailure);
    }


    public void getExercisesForUserAndDay(String userId, String dayOfWeek, OnSuccessListener<List<Exercise>> onSuccess, OnFailureListener onFailure) {

        _db.collection("users").document(userId)
                .collection("dayPlans")  // Используем подколлекцию dayPlans
                .whereEqualTo("id", dayOfWeek)  // Фильтруем по полю id
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Log.d("DayPlanRepository", "План дня найден");
                        Map<String, Object> dayPlan = queryDocumentSnapshots.getDocuments().get(0).getData();
                        List<Map<String, Object>> exercisesMapList = (List<Map<String, Object>>) dayPlan.get("exercises");
                        List<Exercise> exercises = new ArrayList<>();

                        if (exercisesMapList != null) {
                            for (Map<String, Object> exerciseMap : exercisesMapList) {
                                Exercise exercise = Exercise.initializeFromMap(exerciseMap);
                                exercises.add(exercise);
                            }
                        }
                        onSuccess.onSuccess(exercises);
                    } else {
                        Log.d("DayPlanRepository", "План дня не найден для дня недели: " + dayOfWeek);
                        onSuccess.onSuccess(Collections.emptyList());
                    }
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


//    public void markExerciseAsCompleted(String userId, String weekDay, String exerciseId,
//                                        float timeElapsed, OnSuccessListener<Void> onSuccessListener,
//                                        OnFailureListener onFailureListener) {
//        CollectionReference userDayPlansCollection = _db.collection("users").document(userId)
//                .collection("dayPlans");
//        Log.d("markExercise", "weekDay: " + weekDay);
//
//
//        userDayPlansCollection.whereEqualTo("weekDay", weekDay)
//                .limit(1)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
//
//                        List<Map<String, Object>> exercises = (List<Map<String, Object>>) document.get("exercises");
//                        if (exercises != null) {
//                            for (Map<String, Object> exercise : exercises) {
//                                if (exercise.get("id").equals(exerciseId)) {
//                                    exercise.put("completed", true);
//                                    exercise.put("completedTime", timeElapsed);
//                                    break;
//                                }
//                            }
//
//                            document.getReference().set(Collections.singletonMap("exercises", exercises), SetOptions.merge())
//                                    .addOnSuccessListener(onSuccessListener)
//                                    .addOnFailureListener(onFailureListener);
//                        } else {
//                            onFailureListener.onFailure(new Exception("Упражнения не найдены"));
//                        }
//                    } else {
//                        onFailureListener.onFailure(new Exception("План на день не найден для указанного дня недели"));
//                    }
//                })
//                .addOnFailureListener(onFailureListener);
//    }

}
