package com.example.trainaut01.profileActivities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trainaut01.R;

public class UpdateUserProfileActivity extends AppCompatActivity {

    private EditText _etFirstNameUpdate, _etLastNameUpdate, _etEmailUpdate, _etPhoneUpdate, _etBirthDateUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_user_profile);
        init();
        addData_inEditTexts();
    }

    public void init(){
        _etFirstNameUpdate = findViewById(R.id.etFirstNameUpdate);
        _etLastNameUpdate = findViewById(R.id.etLastNameUpdate);
        _etEmailUpdate = findViewById(R.id.etEmailUpdate);
        _etPhoneUpdate = findViewById(R.id.etPhoneUpdate);
        _etBirthDateUpdate = findViewById(R.id.etBirthDateUpdate);
    }

    public void addData_inEditTexts(){
        SharedPreferences sharedPref = getSharedPreferences("user_data", MODE_PRIVATE);

        String firstName = sharedPref.getString("firstName", null);
        String lastName = sharedPref.getString("lastName", null);
        String email = sharedPref.getString("email", null);
        String phone = sharedPref.getString("phone", null);
        String birthDate = sharedPref.getString("birthDate", null);

        if(firstName != null && lastName != null && email != null && phone != null && birthDate != null){
            _etFirstNameUpdate.setText(firstName);
            _etLastNameUpdate.setText(lastName);
            _etEmailUpdate.setText(email);
            _etPhoneUpdate.setText(phone);
            _etBirthDateUpdate.setText(birthDate);
        }
    }
}