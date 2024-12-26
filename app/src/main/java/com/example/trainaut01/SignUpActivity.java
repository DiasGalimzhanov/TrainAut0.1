package com.example.trainaut01;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainaut01.adapter.CountryAdapter;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.databinding.ActivitySignupBinding;
import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.models.User;
import com.example.trainaut01.repository.DayPlanRepository;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.utils.DateUtils;
import com.example.trainaut01.utils.PhoneNumberFormatter;
import com.example.trainaut01.utils.SpinnerUtils;
import com.example.trainaut01.utils.ToastUtils;
import com.example.trainaut01.utils.ValidationUtils;

import javax.inject.Inject;

/**
 * SignUpActivity представляет экран регистрации пользователя.
 * Пользователь заполняет форму, указывает свои данные и переходит к следующему этапу регистрации.
 */
public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding _binding;

    private String[] _countryNames, _countryCodes, _countryLengths;

    @Inject
    UserRepository _userRepository;

    @Inject
    DayPlanRepository _dayPlanRepository;

    /**
     * Метод вызывается при создании активности.
     * Устанавливает макет и инициализирует пользовательский интерфейс.
     *
     * @param savedInstanceState Сохраненное состояние активности.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        _binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        init();
        setupUI();
    }

    /**
     * Вызывается при уничтожении активности.
     * Очищает объект binding для предотвращения утечек памяти.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        _binding = null;
    }

    /**
     * Инициализирует компоненты интерфейса и подключает зависимости через Dagger.
     */
    private void init() {
        if (_binding == null) return;
        AppComponent _appComponent = DaggerAppComponent.create();
        _appComponent.inject(SignUpActivity.this);

        SpinnerUtils.setupGenderAdapter(this, _binding.spGender, Gender.getGenderValues());

        _countryNames = getResources().getStringArray(R.array.country_names);
        _countryCodes = getResources().getStringArray(R.array.country_codes);
        _countryLengths = getResources().getStringArray(R.array.country_lengths);

        _binding.tvCountryCode.setOnClickListener(v -> showCountryCodeDialog());
    }

    /**
     * Настраивает элементы пользовательского интерфейса:
     * - Редирект на экран входа.
     * - Ввод номера телефона с форматированием.
     * - Выбор даты рождения.
     * - Проверка совпадения паролей.
     * - Обработка нажатия на кнопку "Далее".
     * - Добавляет обработчик клика для ссылки на пользовательское соглашение.
     */
    private void setupUI() {
        if (_binding == null) return;
        _binding.tvLogin.setOnClickListener(view -> navigateToLogin());
        PhoneNumberFormatter.setupPhoneNumberFormatting(_binding.etPhoneNumber, getCurrentCountryCode());
        _binding.etBirthDate.setOnClickListener(v -> DateUtils.showDatePickerDialog(this, _binding.etBirthDate));
        setupPasswordValidation();
        setupContinueButton();
        setupUserAgreementLink();
    }

    /**
     * Настраивает проверку совпадения паролей при их вводе.
     */
    private void setupPasswordValidation() {
        if (_binding == null) return;
        _binding.etPassReg.addTextChangedListener(ValidationUtils.createPasswordTextWatcher(
                _binding.etPassReg, _binding.etPasConfirm, _binding.tvPasswordMatch
        ));
    }

    /**
     * Настраивает обработку нажатия на кнопку продолжения.
     * Выполняется проверка формы и переход к следующему этапу регистрации.
     */
    private void setupContinueButton() {
        if (_binding == null) return;

        _binding.btnContinue.setOnClickListener(view -> {
            if (!validateForm()) return;

            User newUser = createNewUser();
            navigateToChildSignUpFragment(newUser);
        });
    }

    /**
     * Настраивает ссылку на пользовательское соглашение.
     */
    private void setupUserAgreementLink() {
        if (_binding == null) return;

        _binding.tvUserAgreementLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, AgreementActivity.class);
                intent.putExtra(AgreementActivity.EXTRA_TITLE, getString(R.string.user_agreement_title));
                intent.putExtra(AgreementActivity.EXTRA_CONTENT_FILE, R.raw.user_agreement_russian);
                startActivity(intent);
            }
        });
    }


    /**
     * Показывает диалоговое окно для выбора кода страны.
     */
    private void showCountryCodeDialog() {
        AlertDialog dialog = createCountryPickerDialog();
        dialog.show();
    }

    /**
     * Создает и настраивает диалог выбора кода страны.
     *
     * @return Настроенный AlertDialog.
     */
    private AlertDialog createCountryPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_country_picker, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        RecyclerView rvCountryList = dialogView.findViewById(R.id.rvCountryList);
        rvCountryList.setLayoutManager(new LinearLayoutManager(this));

        CountryAdapter adapter = new CountryAdapter(_countryNames, _countryCodes, dialog, this::onCountrySelected);
        rvCountryList.setAdapter(adapter);

        return dialog;
    }


    /**
     * Выполняет действия при выборе страны.
     *
     * @param position Позиция выбранной страны в списке.
     */
    private void onCountrySelected(int position) {
        if (_binding == null) return;
        _binding.tvCountryCode.setText(_countryCodes[position]);
        int maxNumberLength = getMaxPhoneLength(position);
        setPhoneNumberInputFilter(maxNumberLength);

        _binding.etPhoneNumber.setEnabled(true);
        _binding.etPhoneNumber.setHintTextColor(getResources().getColor(R.color.violet_blossom));
    }

    /**
     * Устанавливает ограничение на длину ввода номера телефона.
     *
     * @param maxNumberLength максимальная длина номера телефона.
     */
    private void setPhoneNumberInputFilter(int maxNumberLength) {
        if (_binding == null) return;
        _binding.etPhoneNumber.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(maxNumberLength)
        });
    }

    /**
     * Возвращает максимальную длину номера телефона для выбранной страны.
     *
     * @param countryIndex индекс выбранной страны.
     * @return максимальная длина номера телефона.
     */
    private int getMaxPhoneLength(int countryIndex) {
        int codeLength = _countryCodes[countryIndex].length() - 1;
        return Integer.parseInt(_countryLengths[countryIndex]) - codeLength;
    }

    /**
     * Возвращает текущий выбранный код страны без символа '+'.
     *
     * @return код страны.
     */
    private String getCurrentCountryCode() {
        return _binding.tvCountryCode.getText().toString().replace("+", "");
    }


    /**
     * Возвращает индекс текущей выбранной страны.
     *
     * @return индекс выбранной страны или -1, если страна не выбрана.
     */
    private int getSelectedCountryIndex() {
        String countryCode = _binding.tvCountryCode.getText().toString();
        for (int i = 0; i < _countryCodes.length; i++) {
            if (_countryCodes[i].equals(countryCode)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Проверяет, заполнена ли форма регистрации корректно.
     *
     * @return true, если все поля заполнены и соглашение принято, иначе false.
     */
    private boolean validateForm() {
        if (_binding == null) return false;
        if (ValidationUtils.areFieldsFilled(
                _binding.etFullName.getText().toString().trim(), _binding.etPhoneNumber.getText().toString().trim(),
                _binding.etBirthDate.getText().toString().trim(), _binding.etCity.getText().toString().trim(),
                _binding.etEmailReg.getText().toString().trim(), _binding.etPassReg.getText().toString().trim()
        )) {
            ToastUtils.showErrorMessage(this, "Заполните все поля");
            return false;
        }

        String birthDate = _binding.etBirthDate.getText().toString().trim();
        if (!ValidationUtils.isAgeValid(birthDate, 18)) {
            ToastUtils.showErrorMessage(this, "Вам должно быть не менее 18 лет");
            return false;
        }

        int countryIndex = getSelectedCountryIndex();
        if (!ValidationUtils.isPhoneNumberLengthValid(
                _binding.etPhoneNumber.getText().toString().replaceAll("\\s+", ""),
                getMaxPhoneLength(countryIndex),
                getMaxPhoneLength(countryIndex)
        )) {
            ToastUtils.showErrorMessage(this, "Номер телефона должен содержать " + getMaxPhoneLength(countryIndex) + " цифр");
            return false;
        }

        if (!_binding.chbUserAgreement.isChecked()) {
            ToastUtils.showErrorMessage(this, "Примите пользовательское соглашение");
            return false;
        }

        return true;
    }

    /**
     * Перенаправляет пользователя на экран входа.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Создает объект User на основе введенных данных.
     *
     * @return Новый объект User.
     */
    private User createNewUser() {
        return new User(
                _binding.etFullName.getText().toString(),
                _binding.tvCountryCode.getText().toString() + _binding.etPhoneNumber.getText().toString().replaceAll("\\s+", ""),
                _binding.etBirthDate.getText().toString(),
                _binding.etCity.getText().toString(),
                Gender.fromString(_binding.spGender.getSelectedItem().toString()),
                _binding.etEmailReg.getText().toString(),
                _binding.etPassReg.getText().toString()
        );
    }

    /**
     * Переход к следующему этапу регистрации с передачей объекта User.
     *
     * @param user Объект User с данными пользователя.
     */
    private void navigateToChildSignUpFragment(User user) {
        ChildSignUpFragment fragment = ChildSignUpFragment.newInstance(user);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.signUp_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
