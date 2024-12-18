/**
 * Адаптер для отображения списка новостей в RecyclerView.
 * Поддерживает фильтрацию по заголовку новости.
 */
package com.example.trainaut01.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.home.DetailedNewsFragment;
import com.example.trainaut01.models.News;
import com.squareup.picasso.Picasso;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final List<News> newsList;
    private final List<News> filteredNewsList;
    private final OnItemClickListener listener;

    /**
     * Интерфейс для обработки нажатия на элемент списка.
     */
    public interface OnItemClickListener {
        void onItemClick(News newsItem);
    }

    /**
     * Создает адаптер с исходным списком новостей.
     * @param newsList Исходный список новостей.
     * @param listener Слушатель нажатий.
     */
    public NewsAdapter(List<News> newsList, OnItemClickListener listener) {
        this.newsList = newsList;
        this.filteredNewsList = new ArrayList<>(newsList);
        this.listener = listener;
    }

    /**
     * Создает ViewHolder для элемента списка.
     * @param parent Родительский ViewGroup.
     * @param viewType Тип элемента (не используется в данном случае).
     * @return Экземпляр NewsViewHolder.
     */
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    /**
     * Привязывает данные к ViewHolder.
     * @param holder ViewHolder для наполнения данными.
     * @param position Позиция элемента в списке.
     */
    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        News newsItem = filteredNewsList.get(position);
        holder.title.setText(newsItem.getTitle());
        holder.description.setText(newsItem.getDescription());
        Picasso.get().load(newsItem.getImageUrl()).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            DetailedNewsFragment detailedNewsFragment = new DetailedNewsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("news_id", newsItem.getId());
            detailedNewsFragment.setArguments(bundle);

            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, detailedNewsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    /**
     * Возвращает количество отображаемых элементов.
     * @return Размер списка фильтрованных новостей.
     */
    @Override
    public int getItemCount() {
        return filteredNewsList.size();
    }

    /**
     * Фильтрует список новостей по заголовку.
     * @param text Текст для фильтрации.
     */
    public void filter(String text) {
        filteredNewsList.clear();
        if (text.isEmpty()) {
            filteredNewsList.addAll(newsList);
        } else {
            String lowerCaseText = text.toLowerCase();
            for (News news : newsList) {
                if (news.getTitle().toLowerCase().contains(lowerCaseText)) {
                    filteredNewsList.add(news);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * ViewHolder для элемента новости.
     */
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        ImageView image;

        /**
         * Создает новый ViewHolder для элемента списка новостей.
         * @param itemView Корневой View элемента.
         */
        public NewsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title_news);
            description = itemView.findViewById(R.id.tv_subtitle_news);
            image = itemView.findViewById(R.id.image_news);
        }
    }
}
