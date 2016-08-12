package jcgod.sgd.tab;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jcgod.sgd.R;
import jcgod.sgd.activity.MainActivity;

/**
 * Created by Jaecheol on 16. 6. 18..
 */
public class MainTab extends Fragment {

    View view;

    Handler handler;

    MainActivity.Sensor sensor;

    public MainInfo info;

    ProgressDialog alertDialog;

    RelativeLayout mainTab;

    TextView lrText;
    TextView qhText;

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
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.tab_main, container, false);

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
        info = new MainInfo();

        alertDialog = new ProgressDialog(view.getContext());

        lrText = (TextView)view.findViewById(R.id.main_lrText);
        qhText = (TextView)view.findViewById(R.id.main_qhText);

        mainTab = (RelativeLayout)view.findViewById(R.id.mainTab);
        mainTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAvg = !isAvg;

                update("");
            }
        });
    }

    /**
     * update
     */
    public void update(String packet)   {
        info.packet.setText(packet);

        String lr;
        String qh;

        if( isAvg ) {
            lrText.setText("L/R (avg)");
            qhText.setText("Q/H (avg)");

            lr = sensor.getAccumulatedLeftRatio() + "/" + sensor.getAccumulatedRightRatio();
            qh = sensor.getAccumulatedQuadsRatio() + "/" + sensor.getAccumulatedHamsRatio();
        }
        else    {
            lrText.setText("L/R");
            qhText.setText("Q/H");

            lr = sensor.getLeftRatio() + "/" + sensor.getRightRatio();
            qh = sensor.getQuadsRatio() + "/" + sensor.getHamsRatio();
        }

        info.LRRatio.setText(lr);
        info.QHRatio.setText(qh);
    }

    /**
     * showReportDialog
     */
    public void showReportDialog() {
        String title = "Activity Report";
        String contents = "Time : " + info.chronometer.getText() + "\n"
                + "Distance : " + info.distance.getText() + "\n"
                + "Calory : " + info.calory.getText() + "\n"
                + "L/R (avg) : " + info.LRRatio.getText() + "\n"
                + "Q/H (avg) : " + info.QHRatio.getText();

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(title);
        builder.setMessage(contents);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public class MainInfo  {
        Chronometer chronometer;
        TextView distance;
        TextView calory;
        TextView LRRatio;
        TextView QHRatio;

        TextView packet;

        private boolean running;

        private long timeWhenPaused = 0;

        /**
         * MainInfo
         */
        public MainInfo()   {
            running = false;

            chronometer = (Chronometer)view.findViewById(R.id.chronometer);

            distance = (TextView)view.findViewById(R.id.main_distance);
            calory = (TextView)view.findViewById(R.id.main_calory);
            LRRatio = (TextView)view.findViewById(R.id.main_lr);
            QHRatio = (TextView)view.findViewById(R.id.main_qh);
            packet = (TextView)view.findViewById(R.id.packet);
//            packet.setVisibility(View.INVISIBLE);
        }

        /**
         * start
         */
        public void start() {
            running = true;

            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            timeWhenPaused = 0;

            chronometer.start();
        }

        /**
         * pause
         */
        public void pause() {
            running = false;

            timeWhenPaused = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
        }

        /**
         * stop
         */
        public void stop()  {
            showReportDialog();

            running = false;

            distance.setText("0m");
            LRRatio.setText("0/0");
            QHRatio.setText("0/0");
            packet.setText("");

            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
        }
    }
}