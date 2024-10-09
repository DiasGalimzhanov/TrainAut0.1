package com.example.trainaut01.home;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
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


public class AchievementsFragment extends Fragment {
    private AppComponent appComponent;
    @Inject
    AchievementRepository achievementRepository;
    @Inject
    AvatarRepository avatarRepository;

    private ImageView _imgAvatar;
    private TextView _tvLvl;
    private AchievementAdapter achievementAdapter;
//    private AchievementRepository achievementRepository;
//    private AvatarRepository avatarRepository;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);

        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        _imgAvatar = view.findViewById(R.id.imgAvatar);
        _tvLvl = view.findViewById(R.id.tvLvl);
        achievementAdapter = new AchievementAdapter(getActivity(), new ArrayList<>());
        achievementRepository = new AchievementRepository();
        avatarRepository = new AvatarRepository();

        GridView gridView = view.findViewById(R.id.grdAchiv);
        gridView.setAdapter(achievementAdapter);

        // Получаем данные из репозитория
        achievementRepository.getAchievements(new AchievementRepository.AchievementCallback() {
            @Override
            public void onSuccess(List<Achievement> achievements) {
                achievementAdapter.setAchievementList(achievements);  // Устанавливаем данные в адаптере
            }

            @Override
            public void onFailure(Exception e) {
                // Обработка ошибки
            }
        });

        // Получаем уровень пользователя (например, из SharedPreferences или другой логики)
//        int userLevel = 1;
        SharedPreferences sharedPref = getActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);
//        int lvl = sharedPref.getInt("lvl", 0);
        int exp = sharedPref.getInt("exp", 0); // Получите опыт
        Log.d("ACH", String.valueOf(exp));
//        int exp = 22350;
        int lvl = exp / 5000;

        // Загружаем аватарку в зависимости от уровня пользователя
        avatarRepository.getAvatarByLevel(lvl, new AvatarRepository.AvatarCallback() {
            @Override
            public void onSuccess(List<Avatar> avatars) {
                if (!avatars.isEmpty()) {
                    Avatar avatar = avatars.get(0);

                    // Загрузка изображения аватарки с помощью Picasso
                    Picasso.get().load(avatar.getUrlAvatar()).into(_imgAvatar);
                    _tvLvl.setText(avatar.getDesc());
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Обработка ошибки
            }
        });

        return view;
    }


}