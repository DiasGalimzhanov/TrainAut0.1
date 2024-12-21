package com.example.trainaut01.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trainaut01.R;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.models.Child;
import com.example.trainaut01.models.User;
import com.example.trainaut01.repository.ChildRepository;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.utils.SharedPreferencesUtils;
import com.example.trainaut01.utils.SpinnerUtils;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

/**
 * Фрагмент для обновления данных пользователя и его ребенка.
 * Пользователь может изменить личную информацию, а также при необходимости изменить пароль.
 */
public class UserUpdateFragment extends Fragment {

    @Inject
    UserRepository userRepository;

    @Inject
    ChildRepository childRepository;

    private EditText etFullNameUpdate, etPhoneUpdate, etBirthDateUpdate, etCityUpdate, etPasRegUpdate, etPasConfirmUpdate;
    private EditText etFullNameChildUpdate, etBirthDateChildUpdate, etDiagnosisChildUpdate, etHeightChildUpdate, etWeightChildUpdate;
    private Spinner spGenderUpdate, spGenderChildUpdate;
    private Button btnBackUpdate, btnContinueUpdate;
    private TextView tvPasswordMatchUpdate;
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
     * Создание и инициализация макета фрагмента.
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
        return inflater.inflate(R.layout.fragment_user_update, container, false);
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
        initViews(view);
        setupSpinners();
        loadUserDataFromPrefs();
        loadChildDataFromPrefs();
        setupListeners();
    }

    /**
     * Инициализирует элементы интерфейса.
     */
    private void initViews(View view) {
        etFullNameUpdate = view.findViewById(R.id.et_full_name_update);
        etPhoneUpdate = view.findViewById(R.id.et_phone_update);
        etBirthDateUpdate = view.findViewById(R.id.et_birth_date_update);
        etCityUpdate = view.findViewById(R.id.et_city_update);
        etPasRegUpdate = view.findViewById(R.id.et_pas_update);
        etPasConfirmUpdate = view.findViewById(R.id.et_pas_confirm_update);
        tvPasswordMatchUpdate = view.findViewById(R.id.tv_pas_match_update);

        etFullNameChildUpdate = view.findViewById(R.id.et_full_name_child_update);
        etBirthDateChildUpdate = view.findViewById(R.id.et_birth_date_child_update);
        etDiagnosisChildUpdate = view.findViewById(R.id.et_diagnosis_child_update);
        etHeightChildUpdate = view.findViewById(R.id.et_height_child_update);
        etWeightChildUpdate = view.findViewById(R.id.et_weight_child_update);

        spGenderUpdate = view.findViewById(R.id.sp_gender_update);
        spGenderChildUpdate = view.findViewById(R.id.sp_gender_child_update);

        btnBackUpdate = view.findViewById(R.id.btn_back_update);
        btnContinueUpdate = view.findViewById(R.id.btn_continue_update);
    }

    /**
     * Настройка спиннеров для выбора пола.
     */
    private void setupSpinners() {
        SpinnerUtils.setupGenderAdapter(requireContext(), spGenderUpdate, Gender.getGenderValues());
        SpinnerUtils.setupGenderAdapter(requireContext(), spGenderChildUpdate, Gender.getGenderValues());
    }

    /**
     * Загрузка данных о пользователе из SharedPreferences и заполнение полей.
     */
    private void loadUserDataFromPrefs() {
        String fullName = SharedPreferencesUtils.getString(requireContext(), USER_PREFS, "fullName", "");
        String phone = SharedPreferencesUtils.getString(requireContext(), USER_PREFS, "phone", "");
        String birthDate = SharedPreferencesUtils.getString(requireContext(), USER_PREFS, "birthDate", "");
        String city = SharedPreferencesUtils.getString(requireContext(), USER_PREFS, "city", "");
        String genderStr = SharedPreferencesUtils.getString(requireContext(), USER_PREFS, "gender", Gender.MALE.toString());

        if (getArguments() != null) {
            oldPassword = getArguments().getString("PASSWORD_KEY", "");
            etPasRegUpdate.setText(oldPassword);
            etPasConfirmUpdate.setText(oldPassword);
        }

        etFullNameUpdate.setText(fullName);
        etPhoneUpdate.setText(phone);
        etBirthDateUpdate.setText(birthDate);
        etCityUpdate.setText(city);

        Gender userGender = Gender.fromString(genderStr);
        setSpinnerSelection(spGenderUpdate, userGender);
    }

    /**
     * Загрузка данных о ребенке из SharedPreferences и заполнение полей.
     */
    private void loadChildDataFromPrefs() {
        String childFullName = SharedPreferencesUtils.getString(requireContext(), CHILD_PREFS, "fullName", "");
        String childBirthDate = SharedPreferencesUtils.getString(requireContext(), CHILD_PREFS, "birthDate", "");
        String childGenderStr = SharedPreferencesUtils.getString(requireContext(), CHILD_PREFS, "gender", Gender.MALE.toString());
        String diagnosis = SharedPreferencesUtils.getString(requireContext(), CHILD_PREFS, "diagnosis", "");
        float height = SharedPreferencesUtils.getFloat(requireContext(), CHILD_PREFS, "height", 0);
        float weight = SharedPreferencesUtils.getFloat(requireContext(), CHILD_PREFS, "weight", 0);

        etFullNameChildUpdate.setText(childFullName);
        etBirthDateChildUpdate.setText(childBirthDate);
        etDiagnosisChildUpdate.setText(diagnosis);
        etHeightChildUpdate.setText(String.valueOf(height));
        etWeightChildUpdate.setText(String.valueOf(weight));

        Gender childGender = Gender.fromString(childGenderStr);
        setSpinnerSelection(spGenderChildUpdate, childGender);
    }

    /**
     * Настройка слушателей кнопок и проверки пароля.
     */
    private void setupListeners() {
        btnBackUpdate.setOnClickListener(v -> navigateBackToProfile());

        btnContinueUpdate.setOnClickListener(v -> {
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
        String pass = etPasRegUpdate.getText().toString().trim();
        String passConf = etPasConfirmUpdate.getText().toString().trim();

        if (pass.equals(passConf)) {
            tvPasswordMatchUpdate.setText(getString(R.string.passwords_match));
            tvPasswordMatchUpdate.setTextColor(Color.GREEN);
            return true;
        } else {
            tvPasswordMatchUpdate.setText(getString(R.string.passwords_do_not_match));
            tvPasswordMatchUpdate.setTextColor(Color.RED);
            return false;
        }
    }

    /**
     * Обновление данных пользователя и ребенка в Firebase.
     */
    private void updateDataInFirebase() {
        String userId = SharedPreferencesUtils.getString(requireContext(), USER_PREFS, "userId", "");
        if (TextUtils.isEmpty(userId)) {
            showToast(getString(R.string.user_not_defined));
            return;
        }

        String childId = SharedPreferencesUtils.getString(requireContext(), CHILD_PREFS, "childId", "");
        if (TextUtils.isEmpty(childId)) {
            showToast(getString(R.string.child_not_defined));
            return;
        }

        User updatedUser = createUpdatedUser(userId);
        Child updatedChild = createUpdatedChild(childId);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showToast(getString(R.string.user_not_authenticated));
            return;
        }

        String oldEmail = SharedPreferencesUtils.getString(requireContext(), USER_PREFS, "email", "");
        String oldPass = oldPassword;
        String newPass = etPasRegUpdate.getText().toString().trim();

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
        String fullName = etFullNameUpdate.getText().toString().trim();
        String phone = etPhoneUpdate.getText().toString().trim();
        String birthDate = etBirthDateUpdate.getText().toString().trim();
        String city = etCityUpdate.getText().toString().trim();
        String email = SharedPreferencesUtils.getString(requireContext(), USER_PREFS, "email", "");
        String pass = etPasRegUpdate.getText().toString().trim();
        Gender userGender = getSelectedGender(spGenderUpdate);

        return new User(userId, fullName, phone, birthDate, city, userGender, email, pass);
    }

    /**
     * Создает объект Child с обновленными данными.
     *
     * @param childId идентификатор ребенка.
     * @return объект Child с новыми данными.
     */
    private Child createUpdatedChild(String childId) {
        String childFullName = etFullNameChildUpdate.getText().toString().trim();
        String childBirthDate = etBirthDateChildUpdate.getText().toString().trim();
        Gender childGender = getSelectedGender(spGenderChildUpdate);
        String diagnosis = etDiagnosisChildUpdate.getText().toString().trim();
        int lvl = SharedPreferencesUtils.getInt(requireContext(), CHILD_PREFS, "lvl", 0);
        int exp = SharedPreferencesUtils.getInt(requireContext(), CHILD_PREFS, "exp", 0);
        int countDays = SharedPreferencesUtils.getInt(requireContext(), CHILD_PREFS, "countDays", 0);
        float height = parseFloatOrZero(etHeightChildUpdate.getText().toString().trim());
        float weight = parseFloatOrZero(etWeightChildUpdate.getText().toString().trim());

        return new Child(childId, childFullName,childBirthDate, childGender, diagnosis, height, weight, exp,lvl,countDays);
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
            showToast(getString(R.string.user_not_authenticated));
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, oldPassword);
        currentUser.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                currentUser.updatePassword(newPass).addOnCompleteListener(passUpdateTask -> {
                    if (passUpdateTask.isSuccessful()) {
                        onSuccess.run();
                    } else {
                        showToast(String.format(getString(R.string.password_update_error),
                                passUpdateTask.getException() != null ?
                                        passUpdateTask.getException().getMessage() : ""));
                    }
                });
            } else {
                showToast(String.format(getString(R.string.reauthentication_error),
                        reauthTask.getException() != null ?
                                reauthTask.getException().getMessage() : ""));
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
            showToast(getString(R.string.child_data_updated));
            reloadChildDataFromFirebase(userId);
        }, e -> {
            showToast(String.format(getString(R.string.update_child_data_error), e.getMessage()));
        });
    }

    private void reloadChildDataFromFirebase(String userId) {
        String childId = SharedPreferencesUtils.getString(requireContext(), CHILD_PREFS, "childId", "");
        if (TextUtils.isEmpty(childId)) {
            showToast(getString(R.string.child_not_defined));
            return;
        }

        childRepository.getChild(userId, childId, updatedChild -> {
            if (updatedChild != null) {
                SharedPreferencesUtils.saveString(requireContext(), CHILD_PREFS, "fullName", updatedChild.getFullName());
                SharedPreferencesUtils.saveString(requireContext(), CHILD_PREFS, "birthDate", updatedChild.getBirthDate());
                SharedPreferencesUtils.saveString(requireContext(), CHILD_PREFS, "gender", updatedChild.getGender().toString());
                SharedPreferencesUtils.saveString(requireContext(), CHILD_PREFS, "diagnosis", updatedChild.getDiagnosis());
                SharedPreferencesUtils.saveFloat(requireContext(), CHILD_PREFS, "height", updatedChild.getHeight());
                SharedPreferencesUtils.saveFloat(requireContext(), CHILD_PREFS, "weight", updatedChild.getWeight());
                SharedPreferencesUtils.saveInt(requireContext(), CHILD_PREFS, "exp", updatedChild.getExp());
                SharedPreferencesUtils.saveInt(requireContext(), CHILD_PREFS, "lvl", updatedChild.getLvl());
                SharedPreferencesUtils.saveInt(requireContext(), CHILD_PREFS, "countDays", updatedChild.getCountDays());

                loadChildDataFromPrefs();
            }
        }, e -> {
            showToast(String.format(getString(R.string.reload_child_data_error), e.getMessage()));
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
    private Gender getSelectedGender(Spinner spinner) {
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
