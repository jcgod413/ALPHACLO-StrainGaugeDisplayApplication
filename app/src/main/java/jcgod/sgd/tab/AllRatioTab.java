package jcgod.sgd.tab;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jcgod.sgd.R;
import jcgod.sgd.activity.MainActivity;
import jcgod.sgd.view.DonutView;

/**
 * Created by Jaecheol on 16. 6. 18..
 */
public class AllRatioTab extends Fragment {

    View view;

    Handler handler;
    MainActivity.Sensor sensor;


    Button leftRatio;
    Button rightRatio;

    Button quadsRatio;
    Button hamsRatio;

    RelativeLayout allTab;

    boolean isAvg = false;

    DonutView donutView;
    TextView avgText;

    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_all, container, false);

        initView();

        return view;
    }

    /**
     * setHandler
     * @param handler
     */
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * setSensor
     * @param sensor
     */
    public void setSensor(MainActivity.Sensor sensor)   {
        this.sensor = sensor;
    }

    /**
     * initView
     */
    private void initView() {
        allTab = (RelativeLayout)view.findViewById(R.id.allTab);
        allTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAvg = !isAvg;

                update();
            }
        });

        donutView = (DonutView)view.findViewById(R.id.donutView);

        avgText = (TextView)view.findViewById(R.id.allAvgText);
        avgText.setVisibility(View.INVISIBLE);
    }

    /**
     * update
     */
    public void update() {
        if( isAvg ) {

            int leftHams = sensor.getAccumulatedLeftHamsRatioByAll();
            int leftQuads = sensor.getAccumulatedLeftQuadsRatioByAll();
            int rightHams = sensor.getAccumulatedRightHamsRatioByAll();
//            int rightQuads = sensor.getAccumulatedRightQuadsRatioByAll();
            int rightQuads = 100 - leftHams - leftQuads - rightHams;

            donutView.setPercent(leftQuads, leftHams, rightQuads, rightHams);

            avgText.setVisibility(View.VISIBLE);
        }
        else    {
            int leftHams = sensor.getLeftHamsRatioByAll();
            int leftQuads = sensor.getLeftQuadsRatioByAll();
            int rightHams = sensor.getRightHamsRatioByAll();
//            int rightQuads = sensor.getRightQuadsRatioByAll();
            int rightQuads = 100 - leftHams - leftQuads - rightHams;

            donutView.setPercent(leftQuads, leftHams, rightQuads, rightHams);

            avgText.setVisibility(View.INVISIBLE);
        }

//        leftRatio.setText(leftText);
//        rightRatio.setText(rightText);
//        quadsRatio.setText(quadsText);
//        hamsRatio.setText(hamsText);
    }
}