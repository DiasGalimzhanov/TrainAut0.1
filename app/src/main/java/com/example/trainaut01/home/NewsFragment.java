package com.example.trainaut01.home;

import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.trainaut01.R;
import com.example.trainaut01.adapter.NewsAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.News;
import com.example.trainaut01.repository.AvatarRepository;
import com.example.trainaut01.repository.NewsRepository;

import java.util.List;

import javax.inject.Inject;


public class NewsFragment extends Fragment {
    private RecyclerView recyclerViewNews;
    private NewsAdapter adapterNews;
    private EditText _etSearshNews;

    private AppComponent appComponent;
    @Inject
    NewsRepository newsRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        init(view);
        fetchNews();

        return view;
    }

    private void init(View view) {
        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        _etSearshNews = view.findViewById(R.id.etSearchNews);

        // Добавляем TextWatcher для EditText
        _etSearshNews.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapterNews != null) {
                    adapterNews.filter(s.toString()); // Фильтруем список новостей
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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
                    public void onItemClick(News newsItem) {
                        // Обработка клика по новости
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