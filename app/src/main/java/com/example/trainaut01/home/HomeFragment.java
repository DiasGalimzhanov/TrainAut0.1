package com.example.trainaut01.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.adapters.NewsAdapter;
import com.example.trainaut01.models.News;
import com.example.trainaut01.repository.NewsRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView.Adapter adapterNews;
    private RecyclerView recyclerViewNews;
    private NewsRepository newsRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initRecyclerView(view);
        fetchNews();
        return view;
    }

    private void initRecyclerView(View view) {
        recyclerViewNews = view.findViewById(R.id.RecyclerView1);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }


    private void fetchNews() {
        newsRepository = new NewsRepository();
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