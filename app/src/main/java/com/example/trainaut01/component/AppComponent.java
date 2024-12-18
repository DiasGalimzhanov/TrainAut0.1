package com.example.trainaut01.component;

import com.example.trainaut01.BaseActivity;
import com.example.trainaut01.ChildSignUpFragment;
import com.example.trainaut01.LoginActivity;
import com.example.trainaut01.SignUpActivity;
import com.example.trainaut01.TrainingDashboardFragment;
import com.example.trainaut01.dagger.module.RepositoryModule;
import com.example.trainaut01.home.DetailedNewsFragment;
import com.example.trainaut01.home.HomeFragment;
import com.example.trainaut01.home.NewsFragment;
import com.example.trainaut01.profile.NoteFragment;
import com.example.trainaut01.profile.SupportFragment;
import com.example.trainaut01.profile.UserProfileFragment;
import com.example.trainaut01.profile.UserUpdateFragment;
import com.example.trainaut01.repository.AppInitializer;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.training.ExerciseDetailFragment;
import com.example.trainaut01.training.ProgressFragment;
import com.example.trainaut01.training.TodayMusclePlanFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Компонент Dagger для предоставления зависимостей в приложении.
 * Связывает модули с объектами, которые нуждаются в инъекции зависимостей.
 */
@Component(modules = {RepositoryModule.class})
@Singleton
public interface AppComponent {

    void inject(BaseActivity activity);
    void inject(AppInitializer appInitializer);
    void inject(LoginActivity loginActivity);
    void inject(SignUpActivity SignUpActivity);
    void inject(UserUpdateFragment userUpdateFragment);
    void inject(DetailedNewsFragment detailedNewsFragment);
    void inject(HomeFragment homeFragment);
    void inject(SupportFragment supportFragment);
    void inject(ProgressFragment progressFragment);
    void inject(NewsFragment newsFragment);
    void inject(ExerciseDetailFragment exerciseDetailFragment);
    void inject(TrainingDashboardFragment trainingDashboardFragment);
    void inject(UserProfileFragment userProfileFragment);
    void inject(ChildSignUpFragment childSignUpFragment);
    void inject(TodayMusclePlanFragment todayMusclePlanFragment);
    void inject(NoteFragment noteFragment);
}

