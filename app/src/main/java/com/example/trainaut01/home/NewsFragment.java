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

    private RecyclerView recyclerViewNews;
    private NewsAdapter adapterNews;
    private EditText etSearchNews;
    private AppComponent appComponent;

    private LottieAnimationView lottieAnimationView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initDependencies();
        initViews(view);
        fetchNews();
        return view;
    }

    private void initDependencies() {
        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
    }

    private void initViews(View view) {
        etSearchNews = view.findViewById(R.id.etSearchNews);
        recyclerViewNews = view.findViewById(R.id.RecyclerView1);
        lottieAnimationView = view.findViewById(R.id.lottieCatPlaying);

        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewNews.setAdapter(adapterNews);

        setupSearchListener();
    }

    private void setupSearchListener() {
        etSearchNews.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapterNews != null) {
                    adapterNews.filter(s.toString());

                    if (adapterNews.getItemCount() == 0) {
                        recyclerViewNews.setVisibility(View.GONE);
                        lottieAnimationView.setVisibility(View.VISIBLE);
                        lottieAnimationView.playAnimation();
                    } else {
                        recyclerViewNews.setVisibility(View.VISIBLE);
                        lottieAnimationView.setVisibility(View.GONE);
                        lottieAnimationView.cancelAnimation();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    private void fetchNews() {
        newsRepository.fetchNews(new NewsRepository.NewsFetchCallback() {
            @Override
            public void onNewsFetched(List<News> newsList) {
                adapterNews = new NewsAdapter(newsList, newsItem -> {});
                recyclerViewNews.setAdapter(adapterNews);
                recyclerViewNews.setVisibility(View.VISIBLE);
                lottieAnimationView.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                recyclerViewNews.setVisibility(View.GONE);
                lottieAnimationView.setVisibility(View.VISIBLE);
                lottieAnimationView.playAnimation();
            }
        });
    }

}
