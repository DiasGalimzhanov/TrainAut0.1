package com.example.trainaut01.repository;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import com.google.android.gms.tasks.OnFailureListener;

/**
 * Репозиторий для управления данными прогресса ребенка в Firestore Storage.
 */
public class ChildProgressRepository {
    private final FirebaseStorage _firebaseStorage;

    /**
     * Конструктор, инициализирующий Firebase Storage.
     */
    public ChildProgressRepository() {
        this._firebaseStorage = FirebaseStorage.getInstance();
    }

    /**
     * Сохраняет прогресс ребенка.
     *
     * @param userId           идентификатор пользователя.
     * @param year             год прогресса.
     * @param month            месяц прогресса.
     * @param completedDays    список завершенных дней.
     * @param onSuccessListener callback при успешном выполнении.
     * @param onFailureListener callback при ошибке.
     */
    public void saveChildProgress(String userId, String year, String month, List<Integer> completedDays, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference userProgressRef = getChildProgressReference(userId);

        getChildProgress(userProgressRef, progressData -> {
            JSONObject updatedProgress = createOrUpdateProgressObject(progressData, year, month, completedDays);
            uploadProgress(userProgressRef, updatedProgress, onSuccessListener, onFailureListener);
        }, e -> {
            if (isFileNotFound(e)) {
                JSONObject newProgress = createNewProgressObject(year, month, completedDays);
                uploadProgress(userProgressRef, newProgress, onSuccessListener, onFailureListener);
            } else {
                onFailureListener.onFailure(e);
            }
        });
    }

    /**
     * Получает ссылку на файл прогресса пользователя в Firestore Storage.
     *
     * @param userId идентификатор пользователя.
     * @return ссылка на файл прогресса.
     */
    private StorageReference getChildProgressReference(String userId) {
        StorageReference storageRef = _firebaseStorage.getReference();
        return storageRef.child("child_progress/" + userId + "/progress.json");
    }

