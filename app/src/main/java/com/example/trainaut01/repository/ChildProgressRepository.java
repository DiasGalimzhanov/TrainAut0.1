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
    private final FirebaseStorage firebaseStorage;

    public ChildProgressRepository() {
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    public void saveUserProgress(String userId, String year, String month, List<Integer> completedDays, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        Log.d("saveUserProgress", "Saving progress for user: " + userId + ", Year: " + year + ", Month: " + month + ", Completed Days: " + completedDays);
        StorageReference userProgressRef = getUserProgressReference(userId);

        getUserProgress(userProgressRef, progressData -> {
            Log.d("saveUserProgress", "Existing progress data found: " + progressData.toString());
            JSONObject updatedProgress = createOrUpdateProgressObject(progressData, year, month, completedDays);
            uploadProgress(userProgressRef, updatedProgress, onSuccessListener, onFailureListener);
        }, e -> {
            if (isFileNotFound(e)) {
                Log.d("saveUserProgress", "Progress file not found, creating new progress data");
                JSONObject newProgress = createNewProgressObject(year, month, completedDays);
                uploadProgress(userProgressRef, newProgress, onSuccessListener, onFailureListener);
            } else {
                Log.e("saveUserProgress", "Failed to get existing progress: " + e.getMessage(), e);
                onFailureListener.onFailure(e);
            }
        });
    }

    private StorageReference getUserProgressReference(String userId) {
        StorageReference storageRef = firebaseStorage.getReference();
        return storageRef.child("child_progress/" + userId + "/progress.json");
    }

    private void getUserProgress(StorageReference userProgressRef, OnSuccessListener<JSONObject> onSuccessListener, OnFailureListener onFailureListener) {
        userProgressRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            try {
                String jsonStr = new String(bytes);
                JSONObject jsonObject = new JSONObject(jsonStr);
                Log.d("getUserProgress", "Progress data loaded: " + jsonObject.toString());
                onSuccessListener.onSuccess(jsonObject);
            } catch (Exception e) {
                Log.e("getUserProgress", "Error parsing progress data: " + e.getMessage(), e);
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
                    Log.d("createOrUpdateProgress", "Updating existing month progress for Year: " + year + ", Month: " + month);
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
                Log.d("createOrUpdateProgress", "Creating new month progress for Year: " + year + ", Month: " + month);
                JSONObject newMonthProgress = new JSONObject();
                newMonthProgress.put("year", year);
                newMonthProgress.put("month", month);
                newMonthProgress.put("completedDays", new JSONArray(completedDays));
                progressArray.put(newMonthProgress);
            }

            progressData.put("progress", progressArray);
            return progressData;

        } catch (Exception e) {
            Log.e("createOrUpdateProgress", "Failed to update progress object: " + e.getMessage(), e);
            throw new RuntimeException("Failed to update progress object", e);
        }
    }

    private JSONObject createNewProgressObject(String year, String month, List<Integer> completedDays) {
        try {
            Log.d("createNewProgressObject", "Creating new progress object for Year: " + year + ", Month: " + month);
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
            Log.e("createNewProgressObject", "Failed to create new progress object: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create new progress object", e);
        }
    }

    private void uploadProgress(StorageReference userProgressRef, JSONObject progressData, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        try {
            Log.d("uploadProgress", "Uploading progress data: " + progressData.toString());
            InputStream inputStream = new ByteArrayInputStream(progressData.toString().getBytes());
            UploadTask uploadTask = userProgressRef.putStream(inputStream);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Log.d("uploadProgress", "Successfully uploaded progress.json");
                onSuccessListener.onSuccess(null);
            }).addOnFailureListener(e -> {
                Log.e("uploadProgress", "Failed to upload progress: " + e.getMessage(), e);
                onFailureListener.onFailure(e);
            });
        } catch (Exception e) {
            Log.e("uploadProgress", "Exception during upload: " + e.getMessage(), e);
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

                        Log.d("loadUserProgress", "Loaded progress: " + jsonObject.toString());

                        if (jsonObject.has("progress")) {
                            onSuccessListener.onSuccess(jsonObject);
                        } else {
                            Log.e("loadUserProgress", "No progress data found in file");
                            onFailureListener.onFailure(new Exception("No progress data found"));
                        }

                    } catch (Exception e) {
                        Log.e("loadUserProgress", "Error parsing JSON from progress file: " + e.getMessage(), e);
                        onFailureListener.onFailure(e);
                    }
                })
                .addOnFailureListener(e -> {
                    if (isFileNotFound(e)) {
                        Log.e("loadUserProgress", "Progress file not found for user: " + userId);
                    } else {
                        Log.e("loadUserProgress", "Failed to load progress for user: " + userId + " Error: " + e.getMessage(), e);
                    }
                    onFailureListener.onFailure(e);
                });
    }

}
