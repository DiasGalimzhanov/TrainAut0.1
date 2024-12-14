package com.example.trainaut01;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.models.Child;
import com.example.trainaut01.models.User;
import com.example.trainaut01.repository.ChildRepository;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.utils.DateUtils;
import com.example.trainaut01.utils.SpinnerUtils;
import com.example.trainaut01.utils.ToastUtils;
import com.example.trainaut01.utils.ValidationUtils;
import com.google.gson.Gson;

import java.util.Objects;

import javax.inject.Inject;

public class ChildSignUpFragment extends Fragment {

    private static final String ARG_USER = "user";
    private User _user;

    private EditText _etFullNameChild, _etBirthDateChild, _etDiagnosisChild, _etHeightChild, _etWeightChild;
    private Spinner _spGenderChild;

    @Inject
    UserRepository _userRepository;

    @Inject
    ChildRepository _childRepository;

    @Inject
    DayPlanRepository _dayPlanRepository;

    public static ChildSignUpFragment newInstance(User user) {
        ChildSignUpFragment fragment = new ChildSignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, new Gson().toJson(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String userJson = getArguments().getString(ARG_USER);
            if (userJson != null) {
                _user = new Gson().fromJson(userJson, User.class);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setupUI();
    }

    private void init(View view) {
        AppComponent _appComponent = DaggerAppComponent.create();
        _appComponent.inject(ChildSignUpFragment.this);

        _etFullNameChild = view.findViewById(R.id.etFullNameChild);
        _etBirthDateChild = view.findViewById(R.id.etBirthDateChild);
        _etDiagnosisChild = view.findViewById(R.id.etDiagnosisChild);
        _etHeightChild = view.findViewById(R.id.etHeightChild);
        _etWeightChild = view.findViewById(R.id.etWeightChild);
        _spGenderChild = view.findViewById(R.id.spGenderChild);

        Button _btnContinue = view.findViewById(R.id.btnContinue);
        Button _btnBack = view.findViewById(R.id.btnBack);

        _btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        _btnContinue.setOnClickListener(v -> handleContinue());

        SpinnerUtils.setupGenderAdapter(ChildSignUpFragment.this.getContext(), _spGenderChild, Gender.getGenderValues());
    }

    private void handleContinue() {
        String fullName = _etFullNameChild.getText().toString().trim();
        String birthDate = _etBirthDateChild.getText().toString().trim();
        String diagnosis = _etDiagnosisChild.getText().toString().trim();
        String heightStr = _etHeightChild.getText().toString().trim();
        String weightStr = _etWeightChild.getText().toString().trim();
        Gender gender = Gender.fromString(_spGenderChild.getSelectedItem().toString().trim());

        if (!ValidationUtils.areFieldsFilled(fullName, birthDate, diagnosis, heightStr, weightStr)) {
            ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), "Пожалуйста, заполните все поля.");
            return;
        }

        float height, weight;
        try {
            height = Float.parseFloat(heightStr);
            weight = Float.parseFloat(weightStr);
        } catch (NumberFormatException e) {
            ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), "Рост и вес должны быть числовыми значениями.");
            return;
        }

        if (_user == null) {
            ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), "Ошибка: пользователь не найден.");
            return;
        }

        registerUser(fullName, birthDate, diagnosis, height, weight, gender);
    }

    private void registerUser(String fullName, String birthDate, String diagnosis, float height, float weight, Gender gender) {
        _userRepository.addUser(_user, ChildSignUpFragment.this.getContext(), task -> {
            if (task.isSuccessful()) {
                String newUserId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                addChild(newUserId, fullName, birthDate, diagnosis, height, weight, gender);
            } else {
                ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), "Ошибка регистрации пользователя: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private void addChild(String userId, String fullName, String birthDate, String diagnosis, float height, float weight, Gender gender) {
        Child child = new Child(fullName, birthDate, gender, diagnosis, height, weight);

        _childRepository.addChild(userId, child, ChildSignUpFragment.this.getContext(), aVoid -> {
            addDayPlans(userId);
        }, error -> {
            ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), "Ошибка добавления ребенка: " + error.getMessage());
        });
    }


    private void addDayPlans(String userId) {
        _dayPlanRepository.addAllDayPlansToUser(userId, unused -> {
            goToInstructionsForUseFragment();
        }, error -> {
            ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), "Ошибка добавления DayPlans: " + error.getMessage());
        });
    }

    private void goToInstructionsForUseFragment(){
        InstructionsForUseFragment instructionsFragment = new InstructionsForUseFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.signUp_container, instructionsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupUI() {
        setupBirthDateChildPicker();
    }

    private void setupBirthDateChildPicker() {
        _etBirthDateChild.setOnClickListener(v -> DateUtils.showDatePickerDialog(ChildSignUpFragment.this.getContext(), _etBirthDateChild));
    }

}
