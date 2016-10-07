package alphaclo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import alphaclo.BuildConfig;
import alphaclo.R;
import alphaclo.adapter.ReportAdapter;
import alphaclo.network.HttpTask;
import alphaclo.network.IHttpRecvCallback;


/**
 * Created by Jaecheol on 16. 8. 9..
 */
public class ReportActivity extends AppCompatActivity {

    ListView reportList;
    ReportAdapter adapter;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String email;

    final String TAG = "ReportActivity";

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initActivity();
    }

    /**
     * setGlobalFont
     * @param root
     */
    private void setGlobalFont(ViewGroup root) {
        Typeface mTypeface = Typeface.createFromAsset(getAssets(), getString(R.string.app_font));

        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(mTypeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child);
        }
    }

    /**
     * initActivity
     * initialize activity components
     */
    private void initActivity() {
        // set font
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);

        initSharedPref();
        initToolbar();
        initComponents();
    }

    /**
     * initSharedPref
     */
    void initSharedPref()   {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();

        email = sharedPref.getString("email", null);
    }

    /**
     * setToolbar
     */
    private void initToolbar()   {
        final Toolbar toolbar = (Toolbar)findViewById(R.id.reportToolbar);

        toolbar.setTitle("Report");
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * onOptionsItemSelected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * initComponents
     */
    private void initComponents()    {
        // Adapter 생성
        adapter = new ReportAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        reportList = (ListView)findViewById(R.id.reportList);
        reportList.setAdapter(adapter);

        reportList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Long", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

//        addSampleItems();
        getReportList();
    }

    /**
     * addSampleItems
     */
    private void addSampleItems() {
        adapter.addItem("Date : 2016.08.27", "Duration : 39:20", "Distance : 834m", "Calories : 324cal", "L/R : 45:55", "", "", "Total MA : 5344");
        adapter.addItem("Date : 2016.08.27", "Duration : 39:20", "Distance : 834m", "Calories : 324cal", "L/R : 45:55", "", "", "Total MA : 5344");
        adapter.addItem("Date : 2016.08.28", "Duration : 15:39", "Distance : 451m", "Calories : 153cal", "L/R : 59:41", "", "", "Total MA : 3344");
        adapter.addItem("Date : 2016.08.29", "Duration : 31:09", "Distance : 694m", "Calories : 234cal", "L/R : 49:51", "", "", "Total MA : 69344");
        adapter.addItem("Date : 2016.08.30", "Duration : 41:09", "Distance : 994m", "Calories : 434cal", "L/R : 48:52", "", "", "Total MA : 19344");
        adapter.addItem("Date : 2016.08.31", "Duration : 11:32", "Distance : 194m", "Calories : 94cal", "L/R : 53:47", "", "", "Total MA : 69344");
        Intent intent = getIntent();
        if (intent.getStringExtra("time") != null) {
            String date = intent.getStringExtra("date");
            String calory = intent.getStringExtra("calory");
            String distance = intent.getStringExtra("distance");
            String lr = intent.getStringExtra("lr");
            adapter.addItem("Date : 2016.08.31", "Duration : " + intent.getStringExtra("time"), "Distance : " + distance + "m", "Calories : " + calory + "cal", "L/R : " + lr, "", "", "Total MA : " + intent.getStringExtra("ma"));
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * getReportList
     */
    private void getReportList()  {
        String url = getText(R.string.server_url) + "report"
                    + "?email=" + email;
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
                        Toast.makeText(getApplicationContext(),
                                json.get("result_message").toString(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONArray jsonArray = json.getJSONArray("results");
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String date = "Date : " + obj.getString("date");
                        String time = "Duration : " + obj.getString("time");
                        String distance = "Distance : " + obj.getString("distance") + "m";
                        String calory = "Calories : " + obj.getString("calory") + "cal";
                        String lr = "L/R : " + obj.getString("lr");
                        String qh = "Q/H : " + obj.getString("qh");
                        String lrqh = "L/R/Q/H : " + obj.getString("lrqh");
                        String ma = "Total MA :  : " + obj.getString("ma");

                        adapter.addItem(date, time, distance, calory, lr, qh, lrqh, ma);
                    }

                    adapter.notifyDataSetChanged();

                    if( jsonArray.length() == 0 )   {
                        Toast.makeText(getApplicationContext(), "There is no report.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new HttpTask(cb).execute("GET", url);
    }
}
