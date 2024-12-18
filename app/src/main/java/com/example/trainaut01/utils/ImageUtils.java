package com.example.trainaut01.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageUtils {

    /**
     * Метод для загрузки GIF-файлов из Firebase Storage
     */
    public static void loadGifFromFirebase(String firebaseUrl, ImageView imageView) {
        if (!ValidationUtils.isValidFirebaseUrl(firebaseUrl)) {
            return;
        }

        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(firebaseUrl);

            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(imageView.getContext())
                        .asGif()
                        .load(uri.toString())
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Полное кэширование
                        .into(imageView);
            }).addOnFailureListener(e -> {
                System.out.println("ImageUtils: Ошибка загрузки GIF -> " + e.getMessage());
            });
        } catch (IllegalArgumentException e) {
            System.out.println("ImageUtils: Некорректный формат URL -> " + e.getMessage());
        }
    }

    /**
     * Метод для загрузки обычных изображений (PNG/JPG) из Firebase Storage
     */
    public static void loadImageFromFirebase(String firebaseUrl, ImageView imageView) {
        if (!ValidationUtils.isValidFirebaseUrl(firebaseUrl)) {
            return;
        }

        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(firebaseUrl);

            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(imageView.getContext())
                        .load(uri.toString())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }).addOnFailureListener(e -> {
                System.out.println("ImageUtils: Ошибка загрузки изображения -> " + e.getMessage());
            });
        } catch (IllegalArgumentException e) {
            System.out.println("ImageUtils: Некорректный формат URL -> " + e.getMessage());
        }
    }
}
