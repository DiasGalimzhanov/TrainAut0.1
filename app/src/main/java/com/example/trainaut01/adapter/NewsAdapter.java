package com.example.trainaut01.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> newsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(News newsItem);
    }

    public NewsAdapter(List<News> newsList, OnItemClickListener listener) {
        this.newsList = newsList;
        this.listener = listener;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        News newsItem = newsList.get(position);
        holder.bind(newsItem, listener);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
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


//public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
//
//    List<News> _items;
//    Context context;
//
//    public NewsAdapter(List<News> items) {
//        this._items = items;
//    }
//
//    @NonNull
//    @Override
//    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        context = parent.getContext();
//        View inflator = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
//        return new ViewHolder(inflator);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
//        News currentNews = _items.get(position);
//        holder.title.setText(currentNews.getTitle());
//        holder.subtitle.setText(currentNews.getDescription());
//        Glide.with(context)
//                .load(currentNews.getImageUrl())
//                .placeholder(R.drawable.default_image_news)
//                .error(R.drawable.default_image_not_found)
//                .into(holder.image);
//    }
//
//    @Override
//    public int getItemCount() {
//        return _items.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder{
//
//        TextView title, subtitle;
//        ImageView image;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            title = itemView.findViewById(R.id.tv_title_news);
//            subtitle = itemView.findViewById(R.id.tv_subtitle_news);
//            image = itemView.findViewById(R.id.image_news);
//        }
//    }
//}
