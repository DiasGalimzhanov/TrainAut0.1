package com.example.trainaut01.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class LeDeviceListAdapter extends ArrayAdapter<BluetoothDevice> {
    private ArrayList<BluetoothDevice> devices;
    private Context context;
    private boolean hasBluetoothPermission;

    private static final String UNKNOWN_DEVICE_NAME = "Unknown Device";
    private static final String PERMISSION_REQUIRED = "Permission Required";

    public LeDeviceListAdapter(Context context) {
        super(context, 0, new ArrayList<BluetoothDevice>());
        this.devices = new ArrayList<>();
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.hasBluetoothPermission = checkBluetoothConnectPermission();
        }
    }

    public void addDevice(BluetoothDevice device) {
        if (!devices.contains(device)) {
            devices.add(device);
            notifyDataSetChanged();
        }
    }

    public BluetoothDevice getDevice(int position) {
        return devices.get(position);
    }

    public void clear() {
        devices.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = convertView.findViewById(android.R.id.text1);
            viewHolder.deviceAddress = convertView.findViewById(android.R.id.text2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = devices.get(position);

        if (hasBluetoothPermission) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                viewHolder.deviceName.setText(device.getName() != null ? device.getName() : UNKNOWN_DEVICE_NAME);
                viewHolder.deviceAddress.setText(device.getAddress());
            }

        } else {
            viewHolder.deviceName.setText(PERMISSION_REQUIRED);
            viewHolder.deviceAddress.setText(PERMISSION_REQUIRED);
        }

        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private boolean checkBluetoothConnectPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }
}
