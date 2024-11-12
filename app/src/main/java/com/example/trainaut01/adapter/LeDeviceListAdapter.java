package com.example.trainaut01.adapter;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LeDeviceListAdapter extends ArrayAdapter<BluetoothDevice> {
    private ArrayList<BluetoothDevice> devices;
    private Context context;

    public LeDeviceListAdapter(Context context) {
        super(context, 0, new ArrayList<BluetoothDevice>());
        this.devices = new ArrayList<>();
        this.context = context;
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

    @SuppressLint("MissingPermission")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        BluetoothDevice device = devices.get(position);
        TextView deviceName = convertView.findViewById(android.R.id.text1);
        TextView deviceAddress = convertView.findViewById(android.R.id.text2);

        deviceName.setText(device.getName() != null ? device.getName() : "Unknown Device");
        deviceAddress.setText(device.getAddress());

        return convertView;
    }
}
