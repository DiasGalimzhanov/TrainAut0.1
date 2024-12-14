package com.example.trainaut01.repository;


import android.content.Context;

import com.example.trainaut01.models.Avatar;
import com.example.trainaut01.utils.SharedPreferencesUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AvatarRepository {
    private FirebaseFirestore db;
    private SharedPreferencesUtils sharedPreferencesUtils;

    public AvatarRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public interface AvatarCallback {
        void onSuccess(List<Avatar> avatars);
        void onFailure(Exception e);
    }

    public int getLevel(Context context){
        int exp = SharedPreferencesUtils.getInt(context, "child_data", "exp", 1);
        return (exp / 5000) + 1;
    }

    public void getAvatarByLevel(Context context, final AvatarCallback callback) {
        int level = getLevel(context);
        db.collection("avatars")
                .whereEqualTo("lvl", level)
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