    /**
     * Загружает текущий прогресс ребенка из Firestore Storage.
     *
     * @param userProgressRef   ссылка на файл прогресса.
     * @param onSuccessListener callback при успешном выполнении.
     * @param onFailureListener callback при ошибке.
     */
    private void getChildProgress(StorageReference userProgressRef, OnSuccessListener<JSONObject> onSuccessListener, OnFailureListener onFailureListener) {
        userProgressRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            try {
                String jsonStr = new String(bytes);
                JSONObject jsonObject = new JSONObject(jsonStr);
                onSuccessListener.onSuccess(jsonObject);
            } catch (Exception e) {
                onFailureListener.onFailure(e);
            }
        }).addOnFailureListener(onFailureListener);
    }

    /**
     * Создает или обновляет объект прогресса.
     *
     * @param progressData   текущие данные прогресса.
     * @param year           год.
     * @param month          месяц.
     * @param completedDays  список завершенных дней.
     * @return обновленный объект прогресса.
     */
    private JSONObject createOrUpdateProgressObject(JSONObject progressData, String year, String month, List<Integer> completedDays) {
        try {
            JSONArray progressArray = progressData.getJSONArray("progress");
            boolean isMonthFound = false;

            for (int i = 0; i < progressArray.length(); i++) {
                JSONObject monthProgress = progressArray.getJSONObject(i);
                String existingYear = monthProgress.getString("year");
                String existingMonth = monthProgress.getString("month");

                if (existingYear.equals(year) && existingMonth.equals(month)) {
                    JSONArray existingCompletedDays = monthProgress.getJSONArray("completedDays");
                    for (int day : completedDays) {
                        if (!contains(existingCompletedDays, day)) {
                            existingCompletedDays.put(day);
                        }
                    }
                    monthProgress.put("completedDays", existingCompletedDays);
                    isMonthFound = true;
                    break;
                }
            }

            if (!isMonthFound) {
                JSONObject newMonthProgress = new JSONObject();
                newMonthProgress.put("year", year);
                newMonthProgress.put("month", month);
                newMonthProgress.put("completedDays", new JSONArray(completedDays));
                progressArray.put(newMonthProgress);
            }

            progressData.put("progress", progressArray);
            return progressData;

        } catch (Exception e) {
            Log.e("createOrUpdateProgress", "Не удалось обновить объект прогресса: " + e.getMessage(), e);
            throw new RuntimeException("Failed to update progress object", e);
        }
    }

    /**
     * Создает новый объект прогресса.
     *
     * @param year          год.
     * @param month         месяц.
     * @param completedDays список завершенных дней.
     * @return объект прогресса.
     */
    private JSONObject createNewProgressObject(String year, String month, List<Integer> completedDays) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray progressArray = new JSONArray();

            JSONObject newMonthProgress = new JSONObject();
            newMonthProgress.put("year", year);
            newMonthProgress.put("month", month);
            newMonthProgress.put("completedDays", new JSONArray(completedDays));

            progressArray.put(newMonthProgress);
            jsonObject.put("progress", progressArray);
            return jsonObject;

        } catch (Exception e) {
            Log.e("createNewProgressObject", "Не удалось создать новый объект прогресса: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create new progress object", e);
        }
    }

    /**
     * Загружает данные прогресса ребенка из Firestore Storage.
     *
     * @param userProgressRef   ссылка на файл прогресса.
     * @param progressData      объект JSON с прогрессом ребенка.
     * @param onSuccessListener callback при успешной загрузке данных.
     * @param onFailureListener callback при ошибке загрузки данных.
     */
    private void uploadProgress(StorageReference userProgressRef, JSONObject progressData, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        try {
            InputStream inputStream = new ByteArrayInputStream(progressData.toString().getBytes());
            UploadTask uploadTask = userProgressRef.putStream(inputStream);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                onSuccessListener.onSuccess(null);
            }).addOnFailureListener(e -> {
                Log.e("uploadProgress", "Не удалось загрузить прогресс: " + e.getMessage(), e);
                onFailureListener.onFailure(e);
            });
        } catch (Exception e) {
            Log.e("uploadProgress", "Исключение во время загрузки: " + e.getMessage(), e);
            onFailureListener.onFailure(e);
        }
    }

    /**
     * Проверяет, является ли ошибка результатом отсутствия файла в Firestore Storage.
     *
     * @param e исключение для проверки.
     * @return true, если файл не найден, иначе false.
     */
    private boolean isFileNotFound(Exception e) {
        return e instanceof StorageException && ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND;
    }

    /**
     * Проверяет, содержит ли JSONArray указанное значение.
     *
     * @param jsonArray массив для проверки.
     * @param value     значение для поиска.
     * @return true, если значение найдено; false в противном случае.
     */
    private boolean contains(JSONArray jsonArray, int value) {
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.optInt(i) == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Загружает данные прогресса ребенка.
     *
     * @param userId           идентификатор пользователя.
     * @param onSuccessListener callback при успешной загрузке.
     * @param onFailureListener callback при ошибке загрузки.
     */
    public void loadChildProgress(String userId, OnSuccessListener<JSONObject> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference userProgressRef = getChildProgressReference(userId);

        userProgressRef.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener(bytes -> {
                    try {
                        String jsonStr = new String(bytes);
                        JSONObject jsonObject = new JSONObject(jsonStr);

                        if (jsonObject.has("progress")) {
                            onSuccessListener.onSuccess(jsonObject);
                        } else {
                            Log.e("loadUserProgress", "Данные о прогрессе не найдены в файле");
                            onFailureListener.onFailure(new Exception("No progress data found"));
                        }

                    } catch (Exception e) {
                        Log.e("loadUserProgress", "Ошибка при разборе JSON из файла прогресса: " + e.getMessage(), e);
                        onFailureListener.onFailure(e);
                    }
                })
                .addOnFailureListener(e -> {
                    if (isFileNotFound(e)) {
                        Log.e("loadUserProgress", "Файл прогресса не найден для пользователя: " + userId);
                    } else {
                        Log.e("loadUserProgress", "Не удалось загрузить прогресс для пользователя: " + userId + ". Ошибка: " + e.getMessage(), e);
                    }
                    onFailureListener.onFailure(e);
                });
    }

    /**
     * Сохраняет данные прогресса ребенка в Firestore Storage.
     *
     * @param userId           идентификатор пользователя.
     * @param progressData     объект JSON с прогрессом ребенка.
     * @param onSuccessListener callback при успешном сохранении данных.
     * @param onFailureListener callback при ошибке сохранения данных.
     */
    public void saveToStorage(String userId, JSONObject progressData, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        try {
            String fileName = "progress_details" + ".json";
            StorageReference storageRef = _firebaseStorage.getReference("child_progress/" + userId + "/" + fileName);

            InputStream inputStream = new ByteArrayInputStream(progressData.toString().getBytes());
            UploadTask uploadTask = storageRef.putStream(inputStream);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                onSuccessListener.onSuccess(null);
            }).addOnFailureListener(e -> {
                Log.e("saveToStorage", "Ошибка при сохранении в Firestore Storage: " + e.getMessage(), e);
                onFailureListener.onFailure(e);
            });
        } catch (Exception e) {
            Log.e("saveToStorage", "Исключение во время сохранения: " + e.getMessage(), e);
            onFailureListener.onFailure(e);
        }
    }

    /**
     * Загружает детали прогресса ребенка из Firestore Storage.
     *
     * @param userId           идентификатор пользователя.
     * @param onSuccessListener callback при успешной загрузке данных.
     * @param onFailureListener callback при ошибке загрузки данных.
     */
    public void loadChildProgressDetails(String userId, OnSuccessListener<JSONObject> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference storageRef = _firebaseStorage.getReference("child_progress/" + userId + "/progress_details.json");

        storageRef.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener(bytes -> {
                    try {
                        String jsonStr = new String(bytes);
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        onSuccessListener.onSuccess(jsonObject);
                    } catch (Exception e) {
                        Log.e("loadChildProgressDetails", "Ошибка при парсинге JSON: " + e.getMessage(), e);
                        onFailureListener.onFailure(e);
                    }
                })
                .addOnFailureListener(e -> {
                    if (isFileNotFound(e)) {
                        Log.w("loadChildProgressDetails", "Файл не найден, создается новый.");
                        onSuccessListener.onSuccess(null);
                    } else {
                        Log.e("loadChildProgressDetails", "Ошибка загрузки файла: " + e.getMessage(), e);
                        onFailureListener.onFailure(e);
                    }
                });
    }

}
