/**
 * Репозиторий для получения данных о достижениях из Firestore.
 * Позволяет загрузить список достижений.
 */
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

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference achievementRef = db.collection("achievements");

    /**
     * Загружает достижения из Firestore и возвращает их через обратный вызов.
     * @param callback объект для обработки результатов загрузки достижений.
     */
    public void getAchievements(final AchievementCallback callback) {
        achievementRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<Achievement> achievements = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Long dayLong = document.getLong("day");
                            String description = document.getString("desc");
                            String imageUrl = document.getString("urlAchiv");

                            int day = (dayLong != null) ? dayLong.intValue() : 0;
                            Achievement achievement = new Achievement(day, imageUrl, description);
                            achievements.add(achievement);
                        }
                    }
                    callback.onSuccess(achievements);
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    /**
     * Интерфейс для обработки результатов загрузки достижений.
     */
    public interface AchievementCallback {
        /**
         * Вызывается при успешной загрузке достижений.
         * @param achievements список загруженных достижений.
         */
        void onSuccess(List<Achievement> achievements);

        /**
         * Вызывается при ошибке загрузки достижений.
         * @param e исключение, вызвавшее ошибку.
         */
        void onFailure(Exception e);
    }
}
