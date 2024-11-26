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

    public void addForUser(String userId, DayPlan dayPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> dayPlanData = dayPlan.toMap();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("dayPlans")
                .document(dayPlan.getDayPlanId())
                .set(dayPlanData)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void update(String userId, String dayPlanId, Map<String, Object> updatedFields,
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


    public void delete(String userId, String dayPlanId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
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



    //
//    // Метод для получения одного плана дня по дню недели и ID пользователя
//    public void getDayPlanByWeekDay(String userId, WeekDay weekDay, OnSuccessListener<DayPlan> onSuccessListener, OnFailureListener onFailureListener) {
//        Log.d("DayPlanRepository", "Запрос плана дня для пользователя: " + userId + ", день недели: " + weekDay);
//        CollectionReference userDayPlansCollection = _db.collection("users").document(userId).collection("dayPlans");
//
//        userDayPlansCollection.whereEqualTo("weekDay", weekDay.toString())
//                .limit(1)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    Log.d("DayPlanRepository", "Запрос успешно выполнен. Найдено документов: " + queryDocumentSnapshots.size());
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        DayPlan dayPlan = queryDocumentSnapshots.getDocuments().get(0).toObject(DayPlan.class);
//                        dayPlan.setId(queryDocumentSnapshots.getDocuments().get(0).getId());
//                        Log.d("DayPlanRepository", "План дня загружен: " + dayPlan);
//                        onSuccessListener.onSuccess(dayPlan);
//                    } else {
//                        Log.d("DayPlanRepository", "План дня не найден для дня недели: " + weekDay);
//                        onSuccessListener.onSuccess(null);
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("DayPlanRepository", "Ошибка при получении плана дня: " + e.getMessage());
//                    onFailureListener.onFailure(e);
//                });
//    }
//
    // Метод для получения всех планов дня из коллекции
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

    public void getExercisesForUserAndDay(String userId, String dayOfWeek, OnSuccessListener<List<Exercise>> onSuccess, OnFailureListener onFailure) {
        Log.d("DayPlanRepository", "Запрос плана дня для пользователя: " + userId + ", день недели: " + dayOfWeek);
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

    public void saveCurrentExerciseProgress(String userId, String day, int currentExerciseIndex, float timeElapsed,
                                            OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        Map<String, Object> progressData = new HashMap<>();
        progressData.put("currentExerciseIndex", currentExerciseIndex);
        progressData.put("timeElapsed", timeElapsed);

        _db.collection("users")
                .document(userId)
                .collection("dayPlans")
                .document(day)
                .update(progressData)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    //
//    // Метод для получения планов дня пользователя по дню недели
//    public void getUserDayPlans(String userId, WeekDay weekDay, OnSuccessListener<List<DayPlan>> onSuccessListener, OnFailureListener onFailureListener) {
//        CollectionReference userDayPlansCollection = _db.collection("users").document(userId).collection("dayPlans");
//
//        userDayPlansCollection.whereEqualTo("weekDay", weekDay.toString())
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        List<DayPlan> dayPlans = queryDocumentSnapshots.toObjects(DayPlan.class);
//                        onSuccessListener.onSuccess(dayPlans);
//                    } else {
//                        onSuccessListener.onSuccess(Collections.emptyList());
//                    }
//                })
//                .addOnFailureListener(onFailureListener);
//    }
//
//
//
//
//
//    // Метод для обновления статуса завершенности плана дня
//    public void updateDayPlanCompletion(String userId, WeekDay weekDay, boolean completed, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
//        getDayPlanByWeekDay(userId, weekDay, new OnSuccessListener<DayPlan>() {
//            @Override
//            public void onSuccess(DayPlan dayPlan) {
//                if (dayPlan != null) {
//                    _db.collection("users")
//                            .document(userId)
//                            .collection("dayPlans")
//                            .document(dayPlan.getId())
//                            .update("completed", completed)
//                            .addOnSuccessListener(successListener)
//                            .addOnFailureListener(e -> {
//                                Log.e("FirestoreUpdate", "Ошибка при обновлении статуса completed: " + e.getMessage());
//                                failureListener.onFailure(e);
//                            });
//                } else {
//                    Log.e("FirestoreUpdate", "План дня не найден для указанного дня недели");
//                    failureListener.onFailure(new Exception("No day plan found for the specified week day"));
//                }
//            }
//        }, failureListener);
//    }
//
//
    public void markExerciseAsCompleted(String userId, String weekDay, String exerciseId,
                                        float timeElapsed, OnSuccessListener<Void> onSuccessListener,
                                        OnFailureListener onFailureListener) {
        CollectionReference userDayPlansCollection = _db.collection("users").document(userId)
                .collection("dayPlans");
        Log.d("markExercise", "weekDay: " + weekDay);


        userDayPlansCollection.whereEqualTo("weekDay", weekDay)
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

                            document.getReference().set(Collections.singletonMap("exercises", exercises), SetOptions.merge())
                                    .addOnSuccessListener(onSuccessListener)
                                    .addOnFailureListener(onFailureListener);
                        } else {
                            onFailureListener.onFailure(new Exception("Упражнения не найдены"));
                        }
                    } else {
                        onFailureListener.onFailure(new Exception("План на день не найден для указанного дня недели"));
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

}
