package com.example.trainaut01.repository;

import android.os.Handler;
import android.os.Looper;

import com.example.trainaut01.models.DayPlan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

// Репозиторий для работы с планами дня в Firebase Firestore
public class DayPlanRepository implements Repository<DayPlan> {
    private final FirebaseFirestore db;
    private final CollectionReference collection;

    @Inject
    public DayPlanRepository(FirebaseFirestore db) {
        this.db = db;
        this.collection = db.collection("dayPlans");
    }

    // Публичный метод для получения коллекции
    public CollectionReference getWeekPlansCollection() {
        return db.collection("dayPlans");
    }


    // Метод для добавления нового плана дня
    @Override
    public void add(DayPlan dayPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        collection.add(dayPlan) // Добавляем план дня в коллекцию
                .addOnSuccessListener(docRef -> onSuccess.onSuccess(null)) // Успешное добавление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для обновления существующего плана дня по ID
    @Override
    public void update(String id, DayPlan dayPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        collection.document(id).set(dayPlan) // Обновляем документ с указанным ID
                .addOnSuccessListener(onSuccess) // Успешное обновление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для удаления плана дня по ID
    @Override
    public void delete(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        collection.document(id).delete() // Удаляем документ с указанным ID
                .addOnSuccessListener(onSuccess) // Успешное удаление
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для получения одного плана дня по ID
    @Override
    public void get(String id, OnSuccessListener<DayPlan> onSuccess, OnFailureListener onFailure) {
        collection.document(id).get() // Получаем документ с указанным ID
                .addOnSuccessListener(doc -> {
                    // Преобразуем документ в объект DayPlan
                    DayPlan dayPlan = doc.toObject(DayPlan.class);
                    onSuccess.onSuccess(dayPlan); // Успешное получение
                })
                .addOnFailureListener(onFailure); // Обработка ошибки
    }

    // Метод для получения всех планов дня из коллекции
    @Override
    public void getAll(OnSuccessListener<List<DayPlan>> onSuccess, OnFailureListener onFailure) {
        collection.get() // Получаем все документы из коллекции
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



    public void saveUserDayPlans(String userId, List<DayPlan> weekPlans, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        CollectionReference userDayPlansCollection = db.collection("users").document(userId).collection("dayPlans");
        CountDownLatch latch = new CountDownLatch(weekPlans.size());
        Handler handler = new Handler(Looper.getMainLooper()); // Создаем Handler для основного потока

        for (DayPlan dayPlan : weekPlans) {
            userDayPlansCollection.add(dayPlan)
                    .addOnSuccessListener(aVoid -> {
                        latch.countDown();
                    })
                    .addOnFailureListener(e -> {
                        latch.countDown();
                        handler.post(() -> onFailureListener.onFailure(e)); // Вызов onFailure на основном потоке
                    });
        }

        new Thread(() -> {
            try {
                latch.await();
                handler.post(() -> onSuccessListener.onSuccess(null)); // Успешное завершение на основном потоке
            } catch (InterruptedException e) {
                handler.post(() -> onFailureListener.onFailure(e));
            }
        }).start();
    }

    public void getUserDayPlans(String userId, DayPlan.WeekDay weekDay, OnSuccessListener<List<DayPlan>> onSuccessListener, OnFailureListener onFailureListener) {
        // Ссылка на коллекцию dayPlans конкретного пользователя
        CollectionReference userDayPlansCollection = db.collection("users").document(userId).collection("dayPlans");

        // Запрос для получения планов, соответствующих конкретному дню недели
        userDayPlansCollection.whereEqualTo("weekDay", weekDay.toString())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Преобразуем документы в объекты DayPlan
                        List<DayPlan> dayPlans = queryDocumentSnapshots.toObjects(DayPlan.class);
                        onSuccessListener.onSuccess(dayPlans);
                    } else {
                        onSuccessListener.onSuccess(Collections.emptyList());
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

}
