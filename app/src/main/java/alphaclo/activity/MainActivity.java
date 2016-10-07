package alphaclo.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.TransportMediator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import alphaclo.BuildConfig;
import alphaclo.R;
import alphaclo.bluetooth.BluetoothLeService;
import alphaclo.bluetooth.GattAttributes;
import alphaclo.fragment.MainActivityFragment;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    private static final String TAG;
    private final String LIST_NAME;
    private final String LIST_UUID;
    private TextView leftDataField;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected;
    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceAddress;
    private String mDeviceName;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;
    private ExpandableListView mGattServicesList;
    private final BroadcastReceiver mGattUpdateReceiver;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private final ServiceConnection mServiceConnection;
    private MainActivityFragment maf;
    MenuItem pauseItem;
    MenuItem playItem;
    private RenewThread renewThread;
    private TextView rightDataField;
    private Button startButton;
    MenuItem stopItem;
    private int leftQuadsGauge;
    private int leftHamsGauge;
    private int rightQuadsGauge;
    private int rightHamsGauge;

    /* renamed from: com.sjc.alphaclo.activity.MainActivity.1 */
    class C02451 implements ServiceConnection {
        C02451() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MainActivity.this.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!MainActivity.this.mBluetoothLeService.initialize()) {
                Log.e(MainActivity.TAG, "Unable to initialize Bluetooth");
                MainActivity.this.finish();
            }
            MainActivity.this.mBluetoothLeService.connect(MainActivity.this.mDeviceAddress);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            MainActivity.this.mBluetoothLeService = null;
        }
    }

    /* renamed from: com.sjc.alphaclo.activity.MainActivity.2 */
    class C02462 extends BroadcastReceiver {
        C02462() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                MainActivity.this.mConnected = true;
                MainActivity.this.invalidateOptionsMenu();
            }
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                MainActivity.this.mConnected = false;
                MainActivity.this.invalidateOptionsMenu();
            }
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                MainActivity.this.displayGattServices(MainActivity.this.mBluetoothLeService.getSupportedGattServices());
            }
            else if (!BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

            }
            else {
                if (intent.getIntExtra(BluetoothLeService.ACTION_LEFT_QUADS_GAUGE_READY, -1) != -1) {
                    leftQuadsGauge = intent.getIntExtra(BluetoothLeService.ACTION_LEFT_QUADS_GAUGE_READY, -1);
                }
                else if (intent.getIntExtra(BluetoothLeService.ACTION_RIGHT_QUADS_GAUGE_READY, -1) != -1) {
                    rightQuadsGauge = intent.getIntExtra(BluetoothLeService.ACTION_RIGHT_QUADS_GAUGE_READY, -1);
                }
                else if (intent.getIntExtra(BluetoothLeService.ACTION_LEFT_HAMS_GAUGE_READY, -1) != -1)   {
                    leftHamsGauge = intent.getIntExtra(BluetoothLeService.ACTION_LEFT_HAMS_GAUGE_READY, -1);
                }
                else if (intent.getIntExtra(BluetoothLeService.ACTION_RIGHT_HAMS_GAUGE_READY, -1) != -1)   {
                    rightHamsGauge = intent.getIntExtra(BluetoothLeService.ACTION_RIGHT_HAMS_GAUGE_READY, -1);

                    update();
                }
            }
        }
    }

    class RenewThread extends Thread {
        private final long RENEW_TIME;
        private boolean started;
        private boolean working;

        RenewThread() {
            this.RENEW_TIME = 400;
            this.started = false;
            this.working = true;
        }

        public void run() {
            while (true) {
                if (this.working) {
                    try {
                        if (MainActivity.this.mGattCharacteristics != null) {
                            int[] groupPosition = new int[]{2, 3, 4, 5};
                            int[] childPosition = new int[]{0, 0, 0, 0};
                            for (int i = 0; i < groupPosition.length; i++) {
                                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) ((ArrayList) MainActivity.this.mGattCharacteristics.get(groupPosition[i])).get(childPosition[i]);
                                if ((characteristic.getProperties() | 2) > 0) {
                                    if (MainActivity.this.mNotifyCharacteristic != null) {
                                        MainActivity.this.mBluetoothLeService.setCharacteristicNotification(MainActivity.this.mNotifyCharacteristic, false);
                                        MainActivity.this.mNotifyCharacteristic = null;
                                    }
                                    MainActivity.this.mBluetoothLeService.readCharacteristic(characteristic);
                                }
                                sleep(RENEW_TIME);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void work(boolean flag) {
            this.working = flag;
            if (flag) {
                this.started = true;
            }
        }

        public boolean isWork() {
            return this.working;
        }

        public boolean isStarted() {
            return this.started;
        }
    }

    public MainActivity() {
        this.mGattCharacteristics = new ArrayList();
        this.mConnected = false;
        this.LIST_NAME = "NAME";
        this.LIST_UUID = "UUID";
        this.mServiceConnection = new C02451();
        this.mGattUpdateReceiver = new C02462();
    }

    static {
        TAG = MainActivity.class.getSimpleName();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        getWindow().addFlags(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo((int) R.drawable.logo_top);
        toolbar.setTitle(BuildConfig.FLAVOR);
        setSupportActionBar(toolbar);
        this.renewThread = new RenewThread();
        this.maf = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.fragment);
        Intent intent = getIntent();
        this.mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        this.mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        bindService(new Intent(this, BluetoothLeService.class), this.mServiceConnection, 1);
    }

    protected void onResume() {
        super.onResume();
        registerReceiver(this.mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (this.mBluetoothLeService != null) {
            Log.d(TAG, "Connect request result=" + this.mBluetoothLeService.connect(this.mDeviceAddress));
        }
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.mGattUpdateReceiver);
    }

    protected void onDestroy() {
        super.onDestroy();
        unbindService(this.mServiceConnection);
        this.mBluetoothLeService = null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.playItem = menu.findItem(R.id.action_play);
        this.pauseItem = menu.findItem(R.id.action_pause);
        this.stopItem = menu.findItem(R.id.action_stop);
        this.pauseItem.setVisible(false);
        this.stopItem.setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play /*2131493031*/:
                start();
                return true;
            case R.id.action_pause /*2131493032*/:
                pause();
                return true;
            case R.id.action_stop /*2131493033*/:
                stop();
                return true;
            case R.id.action_report /*2131493034*/:
                Intent intent = new Intent(this, ReportActivity.class);
                if (this.maf.reportItem != null) {
                    intent.putExtra("date", this.maf.reportItem.getDate());
                    intent.putExtra("calory", this.maf.reportItem.getCalory());
                    intent.putExtra("distance", this.maf.reportItem.getDistance());
                    intent.putExtra("lr", this.maf.reportItem.getLR());
                    intent.putExtra("time", this.maf.reportItem.getTime());
                    intent.putExtra("ma", this.maf.reportItem.getMA());
                }
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void update() {
        Log.d("SJC", leftQuadsGauge + "  " + leftHamsGauge + "     " + rightQuadsGauge + "  " + rightHamsGauge);
//        int[] arr = new int[]{30, 44, 58, 70};
//        float[] arr2 = new float[]{1.23f, 1.38f, 1.45f, 1.55f};
//        for (int i = arr.length - 1; i >= 0; i--) {
//            if (Math.abs(leftGauge - rightGauge) > arr[i]) {
//                amplificationValue = arr2[i];
//                break;
//            }
//        }
//        if (leftGauge > rightGauge) {
//            leftGauge = (int) (((float) leftGauge) * amplificationValue);
//            rightGauge = (int) (((float) rightGauge) / amplificationValue);
//        } else if (leftGauge < rightGauge) {
//            leftGauge = (int) (((float) leftGauge) / amplificationValue);
//            rightGauge = (int) (((float) rightGauge) * amplificationValue);
//        }
        float leftGauge = leftQuadsGauge + leftHamsGauge;
        float rightGauge = rightQuadsGauge + rightHamsGauge;
        float quadsGauge = leftQuadsGauge + rightQuadsGauge;
        float hamsGauge = leftHamsGauge + rightHamsGauge;

        int leftPercent = Math.round((leftGauge / (leftGauge + rightGauge)) * 100.0f);
        int quadsPercent = Math.round((quadsGauge / (quadsGauge + hamsGauge)) * 100.0f);

        int ma = Math.abs(leftPercent - 50);

        if (Math.abs(leftGauge - rightGauge) < 10) {
            leftPercent = 50;
        }
        if (Math.abs(quadsGauge - hamsGauge) < 10)  {
            quadsPercent = 50;
        }

        maf.updateLRRatio(leftPercent);
        maf.updateQHRatio(quadsPercent);

        maf.update(ma);
    }

    private void start() {
        this.playItem.setVisible(false);
        this.pauseItem.setVisible(true);
        this.stopItem.setVisible(true);
        if (!this.renewThread.isStarted()) {
            this.renewThread.start();
        }
        this.renewThread.work(true);
        this.maf.start();
    }

    private void pause() {
        this.playItem.setVisible(true);
        this.pauseItem.setVisible(false);
        this.stopItem.setVisible(false);
        this.renewThread.work(false);
        this.maf.pause();
    }

    private void stop() {
        this.playItem.setVisible(true);
        this.pauseItem.setVisible(false);
        this.stopItem.setVisible(false);
        this.renewThread.work(false);
        this.maf.stop();
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices != null) {
            String unknownServiceString = getResources().getString(R.string.unknown_service);
            String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
            ArrayList<HashMap<String, String>> gattServiceData = new ArrayList();
            ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList();
            this.mGattCharacteristics = new ArrayList();
            for (BluetoothGattService gattService : gattServices) {
                HashMap<String, String> currentServiceData = new HashMap();
                String uuid = gattService.getUuid().toString();
                currentServiceData.put("NAME", GattAttributes.lookup(uuid, unknownServiceString));
                currentServiceData.put("UUID", uuid);
                gattServiceData.add(currentServiceData);
                ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList();
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                ArrayList<BluetoothGattCharacteristic> charas = new ArrayList();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    charas.add(gattCharacteristic);
                    HashMap<String, String> currentCharaData = new HashMap();
                    uuid = gattCharacteristic.getUuid().toString();
                    currentCharaData.put("NAME", GattAttributes.lookup(uuid, unknownCharaString));
                    currentCharaData.put("UUID", uuid);
                    gattCharacteristicGroupData.add(currentCharaData);
                }
                this.mGattCharacteristics.add(charas);
                gattCharacteristicData.add(gattCharacteristicGroupData);
            }
            SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(this, gattServiceData, 17367047, new String[]{"NAME", "UUID"}, new int[]{16908308, 16908309}, gattCharacteristicData, 17367047, new String[]{"NAME", "UUID"}, new int[]{16908308, 16908309});
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
