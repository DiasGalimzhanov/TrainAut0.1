package com.example.trainaut01.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trainaut01.R;

public class UserUpdateFragment extends Fragment {

    private EditText _etFirstNameUpdate, _etLastNameUpdate, _etEmailUpdate, _etPhoneUpdate, _etBirthDateUpdate;
    Button _btnCange, _btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_update, container, false);

        // Initialize EditText fields
        _etFirstNameUpdate = view.findViewById(R.id.etFirstNameUpdate);
        _etLastNameUpdate = view.findViewById(R.id.etLastNameUpdate);
        _etEmailUpdate = view.findViewById(R.id.etEmailUpdate);
        _etPhoneUpdate = view.findViewById(R.id.etPhoneUpdate);
        _etBirthDateUpdate = view.findViewById(R.id.etBirthDateUpdate);
        _btnCancel = view.findViewById(R.id.btnCancel);

        _btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
}
