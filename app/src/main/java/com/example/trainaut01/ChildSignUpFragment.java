package com.example.trainaut01;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.FragmentChildSignUpBinding;
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

/**
 * Fragment для регистрации ребенка.
 * Обеспечивает ввод данных о ребенке, их проверку и сохранение.
 */
public class ChildSignUpFragment extends Fragment {

    private FragmentChildSignUpBinding _binding;

    private static final String ARG_USER = "user";
    private User _user;

    @Inject
    UserRepository _userRepository;

    @Inject
    ChildRepository _childRepository;

    @Inject
    DayPlanRepository _dayPlanRepository;

    /**
     * Создает новый экземпляр ChildSignUpFragment с передачей объекта пользователя.
     *
     * @param user объект пользователя
     * @return экземпляр ChildSignUpFragment
     */
    public static ChildSignUpFragment newInstance(User user) {
        ChildSignUpFragment fragment = new ChildSignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, new Gson().toJson(user));
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Вызывается при создании фрагмента для инициализации переданного объекта пользователя.
     *
     * @param savedInstanceState сохраненное состояние (может быть null)
     */
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

    /**
     * Создает и возвращает представление для фрагмента.
     *
     * @param inflater  объект LayoutInflater для создания представлений
     * @param container контейнер для представления (может быть null)
     * @param savedInstanceState сохраненное состояние (может быть null)
     * @return корневое представление фрагмента
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _binding = FragmentChildSignUpBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    /**
     * Вызывается после создания представления фрагмента.
     *
     * @param view корневое представление фрагмента
     * @param savedInstanceState сохраненное состояние (может быть null)
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setupBirthDateChildPicker();
    }

    /**
     * Вызывается при уничтожении представления фрагмента.
     * Очищает объект binding для предотвращения утечек памяти.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    /**
     * Инициализирует компоненты, такие как Dagger и обработчики кнопок.
     */
    private void init() {
        AppComponent _appComponent = DaggerAppComponent.create();
        _appComponent.inject(ChildSignUpFragment.this);

        Gender.initializeLocalizedNames(requireContext());

        _binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        _binding.btnContinue.setOnClickListener(v -> handleContinue());

        SpinnerUtils.setupGenderAdapter(ChildSignUpFragment.this.getContext(), _binding.spGenderChild, Gender.getGenderValues());
    }

    /**
     * Обрабатывает нажатие кнопки "Продолжить", проверяя корректность введенных данных.
     */
    private void handleContinue() {
        Gender gender = Gender.fromString(_binding.spGenderChild.getSelectedItem().toString().trim());

        if (ValidationUtils.areFieldsFilled(
                _binding.etFullNameChild.getText().toString().trim(),
                _binding.etBirthDateChild.getText().toString().trim(),
                _binding.etDiagnosisChild.getText().toString().trim(),
                _binding.etHeightChild.getText().toString().trim(),
                _binding.etWeightChild.getText().toString().trim())) {
            ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), getString(R.string.fill_all_fields));
            return;
        }

        float height, weight;
        try {
            height = Float.parseFloat(_binding.etHeightChild.getText().toString().trim());
            weight = Float.parseFloat(_binding.etWeightChild.getText().toString().trim());
        } catch (NumberFormatException e) {
            ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), getString(R.string.height_weght_numeric));
            return;
        }

        if (_user == null) {
            ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), getString(R.string.error_user_not_found));
            return;
        }

        registerUser(
                _binding.etFullNameChild.getText().toString().trim(),
                _binding.etBirthDateChild.getText().toString().trim(),
                _binding.etDiagnosisChild.getText().toString().trim(),
                height, weight, gender);
    }

    /**
     * Регистрирует нового пользователя и добавляет данные о ребенке.
     *
     * @param fullName    полное имя ребенка
     * @param birthDate   дата рождения ребенка
     * @param diagnosis   диагноз ребенка
     * @param height      рост ребенка
     * @param weight      вес ребенка
     * @param gender      пол ребенка
     */
    private void registerUser(String fullName, String birthDate, String diagnosis, float height, float weight, Gender gender) {
        _userRepository.addUser(_user, ChildSignUpFragment.this.getContext(), task -> {
            if (task.isSuccessful()) {
                String newUserId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                addChild(newUserId, fullName, birthDate, diagnosis, height, weight, gender);
            } else {
                ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), getString(R.string.error_register_user) + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    /**
     * Добавляет данные о ребенке в базу данных.
     *
     * @param userId      идентификатор пользователя
     * @param fullName    полное имя ребенка
     * @param birthDate   дата рождения ребенка
     * @param diagnosis   диагноз ребенка
     * @param height      рост ребенка
     * @param weight      вес ребенка
     * @param gender      пол ребенка
     */
    private void addChild(String userId, String fullName, String birthDate, String diagnosis, float height, float weight, Gender gender) {
        Child child = new Child(fullName, birthDate, gender, diagnosis, height, weight);

        _childRepository.addChild(userId, child, ChildSignUpFragment.this.getContext(), aVoid -> {
            addDayPlans(userId);
        }, error -> {
            ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), getString(R.string.error_add_child) + error.getMessage());
        });
    }

    /**
     * Добавляет планы на день для пользователя.
     *
     * @param userId идентификатор пользователя
     */
    private void addDayPlans(String userId) {
        _dayPlanRepository.addAllDayPlansToUser(userId, unused -> {
            goToInstructionsForUseFragment();
        }, error -> {
            ToastUtils.showShortMessage(ChildSignUpFragment.this.getContext(), getString(R.string.error_add_dayplans) + error.getMessage());
        });
    }

    /**
     * Переход к фрагменту с инструкциями для пользователя.
     */
    private void goToInstructionsForUseFragment(){
        InstructionsForUseFragment instructionsFragment = new InstructionsForUseFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.signUp_container, instructionsFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Настраивает выбор даты рождения ребенка через DatePicker.
     */
    private void setupBirthDateChildPicker() {
        _binding.etBirthDateChild.setOnClickListener(v -> DateUtils.showDatePickerDialog(ChildSignUpFragment.this.getContext(), _binding.etBirthDateChild));
    }

}
