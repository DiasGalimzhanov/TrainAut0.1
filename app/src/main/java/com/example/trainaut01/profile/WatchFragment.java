package com.example.trainaut01.profile;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.trainaut01.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class WatchFragment extends Fragment {
    private static final long SCAN_PERIOD = 15000;
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
//    private static final int REQUEST_PERMISSIONS_CODE = 100;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private Handler handler;
    private ListView deviceListView;
    private ArrayAdapter<String> deviceListAdapter;
    private List<BluetoothDevice> scannedDevices;
    private boolean isScanning = false;

    private BluetoothGatt bluetoothGatt;
    private static final Map<String, String> characteristicDescriptions = new HashMap<>();
    static {
        characteristicDescriptions.put("00002a37-0000-1000-8000-00805f9b34fb", "Пульс");
        characteristicDescriptions.put("00002a39-0000-1000-8000-00805f9b34fb", "Команда контроля пульса");
        characteristicDescriptions.put("00002a00-0000-1000-8000-00805f9b34fb", "Имя устройства");
        characteristicDescriptions.put("00002a01-0000-1000-8000-00805f9b34fb", "Внешний вид устройства");

    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("BLE", "Подключено к GATT серверу.");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("BLE", "Отключено от GATT сервера.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (BluetoothGattService service : gatt.getServices()) {
                    Log.i("BLE", "Сервис обнаружен: " + service.getUuid());
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        Log.i("BLE", "Характеристика обнаружена: " + characteristic.getUuid());
                        if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            gatt.setCharacteristicNotification(characteristic, true);
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
                            if (descriptor != null) {
                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(descriptor);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();
            String characteristicUuid = characteristic.getUuid().toString();
            String description = characteristicDescriptions.get(characteristicUuid);

            if (description != null) {
                String readableData = convertDataToReadableFormat(characteristicUuid, data);
                Log.i("BLE", description + ": " + readableData);
                saveDataToSharedPreferences(description, readableData);

                getActivity().runOnUiThread(() -> {
                    TextView dataTextView = getView().findViewById(R.id.data_text);
                    dataTextView.append(description + ": " + readableData + "\n");
                });
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch, container, false);

        Button scanButton = view.findViewById(R.id.scan_button);
        deviceListView = view.findViewById(R.id.device_list);
        scannedDevices = new ArrayList<>();
        deviceListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        deviceListView.setAdapter(deviceListAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        handler = new Handler(Looper.getMainLooper());

        scanButton.setOnClickListener(v -> requestPermissions());
        deviceListView.setOnItemClickListener((parent, view1, position, id) -> connectToDevice(scannedDevices.get(position)));

        return view;
    }

    private void requestPermissions() {
        if (getContext() != null && getActivity() != null) {
            List<String> permissionsNeeded = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.i("Permissions", "BLUETOOTH_SCAN не предоставлено");
                permissionsNeeded.add(android.Manifest.permission.BLUETOOTH_SCAN);
            } else {
                Log.i("Permissions", "BLUETOOTH_SCAN уже предоставлено");
            }

            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.i("Permissions", "BLUETOOTH_CONNECT не предоставлено");
                permissionsNeeded.add(android.Manifest.permission.BLUETOOTH_CONNECT);
            } else {
                Log.i("Permissions", "BLUETOOTH_CONNECT уже предоставлено");
            }

            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("Permissions", "ACCESS_FINE_LOCATION не предоставлено");
                permissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                Log.i("Permissions", "ACCESS_FINE_LOCATION уже предоставлено");
            }
            startScan();
//            if (!permissionsNeeded.isEmpty()) {
//                Log.i("Permissions", "Запрос разрешений");
//                ActivityCompat.requestPermissions(getActivity(), permissionsNeeded.toArray(new String[0]), REQUEST_PERMISSIONS_CODE);
//            } else {
//                Log.i("Permissions", "Все разрешения уже предоставлены, начинаем сканирование");
//                startScan();
//            }
        }
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_PERMISSIONS_CODE) {
//            boolean allPermissionsGranted = true;
//            for (int result : grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    allPermissionsGranted = false;
//                    break;
//                }
//            }
//            if (allPermissionsGranted) {
//                startScan();
//            } else {
//                Toast.makeText(getContext(), "Необходимо разрешение для работы с Bluetooth", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    private void startScan() {
        if (!isScanning) {
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Log.e("BLE", "Bluetooth не включен или не поддерживается");
                Toast.makeText(getContext(), "Bluetooth не включен", Toast.LENGTH_SHORT).show();
                return;
            }

            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.e("BLE", "Службы геолокации отключены");
                Toast.makeText(getContext(), "Пожалуйста, включите службы геолокации для сканирования устройств", Toast.LENGTH_LONG).show();
                return;
            }

            deviceListAdapter.clear();
            scannedDevices.clear();
            isScanning = true;

            handler.postDelayed(() -> {
                if (isScanning) {
                    bluetoothLeScanner.stopScan(leScanCallback);
                    isScanning = false;
                    Log.i("BLE", "Сканирование остановлено");
                }
            }, SCAN_PERIOD);

            bluetoothLeScanner.startScan(leScanCallback);
            Log.i("BLE", "Сканирование начато");
        }
    }


    private void stopScan() {
        if (isScanning) {
            bluetoothLeScanner.stopScan(leScanCallback);
            isScanning = false;
            Log.i("BLE", "Сканирование остановлено вручную");
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (!scannedDevices.contains(device) && device.getName() != null) {
                scannedDevices.add(device);
                deviceListAdapter.add(device.getName() + "\n" + device.getAddress());
                deviceListAdapter.notifyDataSetChanged();
            }
        }
    };

    private void connectToDevice(BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(getContext(), true, gattCallback); // 'true' for autoConnect
        Log.i("BLE", "Подключение к устройству: " + device.getName());
        if (bluetoothGatt != null) {
            Log.i("BLE", "Объект BluetoothGatt не равен null, попытка подключения...");
            if (bluetoothGatt.connect()) {
                Log.i("BLE", "Подключение к устройству успешно инициировано: " + device.getName());
            } else {
                Log.e("BLE", "Не удалось инициировать подключение к устройству: " + device.getName());
            }
        } else {
            Log.e("BLE", "BluetoothGatt равен null, не удалось подключиться к устройству: " + device.getName());
        }
    }

    public void startWorkout() {
        if (bluetoothGatt != null) {
            for (BluetoothGattService service : bluetoothGatt.getServices()) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        bluetoothGatt.setCharacteristicNotification(characteristic, true);
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            bluetoothGatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }
        }
    }

    public void stopWorkout() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    private void saveDataToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("WorkoutData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String convertDataToReadableFormat(String uuid, byte[] data) {
        switch (uuid) {
            case "00002a37-0000-1000-8000-00805f9b34fb":
                int heartRate = data[0] & 0xFF;
                return String.valueOf(heartRate) + " уд/мин";
            case "00002a00-0000-1000-8000-00805f9b34fb":
                return new String(data);
            case "00002a01-0000-1000-8000-00805f9b34fb":
                return "Внешний вид: " + Arrays.toString(data);
            default:
                return Arrays.toString(data);
        }
    }
}









