package jcgod.sgd.activity;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import jcgod.sgd.Network.HttpTask;
import jcgod.sgd.Network.IHttpRecvCallback;
import jcgod.sgd.R;
import jcgod.sgd.adapter.ReportAdapter;

/**
 * Created by Jaecheol on 16. 8. 9..
 */
public class ReportActivity extends AppCompatActivity {

    ListView reportList;
    ReportAdapter adapter;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String uid;

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

        uid = sharedPref.getString("uid", null);
    }

    /**
     * setToolbar
     */
    private void initToolbar()   {
        final Toolbar toolbar = (Toolbar)findViewById(R.id.reportToolbar);
        final TextView title = (TextView)toolbar.findViewById(R.id.title);

        title.setText("Report");
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

//        adapter.addItem("date1", "time1", "distance1", "calory1", "lr1", "qh1", "lrqh1");
//        adapter.addItem("date2", "time2", "distance2", "calory2", "lr2", "qh2", "lrqh2");
//        adapter.addItem("date3", "time3", "distance3", "calory3", "lr3", "qh3", "lrqh3");

        reportList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Long", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        getReportList();
    }

    /**
     * getReports
     */
    private void getReportList()  {
        String url = getText(R.string.server_url) + "report"
                    + "?uid=" + uid;
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

                    JSONArray jsonArray = json.getJSONArray("report");
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        adapter.addItem(obj.getString("date"), obj.getString("time"),
                                        obj.getString("distance"), obj.getString("calory"),
                                        obj.getString("lr"), obj.getString("qh"),
                                        obj.getString("lrqh"));
                    }

                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new HttpTask(cb).execute("GET", url);
    }
}
