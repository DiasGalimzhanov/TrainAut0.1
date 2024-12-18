/**
 * Репозиторий для работы с данными аватаров пользователя в зависимости от уровня.
 */
package com.example.trainaut01.repository;

import android.content.Context;

import com.example.trainaut01.models.Avatar;
import com.example.trainaut01.utils.SharedPreferencesUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AvatarRepository {
    private final FirebaseFirestore db;

    /**
     * Интерфейс для обратного вызова при загрузке аватаров.
     */
    public interface AvatarCallback {
        void onSuccess(List<Avatar> avatars);
        void onFailure(Exception e);
    }

    /**
     * Создает экземпляр репозитория для работы с аватарами.
     */
    public AvatarRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Получает текущий уровень пользователя по накопленному опыту.
     * @param context Контекст для доступа к SharedPreferences.
     * @return Уровень, рассчитанный по накопленному опыту.
     */
    public int getLevel(Context context) {
        int exp = SharedPreferencesUtils.getInt(context, "child_data", "exp", 1);
        return (exp / 5000) + 1;
    }

    /**
     * Получает аватар, соответствующий текущему уровню пользователя.
     * @param context Контекст для вычисления уровня пользователя.
     * @param callback Обратный вызов при успехе или ошибке.
     */
    public void getAvatarByLevel(Context context, final AvatarCallback callback) {
        int level = getLevel(context);
        db.collection("avatars")
                .whereEqualTo("lvl", level)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Avatar> avatarList = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();

                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String id = document.getId();
                            int lvl = document.getLong("lvl").intValue();
                            String urlAvatar = document.getString("urlAvatar");
                            String desc = document.getString("desc");
                            avatarList.add(new Avatar(id, lvl, urlAvatar, desc));
                        }
                        callback.onSuccess(avatarList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
}
