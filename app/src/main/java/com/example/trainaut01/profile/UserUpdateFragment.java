package com.example.trainaut01.profile;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trainaut01.R;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.component.DaggerAppComponent;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class UserUpdateFragment extends Fragment {
    private AppComponent appComponent;
    @Inject
    UserRepository db;

    private EditText _etFirstNameUpdate, _etLastNameUpdate, _etEmailUpdate, _etPhoneUpdate, _etBirthDateUpdate, _etPas, _etPasConf;
    Button _btnCange, _btnCancel;
    private TextView _tvPasswordMatch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_update, container, false);

        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);
        // Initialize EditText fields
        _etFirstNameUpdate = view.findViewById(R.id.etFirstNameUpdate);
        _etLastNameUpdate = view.findViewById(R.id.etLastNameUpdate);
        _etEmailUpdate = view.findViewById(R.id.etEmailUpdate);
        _etPhoneUpdate = view.findViewById(R.id.etPhoneUpdate);
        _etBirthDateUpdate = view.findViewById(R.id.etBirthDateUpdate);
        _btnCancel = view.findViewById(R.id.btnCancel);
        _btnCange = view.findViewById(R.id.btnCange);
        _tvPasswordMatch = view.findViewById(R.id.tvPasswordMatch);
        _etPas = view.findViewById(R.id.etPasReg);
        _etPasConf = view.findViewById(R.id.etPasConfirm);


        _btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new UserProfileFragment());
                transaction.addToBackStack(null);  // Allows going back to the previous fragment
                transaction.commit();
            }
        });

        _btnCange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newFN = _etFirstNameUpdate.getText().toString();
                String newLN = _etLastNameUpdate.getText().toString();
                String newEmail = _etEmailUpdate.getText().toString();
                String newPhone = _etPhoneUpdate.getText().toString();
                String newBD = _etBirthDateUpdate.getText().toString();
                String newPas = _etPas.getText().toString();

                Map<String, Object> updatedUserData = new HashMap<>();
                updatedUserData.put("firstName", newFN);
                updatedUserData.put("lastName", newLN);
                updatedUserData.put("email", newEmail);
                updatedUserData.put("phone", newPhone);
                updatedUserData.put("birthDate", newBD);
                updatedUserData.put("password", newPas);

                db.updateUser(updatedUserData, getActivity());

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new UserProfileFragment());
                transaction.addToBackStack(null);  // Allows going back to the previous fragment
                transaction.commit();
            }
        });

        // Populate data in EditTexts
        addData_inEditTexts();

        return view;
    }

    private void addData_inEditTexts() {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);

        String firstName = sharedPref.getString("firstName", null);
        String lastName = sharedPref.getString("lastName", null);
        String email = sharedPref.getString("email", null);
        String phone = sharedPref.getString("phone", null);
        String birthDate = sharedPref.getString("birthDate", null);

        if (firstName != null && lastName != null && email != null && phone != null && birthDate != null) {
            _etFirstNameUpdate.setText(firstName);
            _etLastNameUpdate.setText(lastName);
            _etEmailUpdate.setText(email);
            _etPhoneUpdate.setText(phone);
            _etBirthDateUpdate.setText(birthDate);
        }
    }

    private void checkPasswordMatch() {
        String password = _etPas.getText().toString();
        String confirmPassword = _etPasConf.getText().toString();

        if (password.equals(confirmPassword)) {
            _tvPasswordMatch.setText("Пароли совпадают");
            _tvPasswordMatch.setTextColor(Color.GREEN);
        } else {
            _tvPasswordMatch.setText("Пароли не совпадают");
            _tvPasswordMatch.setTextColor(Color.RED);
        }
    }
}
