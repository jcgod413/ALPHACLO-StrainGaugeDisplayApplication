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

import jcgod.sgd.R;
import jcgod.sgd.activity.MainActivity;

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
        ViewGroup.LayoutParams layoutParams;

        leftRatio = (Button)view.findViewById(R.id.leftRatio);
        layoutParams = leftRatio.getLayoutParams();
        layoutParams.height = 0;
        leftRatio.setLayoutParams(layoutParams);

        rightRatio = (Button)view.findViewById(R.id.rightRatio);
        layoutParams = rightRatio.getLayoutParams();
        layoutParams.height = 0;
        rightRatio.setLayoutParams(layoutParams);

        lrTab = (RelativeLayout)view.findViewById(R.id.lrTab);
        lrTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAvg = !isAvg;

                update();
            }
        });
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
     * update
     */
    public void update() {
        ViewGroup.LayoutParams layoutParams;

        String leftText;
        String rightText;

        if( isAvg ) {
            leftText = "LEFT(avg)\n" + sensor.getAccumulatedLeftRatio();
            rightText = "RIGHT(avg)\n" + sensor.getAccumulatedRightRatio();

            layoutParams = leftRatio.getLayoutParams();
            layoutParams.height = sensor.getAccumulatedLeftRatio() * 10;
            leftRatio.setLayoutParams(layoutParams);

            layoutParams = rightRatio.getLayoutParams();
            layoutParams.height = sensor.getAccumulatedRightRatio() * 10;
            rightRatio.setLayoutParams(layoutParams);
        }
        else {
            leftText = "LEFT\n" + sensor.getLeftRatio();
            rightText = "RIGHT\n" + sensor.getRightRatio();

            layoutParams = leftRatio.getLayoutParams();
            layoutParams.height = sensor.getLeftRatio() * 10;
            leftRatio.setLayoutParams(layoutParams);

            layoutParams = rightRatio.getLayoutParams();
            layoutParams.height = sensor.getRightRatio() * 10;
            rightRatio.setLayoutParams(layoutParams);
        }


        leftRatio.setText(leftText);
        rightRatio.setText(rightText);
    }
}