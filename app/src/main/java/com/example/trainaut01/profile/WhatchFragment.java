package com.example.trainaut01.profile;

import static androidx.core.content.ContextCompat.getSystemService;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trainaut01.R;


public class WhatchFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whatch, container, false);
//        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
//
//        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
//            // Bluetooth выключен. Предложите пользователю включить его.
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }


        return view;
    }
}