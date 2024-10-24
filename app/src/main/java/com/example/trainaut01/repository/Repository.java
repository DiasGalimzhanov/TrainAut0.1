package com.example.trainaut01.repository;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

// Интерфейс для общего репозитория с базовыми CRUD операциями
public interface Repository<T> {
    // Метод для добавления элемента
    void add(T item, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure);

    // Метод для обновления элемента по его id
    void update(String id, T item, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure);

    // Метод для удаления элемента по его id
    void delete(String id, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure);

    // Метод для получения элемента по его id
    void get(String id, OnSuccessListener<T> onSuccess, OnFailureListener onFailure);

    // Метод для получения всех элементов
    void getAll(OnSuccessListener<List<T>> onSuccess, OnFailureListener onFailure);
}
