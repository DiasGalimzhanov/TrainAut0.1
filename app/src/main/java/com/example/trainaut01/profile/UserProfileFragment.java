package com.example.trainaut01.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trainaut01.BottomNavigationUpdater;
import com.example.trainaut01.LoginActivity;
import com.example.trainaut01.R;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.enums.PasswordAction;
import com.example.trainaut01.models.Avatar;
import com.example.trainaut01.repository.AvatarRepository;
import com.example.trainaut01.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

public class UserProfileFragment extends Fragment {

    @Inject
    AvatarRepository avatarRepository;

    @Inject
    UserRepository userRepository;

    private TextView parentName, emailTextView, phoneTextView, birthDateTextView, cityTextView, childNameTextView, childGenderDiagnosisTextView, childHeightWeightTextView;
    private ImageView userProfileImage, btnExit;
    private Button btnUpdateProfile, btnSupport, btnWatchConnect, btnNotes, btnDelete;
    private SharedPreferences sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        initDependencies();
        initViews(view);
        loadUserData();
        loadAvatar();
        setupListeners();
        return view;
    }

    private void initDependencies() {
        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
    }

    private void initViews(View view) {
        userProfileImage = view.findViewById(R.id.profile_image);
        cityTextView = view.findViewById(R.id.parent_city);
        parentName = view.findViewById(R.id.parent_name);
        emailTextView = view.findViewById(R.id.parent_email);
        phoneTextView = view.findViewById(R.id.parent_phone);
        btnWatchConnect = view.findViewById(R.id.watch_button);
        btnExit = view.findViewById(R.id.btnExit);
        btnUpdateProfile = view.findViewById(R.id.edit_profile_button);
        btnSupport = view.findViewById(R.id.support_button);
        btnNotes = view.findViewById(R.id.notes_button);
        btnDelete = view.findViewById(R.id.delete_button);
        birthDateTextView = view.findViewById(R.id.parent_bd);
        childNameTextView = view.findViewById(R.id.child_name);
        childGenderDiagnosisTextView = view.findViewById(R.id.child_gender_diagnosis);
        childHeightWeightTextView = view.findViewById(R.id.child_height_weight);
    }

    private void loadUserData() {
        sharedPref = requireActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);
        String firstName = sharedPref.getString("fullName", "");
        String email = sharedPref.getString("email", "");
        String city = sharedPref.getString("city", "");
        String phone = sharedPref.getString("phone", "");
        String birthDate = sharedPref.getString("birthDate", "");

        parentName.setText("Имя Фамилия: " + firstName);
        emailTextView.setText("Почта: " + email);
        cityTextView.setText("Город: " + city);
        phoneTextView.setText("Телефон: " + phone);
        birthDateTextView.setText("Дата рождения: " + birthDate);

        loadChildData();
    }

    private void loadChildData() {
        sharedPref = requireActivity().getSharedPreferences("child_data", getActivity().MODE_PRIVATE);
        String childName = sharedPref.getString("fullName", "");
        String childGender = sharedPref.getString("gender", "");
        String childDiagnosis = sharedPref.getString("diagnosis", "");
        float childHeight = sharedPref.getFloat("height", 0.0f);
        float childWeight = sharedPref.getFloat("weight", 0.0f);

        String childGenderDiagnosis = "Пол: " + Gender.fromString(childGender).getDisplayName() + " • Диагноз: " + childDiagnosis;
        String childHeightWeight = "Рост: " + childHeight + " • Вес: " + childWeight;

        childNameTextView.setText(childName);
        childGenderDiagnosisTextView.setText(childGenderDiagnosis);
        childHeightWeightTextView.setText(childHeightWeight);
    }

    private void loadAvatar() {
        sharedPref = requireActivity().getSharedPreferences("child_data", getActivity().MODE_PRIVATE);
        int exp = sharedPref.getInt("exp", 0);
        int level = exp / 5000;

        avatarRepository.getAvatarByLevel(level, new AvatarRepository.AvatarCallback() {
            @Override
            public void onSuccess(List<Avatar> avatars) {
                if (!avatars.isEmpty()) {
                    Avatar avatar = avatars.get(0);
                    Picasso.get().load(avatar.getUrlAvatar()).into(userProfileImage);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HOME", "Failed to load avatar", e);
            }
        });
    }

    private void setupListeners() {
        btnUpdateProfile.setOnClickListener(view -> showPasswordDialog(PasswordAction.UPDATE_PROFILE));
        btnDelete.setOnClickListener(view -> showPasswordDialog(PasswordAction.DELETE_ACCOUNT));
        btnExit.setOnClickListener(view -> logOutUser());
        btnSupport.setOnClickListener(view -> navigateToFragment(new SupportFragment()));
        btnWatchConnect.setOnClickListener(view -> navigateToFragment(new WatchFragment()));
        btnNotes.setOnClickListener(view -> navigateToFragment(new NoteFragment()));
    }

    private void logOutUser() {
        clearSharedPreferences("user_data");
        clearSharedPreferences("child_progress");
        clearSharedPreferences("child_data");

        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getActivity(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void clearSharedPreferences(String prefName) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(prefName, getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void showPasswordDialog(PasswordAction action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password, null);
        builder.setView(dialogView);

        final EditText input = dialogView.findViewById(R.id.et_password);
        Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            String password = input.getText().toString().trim();
            verifyPassword(password, action, dialog);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void verifyPassword(String password, PasswordAction action, AlertDialog dialog) {
        String email = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : null;

        if (email == null) {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        if (action == PasswordAction.UPDATE_PROFILE) {
                            Bundle bundle = new Bundle();
                            bundle.putString("PASSWORD_KEY", password);

                            UserUpdateFragment updateFragment = new UserUpdateFragment();
                            updateFragment.setArguments(bundle);

                            navigateToFragment(updateFragment);
                        } else if (action == PasswordAction.DELETE_ACCOUNT) {
                            deleteUserAccount();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Неправильный пароль", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void deleteUserAccount() {
        sharedPref = requireActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "");
        if (userId.isEmpty()) {
            Toast.makeText(requireContext(), "Не удалось определить пользователя", Toast.LENGTH_SHORT).show();
            return;
        }

        userRepository.deleteUserAccount(userId, requireContext(), aVoid -> {
            Toast.makeText(requireContext(), "Аккаунт удален", Toast.LENGTH_SHORT).show();
            logOutUser();
        }, e -> Toast.makeText(requireContext(), "Ошибка удаления аккаунта: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }



    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void updateBottomNavigation() {
        ((BottomNavigationUpdater) requireActivity()).updateBottomNavigationSelection(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation();
    }
}
