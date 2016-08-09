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

import org.w3c.dom.Text;

import jcgod.sgd.R;
import jcgod.sgd.activity.MainActivity;
import jcgod.sgd.view.PercentView;

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

    PercentView lrView;

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

        lrView = (PercentView)view.findViewById(R.id.lrView);
        lrView.setMode(PercentView.horizontal);

        avgText = (TextView)view.findViewById(R.id.lrAvgText);
        avgText.setVisibility(View.INVISIBLE);
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

            lrView.setPercentage(sensor.getAccumulatedLeftRatio());
            avgText.setVisibility(View.VISIBLE);
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

            lrView.setPercentage(sensor.getLeftRatio());
            avgText.setVisibility(View.INVISIBLE);
        }


        leftRatio.setText(leftText);
        rightRatio.setText(rightText);
    }
}