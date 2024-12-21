package com.example.trainaut01;

import androidx.fragment.app.Fragment;

/**
 * Интерфейс для обновления выбранного элемента нижней навигации.
 * Используется для синхронизации состояния нижней навигационной панели
 * при переходе между фрагментами.
 */
public interface BottomNavigationUpdater {

    /**
     * Обновляет выбранный элемент нижней навигации в зависимости от текущего фрагмента.
     *
     * @param fragment текущий активный фрагмент, который определяет, какой элемент
     *                 нижней навигации должен быть выделен.
     */
    void updateBottomNavigationSelection(Fragment fragment);
}
