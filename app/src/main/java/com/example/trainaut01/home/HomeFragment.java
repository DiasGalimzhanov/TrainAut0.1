package com.example.trainaut01.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.R;
import com.example.trainaut01.adapters.NewsAdapter;
import com.example.trainaut01.models.News;
import com.example.trainaut01.profileActivities.UserProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView.Adapter adapterNews;
    private RecyclerView recyclerViewNews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initRecyclerView(view);
//        profileInit(view);

        return view;
    }

    private void profileInit(View view) {
        LinearLayout profileBtn = view.findViewById(R.id.btnProfilePage);
        profileBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), UserProfileActivity.class)));
    }

    private void initRecyclerView(View view) {
        List<News> items = new ArrayList<>();
        items.add(new News("Помощь в адаптации через занятия",
                "Дети с расстройствами аутистического спектра нуждаются в " +
                        "особой поддержке", R.drawable.news1));
        items.add(new News("Почему тренировки важны для детей с аутизмом?",
                "Физическая активность и моторное развитие", R.drawable.news2));
        items.add(new News("Почему тренировки важны для детей с аутизмом?",
                "Физическая активность и моторное развитие", R.drawable.news2));

        recyclerViewNews = view.findViewById(R.id.RecyclerView1);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        adapterNews = new NewsAdapter(items);
        recyclerViewNews.setAdapter(adapterNews);
    }
}
