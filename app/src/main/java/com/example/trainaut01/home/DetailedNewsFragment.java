package com.example.trainaut01.home;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.FragmentDetailedNewsBinding;
import com.example.trainaut01.models.News;
import com.example.trainaut01.repository.NewsRepository;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class DetailedNewsFragment extends Fragment {

    @Inject
    NewsRepository newsRepository;

    private AppComponent appComponent;
    private FragmentDetailedNewsBinding binding;

    /**
     * Создает и инициализирует интерфейс фрагмента.
     * @param inflater Объект для "надувания" макета фрагмента.
     * @param container Родительский контейнер для макета.
     * @param savedInstanceState Предыдущее состояние фрагмента (если есть).
     * @return Корневой View фрагмента.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailedNewsBinding.inflate(inflater, container, false);
        initDependencies();
        loadNewsDetails();
        return binding.getRoot();
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
     * Получает идентификатор новости из аргументов и загружает данные о ней из репозитория.
     */
    private void loadNewsDetails() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        String newsId = bundle.getString("news_id");
        if (newsId == null) return;
        Log.d("ID", newsId);
        newsRepository.getNewsById(newsId, new NewsRepository.NewsByIdCallback() {
            @Override
            public void onNewsFetched(News news) {
                if (news.getTitle() != null) {
                    binding.detailNewsTitle.setText(news.getTitle());
                } else {
                    binding.detailNewsTitle.setText("Нет заголовка");
                }
                if (news.getDescription() != null) {
                    binding.detailNewsDescription.setText(news.getDescription());
                } else {
                    binding.detailNewsDescription.setText("Нет описания");
                }
                if (news.getImageUrl() != null) {
                    Picasso.get().load(news.getImageUrl()).into(binding.detailNewsImage);
                }
            }

            @Override
            public void onError(Exception e) {
                binding.detailNewsTitle.setText("Ошибка загрузки");
                binding.detailNewsDescription.setText("");
            }
        });
    }
}
