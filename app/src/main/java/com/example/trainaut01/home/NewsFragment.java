package com.example.trainaut01.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.trainaut01.R;
import com.example.trainaut01.adapter.NewsAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.News;
import com.example.trainaut01.repository.NewsRepository;

import java.util.List;

import javax.inject.Inject;

public class NewsFragment extends Fragment {

    @Inject
    NewsRepository newsRepository;

    private RecyclerView recyclerView;
    private EditText etSearchNews;
    private LottieAnimationView lottieCatPlaying;
    private NewsAdapter adapterNews;

    /**
     * Создает и инициализирует интерфейс фрагмента.
     * @param inflater Объект для "надувания" макета фрагмента.
     * @param container Родительский контейнер для макета.
     * @param savedInstanceState Предыдущее состояние фрагмента (если есть).
     * @return Корневой View фрагмента.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initDependencies();
        initViews(view);
        return view;
    }

    /**
     * Вызывается, когда фрагмент стал видимым для пользователя.
     * Здесь загружаются данные для отображения.
     */
    @Override
    public void onResume() {
        super.onResume();
        fetchNews();
    }

    /**
     * Инициализирует зависимости с помощью Dagger.
     */
    private void initDependencies() {
        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
    }

    /**
     * Инициализирует визуальные элементы и настраивает слушатель для поля поиска.
     */
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.RecyclerView1);
        etSearchNews = view.findViewById(R.id.etSearchNews);
        lottieCatPlaying = view.findViewById(R.id.lottieCatPlaying);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        etSearchNews.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapterNews != null) {
                    adapterNews.filter(s.toString());
                    updateViewVisibility();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Загружает данные из репозитория и обновляет список.
     */
    private void fetchNews() {
        newsRepository.fetchNews(new NewsRepository.NewsFetchCallback() {
            @Override
            public void onNewsFetched(List<News> newsList) {
                adapterNews = new NewsAdapter(newsList, newsItem -> {});
                recyclerView.setAdapter(adapterNews);
                updateViewVisibility();
            }

            @Override
            public void onError(Exception e) {
                recyclerView.setVisibility(View.GONE);
                lottieCatPlaying.setVisibility(View.VISIBLE);
                lottieCatPlaying.playAnimation();
            }
        });
    }

    /**
     * Обновляет видимость списка и анимации в зависимости от наличия данных.
     */
    private void updateViewVisibility() {
        if (adapterNews != null && adapterNews.getItemCount() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            lottieCatPlaying.setVisibility(View.GONE);
            lottieCatPlaying.cancelAnimation();
        } else {
            recyclerView.setVisibility(View.GONE);
            lottieCatPlaying.setVisibility(View.VISIBLE);
            lottieCatPlaying.playAnimation();
        }
    }
}

