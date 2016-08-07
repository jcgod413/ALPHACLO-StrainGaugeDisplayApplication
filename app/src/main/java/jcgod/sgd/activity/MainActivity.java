package jcgod.sgd.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jcgod.sgd.R;
import jcgod.sgd.adapter.ViewPagerAdapter;
import jcgod.sgd.tab.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {

    // 사용자 정의 함수로 블루투스 활성 상태의 변경 결과를 App으로 알려줄때 식별자로 사용됨 (0보다 커야함)
    static final int REQUEST_ENABLE_BT = 10;
    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    // 폰의 블루투스 모듈을 사용하기 위한 오브젝트.
    BluetoothAdapter mBluetoothAdapter;
    /**
     BluetoothDevice 로 기기의 장치정보를 알아낼 수 있는 자세한 메소드 및 상태값을 알아낼 수 있다.
     연결하고자 하는 다른 블루투스 기기의 이름, 주소, 연결 상태 등의 정보를 조회할 수 있는 클래스.
     현재 기기가 아닌 다른 블루투스 기기와의 연결 및 정보를 알아낼 때 사용.
     */
    BluetoothDevice mRemoteDevie;
    // 스마트폰과 페어링 된 디바이스간 통신 채널에 대응 하는 BluetoothSocket
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    String mStrDelimiter = "\n";
    char mCharDelimiter =  '\n';

    Thread mWorkerThread = null;
    byte[] readBuffer;
    int readBufferPosition;

    Sensor sensor;

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"DASH", "L/R", "Q/H", "L/R/Q/H"};
    int numOfTabs = 4;

    private ServiceHandler handler;

    final int START = 111;
    final int PAUSE = 112;
    final int STOP = 113;

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActivity(savedInstanceState);
    }

    /**
     * onCreateOptionsMenu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * onOptionsItemSelected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_report) {
            Toast.makeText(this, "액션버튼 이벤트", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * initActivity
     */
    private void initActivity(Bundle savedInstanceState) {
        initToolbar();
        initComponents();

        if( savedInstanceState == null )    {
            initTab();
        }
    }

    /**
     * initToolbar
     */
    private void initToolbar()  {
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        toolbar.setTitle("SGD");
        setSupportActionBar(toolbar);
    }

    /**
     * initComponents
     */
    private void initComponents()   {
        // 블루투스 활성화 시키는 메소드
        checkBluetooth();

        handler = new ServiceHandler();
        sensor = new Sensor();
    }

    /**
     * initTab
     */
    private void initTab()  {
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(), Titles, numOfTabs, handler, sensor);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(numOfTabs-1);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    /**
     * getDeviceFromBondedList
     * @param name
     * @return
     *
     * 블루투스 장치의 이름이 주어졌을때 해당 블루투스 장치 객체를 페어링 된 장치 목록에서 찾아내는 코드.
     */
    BluetoothDevice getDeviceFromBondedList(String name) {
        // BluetoothDevice : 페어링 된 기기 목록을 얻어옴.
        BluetoothDevice selectedDevice = null;
        // getBondedDevices 함수가 반환하는 페어링 된 기기 목록은 Set 형식이며,
        // Set 형식에서는 n 번째 원소를 얻어오는 방법이 없으므로 주어진 이름과 비교해서 찾는다.
        for(BluetoothDevice deivce : mDevices) {
            // getName() : 단말기의 Bluetooth Adapter 이름을 반환
            if(name.equals(deivce.getName())) {
                selectedDevice = deivce;
                break;
            }
        }
        return selectedDevice;
    }

    /**
     * sendData : 문자열 전송하는 함수
     * @param msg
     */
    public void sendData(final String msg) {
//        msg += mStrDelimiter;  // 문자열 종료표시 (\n)

        try{
            // getBytes() : String을 byte로 변환
            // OutputStream.write : 데이터를 쓸때는 write(byte[]) 메소드를 사용함. byte[] 안에 있는 데이터를 한번에 기록해 준다.
            mOutputStream.write(msg.getBytes());  // 문자열 전송.
        }catch(Exception e) {  // 문자열 전송 도중 오류가 발생한 경우
            Toast.makeText(getApplicationContext(), "데이터 전송중 오류가 발생", Toast.LENGTH_LONG).show();
            finish();  // App 종료
        }
    }

    /**
     * connectToSelectedDevice : 원격 장치와 연결하는 과정을 나타냄.
     * @param selectedDeviceName
     *
     * 실제 데이터 송수신을 위해서는 소켓으로부터 입출력 스트림을 얻고 입출력 스트림을 이용하여 이루어 진다.
     */
    void connectToSelectedDevice(String selectedDeviceName) {
        // BluetoothDevice 원격 블루투스 기기를 나타냄.
        mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 소켓 생성, RFCOMM 채널을 통한 연결.
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.
            // 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를 리턴함.
            mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

            // 데이터 송수신을 위한 스트림 얻기.
            // BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
            // 1. 데이터를 보내기 위한 OutputStrem
            // 2. 데이터를 받기 위한 InputStream
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            // 데이터 수신 준비.
            beginListenForData();

        }catch(Exception e) { // 블루투스 연결 중 오류 발생
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            finish();  // App 종료
        }
    }

    /**
     * beginListenForData : 데이터 수신(쓰레드 사용 수신된 메시지를 계속 검사함)
     */
    void beginListenForData() {
        final Handler handler = new Handler();

        readBufferPosition = 0;                 // 버퍼 내 수신 문자 저장 위치.
        readBuffer = new byte[1024];            // 수신 버퍼.


        // 문자열 수신 쓰레드.
        mWorkerThread = new Thread(new Runnable()
        {
            @Override
            public void run() {
                // interrupt() 메소드를 이용 스레드를 종료시키는 예제이다.
                // interrupt() 메소드는 하던 일을 멈추는 메소드이다.
                // isInterrupted() 메소드를 사용하여 멈추었을 경우 반복문을 나가서 스레드가 종료하게 된다.
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        // InputStream.available() : 다른 스레드에서 blocking 하기 전까지 읽은 수 있는 문자열 개수를 반환함.
                        int byteAvailable = mInputStream.available();   // 수신 데이터 확인

                        if(byteAvailable > 0) {                        // 데이터가 수신된 경우.
                            byte[] packetBytes = new byte[byteAvailable];

                            // read(buf[]) : 입력스트림에서 buf[] 크기만큼 읽어서 저장 없을 경우에 -1 리턴.
                            mInputStream.read(packetBytes);
                            for(int i=0; i<byteAvailable; i++) {
                                byte b = packetBytes[i];
                                if(b == mCharDelimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    //  System.arraycopy(복사할 배열, 복사시작점, 복사된 배열, 붙이기 시작점, 복사할 개수)
                                    //  readBuffer 배열을 처음 부터 끝까지 encodedBytes 배열로 복사.
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable(){
                                        // 수신된 문자열 데이터에 대한 처리.
                                        @Override
                                        public void run() {

                                            // mStrDelimiter = '\n';
//                                            mEditReceive.setText(data + mStrDelimiter);

                                            sensor.update(data);

                                            adapter.receivePacket(data);
                                        }

                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }

                    } catch (Exception e) {    // 데이터 수신 중 오류 발생.
                        Toast.makeText(getApplicationContext(), "데이터 수신 중 오류가 발생 했습니다.", Toast.LENGTH_LONG).show();
                        finish();            // App 종료.
                    }
                }
            }

        });

        mWorkerThread.start();
    }

    /**
     * selectDevice : 블루투스 지원하며 활성 상태인 경우.
     */
    void selectDevice() {
        // 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
        // getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if(mPariedDeviceCount == 0 ) { // 페어링된 장치가 없는 경우.
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            finish(); // App 종료.
        }
        // 페어링된 장치가 있는 경우.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices) {
            // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
            listItems.add(device.getName());
        }
        listItems.add("취소");  // 취소 항목 추가.


        // CharSequence : 변경 가능한 문자열.
        // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        // toArray 함수를 이용해서 size만큼 배열이 생성 되었다.
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if(item == mPariedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른 경우.
                    Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
                    connectToSelectedDevice(items[item].toString());
                }
            }

        });

        builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * checkBluetooth : 블루투스 활성화 시키는 메소드
     */
    void checkBluetooth() {
        /**
         * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 을 리턴한다.
         이경우 Toast를 사용해 에러메시지를 표시하고 앱을 종료한다.
         */
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null ) {  // 블루투스 미지원
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();  // 앱종료
        }
        else { // 블루투스 지원
            /** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
             *               true : 지원 ,  false : 미지원
             */
            if(!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로 사용(0이상)
                /**
                 startActivityForResult 함수 호출후 다이얼로그가 나타남
                 "예" 를 선택하면 시스템의 블루투스 장치를 활성화 시키고
                 "아니오" 를 선택하면 비활성화 상태를 유지 한다.
                 선택 결과는 onActivityResult 콜백 함수에서 확인할 수 있다.
                 */
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else // 블루투스 지원하며 활성 상태인 경우.
                selectDevice();
        }
    }

    /**
     * onDestroy : 어플이 종료될때 호출 되는 함수.
     * 블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌.
     */
    @Override
    protected void onDestroy() {
        try{
            sendData("P");

            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
        }catch(Exception e){}
        super.onDestroy();
    }

    /**
     * onActivityResult : 사용자의 선택결과 확인 (아니오, 예)
     * @param requestCode
     * @param resultCode
     * @param data
     *
     * 사용자가 request를 허가(또는 거부)하면 안드로이드 앱의 onActivityResult 메소도를 호출해서 request의 허가/거부를 확인할수 있다.
     * 첫번째 requestCode : startActivityForResult 에서 사용했던 요청 코드. REQUEST_ENABLE_BT 값
     * 두번째 resultCode  : 종료된 액티비티가 setReuslt로 지정한 결과 코드. RESULT_OK, RESULT_CANCELED 값중 하나가 들어감.
     * 세번째 data        : 종료된 액티비티가 인테트를 첨부했을 경우, 그 인텐트가 들어있고 첨부하지 않으면 null
     *
     * RESULT_OK: 블루투스가 활성화 상태로 변경된 경우. "예"
     * RESULT_CANCELED : 오류나 사용자의 "아니오" 선택으로 비활성 상태로 남아 있는 경우  RESULT_CANCELED
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult 를 여러번 사용할 땐 이런 식으로 switch 문을 사용하여 어떤 요청인지 구분하여 사용함.
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) { // 블루투스 활성화 상태
                    selectDevice();
                }
                else if(resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
                    Toast.makeText(getApplicationContext(), "블루투스를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * ServiceHandler
     */
    class ServiceHandler extends Handler
    {
        public void handleMessage(Message message)
        {
            switch( message.what )
            {
                case START:
                    sendData("S");
                    break;
                case PAUSE:
                    for(int i=0; i<50; i++)
                        sendData("P");
                    break;
                case STOP:
                    sendData("E");
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    /**
     * Sensor
     */
    public class Sensor {

        private int leftQuadsUp = 0;
        private int leftQuadsDown = 0;
        private int leftHamsUp = 0;
        private int leftHamsDown = 0;
        private int rightQuadsUp = 0;
        private int rightQuadsDown = 0;
        private int rightHamsUp = 0;
        private int rightHamsDown = 0;

        private double acc_leftQuadsUp = 0L;
        private double acc_leftQuadsDown = 0L;
        private double acc_leftHamsUp = 0L;
        private double acc_leftHamsDown = 0L;
        private double acc_rightQuadsUp = 0L;
        private double acc_rightQuadsDown = 0L;
        private double acc_rightHamsUp = 0L;
        private double acc_rightHamsDown = 0L;

        private long index = 0L;

        public void update(String packet)    {
            int st = 0;
            int end;

            index++;

            try {
                end = packet.indexOf(" ");
                leftQuadsUp = Integer.valueOf(packet.substring(st, end));
                acc_leftQuadsUp *= ((index-1D)/index);
                acc_leftQuadsUp += (1D/index) * (double)leftQuadsUp;

                packet = packet.substring(end + 1);
                end = packet.indexOf(" ");
                leftQuadsDown = Integer.valueOf(packet.substring(st, end));
                acc_leftQuadsDown *= ((index-1D)/index);
                acc_leftQuadsDown += (1D/index) * (double)leftQuadsDown;

                packet = packet.substring(end + 1);
                end = packet.indexOf(" ");
                leftHamsUp = Integer.valueOf(packet.substring(st, end));
                acc_leftHamsUp *= ((index-1D)/index);
                acc_leftHamsUp += (1D/index) * (double)leftHamsUp;

                packet = packet.substring(end + 1);
                end = packet.indexOf(" ");
                leftHamsDown = Integer.valueOf(packet.substring(st, end));
                acc_leftHamsDown *= ((index-1D)/index);
                acc_leftHamsDown += (1D/index) * (double)leftHamsDown;

                packet = packet.substring(end + 1);
                end = packet.indexOf(" ");
                rightQuadsUp = Integer.valueOf(packet.substring(st, end));
                acc_rightQuadsUp *= ((index-1D)/index);
                acc_rightQuadsUp += (1D/index) * (double)rightQuadsUp;

                packet = packet.substring(end + 1);
                end = packet.indexOf(" ");
                rightQuadsDown = Integer.valueOf(packet.substring(st, end));
                acc_rightQuadsDown *= ((index-1D)/index);
                acc_rightQuadsDown += (1D/index) * (double)rightQuadsDown;

                packet = packet.substring(end + 1);
                end = packet.indexOf(" ");
                rightHamsUp = Integer.valueOf(packet.substring(st, end));
                acc_rightHamsUp *= ((index-1D)/index);
                acc_rightHamsUp += (1D/index) * (double)rightHamsUp;

                packet = packet.substring(end + 1);
                end = packet.indexOf(" ");
                rightHamsDown = Integer.valueOf(packet.substring(st, end));
                acc_rightHamsDown *= ((index-1D)/index);
                acc_rightHamsDown += (1D/index) * (double)rightHamsDown;
            }
            catch(Exception e)  {
                e.printStackTrace();
            }
        }

        public int getCalory() {
            int A = 2;
            int sum = leftHamsDown + leftHamsUp + leftQuadsDown + leftQuadsUp
                    + rightHamsDown + rightHamsUp + rightQuadsDown + rightQuadsUp;

            return sum / A;
        }

        public int getLeftAverage() {
            return (leftHamsDown + leftHamsUp + leftQuadsDown + leftQuadsUp) / 4;
        }

        public int getRightAverage()    {
            return (rightHamsDown + rightHamsUp + rightQuadsDown + rightQuadsUp) / 4;
        }

        public int getQuadsAverage()    {
            return (leftQuadsUp + leftQuadsDown + rightQuadsUp + rightQuadsDown) / 4;
        }

        public int getHamsAverage() {
            return (leftHamsUp + leftHamsDown + rightHamsUp + rightHamsDown) / 4;
        }

        public double getAccumulatedLeftAverage()  {
            return (acc_leftHamsDown + leftHamsUp + acc_leftQuadsDown + acc_leftQuadsUp) / 4D;
        }

        public double getAccumualtedRightAverage() {
            return (acc_rightHamsDown + acc_rightHamsUp + acc_rightQuadsDown + acc_rightQuadsUp) / 4D;
        }

        public double getAccumulatedQuadsAverage() {
            return (acc_leftQuadsUp + acc_leftQuadsDown + acc_rightQuadsUp + acc_rightQuadsDown) / 4D;
        }

        public double getAccumulatedHamsAverage()  {
            return (acc_leftHamsUp + acc_leftHamsDown + acc_rightHamsUp + acc_rightHamsDown) / 4D;
        }

        public int getLeftRatio()    {
            int left = getLeftAverage();
            int right = getRightAverage();

            return (int)Math.round((double)left / (double)(left+right) * 100.0d);
        }

        public int getRightRatio()   {
            int left = getLeftAverage();
            int right = getRightAverage();

            return (int)Math.round((double)right / (double)(left+right) * 100.0d);
        }

        public int getQuadsRatio()   {
            int quads = getQuadsAverage();
            int hams = getHamsAverage();

            return (int)Math.round((double)quads / (double)(quads+hams) * 100.0d);
        }

        public int getHamsRatio()    {
            int quads = getQuadsAverage();
            int hams = getHamsAverage();

            return (int)Math.round((double)hams / (double)(quads+hams) * 100.0d);
        }

        public int getLeftRatioByAll()  {
            int left = getLeftAverage();
            int right = getRightAverage();
            int quads = getQuadsAverage();
            int hams = getHamsAverage();

            return (int)Math.round((double)left / (double)(left+right+quads+hams) * 100.0d);
        }

        public int getRightRatioByAll() {
            int left = getLeftAverage();
            int right = getRightAverage();
            int quads = getQuadsAverage();
            int hams = getHamsAverage();

            return (int)Math.round((double)right / (double)(left+right+quads+hams) * 100.0d);
        }

        public int getQuadsRatioByAll() {
            int left = getLeftAverage();
            int right = getRightAverage();
            int quads = getQuadsAverage();
            int hams = getHamsAverage();

            return (int)Math.round((double)quads / (double)(left+right+quads+hams) * 100.0d);
        }

        public int getHamsRatioByAll()  {
            int left = getLeftAverage();
            int right = getRightAverage();
            int quads = getQuadsAverage();
            int hams = getHamsAverage();

            return (int)Math.round((double)hams / (double)(left+right+quads+hams) * 100.0d);
        }

        public int getAccumulatedLeftRatio()    {
            double left = getAccumulatedLeftAverage();
            double right = getAccumualtedRightAverage();

            return (int)Math.round(left / (left+right) * 100.0d);
        }

        public int getAccumulatedRightRatio()   {
            double left = getAccumulatedLeftAverage();
            double right = getAccumualtedRightAverage();

            return (int)Math.round(right / (left+right) * 100.0d);
        }

        public int getAccumulatedQuadsRatio()   {
            double quads = getAccumulatedQuadsAverage();
            double hams = getAccumulatedHamsAverage();

            return (int)Math.round(quads / (quads+hams) * 100.0d);
        }

        public int getAccumulatedHamsRatio()    {
            double quads = getAccumulatedQuadsAverage();
            double hams = getAccumulatedHamsAverage();

            return (int)Math.round(hams / (quads+hams) * 100.0d);
        }

        public int getAccumulatedLeftRatioByAll()  {
            double left = getAccumulatedLeftAverage();
            double right = getAccumualtedRightAverage();
            double quads = getAccumulatedQuadsAverage();
            double hams = getAccumulatedHamsAverage();

            return (int)Math.round(left / (left+right+quads+hams) * 100.0d);
        }

        public int getAccumulatedRightRatioByAll() {
            double left = getAccumulatedLeftAverage();
            double right = getAccumualtedRightAverage();
            double quads = getAccumulatedQuadsAverage();
            double hams = getAccumulatedHamsAverage();

            return (int)Math.round(right / (left+right+quads+hams) * 100.0d);
        }

        public int getAccumulatedQuadsRatioByAll() {
            double left = getAccumulatedLeftAverage();
            double right = getAccumualtedRightAverage();
            double quads = getAccumulatedQuadsAverage();
            double hams = getAccumulatedHamsAverage();

            return (int)Math.round(quads / (left+right+quads+hams) * 100.0d);
        }

        public int getAccumulatedHamsRatioByAll()  {
            double left = getAccumulatedLeftAverage();
            double right = getAccumualtedRightAverage();
            double quads = getAccumulatedQuadsAverage();
            double hams = getAccumulatedHamsAverage();

            return (int)Math.round(hams / (left+right+quads+hams) * 100.0d);
        }
    }
}
