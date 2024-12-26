package com.example.trainaut01.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.R;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.News;
import com.example.trainaut01.repository.NewsRepository;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class DetailedNewsFragment extends Fragment {

    @Inject
    NewsRepository newsRepository;

    private AppComponent appComponent;
    private TextView detailNewsTitle;
    private TextView detailNewsDescription;
    private ImageView detailNewsImage;

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
        View view = inflater.inflate(R.layout.fragment_detailed_news, container, false);
        initDependencies();
        initViews(view);
        loadNewsDetails();
        return view;
    }

    /**
     * Инициализирует зависимости с помощью Dagger.
     */
    private void initDependencies() {
        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
    }

    /**
     * Инициализирует визуальные элементы.
     * @param view Корневой View фрагмента.
     */
    private void initViews(View view) {
        detailNewsTitle = view.findViewById(R.id.detail_news_title);
        detailNewsDescription = view.findViewById(R.id.detail_news_description);
        detailNewsImage = view.findViewById(R.id.detail_news_image);
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
                    detailNewsTitle.setText(news.getTitle());
                } else {
                    detailNewsTitle.setText(getString(R.string.no_title));
                }
                if (news.getDescription() != null) {
                    detailNewsDescription.setText(news.getDescription());
                } else {
                    detailNewsDescription.setText(getString(R.string.no_description));
                }
                if (news.getImageUrl() != null) {
                    Picasso.get().load(news.getImageUrl()).into(detailNewsImage);
                }
            }

            @Override
            public void onError(Exception e) {
                detailNewsTitle.setText(getString(R.string.error_loading_news));
                detailNewsDescription.setText("");
            }
        });
    }
}
