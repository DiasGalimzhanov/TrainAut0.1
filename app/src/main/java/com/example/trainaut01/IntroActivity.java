package com.example.trainaut01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {
    Button _btnLogIn, _btnReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            Intent intent = new Intent(IntroActivity.this, BaseActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_intro);

        init();

        _btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            }
        });

        _btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IntroActivity.this, SignUpActivity.class));
            }
        });

    }

    private void init(){
        _btnReg = findViewById(R.id.btnRegister);
        _btnLogIn = findViewById(R.id.btnLogIn);
    }
}