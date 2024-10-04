package com.example.trainaut01.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.adapter.NewsAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.Avatar;
import com.example.trainaut01.models.News;
import com.example.trainaut01.profile.SupportFragment;
import com.example.trainaut01.repository.AvatarRepository;
import com.example.trainaut01.repository.NewsRepository;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class HomeFragment extends Fragment {
    private TextView tvHello;
    private ImageView _imgAvatar;
    private RecyclerView.Adapter adapterNews;
    private RecyclerView recyclerViewNews;
    private CardView _cardAchiv;
    private AvatarRepository avatarRepository;

    private AppComponent appComponent;
    @Inject
    NewsRepository newsRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);
        String firstName = sharedPref.getString("firstName", null);
        tvHello.setText("Привет, " + firstName);
        fetchNews();

        // Получаем уровень пользователя (например, из SharedPreferences или другой логики)
        int userLevel = 1;

        // Загружаем аватарку в зависимости от уровня пользователя
        avatarRepository.getAvatarByLevel(userLevel, new AvatarRepository.AvatarCallback() {
            @Override
            public void onSuccess(List<Avatar> avatars) {
                if (!avatars.isEmpty()) {
                    Avatar avatar = avatars.get(0);

                    // Загрузка изображения аватарки с помощью Picasso
                    Picasso.get().load(avatar.getUrlAvatar()).into(_imgAvatar);
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Обработка ошибки
            }
        });

        _cardAchiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new AchievementsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void init(View view) {
        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
        tvHello = view.findViewById(R.id.tvHello);
        _cardAchiv = view.findViewById(R.id.cardAchiv);
        _imgAvatar = view.findViewById(R.id.imgAvatar);

        avatarRepository = new AvatarRepository();
        recyclerViewNews = view.findViewById(R.id.RecyclerView1);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }


    private void fetchNews() {
//        newsRepository = new NewsRepository();
        newsRepository.fetchNews(new NewsRepository.NewsFetchCallback() {
            @Override
            public void onNewsFetched(List<News> newsList) {
                adapterNews = new NewsAdapter(newsList, new NewsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(News newsItem) {
                        // Клик по новости обрабатывается в адаптере
                    }
                });
                recyclerViewNews.setAdapter(adapterNews);
            }

            @Override
            public void onError(Exception e) {
                // Обработка ошибок
            }
        });

    }
}