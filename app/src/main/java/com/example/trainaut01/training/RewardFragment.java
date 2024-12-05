package com.example.trainaut01.training;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.trainaut01.R;
import com.example.trainaut01.TrainingDashboardFragment;

/**
 * Фрагмент для отображения экрана награды после завершения упражнений.
 * Показывает количество заработанных очков и предоставляет возможность вернуться на главную страницу тренировок.
 */
public class RewardFragment extends Fragment {

    private static final String ARG_REWARD_POINTS = "rewardPoints";

    /**
     * Создает новый экземпляр RewardFragment с указанным количеством очков награды.
     *
     * @param rewardPoints Количество очков награды для отображения.
     * @return Новый экземпляр RewardFragment.
     */
    public static RewardFragment newInstance(int rewardPoints) {
        RewardFragment fragment = new RewardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_REWARD_POINTS, rewardPoints);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Создает и возвращает представление фрагмента.
     *
     * @param inflater           Объект для раздувания XML-ресурсов.
     * @param container          Контейнер, в который будет добавлено представление.
     * @param savedInstanceState Сохраненное состояние, если оно имеется.
     * @return Представление фрагмента.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward, container, false);
        setupUI(view);
        return view;
    }

    /**
     * Настраивает элементы пользовательского интерфейса.
     *
     * @param view Представление фрагмента.
     */
    private void setupUI(View view) {
        TextView rewardDescriptionText = view.findViewById(R.id.rewardDescriptionText);
        Button btnReturnTrainingDashboard = view.findViewById(R.id.btnReturnTrainingDashboard);

        rewardDescriptionText.setText(getRewardDescription());
        btnReturnTrainingDashboard.setOnClickListener(v -> goToTrainingDashboardFragment());
    }

    /**
     * Формирует текст описания награды с количеством очков.
     *
     * @return Описание награды.
     */
    @SuppressLint("DefaultLocale")
    private String getRewardDescription() {
        int rewardPoints = getArguments() != null ? getArguments().getInt(ARG_REWARD_POINTS, 0) : 0;
        return String.format("Ты заслужил награду! 🎉 Теперь у тебя +%d Exp. Продолжай в том же духе!", rewardPoints);
    }

    /**
     * Осуществляет переход на главную страницу тренировок.
     */
    private void goToTrainingDashboardFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainTraining, new TrainingDashboardFragment())
                .commit();
    }
}
