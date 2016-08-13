package jcgod.sgd.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import jcgod.sgd.Network.HttpTask;
import jcgod.sgd.Network.IHttpRecvCallback;
import jcgod.sgd.R;

/**
 * Created by Jaecheol on 16. 6. 15..
 */
public class LoginActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;

    final String TAG = "LoginActivity";

    HashMap<String, String> userMap = new HashMap<>();

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initActivity();
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
     * setToolbar
     */
    private void initToolbar()   {
        final Toolbar toolbar = (Toolbar)findViewById(R.id.loginToolbar);
        final TextView title = (TextView)toolbar.findViewById(R.id.title);

        title.setText("Login");
        setSupportActionBar(toolbar);
    }

    /**
     * initComponents
     */
    private void initComponents()    {
        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog();
            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        ListView userList = (ListView)findViewById(R.id.userList);
        userList.setAdapter(adapter);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
                editor.putString("uid", userMap.get(adapter.getItem(position)));

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
        userList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l_position) {
                deleteDialog(parent.getAdapter().getItem(position).toString());
                return true;
            }
        });

        getUserList();
    }

    /**
     * addStudent
     */
    private void addDialog()   {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Add user");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String user = input.getText().toString();

                adapter.add(user);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    /**
     * removeDialog
     * @param item
     */
    private void deleteDialog(final String item) {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("Are you sure to remove " + item + "?").setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'Yes' Button
                        adapter.remove(item);

                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("User remove");
        alert.show();
    }

    private void getUserList()  {
        String url = getText(R.string.server_url) + "user";
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

                    JSONArray jsonArray = json.getJSONArray("users");
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        userMap.put(obj.getString("uid"), obj.getString("name"));
                        adapter.add(obj.getString("name"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new HttpTask(cb).execute("GET", url);
    }
}