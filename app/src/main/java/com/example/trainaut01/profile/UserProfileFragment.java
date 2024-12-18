/**
 * Фрагмент для отображения и управления профилем пользователя.
 * Позволяет просматривать данные пользователя, редактировать их после проверки пароля,
 * удалять аккаунт, а также выходить из системы.
 */
package com.example.trainaut01.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.trainaut01.databinding.FragmentUserProfileBinding;
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

    private FragmentUserProfileBinding binding;
    private SharedPreferences sharedPref;
    private static final String TAG = "UserProfileFragment";

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
     * Создает и инициализирует макет фрагмента с использованием ViewBinding.
     *
     * @param inflater объект для создания представлений
     * @param container родительский контейнер
     * @param savedInstanceState состояние фрагмента при его пересоздании (если есть)
     * @return корневой View фрагмента
     */
    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          @Nullable android.view.ViewGroup container,
                                          @Nullable Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Вызывается, когда представление фрагмента полностью создано.
     * Здесь производится инициализация данных и настройка обработчиков.
     *
     * @param view корневой вид фрагмента
     * @param savedInstanceState состояние фрагмента при пересоздании (если есть)
     */
    @Override
    public void onViewCreated(@NonNull android.view.View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
     * Освобождает ресурсы ViewBinding при уничтожении представления.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Загружает данные пользователя из SharedPreferences и обновляет UI.
     */
    @SuppressLint("SetTextI18n")
    private void loadUserData() {
        sharedPref = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);

        String firstName = sharedPref.getString("fullName", "");
        String email = sharedPref.getString("email", "");
        String city = sharedPref.getString("city", "");
        String phone = sharedPref.getString("phone", "");
        String birthDate = sharedPref.getString("birthDate", "");

        binding.parentName.setText("Имя Фамилия: " + firstName);
        binding.parentEmail.setText("Почта: " + email);
        binding.parentCity.setText("Город: " + city);
        binding.parentPhone.setText("Телефон: " + phone);
        binding.parentBd.setText("Дата рождения: " + birthDate);

        loadChildData();
    }

    /**
     * Загружает данные о ребенке из SharedPreferences и обновляет UI.
     */
    private void loadChildData() {
        sharedPref = requireActivity().getSharedPreferences("child_data", Context.MODE_PRIVATE);
        String childName = sharedPref.getString("fullName", "");
        String childGender = sharedPref.getString("gender", "");
        String childDiagnosis = sharedPref.getString("diagnosis", "");
        float childHeight = sharedPref.getFloat("height", 0.0f);
        float childWeight = sharedPref.getFloat("weight", 0.0f);

        String childGenderDiagnosis = "Пол: " + Gender.fromString(childGender).getDisplayName()
                + " • Диагноз: " + childDiagnosis;
        String childHeightWeight = "Рост: " + childHeight + " • Вес: " + childWeight;

        binding.childName.setText(childName);
        binding.childGenderDiagnosis.setText(childGenderDiagnosis);
        binding.childHeightWeight.setText(childHeightWeight);
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
                    Picasso.get().load(avatar.getUrlAvatar()).into(binding.profileImage);
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
        binding.editProfileButton.setOnClickListener(v -> showPasswordDialog(PasswordAction.UPDATE_PROFILE));
        binding.deleteButton.setOnClickListener(v -> showPasswordDialog(PasswordAction.DELETE_ACCOUNT));
        binding.btnExit.setOnClickListener(v -> logOutUser());
        binding.supportButton.setOnClickListener(v -> navigateToFragment(new SupportFragment()));
        binding.watchButton.setOnClickListener(v -> navigateToFragment(new WatchFragment()));
        binding.notesButton.setOnClickListener(v -> navigateToFragment(new NoteFragment()));
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        android.view.LayoutInflater inflater = requireActivity().getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_password, null);
        builder.setView(dialogView);

        final android.widget.EditText input = dialogView.findViewById(R.id.et_password);
        android.widget.Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
        android.widget.Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        final android.app.AlertDialog dialog = builder.create();

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
        sharedPref = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "");
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
