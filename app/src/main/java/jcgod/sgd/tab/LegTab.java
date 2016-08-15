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

    ImageView leftImage;
    ImageView rightImage;

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

        leftImage = (ImageView)view.findViewById(R.id.leftImage);
        rightImage = (ImageView)view.findViewById(R.id.rightImage);

        avgText = (TextView)view.findViewById(R.id.legAvgText);
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
            int leftRatio = sensor.getAccumulatedLeftRatio();
            int rightRatio = sensor.getAccumulatedRightRatio();
            float leftAlpha = leftRatio / 100f;
            float rightAlpha = rightRatio / 100f;

            leftImage.setAlpha(leftAlpha);
            rightImage.setAlpha(rightAlpha);
            /*
            int leftRatio = sensor.getAccumulatedLeftRatio() * 2;
            int leftColor = Color.argb(255, 255, 255 - leftRatio, 255 - leftRatio);
            leftImage.setBackgroundTintList(ColorStateList.valueOf(leftColor));

            int rightRatio = sensor.getAccumulatedRightRatio() * 2;
            int rightColor = Color.argb(255, 255, 255 - rightRatio, 255 - rightRatio);
            rightImage.setBackgroundTintList(ColorStateList.valueOf(rightColor));
            */
            avgText.setVisibility(View.VISIBLE);

        }
        else {
            int leftRatio = sensor.getLeftRatio();
            int rightRatio = sensor.getRightRatio();
            float leftAlpha = leftRatio / 100f;
            float rightAlpha = rightRatio / 100f;

            leftImage.setAlpha(leftAlpha);
            rightImage.setAlpha(rightAlpha);
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
