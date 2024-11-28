package com.example.trainaut01.profile;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trainaut01.R;
import com.example.trainaut01.component.AppComponent;
import com.example.trainaut01.component.DaggerAppComponent;
import com.example.trainaut01.repository.UserRepository;

import javax.inject.Inject;

public class SupportFragment extends Fragment {
    private AppComponent appComponent;
    @Inject
    UserRepository db;

    private EditText _etTheme,_etMessege;
    private Button _btnSupport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        appComponent = DaggerAppComponent.create();
        appComponent.inject(this);

        init(view);

        _btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String theme = _etTheme.getText().toString();
                String messege = _etMessege.getText().toString();
                if (!theme.isEmpty() && !messege.isEmpty()) {
                    db.saveMessageToFirestore(theme, messege, getActivity());
                }else {
                    Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

    public void init(View view){


        _etTheme = view.findViewById(R.id.etThema);
        _etMessege = view.findViewById(R.id.etMessege);
        _btnSupport = view.findViewById(R.id.btnSupport);
    }
}