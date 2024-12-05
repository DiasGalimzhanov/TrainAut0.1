package com.example.trainaut01.utils;

import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ImageUtils {

    public static void loadImageFromFirebase(String firebaseUrl, ImageView imageView, int placeholderResId, int errorResId) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(firebaseUrl);

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri.toString())
                    .placeholder(placeholderResId)
                    .error(errorResId)
                    .into(imageView);
        }).addOnFailureListener(e -> {
            Log.e("ImageUtils", "Ошибка загрузки изображения: " + e.getMessage());
            imageView.setImageResource(errorResId);
        });
    }

    public static void setImagePicasso(String url, ImageView imageView, int placeholderResId, int errorResId) {
        Picasso.get()
                .load(url)
                .placeholder(placeholderResId)
                .error(errorResId)
                .into(imageView);
    }
}
