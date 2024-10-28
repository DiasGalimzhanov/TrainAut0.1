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
    @Singleton
    public FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    // Метод для предоставления репозитория упражнений
    @Provides
    @Singleton
    public ExerciseRepository provideExerciseRepository(FirebaseFirestore db) {
        return new ExerciseRepository(db);
    }

    // Метод для предоставления репозитория пользователей
    @Provides
    @Singleton
    public UserRepository provideUserRepository() {
        return new UserRepository();
    }

    // Метод для предоставления репозитория новостей
    @Provides
    @Singleton
    public NewsRepository provideNewsRepository() {
        return new NewsRepository();
    }

    // Метод для предоставления репозитория достижений
    @Provides
    @Singleton
    public AchievementRepository provideAchievementRepository() {
        return new AchievementRepository();
    }

    // Метод для предоставления репозитория аватаров
    @Provides
    @Singleton
    public AvatarRepository provideAvatarRepository() {
        return new AvatarRepository();
    }

}

