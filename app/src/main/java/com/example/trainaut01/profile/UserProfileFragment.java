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
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.models.Avatar;
import com.example.trainaut01.repository.AvatarRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;


public class UserProfileFragment extends Fragment {
    private TextView _parentName, _tvEmail, _tvPhone, _tvBd, _city , _child_name, _child_gender_diagnosis, _child_height_weight;
    private ImageView _userProfileImage, _btnExit;
    private Button _btnUpdateProfile, _btnSupport, _btnWatchConnect;
    private AppComponent appComponent;
    private SharedPreferences sharedPref;

    @Inject
    AvatarRepository avatarRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        init(view);
        printData();
        loadAvatar();

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

                SharedPreferences userProgress = getActivity().getSharedPreferences("child_progress", getActivity().MODE_PRIVATE);
                clearSharedPreference(userProgress);

                SharedPreferences childData = getActivity().getSharedPreferences("child_data", getActivity().MODE_PRIVATE);
                clearSharedPreference(childData);

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

        _btnWatchConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new WatchFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    public void init(View view) {
        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
        _userProfileImage = view.findViewById(R.id.profile_image);
        _city = view.findViewById(R.id.parent_city);
        _parentName = view.findViewById(R.id.parent_name);
        _tvEmail = view.findViewById(R.id.parent_email);
        _tvPhone = view.findViewById(R.id.parent_phone);
        _btnWatchConnect = view.findViewById(R.id.watch_button);
        _btnExit = view.findViewById(R.id.btnExit);
        _btnUpdateProfile = view.findViewById(R.id.edit_profile_button);
        _btnSupport = view.findViewById(R.id.support_button);
        _tvBd = view.findViewById(R.id.parent_bd);
        _child_name = view.findViewById(R.id.child_name);
        _child_gender_diagnosis = view.findViewById(R.id.child_gender_diagnosis);
        _child_height_weight = view.findViewById(R.id.child_height_weight);

    }

    private void loadAvatar(){
        sharedPref = getActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);
        int exp = sharedPref.getInt("exp", 0);
        int lvl = exp / 5000;
        Log.d("HOME", "User experience: " + exp);

        avatarRepository.getAvatarByLevel(lvl, new AvatarRepository.AvatarCallback() {
            @Override
            public void onSuccess(List<Avatar> avatars) {
                if (!avatars.isEmpty()) {
                    Avatar avatar = avatars.get(0);
                    Picasso.get().load(avatar.getUrlAvatar()).into(_userProfileImage);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HOME", "Failed to load avatar", e);
            }
        });
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
        sharedPref = getActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);
        Log.d("USER DATA", sharedPref.toString());

        String firstName = sharedPref.getString("fullName", null);
        String email = sharedPref.getString("email", null);
        String city = sharedPref.getString("city", null);
        String phone = sharedPref.getString("phone", null);
        String birthDate = sharedPref.getString("birthDate", null);

        if (firstName != null && email != null && phone != null) {
            _parentName.setText("Имя Фамилия: " + firstName);
            _tvEmail.setText("Почта: " + email);
            _city.setText("Город: " + city);
            _tvPhone.setText("Телефон: " + phone);
            _tvBd.setText("Дата рождения: " + birthDate);
        }

        sharedPref = getActivity().getSharedPreferences("child_data", getActivity().MODE_PRIVATE);
        String childName = sharedPref.getString("fullName", null);
        String childGender = sharedPref.getString("gender", null);
        String childDiagnosis = sharedPref.getString("diagnosis", null);

        float childHeight = sharedPref.getFloat("height", 0.0f);
        float childWeight = sharedPref.getFloat("weight", 0.0f);

        String childGenderDiagnosis = "Пол: " + Gender.fromString(childGender).getDisplayName() + " • " + "Диагноз: " + childDiagnosis;
        String childHeightWeight = "Рост: " + childHeight + " • " + "Вес: " + childWeight;

        _child_name.setText(childName);
        _child_gender_diagnosis.setText(childGenderDiagnosis);
        _child_height_weight.setText(childHeightWeight);
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
