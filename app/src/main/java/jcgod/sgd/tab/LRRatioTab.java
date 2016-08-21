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
import jcgod.sgd.view.PieView;

/**
 * Created by Jaecheol on 16. 6. 17..
 */
public class LRRatioTab extends Fragment  {

    View view;

    MainActivity.Sensor sensor;

    Handler handler;

    Button leftRatio;
    Button rightRatio;

    RelativeLayout lrTab;

    boolean isAvg = false;

    PieView lrView;

    TextView avgText;

    TextView leftText;
    TextView rightText;

    private boolean running;

    /**
     * onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_lr, container,false);

        initView();

        return view;
    }

    /**
     * initView
     */
    private void initView() {
        lrTab = (RelativeLayout)view.findViewById(R.id.lrTab);
        lrTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAvg = !isAvg;

                update();
            }
        });

        lrView = (PieView)view.findViewById(R.id.lrView);
        lrView.setMode(PieView.horizontal);
        lrView.setPercentage(50);

        avgText = (TextView)view.findViewById(R.id.lrAvgText);
        avgText.setVisibility(View.INVISIBLE);

        leftText = (TextView)view.findViewById(R.id.leftText);
        rightText = (TextView)view.findViewById(R.id.rightText);
    }

    /**
     * setHanlder
     * @param handler
     */
    public void setHanlder(Handler handler)    {
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
     * start
     */
    public void start() {
        running = true;
    }

    /**
     * pause
     */
    public void pause() {
        running = false;
    }

    /**
     * stop
     */
    public void stop()  {
        running = false;
    }

    /**
     * update
     */
    public void update() {
        if( running )   {
            if( isAvg ) {
//            int left = (int)(sensor.getAccumulatedLeftAverage() / 10.24D);
//            int right = (int)(sensor.getAccumulatedRightAverage() / 10.24D);

                lrView.setPercentage(sensor.getAccumulatedLeftRatio());

//            leftText.setText(String.valueOf(left));
//            rightText.setText(String.valueOf(right));
                leftText.setText(String.valueOf(sensor.getAccumulatedLeftRatio()));
                rightText.setText(String.valueOf(100-sensor.getAccumulatedLeftRatio()));

                avgText.setVisibility(View.VISIBLE);
            }
            else {
//            int left = (int)(sensor.getLeftAverage() / 10.24D);
//            int right = (int)(sensor.getRightAverage() / 10.24D);

                lrView.setPercentage(sensor.getLeftRatio());

//            leftText.setText(String.valueOf(left));
//            rightText.setText(String.valueOf(right));
                leftText.setText(String.valueOf(sensor.getLeftRatio()));
                rightText.setText(String.valueOf(100-sensor.getLeftRatio()));

                avgText.setVisibility(View.INVISIBLE);
            }
        }
    }
}