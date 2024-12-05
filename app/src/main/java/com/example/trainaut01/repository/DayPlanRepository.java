package com.example.trainaut01.repository;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Репозиторий для работы с объектами DayPlan в Firestore.
 * Этот класс предоставляет методы для выполнения CRUD операций с дневными планами,
 * включая получение, добавление, обновление и удаление данных.
 */
public class DayPlanRepository {

    private static final String TAG = "DayPlanRepository";

    private final FirebaseFirestore _db;
    private final CollectionReference _collection;

    /**
     * Конструктор класса DayPlanRepository.
     *
     * @param db экземпляр FirebaseFirestore для взаимодействия с базой данных Firestore.
     */
    @Inject
    public DayPlanRepository(FirebaseFirestore db) {
        this._db = db;
        this._collection = db.collection("dayPlans");
    }

    /**
     * Добавляет дневной план в Firestore.
     *
     * @param dayPlan   объект DayPlan, который необходимо добавить.
     * @param onSuccess callback, вызываемый при успешной операции.
     * @param onFailure callback, вызываемый при ошибке операции.
     */
    public void addDayPlan(DayPlan dayPlan, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.document(dayPlan.getDayPlanId())
                .set(dayPlan)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Копирует все доступные дневные планы в профиль пользователя.
     *
     * @param userId    идентификатор пользователя.
     * @param onSuccess callback, вызываемый при успешной операции.
     * @param onFailure callback, вызываемый при ошибке операции.
     */
    public void addAllDayPlansToUser(String userId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        _collection.get()
                .addOnSuccessListener(querySnapshot -> copyDayPlansToUser(userId, querySnapshot.getDocuments(), onSuccess, onFailure))
                .addOnFailureListener(onFailure);
    }

    /**
     * Получает дневной план для указанного пользователя и идентификатора плана.
     *
     * @param userId    идентификатор пользователя.
     * @param dayPlanId идентификатор дневного плана.
     * @param onSuccess callback, вызываемый при успешной операции с полученным объектом DayPlan.
     * @param onFailure callback, вызываемый при ошибке операции.
     */
    public void getDayPlan(String userId, String dayPlanId, OnSuccessListener<DayPlan> onSuccess, OnFailureListener onFailure) {
        DocumentReference dayPlanRef = getUserDayPlanReference(userId, dayPlanId);

        dayPlanRef.get()
                .addOnSuccessListener(documentSnapshot -> handleDayPlanDocument(documentSnapshot, onSuccess, onFailure))
                .addOnFailureListener(onFailure);
    }

    /**
     * Получает список всех доступных дневных планов.
     *
     * @param onSuccess callback, вызываемый при успешной операции с полученным списком дневных планов.
     * @param onFailure callback, вызываемый при ошибке операции.
     */
    public void getAllDayPlans(OnSuccessListener<List<DayPlan>> onSuccess, OnFailureListener onFailure) {
        _collection.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<DayPlan> dayPlans = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        dayPlans.add(document.toObject(DayPlan.class));
                    }
                    onSuccess.onSuccess(dayPlans);
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Обновляет время завершения упражнения в дневном плане.
     *
     * @param userId       идентификатор пользователя.
     * @param dayPlanId    идентификатор дневного плана.
     * @param exerciseId   идентификатор упражнения.
     * @param timeElapsed  время, затраченное на выполнение упражнения.
     * @param onSuccess    callback, вызываемый при успешной операции.
     * @param onFailure    callback, вызываемый при ошибке операции.
     */
    public void updateExerciseCompletedTime(String userId, String dayPlanId, String exerciseId, float timeElapsed,
                                            OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        DocumentReference dayPlanRef = getUserDayPlanReference(userId, dayPlanId);

        dayPlanRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        handleExerciseTimeUpdate(documentSnapshot, exerciseId, timeElapsed, dayPlanRef, onSuccess, onFailure);
                    } else {
                        onFailure.onFailure(new Exception("Документ плана дня не найден."));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Получает дневной план для пользователя на определенный день недели.
     *
     * @param userId    идентификатор пользователя.
     * @param dayOfWeek день недели, для которого требуется получить план.
     * @param onSuccess callback, вызываемый при успешной операции с полученным объектом DayPlan.
     * @param onFailure callback, вызываемый при ошибке операции.
     */
    public void getDayPlanForUserAndDay(String userId, String dayOfWeek, OnSuccessListener<DayPlan> onSuccess, OnFailureListener onFailure) {
        DocumentReference dayPlanRef = getUserDayPlanReference(userId, dayOfWeek.toLowerCase());

        dayPlanRef.get()
                .addOnSuccessListener(documentSnapshot -> handleDayPlanDocument(documentSnapshot, onSuccess, onFailure))
                .addOnFailureListener(onFailure);
    }


    /**
     * Копирует дневные планы в профиль пользователя.
     *
     * @param userId    идентификатор пользователя.
     * @param documents список документов дневных планов.
     * @param onSuccess callback, вызываемый при успешной операции.
     * @param onFailure callback, вызываемый при ошибке операции.
     */
    private void copyDayPlansToUser(String userId, List<DocumentSnapshot> documents, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        WriteBatch batch = _db.batch();
        for (DocumentSnapshot document : documents) {
            DayPlan dayPlan = document.toObject(DayPlan.class);
            if (dayPlan != null) {
                dayPlan.setDayPlanId(document.getId());
                DocumentReference userDayPlanRef = getUserDayPlanReference(userId, dayPlan.getDayPlanId());
                batch.set(userDayPlanRef, dayPlan.toMap());
            }
        }
        batch.commit().addOnSuccessListener(onSuccess).addOnFailureListener(onFailure);
    }

    /**
     * Обрабатывает обновление времени выполнения упражнения в дневном плане.
     *
     * @param documentSnapshot документ дневного плана.
     * @param exerciseId       идентификатор упражнения.
     * @param timeElapsed      время, затраченное на выполнение упражнения.
     * @param dayPlanRef       ссылка на документ дневного плана.
     * @param onSuccess        callback, вызываемый при успешной операции.
     * @param onFailure        callback, вызываемый при ошибке операции.
     */
    private void handleExerciseTimeUpdate(DocumentSnapshot documentSnapshot, String exerciseId, float timeElapsed,
                                          DocumentReference dayPlanRef, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        DayPlan dayPlan = documentSnapshot.toObject(DayPlan.class);
        if (dayPlan == null || dayPlan.getExercisesGrossMotor() == null) {
            onFailure.onFailure(new Exception("Массив упражнений пуст или отсутствует."));
            return;
        }

        if (updateExerciseTime(dayPlan.getExercisesGrossMotor(), exerciseId, timeElapsed)) {
            saveUpdatedDayPlan(dayPlanRef, dayPlan.getExercisesGrossMotor(), onSuccess, onFailure);
        } else {
            onFailure.onFailure(new Exception("Упражнение с указанным ID не найдено."));
        }
    }

    /**
     * Обновляет время выполнения для указанного упражнения.
     *
     * @param exercises   список упражнений.
     * @param exerciseId  идентификатор упражнения.
     * @param timeElapsed время, затраченное на выполнение упражнения.
     * @return true, если упражнение найдено и обновлено; false, если упражнение не найдено.
     */
    private boolean updateExerciseTime(List<Exercise> exercises, String exerciseId, float timeElapsed) {
        for (Exercise exercise : exercises) {
            if (exercise.getId().equals(exerciseId)) {
                exercise.setCompletedTime(timeElapsed);
                return true;
            }
        }
        return false;
    }

    /**
     * Сохраняет обновленный дневной план в Firestore.
     *
     * @param dayPlanRef       ссылка на документ дневного плана.
     * @param updatedExercises обновленный список упражнений.
     * @param onSuccess        callback, вызываемый при успешной операции.
     * @param onFailure        callback, вызываемый при ошибке операции.
     */
    private void saveUpdatedDayPlan(DocumentReference dayPlanRef, List<Exercise> updatedExercises,
                                    OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("exercisesGrossMotor", updatedExercises);

        dayPlanRef.set(updatedData, SetOptions.merge())
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Обрабатывает получение документа дневного плана и преобразует его в объект DayPlan.
     *
     * @param documentSnapshot документ дневного плана.
     * @param onSuccess        callback, вызываемый при успешной операции с объектом DayPlan.
     * @param onFailure        callback, вызываемый при ошибке операции.
     */
    private void handleDayPlanDocument(DocumentSnapshot documentSnapshot, OnSuccessListener<DayPlan> onSuccess, OnFailureListener onFailure) {
        if (documentSnapshot.exists()) {
            DayPlan dayPlan = parseDayPlanDocument(documentSnapshot);
            if (dayPlan != null) {
                onSuccess.onSuccess(dayPlan);
            } else {
                onFailure.onFailure(new Exception("Не удалось преобразовать документ в объект DayPlan."));
            }
        } else {
            onFailure.onFailure(new Exception("Документ плана дня не найден."));
        }
    }

    /**
     * Преобразует документ дневного плана в объект DayPlan.
     *
     * @param documentSnapshot документ дневного плана.
     * @return объект DayPlan или null, если преобразование не удалось.
     */
    private DayPlan parseDayPlanDocument(DocumentSnapshot documentSnapshot) {
        DayPlan dayPlan = documentSnapshot.toObject(DayPlan.class);
        if (dayPlan != null) {
            dayPlan.setDayPlanId(documentSnapshot.getId());
        }
        return dayPlan;
    }

    /**
     * Получает ссылку на документ дневного плана для указанного пользователя и идентификатора плана.
     *
     * @param userId    идентификатор пользователя.
     * @param dayPlanId идентификатор дневного плана.
     * @return ссылка на документ дневного плана.
     */
    private DocumentReference getUserDayPlanReference(String userId, String dayPlanId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId не может быть пустым.");
        }
        if (dayPlanId == null || dayPlanId.isEmpty()) {
            throw new IllegalArgumentException("DayPlanId не может быть пустым.");
        }

        return _db.collection("users")
                .document(userId)
                .collection("dayPlans")
                .document(dayPlanId);
    }


}
