package alphaclo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import alphaclo.R;


public class SplashActivity extends Activity {
    private Handler mHandler;
    private Runnable mRunnable;
    private final int splashTime = 2500;

    public SplashActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        };

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, splashTime);
    }
}
