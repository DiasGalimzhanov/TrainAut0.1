package com.example.trainaut01.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.models.Achievement;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AchievementAdapter extends BaseAdapter {
    private Context context;
    private List<Achievement> achievementList;

    public AchievementAdapter(Context context, List<Achievement> achievementList) {
        this.context = context;
        this.achievementList = achievementList;
    }

    @Override
    public int getCount() {
        return achievementList.size();
    }

    @Override
    public Object getItem(int position) {
        return achievementList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_achiv, parent, false);
        }

//        int countDays = 51;
        SharedPreferences sharedPref = context.getSharedPreferences("child_data", Context.MODE_PRIVATE);
        int countDays = sharedPref.getInt("countDays", 0);


        Achievement achievement = achievementList.get(position);

        if (achievement.getDay() <= countDays) {
        ImageView imageView = convertView.findViewById(R.id.item_image);
        TextView textView = convertView.findViewById(R.id.item_text);

        Picasso.get()
                .load(achievement.getImageUrl())
                .into(imageView);

        textView.setText(achievement.getDescription());
        }
        return convertView;
    }

    public void setAchievementList(List<Achievement> achievements) {
        this.achievementList = achievements;
        notifyDataSetChanged();
    }
}