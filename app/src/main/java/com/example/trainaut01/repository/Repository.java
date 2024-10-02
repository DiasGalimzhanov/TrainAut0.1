package com.example.trainaut01.repository;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public interface Repository<T> {
    void add(T item, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure);
    void update(String id, T item, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure);
    void delete(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure);
    void get(String id, OnSuccessListener<T> onSuccess, OnFailureListener onFailure);
    void getAll(OnSuccessListener<List<T>> onSuccess, OnFailureListener onFailure);
}
