package com.example.trainaut01.training;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trainaut01.R;

public class BaseTrainingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, new TrainingWeekFragment())
                    .commit();
        }
    }
}