/**
 * Фрагмент для обновления данных пользователя и его ребенка.
 * Пользователь может изменить личную информацию, а также при необходимости изменить пароль.
 */
package com.example.trainaut01.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trainaut01.R;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.FragmentUserUpdateBinding;
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

    private FragmentUserUpdateBinding binding;
    private SharedPreferences userPrefs, childPrefs;
    private String oldPassword;

    private static final String USER_PREFS = "user_data";
    private static final String CHILD_PREFS = "child_data";

    /**
     * Инициализация Dagger-зависимостей.
     *
     * @param savedInstanceState предыдущее состояние фрагмента, если было.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
    }

    /**
     * Создание и инициализация макета фрагмента с помощью ViewBinding.
     *
     * @param inflater объект для "надувания" макета.
     * @param container родительский контейнер.
     * @param savedInstanceState предыдущее состояние фрагмента, если было.
     * @return корневой View фрагмента.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserUpdateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Вызывается, когда View фрагмента полностью создана.
     * Здесь происходит инициализация UI, загрузка данных и настройка слушателей.
     *
     * @param view корневой вид фрагмента.
     * @param savedInstanceState предыдущее состояние фрагмента, если было.
     */
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPreferences();
        setupSpinners();
        loadUserDataFromPrefs();
        loadChildDataFromPrefs();
        setupListeners();
    }

    /**
     * Освобождает ресурсы ViewBinding при уничтожении View.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Инициализация SharedPreferences.
     */
    private void initPreferences() {
        userPrefs = requireActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        childPrefs = requireActivity().getSharedPreferences(CHILD_PREFS, Context.MODE_PRIVATE);
    }

    /**
     * Настройка спиннеров для выбора пола.
     */
    private void setupSpinners() {
        SpinnerUtils.setupGenderAdapter(requireContext(), binding.spGenderUpdate, Gender.getGenderValues());
        SpinnerUtils.setupGenderAdapter(requireContext(), binding.spGenderChildUpdate, Gender.getGenderValues());
    }

    /**
     * Загрузка данных о пользователе из SharedPreferences и заполнение полей.
     */
    private void loadUserDataFromPrefs() {
        String fullName = userPrefs.getString("fullName", "");
        String phone = userPrefs.getString("phone", "");
        String birthDate = userPrefs.getString("birthDate", "");
        String city = userPrefs.getString("city", "");
        String genderStr = userPrefs.getString("gender", Gender.MALE.toString());
        String email = userPrefs.getString("email", "");

        if (getArguments() != null) {
            oldPassword = getArguments().getString("PASSWORD_KEY", "");
            binding.etPasRegUpdate.setText(oldPassword);
            binding.etPasConfirmUpdate.setText(oldPassword);
        }

        binding.etFullNameUpdate.setText(fullName);
        binding.etPhoneUpdate.setText(phone);
        binding.etBirthDateUpdate.setText(birthDate);
        binding.etCityUpdate.setText(city);
        // binding.etEmailRegUpdate.setText(email);

        Gender userGender;
        try {
            userGender = Gender.fromString(genderStr);
        } catch (IllegalArgumentException e) {
            userGender = Gender.MALE;
        }

        setSpinnerSelection(binding.spGenderUpdate, userGender);
    }

    /**
     * Загрузка данных о ребенке из SharedPreferences и заполнение полей.
     */
    private void loadChildDataFromPrefs() {
        String childFullName = childPrefs.getString("fullName", "");
        String childBirthDate = childPrefs.getString("birthDate", "");
        String childGenderStr = childPrefs.getString("gender", Gender.MALE.toString());
        String diagnosis = childPrefs.getString("diagnosis", "");
        float height = childPrefs.getFloat("height", 0.0f);
        float weight = childPrefs.getFloat("weight", 0.0f);

        binding.etFullNameChildUpdate.setText(childFullName);
        binding.etBirthDateChildUpdate.setText(childBirthDate);
        binding.etDiagnosisChildUpdate.setText(diagnosis);
        binding.etHeightChildUpdate.setText(String.valueOf(height));
        binding.etWeightChildUpdate.setText(String.valueOf(weight));

        Gender childGender;
        try {
            childGender = Gender.fromString(childGenderStr);
        } catch (IllegalArgumentException e) {
            childGender = Gender.MALE;
        }

        setSpinnerSelection(binding.spGenderChildUpdate, childGender);
    }

    /**
     * Настройка слушателей кнопок и проверки пароля.
     */
    private void setupListeners() {
        binding.btnBackUpdate.setOnClickListener(v -> navigateBackToProfile());

        binding.btnContinueUpdate.setOnClickListener(v -> {
            if (checkPasswordMatch()) {
                updateDataInFirebase();
            }
        });
    }

    /**
     * Проверка совпадения введенных паролей.
     *
     * @return true, если пароли совпадают, false иначе.
     */
    private boolean checkPasswordMatch() {
        String pass = binding.etPasRegUpdate.getText().toString().trim();
        String passConf = binding.etPasConfirmUpdate.getText().toString().trim();

        if (pass.equals(passConf)) {
            binding.tvPasswordMatchUpdate.setText("Пароли совпадают");
            binding.tvPasswordMatchUpdate.setTextColor(Color.GREEN);
            return true;
        } else {
            binding.tvPasswordMatchUpdate.setText("Пароли не совпадают");
            binding.tvPasswordMatchUpdate.setTextColor(Color.RED);
            return false;
        }
    }

    /**
     * Обновление данных пользователя и ребенка в Firebase.
     */
    private void updateDataInFirebase() {
        String userId = userPrefs.getString("userId", "");
        if (TextUtils.isEmpty(userId)) {
            showToast("Не удалось определить пользователя");
            return;
        }

        String childId = childPrefs.getString("childId", "");
        if (TextUtils.isEmpty(childId)) {
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
        String oldPass = oldPassword;
        String newPass = binding.etPasRegUpdate.getText().toString().trim();

        boolean passwordChanged = !newPass.equals(oldPass);

        if (!passwordChanged) {
            updateFirestoreData(updatedUser, updatedChild, userId);
            return;
        }

        reauthenticateAndChangePassword(oldEmail, oldPass, newPass,
                () -> updateFirestoreData(updatedUser, updatedChild, userId));
    }

    /**
     * Создает объект User с обновленными данными.
     *
     * @param userId идентификатор пользователя.
     * @return объект User с новыми данными.
     */
    private User createUpdatedUser(String userId) {
        String fullName = binding.etFullNameUpdate.getText().toString().trim();
        String phone = binding.etPhoneUpdate.getText().toString().trim();
        String birthDate = binding.etBirthDateUpdate.getText().toString().trim();
        String city = binding.etCityUpdate.getText().toString().trim();
        // String email = binding.etEmailRegUpdate.getText().toString().trim();
        String email = userPrefs.getString("email", "");

        String pass = binding.etPasRegUpdate.getText().toString().trim();
        Gender userGender = getSelectedGender(binding.spGenderUpdate);

        return new User(userId, fullName, phone, birthDate, city, userGender, email, pass);
    }

    /**
     * Создает объект Child с обновленными данными.
     *
     * @param childId идентификатор ребенка.
     * @return объект Child с новыми данными.
     */
    private Child createUpdatedChild(String childId) {
        String childFullName = binding.etFullNameChildUpdate.getText().toString().trim();
        String childBirthDate = binding.etBirthDateChildUpdate.getText().toString().trim();
        Gender childGender = getSelectedGender(binding.spGenderChildUpdate);
        String diagnosis = binding.etDiagnosisChildUpdate.getText().toString().trim();
        float height = parseFloatOrZero(binding.etHeightChildUpdate.getText().toString().trim());
        float weight = parseFloatOrZero(binding.etWeightChildUpdate.getText().toString().trim());

        return new Child(childId, childFullName, childBirthDate, childGender, diagnosis, height, weight);
    }

    /**
     * Пытается реаутентифицировать пользователя и, при успехе, обновить пароль.
     *
     * @param oldEmail старый email.
     * @param oldPassword старый пароль.
     * @param newPass новый пароль.
     * @param onSuccess действие при успешном обновлении пароля.
     */
    private void reauthenticateAndChangePassword(String oldEmail, String oldPassword, String newPass, Runnable onSuccess) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showToast("Пользователь не авторизован");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, oldPassword);
        currentUser.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                currentUser.updatePassword(newPass).addOnCompleteListener(passUpdateTask -> {
                    if (passUpdateTask.isSuccessful()) {
                        onSuccess.run();
                    } else {
                        showToast("Не удалось обновить пароль: " + (passUpdateTask.getException() != null ? passUpdateTask.getException().getMessage() : ""));
                    }
                });
            } else {
                showToast("Ошибка реаутентификации: " + (reauthTask.getException() != null ? reauthTask.getException().getMessage() : ""));
            }
        });
    }

    /**
     * Обновляет данные пользователя и ребенка в Firestore.
     *
     * @param updatedUser обновленные данные пользователя.
     * @param updatedChild обновленные данные ребенка.
     * @param userId идентификатор пользователя.
     */
    private void updateFirestoreData(User updatedUser, Child updatedChild, String userId) {
        userRepository.updateUser(updatedUser, requireContext());
        childRepository.updateChild(userId, updatedChild, aVoid -> {
            showToast("Данные ребенка обновлены");
            navigateBackToProfile();
        }, e -> {
            showToast("Ошибка обновления ребенка: " + e.getMessage());
        });
    }

    /**
     * Отображает Toast-сообщение.
     *
     * @param message текст сообщения.
     */
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Преобразует строку в число с плавающей точкой, возвращает 0.0f при ошибке.
     *
     * @param value строковое значение.
     * @return число float или 0.0f при ошибке.
     */
    private float parseFloatOrZero(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    /**
     * Получает выбранный пол из спиннера.
     *
     * @param spinner спиннер выбора пола.
     * @return выбранное значение Gender.
     */
    private Gender getSelectedGender(android.widget.Spinner spinner) {
        Object selectedObject = spinner.getSelectedItem();
        if (selectedObject == null) {
            return Gender.MALE;
        }
        String selected = selectedObject.toString();
        return Gender.fromString(selected);
    }

    /**
     * Устанавливает выбранный элемент спиннера в соответствии с переданным полом.
     *
     * @param spinner спиннер.
     * @param gender пол для установки.
     */
    private void setSpinnerSelection(android.widget.Spinner spinner, Gender gender) {
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

    /**
     * Возвращается к фрагменту профиля пользователя.
     */
    private void navigateBackToProfile() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new UserProfileFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
