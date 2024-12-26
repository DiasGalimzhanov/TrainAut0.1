package com.example.trainaut01.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.trainaut01.BottomNavigationUpdater;
import com.example.trainaut01.LoginActivity;
import com.example.trainaut01.R;
import com.example.trainaut01.adapter.LanguageSpinnerAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.enums.Language;
import com.example.trainaut01.enums.PasswordAction;
import com.example.trainaut01.helper.LocaleHelper;
import com.example.trainaut01.models.Avatar;
import com.example.trainaut01.repository.AvatarRepository;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.utils.SharedPreferencesUtils;
import com.example.trainaut01.utils.ToastUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

/**
 * Фрагмент для отображения и управления профилем пользователя.
 * Позволяет просматривать данные пользователя, редактировать их после проверки пароля,
 * удалять аккаунт, а также выходить из системы.
 */
public class UserProfileFragment extends Fragment {
    @Inject
    AvatarRepository avatarRepository;

    @Inject
    UserRepository userRepository;

    private static final String TAG = "UserProfileFragment";

    private TextView parentName, parentEmail, parentCity, parentPhone, parentBd;
    private TextView childName, childGenderDiagnosis, childHeightWeight;
    private ImageView profileImage, btnExit;
    private Button editProfileButton, deleteButton, supportButton, watchButton, notesButton;
    private Spinner spinnerLanguages;

//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(LocaleHelper.setLocale(context, LocaleHelper.getLanguage(context)));
//    }

