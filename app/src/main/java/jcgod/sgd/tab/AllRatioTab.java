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

//        ViewGroup.LayoutParams layoutParams;
//
//        leftRatio = (Button)view.findViewById(R.id.all_leftRatio);
//        layoutParams = leftRatio.getLayoutParams();
//        layoutParams.height = 0;
//        leftRatio.setLayoutParams(layoutParams);
//
//        rightRatio = (Button)view.findViewById(R.id.all_rightRatio);
//        layoutParams = rightRatio.getLayoutParams();
//        layoutParams.height = 0;
//        rightRatio.setLayoutParams(layoutParams);
//
//        quadsRatio = (Button)view.findViewById(R.id.all_quadsRatio);
//        layoutParams = quadsRatio.getLayoutParams();
//        layoutParams.width = 0;
//        quadsRatio.setLayoutParams(layoutParams);
//
//        hamsRatio = (Button)view.findViewById(R.id.all_hamsRatio);
//        layoutParams = hamsRatio.getLayoutParams();
//        layoutParams.width = 0;
//        hamsRatio.setLayoutParams(layoutParams);

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
        ViewGroup.LayoutParams layoutParams;

        String leftText;
        String rightText;
        String quadsText;
        String hamsText;

        if( isAvg ) {
//            leftText = "LEFT\n(avg)\n" + sensor.getAccumulatedLeftRatioByAll();
//            rightText = "RIGHT\n(avg)\n" + sensor.getAccumulatedRightRatioByAll();
//            quadsText = "QUADS\n(avg)\n" + sensor.getAccumulatedQuadsRatioByAll();
//            hamsText = "HAMS\n(avg)\n" + sensor.getAccumulatedHamsRatioByAll();
//
//            layoutParams = leftRatio.getLayoutParams();
//            layoutParams.height = sensor.getAccumulatedLeftRatioByAll() * 10;
//            leftRatio.setLayoutParams(layoutParams);
//
//            layoutParams = rightRatio.getLayoutParams();
//            layoutParams.height = sensor.getAccumulatedRightRatioByAll() * 10;
//            rightRatio.setLayoutParams(layoutParams);
//
//            layoutParams = quadsRatio.getLayoutParams();
//            layoutParams.width = sensor.getAccumulatedQuadsRatioByAll() * 10;
//            quadsRatio.setLayoutParams(layoutParams);
//
//            layoutParams = hamsRatio.getLayoutParams();
//            layoutParams.width = sensor.getAccumulatedHamsRatioByAll() * 10;
//            hamsRatio.setLayoutParams(layoutParams);

            int leftHams = sensor.getAccumulatedLeftHamsRatioByAll();
            int leftQuads = sensor.getAccumulatedLeftQuadsRatioByAll();
            int rightHams = sensor.getAccumulatedRightHamsRatioByAll();
            int rightQuads = sensor.getAccumulatedRightQuadsRatioByAll();

            donutView.setPercent(leftQuads, leftHams, rightQuads, rightHams);

            avgText.setVisibility(View.VISIBLE);
        }
        else    {
//            leftText = "LEFT\n" + sensor.getLeftRatioByAll();
//            rightText = "RIGHT\n" + sensor.getRightRatioByAll();
//            quadsText = "QUADS\n" + sensor.getQuadsRatioByAll();
//            hamsText = "HAMS\n" + sensor.getHamsRatioByAll();
//
//            layoutParams = leftRatio.getLayoutParams();
//            layoutParams.height = sensor.getLeftRatioByAll() * 10;
//            leftRatio.setLayoutParams(layoutParams);
//
//            layoutParams = rightRatio.getLayoutParams();
//            layoutParams.height = sensor.getRightRatioByAll() * 10;
//            rightRatio.setLayoutParams(layoutParams);
//
//            layoutParams = quadsRatio.getLayoutParams();
//            layoutParams.width = sensor.getQuadsRatioByAll() * 10;
//            quadsRatio.setLayoutParams(layoutParams);
//
//            layoutParams = hamsRatio.getLayoutParams();
//            layoutParams.width = sensor.getHamsRatioByAll() * 10;
//            hamsRatio.setLayoutParams(layoutParams);
            int leftHams = sensor.getLeftHamsRatioByAll();
            int leftQuads = sensor.getLeftQuadsRatioByAll();
            int rightHams = sensor.getRightHamsRatioByAll();
            int rightQuads = sensor.getRightQuadsRatioByAll();

            donutView.setPercent(leftQuads, leftHams, rightQuads, rightHams);

            avgText.setVisibility(View.INVISIBLE);
        }

//        leftRatio.setText(leftText);
//        rightRatio.setText(rightText);
//        quadsRatio.setText(quadsText);
//        hamsRatio.setText(hamsText);
    }
}