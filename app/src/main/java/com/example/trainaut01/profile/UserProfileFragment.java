package com.example.trainaut01.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trainaut01.LoginActivity;
import com.example.trainaut01.R;
import com.google.firebase.auth.FirebaseAuth;


public class UserProfileFragment extends Fragment {
    private TextView _parentName, _tvEmail, _tvPhone, _tvBirthDate;
    private ImageView _userProfileImage, _btnExit;
    private Button _btnUpdateProfile, _btnSupport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        init(view);
        printData();

        _userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new UserUpdateFragment());
                transaction.addToBackStack(null);  // Allows going back to the previous fragment
                transaction.commit();
            }
        });

        _btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new UserUpdateFragment());
                transaction.addToBackStack(null);  // Allows going back to the previous fragment
                transaction.commit();
            }
        });

        _btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();

                FirebaseAuth.getInstance().signOut();

                Toast.makeText(getActivity(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                getActivity().finish();
            }
        });

        _btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SupportFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    public void init(View view) {
        _userProfileImage = view.findViewById(R.id.userProfileImage);
        _parentName = view.findViewById(R.id.parentName);
        _tvEmail = view.findViewById(R.id.tvEmail);
        _tvPhone = view.findViewById(R.id.tvPhone);
        _tvBirthDate = view.findViewById(R.id.tvBirthDate);
        _btnExit = view.findViewById(R.id.btnExit);
        _btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        _btnSupport = view.findViewById(R.id.btnSupport);
    }

    public void printData() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);
        String firstName = sharedPref.getString("firstName", null);
        String lastName = sharedPref.getString("lastName", null);
        String email = sharedPref.getString("email", null);
        String phone = sharedPref.getString("phone", null);
        String birthDate = sharedPref.getString("birthDate", null);

        if (firstName != null && lastName != null && email != null && phone != null) {
            _parentName.setText(firstName + " " + lastName);
            _tvEmail.setText(email);
            _tvPhone.setText(phone);
            _tvBirthDate.setText(birthDate);
        }
    }
}
