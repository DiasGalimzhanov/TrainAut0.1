package com.example.trainaut01.repository;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.trainaut01.models.Achievement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AchievementRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference achievementRef = db.collection("achievements");

    public void getAchievements(final AchievementCallback callback) {
        achievementRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Achievement> achievements = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        int day = document.getLong("day").intValue();
                        String description = document.getString("desc");
                        String imageUrl = document.getString("urlAchiv");

                        Achievement achievement = new Achievement(day, imageUrl, description);
                        achievements.add(achievement);
                    }
                    callback.onSuccess(achievements);
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public interface AchievementCallback {
        void onSuccess(List<Achievement> achievements);
        void onFailure(Exception e);
    }
}