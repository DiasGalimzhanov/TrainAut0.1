package com.example.trainaut01.profile;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.models.User;
import com.example.trainaut01.repository.UserRepository;
import com.example.trainaut01.component.DaggerAppComponent;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class UserUpdateFragment extends Fragment {
    private AppComponent appComponent;
    @Inject
    UserRepository db;

    private EditText _etFirstNameUpdate, _etLastNameUpdate, _etEmailUpdate, _etPhoneUpdate, _etPas, _etPasConf;
    Button _btnCange, _btnCancel;
    private TextView _tvPasswordMatch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_update, container, false);

        init(view);


        _btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new UserProfileFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        _btnCange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                User updatedUser = createUpdatedUser();
//                db.updateUser(updatedUser, UserUpdateFragment.this.getContext());

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new UserProfileFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        addData_inEditTexts();

        return view;
    }

    private void init(View view){
        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        _etFirstNameUpdate = view.findViewById(R.id.etFirstNameUpdate);
        _etLastNameUpdate = view.findViewById(R.id.etLastNameUpdate);
        _etEmailUpdate = view.findViewById(R.id.etEmailUpdate);
        _etPhoneUpdate = view.findViewById(R.id.etPhoneUpdate);
        _btnCancel = view.findViewById(R.id.btnCancel);
        _btnCange = view.findViewById(R.id.btnCange);
        _tvPasswordMatch = view.findViewById(R.id.tvPasswordMatch);
        _etPas = view.findViewById(R.id.etPasReg);
        _etPasConf = view.findViewById(R.id.etPasConfirm);
    }


//    private User createUpdatedUser() {
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        String newFullName = _etFullNameUpdate.getText().toString();
//        String newPhone = _etPhoneUpdate.getText().toString();
//        String newBirthDate = _etBirthDate.getText().toString();
//        String newCity = _etCity.getText().toString();
//        Gender newGender = Gender.fromString(_etGender.getText().toString());
//        String newEmail = _etEmailUpdate.getText().toString();
//
//        return new User(userId, newFullName, newPhone, newBirthDate, newCity, newGender, newEmail);
//    }


    private void addData_inEditTexts() {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_data", getActivity().MODE_PRIVATE);

        String firstName = sharedPref.getString("firstName", null);
        String lastName = sharedPref.getString("lastName", null);
        String email = sharedPref.getString("email", null);
        String phone = sharedPref.getString("phone", null);

        Log.d("UserUpdate", "firstName:" + firstName + "\n lastName" + lastName + "\n email" + email  + "\n phone" + phone);

        if (firstName != null && lastName != null && email != null && phone != null) {
            _etFirstNameUpdate.setText(firstName);
            _etLastNameUpdate.setText(lastName);
            _etEmailUpdate.setText(email);
            _etPhoneUpdate.setText(phone);
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
