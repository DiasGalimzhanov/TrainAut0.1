package com.example.trainaut01.component;

import com.example.trainaut01.BaseActivity;
import com.example.trainaut01.dagger.module.RepositoryModule;
import com.example.trainaut01.repository.AppInitializer;
import com.example.trainaut01.training.TrainingListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {RepositoryModule.class})
@Singleton
public interface AppComponent {
    void inject(BaseActivity activity);
    void inject(AppInitializer appInitializer);
    void inject(TrainingListFragment trainingListFragment);
}

