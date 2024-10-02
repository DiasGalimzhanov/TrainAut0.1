package com.example.trainaut01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trainaut01.R;
import com.example.trainaut01.models.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    List<News> _items;
    Context context;

    public NewsAdapter(List<News> items) {
        this._items = items;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflator = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new ViewHolder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        News currentNews = _items.get(position);
        holder.title.setText(currentNews.getTitle());
        holder.subtitle.setText(currentNews.getSubtitle());
        Glide.with(context)
                .load(currentNews.getImageUrl())
                .placeholder(R.drawable.default_image_news)
                .error(R.drawable.default_image_not_found)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return _items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title, subtitle;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title_news);
            subtitle = itemView.findViewById(R.id.tv_subtitle_news);
            image = itemView.findViewById(R.id.image_news);
        }
    }
}
