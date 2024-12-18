package com.example.trainaut01.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.trainaut01.adapter.NewsAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.FragmentNewsBinding;
import com.example.trainaut01.models.News;
import com.example.trainaut01.repository.NewsRepository;

import java.util.List;

import javax.inject.Inject;

public class NewsFragment extends Fragment {

    @Inject
    NewsRepository newsRepository;

    private AppComponent appComponent;
    private FragmentNewsBinding binding;
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
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        initDependencies();
        initViews();
        return binding.getRoot();
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
     * Освобождает ресурсы ViewBinding при уничтожении View.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Инициализирует зависимости с помощью Dagger.
     */
    private void initDependencies() {
        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
    }

    /**
     * Инициализирует визуальные элементы и настраивает слушатель для поля поиска.
     */
    private void initViews() {
        binding.RecyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.etSearchNews.addTextChangedListener(new TextWatcher() {
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
                binding.RecyclerView1.setAdapter(adapterNews);
                updateViewVisibility();
            }

            @Override
            public void onError(Exception e) {
                binding.RecyclerView1.setVisibility(View.GONE);
                binding.lottieCatPlaying.setVisibility(View.VISIBLE);
                binding.lottieCatPlaying.playAnimation();
            }
        });
    }

    /**
     * Обновляет видимость списка и анимации в зависимости от наличия данных.
     */
    private void updateViewVisibility() {
        if (adapterNews != null && adapterNews.getItemCount() > 0) {
            binding.RecyclerView1.setVisibility(View.VISIBLE);
            binding.lottieCatPlaying.setVisibility(View.GONE);
            binding.lottieCatPlaying.cancelAnimation();
        } else {
            binding.RecyclerView1.setVisibility(View.GONE);
            binding.lottieCatPlaying.setVisibility(View.VISIBLE);
            binding.lottieCatPlaying.playAnimation();
        }
    }
}
