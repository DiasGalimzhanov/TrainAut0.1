package com.example.trainaut01.dagger.module;

import com.example.trainaut01.repository.ExerciseRepository;
import com.example.trainaut01.repository.NewsRepository;
import com.example.trainaut01.repository.UserRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public ExerciseRepository provideExerciseRepository(FirebaseFirestore db) {
        return new ExerciseRepository(db);
    }

    @Provides
    @Singleton
    public UserRepository provideUserRepository(){
        return new UserRepository();
    }

    @Provides
    @Singleton
    public NewsRepository provideNewsRepository(){
        return new NewsRepository();
    }


}

