package alphaclo.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;

import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {
    public static final String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public static final String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_LEFT_QUADS_GAUGE_READY = "ACTION_LEFT_QUADS_GAUGE_AVAILABLE";
    public static final String ACTION_LEFT_HAMS_GAUGE_READY = "ACTION_LEFT_HAMS_GAUGE_AVAILABLE";
    public static final String ACTION_RIGHT_QUADS_GAUGE_READY = "ACTION_RIGHT_QUADS_GAUGE_AVAILABLE";
    public static final String ACTION_RIGHT_HAMS_GAUGE_READY = "ACTION_RIGHT_HAMS_GAUGE_AVAILABLE";

    public static final String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_DISCONNECTED = 0;
    private static final String TAG;
    public static final UUID UUID_LEFT_QUADS_MUSCLE_MEASUREMENT;
    public static final UUID UUID_RIGHT_QUADS_MUSCLE_MEASUREMENT;
    public static final UUID UUID_LEFT_HAMS_MUSCLE_MEASUREMENT;
    public static final UUID UUID_RIGHT_HAMS_MUSCLE_MEASUREMENT;
    private final IBinder mBinder;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private int mConnectionState;
    private final BluetoothGattCallback mGattCallback;

    /* renamed from: com.sjc.alphaclo.bluetooth.BluetoothLeService.1 */
    class C02521 extends BluetoothGattCallback {
        C02521() {
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothLeService.STATE_CONNECTED) {
                intentAction = BluetoothLeService.ACTION_GATT_CONNECTED;
                BluetoothLeService.this.mConnectionState = BluetoothLeService.STATE_CONNECTED;
                BluetoothLeService.this.broadcastUpdate(intentAction);
                Log.i(BluetoothLeService.TAG, "Connected to GATT server.");
                Log.i(BluetoothLeService.TAG, "Attempting to start service discovery:" + BluetoothLeService.this.mBluetoothGatt.discoverServices());
            } else if (newState == 0) {
                intentAction = BluetoothLeService.ACTION_GATT_DISCONNECTED;
                BluetoothLeService.this.mConnectionState = 0;
                Log.i(BluetoothLeService.TAG, "Disconnected from GATT server.");
                BluetoothLeService.this.broadcastUpdate(intentAction);
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == 0) {
                BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(BluetoothLeService.TAG, "onServicesDiscovered received: " + status);
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0) {
                BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, characteristic);
        }
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    public BluetoothLeService() {
        this.mConnectionState = 0;
        this.mGattCallback = new C02521();
        this.mBinder = new LocalBinder();
    }

    static {
        TAG = BluetoothLeService.class.getSimpleName();
        UUID_LEFT_QUADS_MUSCLE_MEASUREMENT = UUID.fromString(GattAttributes.LEFT_QUADS_MUSCLE_MEASUREMENT);
        UUID_RIGHT_QUADS_MUSCLE_MEASUREMENT = UUID.fromString(GattAttributes.RIGHT_QUADS_MUSCLE_MEASUREMENT);
        UUID_LEFT_HAMS_MUSCLE_MEASUREMENT = UUID.fromString(GattAttributes.LEFT_HAMS_MUSCLE_MEASUREMENT);
        UUID_RIGHT_HAMS_MUSCLE_MEASUREMENT = UUID.fromString(GattAttributes.RIGHT_HAMS_MUSCLE_MEASUREMENT);
    }

    private void broadcastUpdate(String action) {
        sendBroadcast(new Intent(action));
    }

    private void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(action);
        int num = 0;
        byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            num = (data[0] & MotionEventCompat.ACTION_MASK) | ((data[STATE_CONNECTING] & MotionEventCompat.ACTION_MASK) << 8);
        }
        StringBuilder stringBuilder;
        byte[] arr$;
        int len$;
        int i$;
        Object[] objArr;
        if (UUID_LEFT_QUADS_MUSCLE_MEASUREMENT.equals(characteristic.getUuid())) {
            if (data != null && data.length > 0) {
                stringBuilder = new StringBuilder(data.length);
                arr$ = data;
                len$ = arr$.length;
                for (i$ = 0; i$ < len$; i$ += STATE_CONNECTING) {
                    objArr = new Object[STATE_CONNECTING];
                    objArr[0] = Byte.valueOf(arr$[i$]);
                    stringBuilder.append(String.format("%02X ", objArr));
                }
                Log.d(TAG, String.format("Received left quads gauge: " + stringBuilder.toString(), new Object[0]));
                intent.putExtra(ACTION_LEFT_QUADS_GAUGE_READY, num);
            }
        }
        else if (UUID_RIGHT_QUADS_MUSCLE_MEASUREMENT.equals(characteristic.getUuid()) && data != null && data.length > 0) {
            stringBuilder = new StringBuilder(data.length);
            arr$ = data;
            len$ = arr$.length;
            for (i$ = 0; i$ < len$; i$ += STATE_CONNECTING) {
                objArr = new Object[STATE_CONNECTING];
                objArr[0] = Byte.valueOf(arr$[i$]);
                stringBuilder.append(String.format("%02X ", objArr));
            }
            Log.d(TAG, String.format("Received right quads gauge: " + stringBuilder.toString(), new Object[0]));
            intent.putExtra(ACTION_RIGHT_QUADS_GAUGE_READY, num);
        }
        else if (UUID_LEFT_HAMS_MUSCLE_MEASUREMENT.equals(characteristic.getUuid())) {
            if (data != null && data.length > 0) {
                stringBuilder = new StringBuilder(data.length);
                arr$ = data;
                len$ = arr$.length;
                for (i$ = 0; i$ < len$; i$ += STATE_CONNECTING) {
                    objArr = new Object[STATE_CONNECTING];
                    objArr[0] = Byte.valueOf(arr$[i$]);
                    stringBuilder.append(String.format("%02X ", objArr));
                }
                Log.d(TAG, String.format("Received left hams gauge: " + stringBuilder.toString(), new Object[0]));
                intent.putExtra(ACTION_LEFT_HAMS_GAUGE_READY, num);
            }
        }
        else if (UUID_RIGHT_HAMS_MUSCLE_MEASUREMENT.equals(characteristic.getUuid())) {
            if (data != null && data.length > 0) {
                stringBuilder = new StringBuilder(data.length);
                arr$ = data;
                len$ = arr$.length;
                for (i$ = 0; i$ < len$; i$ += STATE_CONNECTING) {
                    objArr = new Object[STATE_CONNECTING];
                    objArr[0] = Byte.valueOf(arr$[i$]);
                    stringBuilder.append(String.format("%02X ", objArr));
                }
                Log.d(TAG, String.format("Received right hams gauge: " + stringBuilder.toString(), new Object[0]));
                intent.putExtra(ACTION_RIGHT_HAMS_GAUGE_READY, num);
            }
        }
        sendBroadcast(intent);
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean initialize() {
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) getSystemService("bluetooth");
            if (this.mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        if (this.mBluetoothAdapter != null) {
            return true;
        }
        Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        return false;
    }

    public boolean connect(String address) {
        if (this.mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        } else if (this.mBluetoothDeviceAddress == null || !address.equals(this.mBluetoothDeviceAddress) || this.mBluetoothGatt == null) {
            BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
            if (device == null) {
                Log.w(TAG, "Device not found.  Unable to connect.");
                return false;
            }
            this.mBluetoothGatt = device.connectGatt(this, false, this.mGattCallback);
            Log.d(TAG, "Trying to create a new connection.");
            this.mBluetoothDeviceAddress = address;
            this.mConnectionState = STATE_CONNECTING;
            return true;
        } else {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (!this.mBluetoothGatt.connect()) {
                return false;
            }
            this.mConnectionState = STATE_CONNECTING;
            return true;
        }
    }

    public void disconnect() {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
        } else {
            this.mBluetoothGatt.disconnect();
        }
    }

    public void close() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
        } else {
            this.mBluetoothGatt.readCharacteristic(characteristic);
        }
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        this.mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (UUID_LEFT_QUADS_MUSCLE_MEASUREMENT.equals(characteristic.getUuid()) || UUID_RIGHT_QUADS_MUSCLE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            this.mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (this.mBluetoothGatt == null) {
            return null;
        }
        return this.mBluetoothGatt.getServices();
    }
}
