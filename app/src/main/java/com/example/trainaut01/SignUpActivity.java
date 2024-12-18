package com.example.trainaut01;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
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

    private EditText _etFullName, _etPhoneNumber, _etBirthDate, _etCity, _etEmail, _etPassReg, _etPasConfirm;
    private TextView _tvPasswordMatch, _tvLogin, _tvCountryCode;
    private Spinner _spGender;
    private Button _btnContinue;
    private CheckBox _chbUserAgreement;

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

        init();
        setupUI();
    }

    /**
     * Инициализирует компоненты интерфейса и подключает зависимости через Dagger.
     */
    private void init() {
        AppComponent _appComponent = DaggerAppComponent.create();
        _appComponent.inject(SignUpActivity.this);

        findViews();

        SpinnerUtils.setupGenderAdapter(this, _spGender, Gender.getGenderValues());

        _countryNames = getResources().getStringArray(R.array.country_names);
        _countryCodes = getResources().getStringArray(R.array.country_codes);
        _countryLengths = getResources().getStringArray(R.array.country_lengths);

        _tvCountryCode.setOnClickListener(v -> showCountryCodeDialog());
    }

    /**
     * Ищет и связывает элементы пользовательского интерфейса с переменными класса.
     */
    private void findViews() {
        _tvLogin = findViewById(R.id.tvLogin);
        _tvPasswordMatch = findViewById(R.id.tvPasswordMatch);
        _etFullName = findViewById(R.id.etFullName);
        _etPhoneNumber = findViewById(R.id.etPhoneNumber);
        _etBirthDate = findViewById(R.id.etBirthDate);
        _etCity = findViewById(R.id.etCity);
        _etEmail = findViewById(R.id.etEmailReg);
        _etPassReg = findViewById(R.id.etPasReg);
        _etPasConfirm = findViewById(R.id.etPasConfirm);
        _tvCountryCode = findViewById(R.id.tvCountryCode);
        _chbUserAgreement = findViewById(R.id.chbUserAgreement);
        _spGender = findViewById(R.id.spGender);
        _btnContinue = findViewById(R.id.btnContinue);
    }

    /**
     * Настраивает элементы пользовательского интерфейса:
     * - Редирект на экран входа.
     * - Ввод номера телефона с форматированием.
     * - Выбор даты рождения.
     * - Проверка совпадения паролей.
     * - Обработка нажатия на кнопку "Далее".
     */
    private void setupUI() {
        _tvLogin.setOnClickListener(view -> navigateToLogin());
        PhoneNumberFormatter.setupPhoneNumberFormatting(_etPhoneNumber, getCurrentCountryCode());
        _etBirthDate.setOnClickListener(v -> DateUtils.showDatePickerDialog(this, _etBirthDate));
        setupPasswordValidation();

        setupContinueButton();
    }

    /**
     * Настраивает проверку совпадения паролей при их вводе.
     */
    private void setupPasswordValidation() {
        _etPassReg.addTextChangedListener(ValidationUtils.createPasswordTextWatcher(
                _etPassReg, _etPasConfirm, _tvPasswordMatch
        ));
    }

    /**
     * Настраивает обработку нажатия на кнопку продолжения.
     * Выполняется проверка формы и переход к следующему этапу регистрации.
     */
    private void setupContinueButton() {
        _btnContinue.setOnClickListener(view -> {
            if (!validateForm()) return;

            User newUser = createNewUser();
            navigateToChildSignUpFragment(newUser);
        });
    }

    /**
     * Показывает диалоговое окно для выбора кода страны.
     */
    private void showCountryCodeDialog() {
        AlertDialog dialog = createCountryPickerDialog();
        dialog.show();

        ListView lvCountryList = dialog.findViewById(R.id.lvCountryList);
        if (lvCountryList != null) {
            lvCountryList.setOnItemClickListener((parent, view, position, id) -> {
                onCountrySelected(position);

                dialog.dismiss();
            });
        }
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

        ListView lvCountryList = dialogView.findViewById(R.id.lvCountryList);

        setupCountryListView(lvCountryList);

        return builder.create();
    }

    /**
     * Настраивает список стран в диалоге.
     *
     * @param lvCountryList ListView для отображения списка стран.
     */
    private void setupCountryListView(ListView lvCountryList) {
        ArrayAdapter<String> adapter = createCountryListAdapter();
        lvCountryList.setAdapter(adapter);

    }

    /**
     * Создает адаптер для списка стран.
     *
     * @return Настроенный ArrayAdapter.
     */
    private ArrayAdapter<String> createCountryListAdapter() {
        return new ArrayAdapter<String>(
                this,
                R.layout.item_country,
                R.id.tvCountryName,
                _countryNames
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tvCountryCode = view.findViewById(R.id.tvCountryCode);
                tvCountryCode.setText(_countryCodes[position]);
                return view;
            }
        };
    }

    /**
     * Выполняет действия при выборе страны.
     *
     * @param position Позиция выбранной страны в списке.
     */
    private void onCountrySelected(int position) {
        _tvCountryCode.setText(_countryCodes[position]);
        int maxNumberLength = getMaxPhoneLength(position);
        setPhoneNumberInputFilter(maxNumberLength);
    }

    /**
     * Устанавливает ограничение на длину ввода номера телефона.
     *
     * @param maxNumberLength максимальная длина номера телефона.
     */
    private void setPhoneNumberInputFilter(int maxNumberLength) {
        _etPhoneNumber.setFilters(new InputFilter[]{
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
        return _tvCountryCode.getText().toString().replace("+", "");
    }


    /**
     * Возвращает индекс текущей выбранной страны.
     *
     * @return индекс выбранной страны или -1, если страна не выбрана.
     */
    private int getSelectedCountryIndex() {
        String countryCode = _tvCountryCode.getText().toString();
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
        if (!ValidationUtils.areFieldsFilled(
                _etFullName.getText().toString().trim(), _etPhoneNumber.getText().toString().trim(),
                _etBirthDate.getText().toString().trim(),_etCity.getText().toString().trim(),
                _etEmail.getText().toString().trim(),_etPassReg.getText().toString().trim()
        )) {
            ToastUtils.showErrorMessage(this, "Заполните все поля");
            return false;
        }

        String birthDate = _etBirthDate.getText().toString().trim();
        if (!ValidationUtils.isAgeValid(birthDate, 18)) {
            ToastUtils.showErrorMessage(this, "Вам должно быть не менее 18 лет");
            return false;
        }

        int countryIndex = getSelectedCountryIndex();
        if (!ValidationUtils.isPhoneNumberLengthValid(
                _etPhoneNumber.getText().toString().replaceAll("\\s+", ""),
                getMaxPhoneLength(countryIndex),
                getMaxPhoneLength(countryIndex)
        )) {
            ToastUtils.showErrorMessage(this, "Номер телефона должен содержать " + getMaxPhoneLength(countryIndex) + " цифр");
            return false;
        }

        if (!_chbUserAgreement.isChecked()) {
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
                _etFullName.getText().toString(),
                _tvCountryCode.getText().toString() + _etPhoneNumber.getText().toString().replaceAll("\\s+", ""),
                _etBirthDate.getText().toString(),
                _etCity.getText().toString(),
                Gender.fromString(_spGender.getSelectedItem().toString()),
                _etEmail.getText().toString(),
                _etPassReg.getText().toString()
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
