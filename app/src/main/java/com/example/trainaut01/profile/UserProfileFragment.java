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
import com.example.trainaut01.utils.SharedPreferencesUtils;
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
     * @param inflater           объект для создания представлений
     * @param container          родительский контейнер
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
     * @param view               корневой вид фрагмента
     * @param savedInstanceState состояние фрагмента при пересоздании (если есть)
     */
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
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

        parentName.setText("Имя Фамилия: " + firstName);
        parentEmail.setText("Почта: " + email);
        parentCity.setText("Город: " + city);
        parentPhone.setText("Телефон: " + phone);
        parentBd.setText("Дата рождения: " + birthDate);

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

        String genderDiagnosis = "Пол: " + Gender.fromString(childGender).getDisplayName() + " • Диагноз: " + childDiagnosis;
        String heightWeight = "Рост: " + childHeight + " • Вес: " + childWeight;

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
        Toast.makeText(getActivity(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

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

            if (password.isEmpty()) {
                Toast.makeText(requireContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyPassword(password, action, dialog);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Проверяет введенный пароль, и если он корректен, выполняет запрошенное действие.
     *
     * @param password введенный пароль
     * @param action   действие для выполнения после проверки пароля
     * @param dialog   диалоговое окно ввода пароля
     */
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

    /**
     * Удаляет аккаунт пользователя из системы.
     */
    private void deleteUserAccount() {
        String userId = SharedPreferencesUtils.getString(requireActivity(), "user_data", "userId", "");
        if (userId.isEmpty()) {
            Toast.makeText(requireContext(), "Не удалось определить пользователя", Toast.LENGTH_SHORT).show();
            return;
        }

        userRepository.deleteUserAccount(userId, aVoid -> {
            Toast.makeText(requireContext(), "Аккаунт удален", Toast.LENGTH_SHORT).show();
            logOutUser();
        }, e -> Toast.makeText(requireContext(), "Ошибка удаления аккаунта: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
