package alphaclo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import alphaclo.R;

public class PercentView extends View {
    public static int horizontal;
    public static int vertical;
    Paint backPaint;
    Paint frontPaint;
    boolean isAvg;
    int mode;
    int percentage;
    RectF rect;

    static {
        vertical = 1;
        horizontal = 2;
    }

    public PercentView(Context context) {
        super(context);
        this.percentage = 0;
        this.isAvg = false;
        initView();
    }

    public PercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.percentage = 0;
        this.isAvg = false;
        initView();
    }

    public PercentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.percentage = 0;
        this.isAvg = false;
        initView();
    }

    private void initView() {
        this.frontPaint = new Paint();
        this.frontPaint.setColor(getContext().getResources().getColor(R.color.accent));
        this.frontPaint.setAntiAlias(true);
        this.frontPaint.setStyle(Style.FILL);
        this.frontPaint.setTextSize(50.0f);
        this.backPaint = new Paint();
        this.backPaint.setColor(getContext().getResources().getColor(R.color.accent_2));
        this.backPaint.setAntiAlias(true);
        this.backPaint.setStyle(Style.FILL);
        this.backPaint.setTextSize(50.0f);
        this.rect = new RectF();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        rect.set((float) 0, (float) 0, width, width);

        if (this.mode == horizontal) {
            this.backPaint.setColor(getContext().getResources().getColor(R.color.accent));
            this.frontPaint.setColor(getContext().getResources().getColor(R.color.accent_2));

            if (this.percentage == 0) {
                canvas.drawArc(this.rect, 60.0f, -300.0f, true, this.frontPaint);
            }
            else if (this.percentage == 100) {
                canvas.drawArc(this.rect, 120.0f, 300.0f, true, this.backPaint);
            }
            else {
                canvas.drawArc(this.rect, 120.0f, (float) (this.percentage * 3), true, this.backPaint);
                canvas.drawArc(this.rect, 60.0f, (float) (-((100 - this.percentage) * 3)), true, this.frontPaint);
            }
        }
        else    {
            this.backPaint.setColor(getContext().getResources().getColor(R.color.accent));
            this.frontPaint.setColor(getContext().getResources().getColor(R.color.accent_2));

            if (this.percentage == 0) {
                canvas.drawArc(this.rect, 60.0f, -300.0f, true, this.frontPaint);
            }
            else if (this.percentage == 100) {
                canvas.drawArc(this.rect, 120.0f, 300.0f, true, this.backPaint);
            }
            else {
                canvas.drawArc(this.rect, 120.0f, (float) (this.percentage * 3), true, this.backPaint);
                canvas.drawArc(this.rect, 60.0f, (float) (-((100 - this.percentage) * 3)), true, this.frontPaint);
            }
        }

        // width = 945 기준
        float a = width / 13.5f;    // 70
        float b = width / 2.48f;    // 380
        float c = width / 1.35f;    // 700
        float d = width / 1.783f;   // 530
        float e = width / 1.718f;   // 550
        float f = width / 1.08f;    // 875

        this.frontPaint.setColor(getContext().getResources().getColor(R.color.primary));
        this.rect.set(a, a, f, f);
        canvas.drawArc(this.rect, -90.0f, 360.0f, true, this.frontPaint);

        this.frontPaint.setColor(getContext().getResources().getColor(R.color.accent_2));
        this.frontPaint.setTextSize(50.0f);
        if( width < 800 )
            this.frontPaint.setTextSize(40.0f);
        this.frontPaint.setColor(getContext().getResources().getColor(R.color.material_grey_300));

        if (this.mode == horizontal) {
            canvas.drawText("Left", width * 0.25f, b, this.frontPaint);
            canvas.drawText("Right", width * 0.62f, b, this.frontPaint);
        }
        else    {
            canvas.drawText("Quads", width * 0.22f, b, this.frontPaint);
            canvas.drawText("Hams", width * 0.62f, b, this.frontPaint);
        }

        if (this.isAvg) {
            canvas.drawText("(Avg)", width * 0.43f, c, this.frontPaint);
        }

        this.frontPaint.setTextSize(100.0f);
        if( width < 800 )
            this.frontPaint.setTextSize(80.0f);
        this.frontPaint.setColor(getContext().getResources().getColor(R.color.material_grey_600));
        canvas.drawText("%", width * 0.46f, d, this.frontPaint);
        this.frontPaint.setTextSize(150.0f);
        if( width < 800 )
            this.frontPaint.setTextSize(120.0f);
        if (this.percentage > 50) {
            this.frontPaint.setColor(getContext().getResources().getColor(R.color.accent));
            if (this.percentage < 100) {
                canvas.drawText(String.valueOf(this.percentage), width * 0.21f, e, this.frontPaint);
            } else {
                canvas.drawText(String.valueOf(this.percentage), width * 0.15f, e, this.frontPaint);
            }
            this.frontPaint.setColor(getContext().getResources().getColor(R.color.white));
            canvas.drawText(String.valueOf(100 - this.percentage), width * 0.6f, e, this.frontPaint);
        } else if (this.percentage == 50) {
            this.frontPaint.setColor(getContext().getResources().getColor(R.color.accent));
            canvas.drawText(String.valueOf(this.percentage), width * 0.21f, e, this.frontPaint);
            this.frontPaint.setColor(getContext().getResources().getColor(R.color.accent_2));
            canvas.drawText(String.valueOf(100 - this.percentage), width * 0.6f, e, this.frontPaint);
        } else {
            this.frontPaint.setColor(getContext().getResources().getColor(R.color.white));
            canvas.drawText(String.valueOf(this.percentage), width * 0.21f, e, this.frontPaint);
            this.frontPaint.setColor(getContext().getResources().getColor(R.color.accent_2));
            canvas.drawText(String.valueOf(100 - this.percentage), width * 0.6f, e, this.frontPaint);
        }
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
        invalidate();
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setColor(int front, int back) {
        this.frontPaint.setColor(front);
        this.backPaint.setColor(back);
    }

    public void setAvgMode(boolean isAvg) {
        this.isAvg = isAvg;
    }
}
