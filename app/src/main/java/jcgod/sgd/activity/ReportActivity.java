package jcgod.sgd.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import jcgod.sgd.R;

/**
 * Created by Jaecheol on 16. 8. 9..
 */
public class ReportActivity extends AppCompatActivity {

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

        initComponents();
    }

    /**
     * initComponents
     */
    private void initComponents()    {

    }
}
