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
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

public class HomeFragment extends Fragment {

    private TextView tvHello, _tvMoreNews;
    private ImageView _imgAvatar, _imgMsg;
    private Button _button_today_exercise;

    private NewsAdapter adapterNews;
    private RecyclerView recyclerViewNews;
    private CardView _cardAchiv, _cardProgress, _cardDoc;
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

        _cardAchiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new AchievementsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        _cardProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,new ProgressFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        _cardDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");

                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"doc@gamil.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Тема письма");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Текст сообщения");

                try {
                    // Открываем почтовое приложение
                    startActivity(Intent.createChooser(emailIntent, "Выберите почтовое приложение"));
                } catch (android.content.ActivityNotFoundException ex) {
                    // Если нет почтового приложения, выводим сообщение об ошибке
                    Toast.makeText(view.getContext(), "Почтовое приложение не найдено.", Toast.LENGTH_SHORT).show();
                }
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

        _imgMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,new MessageFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void init(View view) {

        sharedPref = getActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);

        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        tvHello = view.findViewById(R.id.tvHello);
        _cardAchiv = view.findViewById(R.id.cardAchiv);
        _cardDoc = view.findViewById(R.id.cardDoc);
        _imgAvatar = view.findViewById(R.id.imgAvatar);
        _cardProgress = view.findViewById(R.id.cardProgress);
        _tvMoreNews = view.findViewById(R.id.tvMoreNews);
        _imgMsg = view.findViewById(R.id.imgMessage);
        _button_today_exercise = view.findViewById(R.id.button_today_exercise);


        String firstName = sharedPref.getString("firstName", "Гость");
        tvHello.setText("Привет, " + firstName);

        avatarRepository = new AvatarRepository();
        recyclerViewNews = view.findViewById(R.id.RecyclerView1);
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