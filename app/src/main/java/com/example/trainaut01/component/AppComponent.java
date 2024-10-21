package com.example.trainaut01.component;

import com.example.trainaut01.BaseActivity;
import com.example.trainaut01.LoginActivity;
import com.example.trainaut01.RegisterActivity;
import com.example.trainaut01.dagger.module.RepositoryModule;
import com.example.trainaut01.home.AchievementsFragment;
import com.example.trainaut01.home.DetailedNewsFragment;
import com.example.trainaut01.home.HomeFragment;
import com.example.trainaut01.profile.SupportFragment;
import com.example.trainaut01.profile.UserUpdateFragment;
import com.example.trainaut01.repository.AppInitializer;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.training.ExerciseDetailFragment;
import com.example.trainaut01.training.TrainingListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {RepositoryModule.class})
@Singleton
public interface AppComponent {
    void inject(BaseActivity activity);
    void inject(AppInitializer appInitializer);
    void inject(TrainingListFragment trainingListFragment);
    void inject(LoginActivity loginActivity);
    void inject(RegisterActivity registerActivity);
    void inject(UserUpdateFragment userUpdateFragment);
    void inject(DetailedNewsFragment detailedNewsFragment);
    void inject(HomeFragment homeFragment);
    void inject(SupportFragment supportFragment);
    void inject(AchievementsFragment achievementsFragment);
    void inject(ExerciseDetailFragment exerciseDetailFragment);
}

