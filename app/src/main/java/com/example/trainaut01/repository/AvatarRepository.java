package com.example.trainaut01.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.trainaut01.models.Avatar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AvatarRepository {
    private FirebaseFirestore db;

    public AvatarRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public interface AvatarCallback {
        void onSuccess(List<Avatar> avatars);
        void onFailure(Exception e);
    }

    public void getAvatars(final AvatarCallback callback) {
        db.collection("avatars")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Avatar> avatarList = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String id = document.getId();
                                int lvl = document.getLong("lvl").intValue();
                                String urlAvatar = document.getString("urlAvatar");
                                String desc = document.getString("desc");

                                Avatar avatar = new Avatar(id, lvl, urlAvatar, desc);
                                avatarList.add(avatar);
                            }
                        }

                        callback.onSuccess(avatarList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void getAvatarByLevel(int userLvl, final AvatarCallback callback) {
        db.collection("avatars")
                .whereEqualTo("lvl", userLvl)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Avatar> avatarList = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String id = document.getId();
                            int lvl = document.getLong("lvl").intValue();
                            String urlAvatar = document.getString("urlAvatar");
                            String desc = document.getString("desc");

                            Avatar avatar = new Avatar(id, lvl, urlAvatar, desc);
                            avatarList.add(avatar);
                        }

                        callback.onSuccess(avatarList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
}
