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
        if( isAvg ) {
            lrView.setPercentage(sensor.getAccumulatedLeftRatio());
            avgText.setVisibility(View.VISIBLE);
        }
        else {
            lrView.setPercentage(sensor.getLeftRatio());
            avgText.setVisibility(View.INVISIBLE);
        }
    }
}