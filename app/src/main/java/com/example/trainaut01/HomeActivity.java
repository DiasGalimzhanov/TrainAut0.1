package com.example.trainaut01;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.adapters.NewsAdapter;
import com.example.trainaut01.models.News;
import com.example.trainaut01.profileActivities.UserProfileActivity;
import com.example.trainaut01.training.BaseTrainingActivity;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterNews;
    private RecyclerView recyclerViewNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        initRecyclerView();
        profileInit();

    }

    private void profileInit() {
        LinearLayout profileBtn = findViewById(R.id.btnProfilePage);
        profileBtn.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, UserProfileActivity.class)));
    }

    private void TrainingInit(){
        LinearLayout trainingBtn = findViewById(R.id.btnTrainingPage);
        trainingBtn.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, BaseTrainingActivity.class)));
    }

    private void initRecyclerView() {
        List<News> items = new ArrayList<>();
        items.add(new News("Помощь в адаптации через занятия",
                "Дети с расстройствами аутистического спектра нуждаются в " +
                        "особой поддержке", R.drawable.news1));
        items.add(new News("Почему тренировки важны для детей с аутизмом?",
                "Физическая активность и моторное развитие", R.drawable.news2));
        items.add(new News("Почему тренировки важны для детей с аутизмом?",
                "Физическая активность и моторное развитие", R.drawable.news2));

        recyclerViewNews = findViewById(R.id.RecyclerView1);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterNews = new NewsAdapter(items);
        recyclerViewNews.setAdapter(adapterNews);
    }
}
