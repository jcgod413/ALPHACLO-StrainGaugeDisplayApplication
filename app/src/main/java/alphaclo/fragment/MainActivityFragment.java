package alphaclo.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import alphaclo.R;
import alphaclo.adapter.ViewPagerAdapter;
import alphaclo.item.ReportItem;
import alphaclo.network.HttpTask;
import alphaclo.network.IHttpRecvCallback;

public class MainActivityFragment extends Fragment {
    private ViewPagerAdapter _adapter;
    private ImageView _btn1;
    private ImageView _btn2;
    private ImageView _btn3;
    private ViewPager _mViewPager;
    private MainInfo info;
    public ReportItem reportItem;

    private String email;

    final private String TAG = "MainActivityFragment";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public MainActivityFragment() {
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView();
        setTab();
        onCircleButtonClick();

        initSharedPref();

        this.info = new MainInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    /**
     * initSharedPref
     */
    void initSharedPref()   {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
        editor = sharedPref.edit();

        email = sharedPref.getString("email", null);
    }

    private void onCircleButtonClick() {

        _btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btn1.setImageResource(R.drawable.fill_circle);
                _mViewPager.setCurrentItem(0);
            }
        });

        _btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btn2.setImageResource(R.drawable.fill_circle);
                _mViewPager.setCurrentItem(1);
            }
        });
    }

    private void setUpView() {
        this._mViewPager = (ViewPager) getView().findViewById(R.id.imageviewPager);
        this._adapter = new ViewPagerAdapter(getActivity(), getFragmentManager());
        this._mViewPager.setAdapter(this._adapter);
        this._mViewPager.setCurrentItem(0);
        initButton();
    }

    private void setTab() {
        _mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int position) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                _btn1.setImageResource(R.drawable.holo_circle);
                _btn2.setImageResource(R.drawable.holo_circle);
                btnAction(position);
            }
        });
    }

    private void btnAction(int action) {
        switch (action) {
            case 0:
                _btn1.setImageResource(R.drawable.fill_circle);
                break;
            case 1:
                _btn2.setImageResource(R.drawable.fill_circle);
                break;
            case 2:
                _btn3.setImageResource(R.drawable.fill_circle);
                break;
        }
    }

    private void initButton() {
        _btn1 = (ImageView) getView().findViewById(R.id.btn1);
        _btn1.setImageResource(R.drawable.fill_circle);
        _btn2 = (ImageView) getView().findViewById(R.id.btn2);
    }

    private void setButton(Button btn, String text, int h, int w) {
        btn.setWidth(w);
        btn.setHeight(h);
        btn.setText(text);
    }

    public void updateLRRatio(int leftPercent) {
        _adapter.updateLRRatio(leftPercent, info.accumulateLeftPercent(leftPercent));
    }

    public void updateQHRatio(int quadsPercent) {
        _adapter.updateQHRatio(quadsPercent, info.accumulateQuadsPercent(quadsPercent));
    }

    public void start() {
        this.info.start();
    }

    public void pause() {
        this.info.pause();
    }

    public void stop() {
        String url = getText(R.string.server_url) + "report/add"
                + "?email=" + email
                + "&time=" + info.chronometer.getText()
                + "&distance=" + info.distanceText.getText()
                + "&calory=" + info.caloryText.getText()
                + "&lr=" + Math.round(info.accLeftPercent) + ":" + Math.round(info.accRightPercent)
                + "&qh=" + Math.round(info.accQuadsPercent) + ":" + Math.round(info.accHamsPercent)
                + "&lrqh=" + Math.round(info.accLeftPercent/2f) + ":" + Math.round(info.accRightPercent/2f) + ":"
                            + Math.round(info.accQuadsPercent/2f) + ":" + Math.round(info.accHamsPercent/2f);
        IHttpRecvCallback cb = new IHttpRecvCallback() {
            @Override
            public void onRecv(String result) {
                // 응답 메시지 수신
                if( result == null )
                    return;

                Log.d(TAG, result);

                try {
                    JSONObject json = new JSONObject(result);
                    String result_code = json.get("result_code").toString();
                    if( "-1".equals(result_code) )  {
                        Toast.makeText(MainActivityFragment.this.getView().getContext(),
                                json.get("result_message").toString(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    info.reset();
                    _adapter.setMA(0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new HttpTask(cb).execute("GET", url);
    }

    public void update(int ma) {
        this.info.addMA(ma);
        this.info.update();
    }

    public class MainInfo {
        private final int MINUTE_INTERVAL;
        private int accMA;
        private float accLeftPercent;
        private float accRightPercent;
        private float accQuadsPercent;
        private float accHamsPercent;
        private TextView caloryText;
        private Chronometer chronometer;
        private TextView distanceText;
        private int index;
        private ArrayList<Integer> maArray;
        private boolean running;
        private long timeWhenPaused;
        private int totalMA;

        /* renamed from: com.sjc.alphaclo.fragment.MainActivityFragment.MainInfo.1 */
        class C02561 implements DialogInterface.OnClickListener {
            C02561() {
            }

            public void onClick(DialogInterface dialog, int which) {
            }
        }

        public MainInfo() {
            this.timeWhenPaused = 0;
            this.accMA = 0;
            this.totalMA = 0;
            this.accLeftPercent = 0.0f;
            this.accRightPercent = 0.0f;
            this.accQuadsPercent = 0.0f;
            this.accHamsPercent = 0.0f;
            this.index = 0;
            this.MINUTE_INTERVAL = 75;
            this.running = false;
            View view = MainActivityFragment.this.getView().findViewById(R.id.mainHeader);
            this.chronometer = (Chronometer) view.findViewById(R.id.chronometer);
            this.distanceText = (TextView) view.findViewById(R.id.distanceText);
            this.caloryText = (TextView) view.findViewById(R.id.caloryText);
            this.maArray = new ArrayList();
        }

        public void start() {
            this.running = true;
            this.chronometer.setBase(SystemClock.elapsedRealtime() + this.timeWhenPaused);
            this.timeWhenPaused = 0;
            this.chronometer.start();
        }

        public void pause() {
            this.running = false;
            this.timeWhenPaused = this.chronometer.getBase() - SystemClock.elapsedRealtime();
            this.chronometer.stop();
        }

        public void reset() {
            showReportDialog();
            this.running = false;
            this.maArray.clear();
            this.accMA = 0;
            this.totalMA = 0;
            this.accLeftPercent = 0.0f;
            this.accRightPercent = 0.0f;
            this.index = 0;
            this.distanceText.setText("0");
            this.caloryText.setText("0");
            this.chronometer.stop();
            this.chronometer.setBase(SystemClock.elapsedRealtime());
        }

        public void showReportDialog() {
            CharSequence contents = "Duration : " + this.chronometer.getText() + "\n" + "Distance : " + this.distanceText.getText() + " m\n" + "Calory : " + this.caloryText.getText() + " cal\n" + "L/R : " + Math.round(this.accLeftPercent) + ":" + Math.round(this.accRightPercent) + "\n" + "Total MA : " + this.totalMA + "\n";
            MainActivityFragment.this.reportItem = new ReportItem();
            MainActivityFragment.this.reportItem.setTime(this.chronometer.getText().toString());
            MainActivityFragment.this.reportItem.setDistance(this.distanceText.getText().toString());
            MainActivityFragment.this.reportItem.setCalory(this.caloryText.getText().toString());
            MainActivityFragment.this.reportItem.setLR(Math.round(this.accLeftPercent) + ":" + Math.round(this.accRightPercent));
            MainActivityFragment.this.reportItem.setMA(String.valueOf(this.totalMA));
            Builder builder = new Builder(MainActivityFragment.this.getView().getContext());
            builder.setTitle((CharSequence) "Activity Report");
            builder.setMessage(contents);
            builder.setPositiveButton(MainActivityFragment.this.getString(17039370), new C02561());
            builder.create().show();
        }

        public void addMA(int ma) {
            this.maArray.add(Integer.valueOf(ma));
            this.accMA += ma;
            this.totalMA += ma;
            if (this.maArray.size() > 75) {
                this.accMA -= ((Integer) this.maArray.get((this.maArray.size() - 75) - 1)).intValue();
            }
        }

        public int accumulateLeftPercent(int leftPercent) {
            this.index++;
            this.accLeftPercent *= ((float) (this.index - 1)) / ((float) this.index);
            this.accLeftPercent += (1.0f / ((float) this.index)) * ((float) leftPercent);
            this.accRightPercent = 100.0f - this.accLeftPercent;
            return Math.round(accLeftPercent);
        }

        public int accumulateQuadsPercent(int quadsPercent) {
            accQuadsPercent *= ((float) (this.index - 1)) / ((float) this.index);
            accQuadsPercent += (1.0f / ((float) this.index)) * ((float) quadsPercent);
            accHamsPercent = 100.0f - this.accQuadsPercent;
            return Math.round(accQuadsPercent);
        }

        public void updateMA() {
            if (this.index % 4 == 0) {
                MainActivityFragment.this._adapter.setMA(this.accMA);
            }
        }

        public void updateCalory() {
            this.caloryText.setText(String.valueOf(Math.ceil((((double) this.totalMA) * 0.0017d) * 10.0d) / 10.0d));
        }

        public void updateDistance() {
            this.distanceText.setText(String.valueOf(Math.round(((double) this.totalMA) * 0.0401d)));
        }

        public void update() {
            updateMA();
            updateCalory();
            updateDistance();
        }
    }
}
