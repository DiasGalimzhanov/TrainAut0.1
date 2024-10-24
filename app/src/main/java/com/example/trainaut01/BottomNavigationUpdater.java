package com.example.trainaut01;

import androidx.fragment.app.Fragment;

// Интерфейс для обновления состояния нижней навигации
public interface BottomNavigationUpdater {
    // Метод для обновления выбранного элемента навигации в зависимости от текущего фрагмента
    void updateBottomNavigationSelection(Fragment fragment);
}
