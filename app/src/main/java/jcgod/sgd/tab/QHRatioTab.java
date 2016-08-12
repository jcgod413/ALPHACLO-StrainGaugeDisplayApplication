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
public class QHRatioTab extends Fragment {

    View view;

    Handler handler;
    MainActivity.Sensor sensor;

    Button quadsRatio;
    Button hamsRatio;

    RelativeLayout qhTab;

    boolean isAvg = false;


    PieView qhView;

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
        view = inflater.inflate(R.layout.tab_qh, container, false);

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
        qhTab = (RelativeLayout)view.findViewById(R.id.qhTab);
        qhTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAvg = !isAvg;

                update();
            }
        });

        qhView = (PieView)view.findViewById(R.id.qhView);
        qhView.setMode(PieView.vertical);

        avgText = (TextView)view.findViewById(R.id.qhAvgText);
        avgText.setVisibility(View.INVISIBLE);
    }

    /**
     * update
     */
    public void update() {
        if( isAvg ) {
            qhView.setPercentage(sensor.getAccumulatedQuadsRatio());
            avgText.setVisibility(View.VISIBLE);
        }
        else {
            qhView.setPercentage(sensor.getQuadsRatio());
            avgText.setVisibility(View.INVISIBLE);
        }
    }
}