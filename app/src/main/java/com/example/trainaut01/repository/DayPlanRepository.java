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
    private final FirebaseFirestore _db; // Экземпляр Firestore
    private final CollectionReference _collection; // Ссылка на коллекцию dayPlans

    @Inject
    public DayPlanRepository(FirebaseFirestore db) {
        this._db = db;
        this._collection = db.collection("dayPlans"); // Инициализация коллекции
    }

    // Метод для получения коллекции
    public CollectionReference getWeekPlansCollection() {
        return _db.collection("dayPlans");
    }

    // Метод для добавления нового плана дня
    @Override
    public void add(DayPlan dayPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.add(dayPlan) // Добавляем план дня в коллекцию
                .addOnSuccessListener(docRef -> onSuccess.onSuccess(null)) // Успешное добавление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для обновления существующего плана дня по ID
    @Override
    public void update(String id, DayPlan dayPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).set(dayPlan) // Обновляем документ с указанным ID
                .addOnSuccessListener(onSuccess) // Успешное обновление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для удаления плана дня по ID
    @Override
    public void delete(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).delete() // Удаляем документ с указанным ID
                .addOnSuccessListener(onSuccess) // Успешное удаление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для получения одного плана дня по ID
    @Override
    public void get(String id, OnSuccessListener<DayPlan> onSuccess, OnFailureListener onFailure) {
        _collection.document(id).get()
                .addOnSuccessListener(doc -> {
                    DayPlan dayPlan = doc.toObject(DayPlan.class); // Преобразуем документ в объект DayPlan
                    onSuccess.onSuccess(dayPlan); // Успешное получение
                })
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для получения одного плана дня по дню недели и ID пользователя
    public void getDayPlanByWeekDay(String userId, DayPlan.WeekDay weekDay, OnSuccessListener<DayPlan> onSuccessListener, OnFailureListener onFailureListener) {
        // Ссылка на коллекцию dayPlans конкретного пользователя
        CollectionReference userDayPlansCollection = _db.collection("users").document(userId).collection("dayPlans");

        // Запрос для получения плана, соответствующего конкретному дню недели
        userDayPlansCollection.whereEqualTo("weekDay", weekDay.toString())
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Получаем первый документ
                        DayPlan dayPlan = queryDocumentSnapshots.getDocuments().get(0).toObject(DayPlan.class);
                        dayPlan.setId(queryDocumentSnapshots.getDocuments().get(0).getId()); // Устанавливаем ID
                        onSuccessListener.onSuccess(dayPlan);
                    } else {
                        onSuccessListener.onSuccess(null); // Если план не найден
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    // Метод для получения всех планов дня из коллекции
    @Override
    public void getAll(OnSuccessListener<List<DayPlan>> onSuccess, OnFailureListener onFailure) {
        _collection.get() // Получаем все документы из коллекции
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DayPlan> dayPlans = new ArrayList<>(); // Список для хранения полученных планов дня
                    // Итерация по всем документам в коллекции
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        // Преобразуем каждый документ в объект DayPlan и добавляем в список
                        dayPlans.add(document.toObject(DayPlan.class));
                    }
                    onSuccess.onSuccess(dayPlans); // Успешное получение всех планов дня
                })
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для сохранения планов дня пользователя
    public void saveUserDayPlans(String userId, List<DayPlan> weekPlans, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        CollectionReference userDayPlansCollection = _db.collection("users").document(userId).collection("dayPlans");
        CountDownLatch latch = new CountDownLatch(weekPlans.size()); // Синхронизация потоков
        Handler handler = new Handler(Looper.getMainLooper()); // Создаем Handler для основного потока

        for (DayPlan dayPlan : weekPlans) {
            userDayPlansCollection.add(dayPlan)
                    .addOnSuccessListener(aVoid -> {
                        latch.countDown(); // Уменьшаем счетчик при успешном добавлении
                    })
                    .addOnFailureListener(e -> {
                        latch.countDown(); // Уменьшаем счетчик при ошибке
                        handler.post(() -> onFailureListener.onFailure(e)); // Вызов onFailure на основном потоке
                    });
        }

        // Запуск потока для ожидания завершения добавления всех планов
        new Thread(() -> {
            try {
                latch.await(); // Ожидаем завершения всех добавлений
                handler.post(() -> onSuccessListener.onSuccess(null)); // Успешное завершение на основном потоке
            } catch (InterruptedException e) {
                handler.post(() -> onFailureListener.onFailure(e)); // Обработка прерывания
            }
        }).start();
    }

    // Метод для получения планов дня пользователя по дню недели
    public void getUserDayPlans(String userId, DayPlan.WeekDay weekDay, OnSuccessListener<List<DayPlan>> onSuccessListener, OnFailureListener onFailureListener) {
        // Ссылка на коллекцию dayPlans конкретного пользователя
        CollectionReference userDayPlansCollection = _db.collection("users").document(userId).collection("dayPlans");

        // Запрос для получения планов, соответствующих конкретному дню недели
        userDayPlansCollection.whereEqualTo("weekDay", weekDay.toString())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Преобразуем документы в объекты DayPlan
                        List<DayPlan> dayPlans = queryDocumentSnapshots.toObjects(DayPlan.class);
                        onSuccessListener.onSuccess(dayPlans);
                    } else {
                        onSuccessListener.onSuccess(Collections.emptyList()); // Возвращаем пустой список
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    // Метод для обновления статуса завершенности плана дня
    public void updateDayPlanCompletion(String userId, DayPlan.WeekDay weekDay, boolean completed, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        // Получаем план дня по дню недели
        getDayPlanByWeekDay(userId, weekDay, new OnSuccessListener<DayPlan>() {
            @Override
            public void onSuccess(DayPlan dayPlan) {
                if (dayPlan != null) {
                    // Если план найден, обновляем его поле completed
                    _db.collection("users")
                            .document(userId)
                            .collection("dayPlans")
                            .document(dayPlan.getId()) // Используем ID плана дня
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
        // Ссылка на коллекцию dayPlans конкретного пользователя
        CollectionReference userDayPlansCollection = _db.collection("users").document(userId).collection("dayPlans");

        // Запрос для получения плана, соответствующего конкретному дню недели
        userDayPlansCollection.whereEqualTo("id", weekDay)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        // Получаем массив exercises
                        List<Map<String, Object>> exercises = (List<Map<String, Object>>) document.get("exercises");
                        if (exercises != null) {
                            // Ищем нужное упражнение по ID
                            for (Map<String, Object> exercise : exercises) {
                                if (exercise.get("id").equals(exerciseId)) {
                                    // Обновляем состояние completed
                                    exercise.put("completed", true);
                                    exercise.put("completedTime", timeElapsed);
                                    break;
                                }
                            }

                            // Сохраняем обновленный массив обратно в Firestore
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
