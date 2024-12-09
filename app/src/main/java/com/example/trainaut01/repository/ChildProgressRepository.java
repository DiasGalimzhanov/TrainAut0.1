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

public class ChildProgressRepository {
    private final FirebaseStorage _firebaseStorage;

    public ChildProgressRepository() {
        this._firebaseStorage = FirebaseStorage.getInstance();
    }

    public void saveUserProgress(String userId, String year, String month, List<Integer> completedDays, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference userProgressRef = getUserProgressReference(userId);

        getUserProgress(userProgressRef, progressData -> {
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

    private StorageReference getUserProgressReference(String userId) {
        StorageReference storageRef = _firebaseStorage.getReference();
        return storageRef.child("child_progress/" + userId + "/progress.json");
    }

    private void getUserProgress(StorageReference userProgressRef, OnSuccessListener<JSONObject> onSuccessListener, OnFailureListener onFailureListener) {
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

    private boolean isFileNotFound(Exception e) {
        return e instanceof StorageException && ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND;
    }

    private boolean contains(JSONArray jsonArray, int value) {
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.optInt(i) == value) {
                return true;
            }
        }
        return false;
    }

    public void loadUserProgress(String userId, OnSuccessListener<JSONObject> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference userProgressRef = getUserProgressReference(userId);

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

}
