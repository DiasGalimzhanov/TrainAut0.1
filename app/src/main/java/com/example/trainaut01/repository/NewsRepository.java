/**
 * Репозиторий для получения новостей из Firebase Firestore.
 */
package com.example.trainaut01.repository;

import com.example.trainaut01.models.News;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewsRepository {
    private final FirebaseFirestore db;

    /**
     * Инициализирует репозиторий с доступом к Firestore.
     */
    public NewsRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Получает список всех новостей.
     * @param callback Обратный вызов при успешной или неуспешной загрузке.
     */
    public void fetchNews(final NewsFetchCallback callback) {
        db.collection("news").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<News> newsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String imageUrl = document.getString("imageUrl");
                            newsList.add(new News(id, title, description, imageUrl));
                        }
                        callback.onNewsFetched(newsList);
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }

    /**
     * Получает новость по её идентификатору.
     * @param newsId Идентификатор новости.
     * @param callback Обратный вызов при успешной или неуспешной загрузке.
     */
    public void getNewsById(String newsId, final NewsByIdCallback callback) {
        db.collection("news").document(newsId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String id = documentSnapshot.getId();
                        String title = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("discription");
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        callback.onNewsFetched(new News(id, title, description, imageUrl));
                    } else {
                        callback.onError(new Exception("Документ не найден"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Интерфейс для обратного вызова при загрузке списка новостей.
     */
    public interface NewsFetchCallback {
        void onNewsFetched(List<News> newsList);
        void onError(Exception e);
    }

    /**
     * Интерфейс для обратного вызова при загрузке новости по её идентификатору.
     */
    public interface NewsByIdCallback {
        void onNewsFetched(News news);
        void onError(Exception e);
    }
}
