package com.example.trainaut01.component;

import com.example.trainaut01.BaseActivity;
import com.example.trainaut01.LoginActivity;
import com.example.trainaut01.RegisterActivity;
import com.example.trainaut01.dagger.module.RepositoryModule;
import com.example.trainaut01.home.AchievementsFragment;
import com.example.trainaut01.home.DetailedNewsFragment;
import com.example.trainaut01.home.HomeFragment;
import com.example.trainaut01.home.NewsFragment;
import com.example.trainaut01.profile.SupportFragment;
import com.example.trainaut01.profile.UserUpdateFragment;
import com.example.trainaut01.repository.AppInitializer;
import com.example.trainaut01.training.ExerciseDetailFragment;
import com.example.trainaut01.training.TrainingListFragment;

import javax.inject.Singleton;

import dagger.Component;

// Определение компонента Dagger, который использует модуль RepositoryModule
@Component(modules = {RepositoryModule.class})
@Singleton // Обозначаем, что этот компонент является синглтоном
public interface AppComponent {

    // Метод для внедрения зависимостей в базовую активность
    void inject(BaseActivity activity);

    // Метод для внедрения зависимостей в класс инициализации приложения
    void inject(AppInitializer appInitializer);

    // Метод для внедрения зависимостей в фрагмент списка тренировок
    void inject(TrainingListFragment trainingListFragment);

    // Метод для внедрения зависимостей в активность входа
    void inject(LoginActivity loginActivity);

    // Метод для внедрения зависимостей в активность регистрации
    void inject(RegisterActivity registerActivity);

    // Метод для внедрения зависимостей в фрагмент обновления пользователя
    void inject(UserUpdateFragment userUpdateFragment);

    // Метод для внедрения зависимостей в фрагмент подробных новостей
    void inject(DetailedNewsFragment detailedNewsFragment);

    // Метод для внедрения зависимостей в фрагмент главной страницы
    void inject(HomeFragment homeFragment);

    // Метод для внедрения зависимостей в фрагмент службы поддержки
    void inject(SupportFragment supportFragment);

    // Метод для внедрения зависимостей в фрагмент достижений
    void inject(AchievementsFragment achievementsFragment);

    // Метод для внедрения зависимостей в фрагмент новостей
    void inject(NewsFragment newsFragment);

    // Метод для внедрения зависимостей в фрагмент деталей упражнения
    void inject(ExerciseDetailFragment exerciseDetailFragment);
}

