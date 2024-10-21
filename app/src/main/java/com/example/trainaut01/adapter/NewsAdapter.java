package com.example.trainaut01.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trainaut01.R;
import com.example.trainaut01.home.DetailedNewsFragment;
import com.example.trainaut01.models.News;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> newsList;
    private List<News> filteredNewsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(News newsItem);
    }

    public NewsAdapter(List<News> newsList, OnItemClickListener listener) {
        this.newsList = newsList;
        this.filteredNewsList = new ArrayList<>(newsList);
        this.listener = listener;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        News newsItem = filteredNewsList.get(position); // Используем filteredNewsList
        holder.bind(newsItem, listener);
    }

    @Override
    public int getItemCount() {
        return filteredNewsList.size(); // Возвращаем размер filteredNewsList
    }

    // Метод для фильтрации новостей по заголовку
    public void filter(String text) {
        filteredNewsList.clear();
        if (text.isEmpty()) {
            filteredNewsList.addAll(newsList);
        } else {
            text = text.toLowerCase();
            for (News news : newsList) {
                if (news.getTitle().toLowerCase().contains(text)) {
                    filteredNewsList.add(news);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        ImageView image;

        public NewsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title_news);
            description = itemView.findViewById(R.id.tv_subtitle_news);
            image = itemView.findViewById(R.id.image_news);
        }

        public void bind(final News newsItem, final OnItemClickListener listener) {
            title.setText(newsItem.getTitle());
            description.setText(newsItem.getDescription());
            Picasso.get().load(newsItem.getImageUrl()).into(image); // Загрузка изображения в элемент списка

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Создание нового фрагмента
                    DetailedNewsFragment detailedNewsFragment = new DetailedNewsFragment();

                    // Передача данных через Bundle
                    Bundle bundle = new Bundle();
                    bundle.putString("news_id", newsItem.getId());
//                    bundle.putString("news_image_url", newsItem.getImageUrl()); // URL изображения
                    detailedNewsFragment.setArguments(bundle);

                    // Открытие фрагмента
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, detailedNewsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }


    }
}

