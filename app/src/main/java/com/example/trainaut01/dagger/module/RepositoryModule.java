package com.example.trainaut01.dagger.module;

import com.example.trainaut01.repository.AchievementRepository;
import com.example.trainaut01.repository.AvatarRepository;
import com.example.trainaut01.repository.ExerciseRepository;
import com.example.trainaut01.repository.NewsRepository;
import com.example.trainaut01.repository.UserRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

// Модуль Dagger для предоставления репозиториев
@Module
public class RepositoryModule {

    // Метод для предоставления экземпляра FirebaseFirestore
    @Provides
    @Singleton // Обозначаем, что этот метод предоставляет синглтон
    public FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance(); // Возвращаем экземпляр Firestore
    }

    // Метод для предоставления репозитория упражнений
    @Provides
    @Singleton // Обозначаем, что этот метод предоставляет синглтон
    public ExerciseRepository provideExerciseRepository(FirebaseFirestore db) {
        return new ExerciseRepository(db); // Возвращаем новый экземпляр ExerciseRepository
    }

    // Метод для предоставления репозитория пользователей
    @Provides
    @Singleton // Обозначаем, что этот метод предоставляет синглтон
    public UserRepository provideUserRepository() {
        return new UserRepository(); // Возвращаем новый экземпляр UserRepository
    }

    // Метод для предоставления репозитория новостей
    @Provides
    @Singleton // Обозначаем, что этот метод предоставляет синглтон
    public NewsRepository provideNewsRepository() {
        return new NewsRepository(); // Возвращаем новый экземпляр NewsRepository
    }

    // Метод для предоставления репозитория достижений
    @Provides
    @Singleton // Обозначаем, что этот метод предоставляет синглтон
    public AchievementRepository provideAchievementRepository() {
        return new AchievementRepository(); // Возвращаем новый экземпляр AchievementRepository
    }

    // Метод для предоставления репозитория аватаров
    @Provides
    @Singleton // Обозначаем, что этот метод предоставляет синглтон
    public AvatarRepository provideAvatarRepository() {
        return new AvatarRepository(); // Возвращаем новый экземпляр AvatarRepository
    }

}

