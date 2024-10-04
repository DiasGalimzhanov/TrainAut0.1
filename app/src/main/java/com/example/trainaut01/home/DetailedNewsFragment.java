package com.example.trainaut01.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trainaut01.R;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.models.News;
import com.example.trainaut01.repository.NewsRepository;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class DetailedNewsFragment extends Fragment {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView imageView;


    private AppComponent appComponent;
    @Inject
    NewsRepository newsRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_news, container, false);

        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        titleTextView = view.findViewById(R.id.detail_news_title);
        descriptionTextView = view.findViewById(R.id.detail_news_description);
        imageView = view.findViewById(R.id.detail_news_image);

        Bundle bundle = getArguments();
        String newsId = bundle.getString("news_id");
        if (newsId != null) {
            // Fetch the news by ID and set it in the view
            Log.d("ID",newsId);
            newsRepository.getNewsById(newsId, new NewsRepository.NewsByIdCallback() {
                @Override
                public void onNewsFetched(News news) {

                    String title = news.getTitle();
                    String description = news.getDescription();
                    String imageUrl = news.getImageUrl();
                    if (title != null) {
                        titleTextView.setText(title);
                    } else {
                        titleTextView.setText("Нет заголовка");
                    }

                    if (description != null) {
                        descriptionTextView.setText(description);
                    } else {
                        descriptionTextView.setText("Нет описания");
                    }

                    // Загрузка изображения с помощью Picasso
                    if (imageUrl != null) {
                        Picasso.get().load(imageUrl).into(imageView);
                    }

//                    titleTextView.setText(news.getTitle());
//                    descriptionTextView.setText(news.getDescription());
//                    Picasso.get().load(news.getImageUrl()).into(imageView); // Load the image using Picasso
                }

                @Override
                public void onError(Exception e) {
                    // Handle the error here
                }
            });
        }



        return view;
    }
}