    /**
     * Инициализирует зависимости с помощью Dagger.
     *
     * @param savedInstanceState состояние фрагмента при его пересоздании (если есть)
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
    }

    /**
     * Создает и инициализирует макет фрагмента.
     *
     * @param inflater объект для создания представлений
     * @param container родительский контейнер
     * @param savedInstanceState состояние фрагмента при его пересоздании (если есть)
     * @return корневой View фрагмента
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    /**
     * Вызывается, когда представление фрагмента полностью создано.
     * Здесь производится инициализация данных и настройка обработчиков.
     *
     * @param view корневой вид фрагмента
     * @param savedInstanceState состояние фрагмента при его пересоздании (если есть)
     */
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Gender.initializeLocalizedNames(requireContext());
        initViews(view);
        spinnerAdapter();
        loadUserData();
        loadAvatar();
        setupListeners();
    }

    /**
     * Вызывается, когда фрагмент становится видимым для пользователя.
     * Обновляет выбранный пункт нижней навигации.
     */
    @Override
    public void onResume() {
        super.onResume();
        updateBottomNavigation();
    }

    /**
     * Инициализирует элементы интерфейса.
     */
    private void initViews(View view) {
        parentName = view.findViewById(R.id.parent_name);
        parentEmail = view.findViewById(R.id.parent_email);
        parentCity = view.findViewById(R.id.parent_city);
        parentPhone = view.findViewById(R.id.parent_phone);
        parentBd = view.findViewById(R.id.parent_bd);

        childName = view.findViewById(R.id.child_name);
        childGenderDiagnosis = view.findViewById(R.id.child_gender_diagnosis);
        childHeightWeight = view.findViewById(R.id.child_height_weight);

        profileImage = view.findViewById(R.id.profile_image);
        btnExit = view.findViewById(R.id.btnExit);

        editProfileButton = view.findViewById(R.id.edit_profile_button);
        deleteButton = view.findViewById(R.id.delete_button);
        supportButton = view.findViewById(R.id.support_button);
        watchButton = view.findViewById(R.id.watch_button);
        notesButton = view.findViewById(R.id.notes_button);
        spinnerLanguages = view.findViewById(R.id.spinnerLanguages);
    }

    /**
     * Загружает данные пользователя из SharedPreferences и обновляет UI.
     */
    @SuppressLint("SetTextI18n")
    private void loadUserData() {
        String firstName = SharedPreferencesUtils.getString(requireActivity(), "user_data", "fullName", "");
        String email = SharedPreferencesUtils.getString(requireActivity(), "user_data", "email", "");
        String city = SharedPreferencesUtils.getString(requireActivity(), "user_data", "city", "");
        String phone = SharedPreferencesUtils.getString(requireActivity(), "user_data", "phone", "");
        String birthDate = SharedPreferencesUtils.getString(requireActivity(), "user_data", "birthDate", "");

        parentName.setText(getString(R.string.user_name, firstName));
        parentEmail.setText(getString(R.string.user_email, email));
        parentCity.setText(getString(R.string.user_city, city));
        parentPhone.setText(getString(R.string.user_phone, phone));
        parentBd.setText(getString(R.string.user_birth_date, birthDate));

        loadChildData();
    }

    /**
     * Загружает данные о ребенке из SharedPreferences и обновляет UI.
     */
    private void loadChildData() {
        String childNameStr = SharedPreferencesUtils.getString(requireActivity(), "child_data", "fullName", "");
        String childGender = SharedPreferencesUtils.getString(requireActivity(), "child_data", "gender", "");
        String childDiagnosis = SharedPreferencesUtils.getString(requireActivity(), "child_data", "diagnosis", "");
        float childHeight = SharedPreferencesUtils.getFloat(requireActivity(), "child_data", "height", 0);
        float childWeight = SharedPreferencesUtils.getFloat(requireActivity(), "child_data", "weight", 0);

        String genderDiagnosis = getString(R.string.child_gender_diagnosis,
                Gender.fromString(childGender).getDisplayName(), childDiagnosis);
        String heightWeight = getString(R.string.child_height_weight, childHeight, childWeight);

        childName.setText(childNameStr);
        childGenderDiagnosis.setText(genderDiagnosis);
        childHeightWeight.setText(heightWeight);
    }

    /**
     * Загружает аватар пользователя в соответствии с его уровнем.
     */
    private void loadAvatar() {
        avatarRepository.getAvatarByLevel(requireActivity(), new AvatarRepository.AvatarCallback() {
            @Override
            public void onSuccess(List<Avatar> avatars) {
                if (!avatars.isEmpty()) {
                    Avatar avatar = avatars.get(0);
                    Picasso.get().load(avatar.getUrlAvatar()).into(profileImage);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Не удалось загрузить аватар", e);
            }
        });
    }

    /**
     * Настраивает обработчики нажатий для элементов интерфейса.
     */
    private void setupListeners() {
        btnExit.setOnClickListener(v -> logOutUser());
        editProfileButton.setOnClickListener(v -> showPasswordDialog(PasswordAction.UPDATE_PROFILE));
        deleteButton.setOnClickListener(v -> showPasswordDialog(PasswordAction.DELETE_ACCOUNT));
        supportButton.setOnClickListener(v -> navigateToFragment(new SupportFragment()));
        watchButton.setOnClickListener(v -> navigateToFragment(new WatchFragment()));
        notesButton.setOnClickListener(v -> navigateToFragment(new NoteFragment()));
    }

    /**
     * Производит выход пользователя из аккаунта и очищает локальные данные.
     */
    private void logOutUser() {
        clearSharedPreferences("user_data");
        clearSharedPreferences("child_progress");
        clearSharedPreferences("child_data");

        FirebaseAuth.getInstance().signOut();
        ToastUtils.showShortMessage(requireActivity(), getString(R.string.user_logged_out));

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Очищает данные в указанных SharedPreferences.
     *
     * @param prefName имя файла SharedPreferences
     */
    private void clearSharedPreferences(String prefName) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Показывает диалог для ввода пароля перед определенными действиями.
     *
     * @param action действие, которое будет выполнено после валидации пароля
     */
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

    /**
     * Проверяет введенный пароль, и если он корректен, выполняет запрошенное действие.
     *
     * @param password введенный пароль
     * @param action действие для выполнения после проверки пароля
     * @param dialog диалоговое окно ввода пароля
     */
    private void verifyPassword(String password, PasswordAction action, AlertDialog dialog) {
        String email = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : null;

        if (email == null) {
            ToastUtils.showErrorMessage(requireContext(), getString(R.string.user_not_authenticated));
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
                        ToastUtils.showErrorMessage(getActivity(), getString(R.string.incorrect_password));
                    }
                });
    }

    /**
     * Удаляет аккаунт пользователя из системы.
     */
    private void deleteUserAccount() {
        String userId = SharedPreferencesUtils.getString(requireActivity(), "user_data", "userId", "");
        if (userId.isEmpty()) {
            ToastUtils.showErrorMessage(requireContext(), getString(R.string.user_not_defined));
            return;
        }

        userRepository.deleteUserAccount(userId, aVoid -> {
            ToastUtils.showShortMessage(requireContext(), getString(R.string.account_deleted));
            logOutUser();
        }, e -> ToastUtils.showErrorMessage(requireContext(),
                getString(R.string.delete_account_error, e.getMessage())));
    }

    public void spinnerAdapter(){
        Language[] languages = Language.values();

        LanguageSpinnerAdapter adapter = new LanguageSpinnerAdapter(
                requireContext(),
                languages,
                R.layout.item_spinner_language,
                R.layout.item_spinner_dropdown_language
        );

        spinnerLanguages.setAdapter(adapter);
        spinnerListener();

        String currentLanguageCode = LocaleHelper.getLanguage(requireContext());
        Language currentLanguage = Language.fromCode(currentLanguageCode);
        int spinnerPosition = adapter.getPosition(currentLanguage);
        spinnerLanguages.setSelection(spinnerPosition);
    }


    public void spinnerListener(){
        spinnerLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }
                Language selectedLanguage = (Language) parent.getItemAtPosition(position);

                if (!selectedLanguage.getCode().equals(LocaleHelper.getLanguage(getContext()))) {
                    LocaleHelper.setLocale(getContext(), selectedLanguage.getCode());

                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                }
                ToastUtils.showShortMessage(requireContext(), getString(R.string.language) + selectedLanguage.getDisplayName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Открывает указанный фрагмент.
     *
     * @param fragment фрагмент для отображения
     */
    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Обновляет выбранный пункт нижней навигации при отображении данного фрагмента.
     */
    public void updateBottomNavigation() {
        ((BottomNavigationUpdater) requireActivity()).updateBottomNavigationSelection(this);
    }
}
