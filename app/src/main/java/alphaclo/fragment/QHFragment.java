package alphaclo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import alphaclo.R;
import alphaclo.view.PercentView;


public class QHFragment extends Fragment {
    private int beforePercent;
    private int currentPercent;
    private boolean isAvg;
    private RelativeLayout lrLayout;
    private Handler mHandler;
    private Runnable mRunnable;
    private TextView maText;
    private PercentView percentView;

    /* renamed from: com.sjc.alphaclo.fragment.QHFragment.1 */
    class C02531 implements View.OnClickListener {
        C02531() {
        }

        public void onClick(View v) {
            QHFragment.this.percentView.setAvgMode(QHFragment.this.isAvg = !QHFragment.this.isAvg);
            QHFragment.this.percentView.invalidate();
        }
    }

    /* renamed from: com.sjc.alphaclo.fragment.QHFragment.2 */
    class C02542 implements Runnable {
        C02542() {
        }

        public void run() {
            if (QHFragment.this.beforePercent > QHFragment.this.currentPercent) {
                QHFragment.this.beforePercent = QHFragment.this.beforePercent - 1;
                QHFragment.this.mHandler.postDelayed(QHFragment.this.mRunnable, 3);
            } else if (QHFragment.this.beforePercent < QHFragment.this.currentPercent) {
                QHFragment.this.beforePercent = QHFragment.this.beforePercent + 1;
                QHFragment.this.mHandler.postDelayed(QHFragment.this.mRunnable, 3);
            }
            QHFragment.this.percentView.setPercentage(QHFragment.this.beforePercent);
        }
    }

    public QHFragment() {
        this.isAvg = false;
        this.beforePercent = 0;
        this.currentPercent = 0;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lr, container, false);
        this.percentView = (PercentView) view.findViewById(R.id.percentView);
        this.percentView.setPercentage(50);
        this.percentView.setMode(PercentView.vertical);
        this.maText = (TextView) view.findViewById(R.id.maText);
        this.lrLayout = (RelativeLayout) view.findViewById(R.id.layout_lr);
        this.lrLayout.setOnClickListener(new C02531());
        this.mHandler = new Handler();
        return view;
    }

    public void setMA(int ma) {
        this.maText.setText(String.valueOf(ma));
    }

    public void updateRatio(int leftPercent, int accLeftPercent) {
        if (this.isAvg) {
            this.currentPercent = accLeftPercent;
        } else {
            this.currentPercent = leftPercent;
        }
        this.mRunnable = new C02542();
        this.mHandler.post(this.mRunnable);
    }
}
