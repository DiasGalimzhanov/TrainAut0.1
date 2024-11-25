package com.example.trainaut01.training;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trainaut01.R;
import com.example.trainaut01.adapter.AchievementAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.Achievement;
import com.example.trainaut01.models.Avatar;
import com.example.trainaut01.repository.AchievementRepository;
import com.example.trainaut01.repository.AvatarRepository;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ProgressFragment extends Fragment {
    private AppComponent appComponent;

    @Inject
    AchievementRepository achievementRepository;
    @Inject
    AvatarRepository avatarRepository;

    private TextView levelTitle, levelProgressText, streakTitle;
    private ProgressBar levelProgressBar;
    private ImageView imgAvatar;
    private TextView tvLvl;
    private AchievementAdapter achievementAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        init(view);

        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        int exp = sharedPreferences.getInt("exp", 0);
        int streakDays = sharedPreferences.getInt("countDays", 0);

        setLevelProgress(exp);
        setStreakProgress(streakDays);
        loadAchievements();
        loadAvatar(exp);

        return view;
    }

    private void init(View view) {
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvLvl = view.findViewById(R.id.tvLvl);
        levelTitle = view.findViewById(R.id.tvLevel);
        levelProgressText = view.findViewById(R.id.levelProgressText);
        levelProgressBar = view.findViewById(R.id.progressBarLevel);
        streakTitle = view.findViewById(R.id.tvDays);

        GridView gridView = view.findViewById(R.id.grdAchiv);
        achievementAdapter = new AchievementAdapter(getActivity(), new ArrayList<>());
        gridView.setAdapter(achievementAdapter);
    }

    private void setLevelProgress(int exp) {
        int level = exp / 5000;
        int expForNextLevel = 5000;
        int progress = (int) ((exp % expForNextLevel) * 100 / expForNextLevel);

        levelTitle.setText("Уровень: " + level);
        levelProgressText.setText(exp + " / " + (level + 1) * expForNextLevel + " опыта");
        levelProgressBar.setProgress(progress);
    }

    private void setStreakProgress(int streakDays) {
        streakTitle.setText("Дней: " + streakDays);
    }

    private void loadAchievements() {
        achievementRepository.getAchievements(new AchievementRepository.AchievementCallback() {
            @Override
            public void onSuccess(List<Achievement> achievements) {
                achievementAdapter.setAchievementList(achievements);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ProgressFragment", "Ошибка загрузки достижений", e);
            }
        });
    }

    private void loadAvatar(int exp) {
        int level = exp / 5000;
        avatarRepository.getAvatarByLevel(level, new AvatarRepository.AvatarCallback() {
            @Override
            public void onSuccess(List<Avatar> avatars) {
                if (!avatars.isEmpty()) {
                    Avatar avatar = avatars.get(0);
                    Picasso.get().load(avatar.getUrlAvatar()).into(imgAvatar);
                    tvLvl.setText(avatar.getDesc());
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ProgressFragment", "Ошибка загрузки аватара", e);
            }
        });
    }
}
