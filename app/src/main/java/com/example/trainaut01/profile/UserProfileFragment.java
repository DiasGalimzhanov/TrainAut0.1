package com.example.trainaut01.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trainaut01.BottomNavigationUpdater;
import com.example.trainaut01.LoginActivity;
import com.example.trainaut01.R;
import com.google.firebase.auth.FirebaseAuth;


public class UserProfileFragment extends Fragment {
    private TextView _parentName, _tvEmail, _tvPhone;
    private ImageView _userProfileImage, _btnExit;
    private CardView _btnUpdateProfile, _btnSupport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        init(view);
        printData();

        _userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog();
            }
        });

        _btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordDialog();
            }
        });

        _btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userData = getActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);
                clearSharedPreference(userData);

                SharedPreferences userProgress = getActivity().getSharedPreferences("user_progress", getActivity().MODE_PRIVATE);
                clearSharedPreference(userProgress);

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
                transaction.addToBackStack(null);  // Allows going back to the previous fragment
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
        _btnExit = view.findViewById(R.id.btnExit);
        _btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        _btnSupport = view.findViewById(R.id.btnSupport);
    }

    private void clearSharedPreference(SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_password, null);
        builder.setView(dialogView);

        final EditText input = dialogView.findViewById(R.id.et_password);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = input.getText().toString();
                verifyPassword(password);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void verifyPassword(String password) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (email != null) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            goToUpdateProfile();
                        } else {
                            Toast.makeText(getActivity(), "Неправильный пароль", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void goToUpdateProfile() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new UserUpdateFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void printData() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);
        String firstName = sharedPref.getString("firstName", null);
        String lastName = sharedPref.getString("lastName", null);
        String email = sharedPref.getString("email", null);
        String phone = sharedPref.getString("phone", null);

        if (firstName != null && lastName != null && email != null && phone != null) {
            _parentName.setText(firstName + " " + lastName);
            _tvEmail.setText(email);
            _tvPhone.setText(phone);
        }
    }

    public void updateBottomNavigation() {
        ((BottomNavigationUpdater) getActivity()).updateBottomNavigationSelection(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation();
    }
}
