package com.example.trainaut01.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.trainaut01.models.News;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NewsRepository {
    private FirebaseFirestore db;

    public NewsRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void fetchNews(final NewsFetchCallback callback) {
        db.collection("news").get()
                .addOnCompleteListener(task -> {
                    List<News> newsList = new ArrayList<>();
                    if (task.isSuccessful()) {
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

    // Новый метод для получения новости по ID
    public void getNewsById(String newsId, final NewsByIdCallback callback) {
        db.collection("news").document(newsId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String id = documentSnapshot.getId();
                        String title = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("discription");
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        News news = new News(id, title, description, imageUrl);
                        callback.onNewsFetched(news);
                    } else {
                        callback.onError(new Exception("Документ не найден"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    public interface NewsFetchCallback {
        void onNewsFetched(List<News> newsList);
        void onError(Exception e);
    }


    public interface NewsByIdCallback {
        void onNewsFetched(News news);
        void onError(Exception e);
    }
}
