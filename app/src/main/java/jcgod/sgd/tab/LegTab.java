package jcgod.sgd.tab;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jcgod.sgd.R;
import jcgod.sgd.activity.MainActivity;

/**
 * Created by Jaecheol on 16. 8. 12..
 */
public class LegTab extends Fragment {

    View view;

    MainActivity.Sensor sensor;

    Handler handler;

    RelativeLayout legTab;

    boolean isAvg = false;

    TextView avgText;

    ImageView leftMuscle;
    ImageView rightMuscle;

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
        view = inflater.inflate(R.layout.tab_leg, container,false);

        initView();

        return view;
    }

    /**
     * initView
     */
    private void initView() {
        legTab = (RelativeLayout)view.findViewById(R.id.legTab);
        legTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAvg = !isAvg;

                update();
            }
        });

        leftMuscle = (ImageView)view.findViewById(R.id.leftMuscle);
        rightMuscle = (ImageView)view.findViewById(R.id.rightMuscle);

        avgText = (TextView)view.findViewById(R.id.legAvgText);
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
                int leftRatio = (int)(sensor.getAccumulatedLeftAverage() / 10.24D);
                int rightRatio = (int)(sensor.getAccumulatedRightAverage() / 10.24D);
                float leftAlpha = leftRatio / 100f;
                float rightAlpha = rightRatio / 100f;

                leftMuscle.setAlpha(leftAlpha);
                rightMuscle.setAlpha(rightAlpha);

                leftText.setText(String.valueOf(leftRatio));
                rightText.setText(String.valueOf(rightRatio));

                avgText.setVisibility(View.VISIBLE);
            }
            else {
                int leftRatio = (int)(sensor.getLeftAverage() / 10.24D);
                int rightRatio = (int)(sensor.getRightAverage() / 10.24D);
                float leftAlpha = leftRatio / 100f;
                float rightAlpha = rightRatio / 100f;

                leftMuscle.setAlpha(leftAlpha);
                rightMuscle.setAlpha(rightAlpha);

                leftText.setText(String.valueOf(leftRatio));
                rightText.setText(String.valueOf(rightRatio));

            /*
            int leftRatio = sensor.getLeftRatio() * 2;
            int leftColor = Color.argb(255, 255, 255 - leftRatio, 255 - leftRatio);
            leftImage.setBackgroundTintList(ColorStateList.valueOf(leftColor));

            int rightRatio = sensor.getRightRatio() * 2;
            int rightColor = Color.argb(255, 255, 255 - rightRatio, 255 - rightRatio);
            rightImage.setBackgroundTintList(ColorStateList.valueOf(rightColor));
            */
                avgText.setVisibility(View.INVISIBLE);
            }
        }
    }
}
