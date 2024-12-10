package com.example.trainaut01.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainaut01.R;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.models.Child;
import com.example.trainaut01.models.User;
import com.example.trainaut01.repository.ChildRepository;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.utils.SpinnerUtils;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

import javax.inject.Inject;

public class UserUpdateFragment extends Fragment {

    @Inject
    UserRepository userRepository;
    @Inject
    ChildRepository childRepository;

    private EditText etFullName_update, etPhone_update, etBirthDate_update, etCity_update, etEmailReg_update;
    private Spinner spGender_update;

    private EditText etPasReg_update, etPasConfirm_update;
    private TextView tvPasswordMatch_update;

    private EditText etFullNameChild_update, etBirthDateChild_update, etDiagnosisChild_update, etHeightChild_update, etWeightChild_update;
    private Spinner spGenderChild_update;

    private Button btnBack_update, btnContinue_update;

    private SharedPreferences userPrefs, childPrefs;
    private String PASS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_update, container, false);

        initDependencies();
        initViews(view);
        setupSpinners();
        loadUserDataFromPrefs();
        loadChildDataFromPrefs();
        setupListeners();

        return view;
    }

    private void initDependencies() {
        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
    }

    private void initViews(View view) {
        etFullName_update = view.findViewById(R.id.etFullName_update);
        etPhone_update = view.findViewById(R.id.etPhone_update);
        etBirthDate_update = view.findViewById(R.id.etBirthDate_update);
        etCity_update = view.findViewById(R.id.etCity_update);
        spGender_update = view.findViewById(R.id.spGender_update);
        etEmailReg_update = view.findViewById(R.id.etEmailReg_update);

        etPasReg_update = view.findViewById(R.id.etPasReg_update);
        etPasConfirm_update = view.findViewById(R.id.etPasConfirm_update);
        tvPasswordMatch_update = view.findViewById(R.id.tvPasswordMatch_update);

        etFullNameChild_update = view.findViewById(R.id.etFullNameChild_update);
        etBirthDateChild_update = view.findViewById(R.id.etBirthDateChild_update);
        spGenderChild_update = view.findViewById(R.id.spGenderChild_update);
        etDiagnosisChild_update = view.findViewById(R.id.etDiagnosisChild_update);
        etHeightChild_update = view.findViewById(R.id.etHeightChild_update);
        etWeightChild_update = view.findViewById(R.id.etWeightChild_update);

        btnBack_update = view.findViewById(R.id.btnBack_update);
        btnContinue_update = view.findViewById(R.id.btnContinue_update);

        userPrefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        childPrefs = requireActivity().getSharedPreferences("child_data", Context.MODE_PRIVATE);
    }

    /**
     * Инициализация спиннеров используя SpinnerUtils и Gender.
     */
    private void setupSpinners() {
        SpinnerUtils.setupGenderAdapter(requireContext(), spGender_update, Gender.getGenderValues());
        SpinnerUtils.setupGenderAdapter(requireContext(), spGenderChild_update, Gender.getGenderValues());
    }

    private void loadUserDataFromPrefs() {
        String fullName = userPrefs.getString("fullName", "");
        String phone = userPrefs.getString("phone", "");
        String birthDate = userPrefs.getString("birthDate", "");
        String city = userPrefs.getString("city", "");
        String genderStr = userPrefs.getString("gender", Gender.MALE.toString());
        String email = userPrefs.getString("email", "");

        if (getArguments() != null) {
            String password = getArguments().getString("PASSWORD_KEY", "");
            PASS = password;
            etPasReg_update.setText(password);
            etPasConfirm_update.setText(password);
        }

        etFullName_update.setText(fullName);
        etPhone_update.setText(phone);
        etBirthDate_update.setText(birthDate);
        etCity_update.setText(city);
        etEmailReg_update.setText(email);

        Gender userGender;
        try {
            userGender = Gender.fromString(genderStr);
        } catch (IllegalArgumentException e) {
            userGender = Gender.MALE;
        }

        setSpinnerSelection(spGender_update, userGender);
    }

    private void loadChildDataFromPrefs() {
        String childFullName = childPrefs.getString("fullName", "");
        String childBirthDate = childPrefs.getString("birthDate", "");
        String childGenderStr = childPrefs.getString("gender", Gender.MALE.toString());
        String diagnosis = childPrefs.getString("diagnosis", "");
        float height = childPrefs.getFloat("height", 0.0f);
        float weight = childPrefs.getFloat("weight", 0.0f);

        etFullNameChild_update.setText(childFullName);
        etBirthDateChild_update.setText(childBirthDate);
        etDiagnosisChild_update.setText(diagnosis);
        etHeightChild_update.setText(String.valueOf(height));
        etWeightChild_update.setText(String.valueOf(weight));

        Gender childGender;
        try {
            childGender = Gender.fromString(childGenderStr);
        } catch (IllegalArgumentException e) {
            childGender = Gender.MALE;
        }

        setSpinnerSelection(spGenderChild_update, childGender);
    }

    private void setupListeners() {
        btnBack_update.setOnClickListener(v -> navigateBackToProfile());

        btnContinue_update.setOnClickListener(v -> {
            if (checkPasswordMatch()) {
                updateDataInFirebase();
            }
        });
    }

    private boolean checkPasswordMatch() {
        String pass = etPasReg_update.getText().toString().trim();
        String passConf = etPasConfirm_update.getText().toString().trim();

        if (pass.equals(passConf)) {
            tvPasswordMatch_update.setText("Пароли совпадают");
            tvPasswordMatch_update.setTextColor(Color.GREEN);
            return true;
        } else {
            tvPasswordMatch_update.setText("Пароли не совпадают");
            tvPasswordMatch_update.setTextColor(Color.RED);
            return false;
        }
    }

    private void updateDataInFirebase() {
        String userId = userPrefs.getString("userId", "");
        if (userId.isEmpty()) {
            showToast("Не удалось определить пользователя");
            return;
        }

        String childId = childPrefs.getString("childId", "");
        if (childId.isEmpty()) {
            showToast("Не удалось определить ребенка");
            return;
        }

        User updatedUser = createUpdatedUser(userId);
        Child updatedChild = createUpdatedChild(childId);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showToast("Пользователь не авторизован");
            return;
        }

        String oldEmail = userPrefs.getString("email", "");
        String oldPassword = PASS;
        String newPass = etPasReg_update.getText().toString().trim();

        boolean passwordChanged = isPasswordChanged(newPass, oldPassword);

        if (!passwordChanged) {
            updateFirestoreData(updatedUser, updatedChild, userId);
            return;
        }

        reauthenticateAndChangePassword(oldEmail, oldPassword, newPass, () -> updateFirestoreData(updatedUser, updatedChild, userId));
    }

    private User createUpdatedUser(String userId) {
        String fullName = etFullName_update.getText().toString().trim();
        String phone = etPhone_update.getText().toString().trim();
        String birthDate = etBirthDate_update.getText().toString().trim();
        String city = etCity_update.getText().toString().trim();
        String email = etEmailReg_update.getText().toString().trim();
        String pass = etPasReg_update.getText().toString().trim();
        Gender userGender = getSelectedGender(spGender_update);

        return new User(userId, fullName, phone, birthDate, city, userGender, email, pass);
    }

    private Child createUpdatedChild(String childId) {
        String childFullName = etFullNameChild_update.getText().toString().trim();
        String childBirthDate = etBirthDateChild_update.getText().toString().trim();
        Gender childGender = getSelectedGender(spGenderChild_update);
        String diagnosis = etDiagnosisChild_update.getText().toString().trim();
        float height = parseFloatOrZero(etHeightChild_update.getText().toString().trim());
        float weight = parseFloatOrZero(etWeightChild_update.getText().toString().trim());

        return new Child(childId, childFullName, childBirthDate, childGender, diagnosis, height, weight);
    }

    private boolean isPasswordChanged(String newPass, String oldPassword) {
        return !newPass.equals(oldPassword);
    }

    private void reauthenticateAndChangePassword(String oldEmail, String oldPassword, String newPass, Runnable onSuccess) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showToast("Пользователь не авторизован");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, oldPassword);
        currentUser.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                updatePassword(currentUser, newPass, onSuccess);
            } else {
                showToast("Ошибка реаутентификации: " + (reauthTask.getException() != null ? reauthTask.getException().getMessage() : ""));
            }
        });
    }

    private void updatePassword(FirebaseUser currentUser, String newPass, Runnable onSuccess) {
        currentUser.updatePassword(newPass).addOnCompleteListener(passUpdateTask -> {
            if (passUpdateTask.isSuccessful()) {
                onSuccess.run();
            } else {
                showToast("Не удалось обновить пароль: " + (passUpdateTask.getException() != null ? passUpdateTask.getException().getMessage() : ""));
            }
        });
    }

    private void updateFirestoreData(User updatedUser, Child updatedChild, String userId) {
        updateUserData(updatedUser);
        updateChildData(userId, updatedChild);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private float parseFloatOrZero(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    private Gender getSelectedGender(Spinner spinner) {
        Object selectedObject = spinner.getSelectedItem();
        if (selectedObject == null) {
            return Gender.MALE;
        }
        String selected = selectedObject.toString();
        return Gender.fromString(selected);
    }

    private void setSpinnerSelection(Spinner spinner, Gender gender) {
        String[] genderValues = Gender.getGenderValues();
        int position = 0;
        for (int i = 0; i < genderValues.length; i++) {
            if (genderValues[i].equalsIgnoreCase(gender.getDisplayName())) {
                position = i;
                break;
            }
        }
        spinner.setSelection(position);
    }

    private void updateUserData(User updatedUser) {
        userRepository.updateUser(updatedUser, requireContext());
    }

    private void updateChildData(String userId, Child updatedChild) {
        childRepository.updateChild(userId, updatedChild, aVoid -> {
            Toast.makeText(requireContext(), "Данные ребенка обновлены", Toast.LENGTH_SHORT).show();
            navigateBackToProfile();
        }, e -> {
            Toast.makeText(requireContext(), "Ошибка обновления ребенка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void navigateBackToProfile() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new UserProfileFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

