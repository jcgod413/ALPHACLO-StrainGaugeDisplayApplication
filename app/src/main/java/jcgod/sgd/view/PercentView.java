package jcgod.sgd.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import jcgod.sgd.R;

/**
 * Created by Jaecheol on 16. 7. 4..
 */
public class PercentView extends View {

    Paint frontPaint;
    Paint backPaint;

    RectF rect;

    int percentage = 0;

    int mode;

    public static int vertical = 1;
    public static int horizontal = 2;

    public PercentView(Context context) {
        super(context);
        initView();
    }
    public PercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    public PercentView(Context context, AttributeSet attrs, int defStyle)   {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        frontPaint = new Paint();
        frontPaint.setColor(getContext().getResources().getColor(R.color.colorAccent));
        frontPaint.setAntiAlias(true);
        frontPaint.setStyle(Paint.Style.FILL);
        frontPaint.setTextSize(50);

        backPaint = new Paint();
        backPaint.setColor(getContext().getResources().getColor(R.color.colorPrimary));
        backPaint.setAntiAlias(true);
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setTextSize(50);

        rect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas)    {
        super.onDraw(canvas);

        int left = 0;
        int width = getWidth();
        int top = 0;
        rect.set(left, top, left + width, top + width);
        canvas.drawArc(rect, -90, 360, true, backPaint);

        if (mode == vertical) {
            canvas.drawArc(rect, 90f - (1.8f * percentage), 3.6f * percentage, true, frontPaint);
            backPaint.setColor(Color.WHITE);
            frontPaint.setColor(Color.WHITE);
            if( percentage == 0 )   {
                canvas.drawText(String.valueOf(100 - percentage), width / 2f / 10f * 8f, 80f, backPaint);
                canvas.drawText(String.valueOf(percentage), width / 2f / 11f * 10f, width - 50f, frontPaint);
            }
            else if( percentage == 100 )    {
                canvas.drawText(String.valueOf(100 - percentage), width / 2f / 11f * 10f, 80f, backPaint);
                canvas.drawText(String.valueOf(percentage), width / 2f / 10f * 8f, width - 50f, frontPaint);
            }
            else {
                canvas.drawText(String.valueOf(100 - percentage), width / 2f / 9f * 8f, 80f, backPaint);
                canvas.drawText(String.valueOf(percentage), width / 2f / 10f * 9f, width - 50f, frontPaint);
            }
            backPaint.setColor(getContext().getResources().getColor(R.color.colorPrimary));
            frontPaint.setColor(getContext().getResources().getColor(R.color.colorAccent));
        }
        else if( mode == horizontal )   {
            canvas.drawArc(rect, 180f - (1.8f * percentage), 3.6f * percentage, true, frontPaint);
            backPaint.setColor(Color.WHITE);
            frontPaint.setColor(Color.WHITE);
            canvas.drawText(String.valueOf(percentage), 50f, width / 2f / 14f * 15f, backPaint);
            canvas.drawText(String.valueOf(100-percentage), width-110f, width / 2f / 14f * 15f, frontPaint);
            backPaint.setColor(getContext().getResources().getColor(R.color.colorPrimary));
            frontPaint.setColor(getContext().getResources().getColor(R.color.colorAccent));
        }

    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
        invalidate();
    }

    public void setMode(int mode)   {
        this.mode = mode;
    }

    public void setColor(int front, int back)   {
        frontPaint.setColor(front);
        backPaint.setColor(back);
    }
}
