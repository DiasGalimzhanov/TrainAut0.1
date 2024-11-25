package com.example.trainaut01.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.BottomNavigationUpdater;
import com.example.trainaut01.R;
import com.example.trainaut01.adapter.NewsAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.Avatar;
import com.example.trainaut01.models.News;
import com.example.trainaut01.repository.AvatarRepository;
import com.example.trainaut01.repository.NewsRepository;
import com.example.trainaut01.training.ProgressFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

public class HomeFragment extends Fragment {

    private TextView tvHello, _tvMoreNews;
    private ImageView _imgAvatar, _imgMsg;

    private NewsAdapter adapterNews;
    private RecyclerView recyclerViewNews;
    private AvatarRepository avatarRepository;

    private SharedPreferences sharedPref;

    private AppComponent appComponent;

    @Inject
    NewsRepository newsRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);

        fetchNews();

        int exp = sharedPref.getInt("exp", 0); // Получите опыт
        int lvl = exp / 5000;
        Log.d("HOME", String.valueOf(exp));

        // Загрузка аватарки в зависимости от уровня пользователя
        avatarRepository.getAvatarByLevel(lvl, new AvatarRepository.AvatarCallback() {
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







        _tvMoreNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,new NewsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

//        _imgMsg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container,new MessageFragment());
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });

        return view;
    }

    private void init(View view) {

        sharedPref = getActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);

        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        tvHello = view.findViewById(R.id.tvHello);
        _imgAvatar = view.findViewById(R.id.imgAvatar);
        _tvMoreNews = view.findViewById(R.id.tvMoreNews);
        // _imgMsg = view.findViewById(R.id.imgMessage); // Комментируем, если не используется

        String firstName = sharedPref.getString("firstName", "Гость");
        tvHello.setText("Привет, " + firstName);

        avatarRepository = new AvatarRepository();

        // Исправленный идентификатор для RecyclerView
        recyclerViewNews = view.findViewById(R.id.recyclerViewNews);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewNews.setLayoutManager(layoutManager);
        recyclerViewNews.setAdapter(adapterNews);
    }



    private void fetchNews() {

        newsRepository.fetchNews(new NewsRepository.NewsFetchCallback() {
            @Override
            public void onNewsFetched(List<News> newsList) {
                adapterNews = new NewsAdapter(newsList, new NewsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(News newsItem) {}
                });
                recyclerViewNews.setAdapter(adapterNews);
            }

            @Override
            public void onError(Exception e) {
                // Обработка ошибок
            }
        });

    }


    public void updateBottomNavigation() {
        ((BottomNavigationUpdater) getActivity()).updateBottomNavigationSelection(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation();
    }

}