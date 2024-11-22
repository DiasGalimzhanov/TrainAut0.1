package com.example.trainaut01.training;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.trainaut01.R;
import com.example.trainaut01.TrainingDashboardFragment;

public class RewardFragment extends Fragment {

    private Button _btnReturnTrainingDashboard;

    public static RewardFragment newInstance() {
        return new RewardFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward, container, false);

        init(view);

        _btnReturnTrainingDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTrainingDashboardFragment();
            }
        });

        return view;
    }

    private void init(View view){
        _btnReturnTrainingDashboard = view.findViewById(R.id.btnReturnTrainingDashboard);
    }

    private void goToTrainingDashboardFragment() {
        TrainingDashboardFragment trainingDashboardFragment = new TrainingDashboardFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainTraining, trainingDashboardFragment)
                .commit();
    }
}