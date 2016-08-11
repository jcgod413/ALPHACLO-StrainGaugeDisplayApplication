package jcgod.sgd.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import jcgod.sgd.R;

/**
 * Created by Jaecheol on 16. 7. 7..
 */
public class DonutView extends View {


    private float radius;

    Paint paint;
    Paint shadowPaint;

    Path myPath;
    Path shadowPath;

    RectF outterCircle;
    RectF innerCircle;
    RectF shadowRectF;

    int yellow = 25;
    int blue = 25;
    int green = 25;
    int red = 25;

    public DonutView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DonutView,
                0, 0
        );

        try {
            radius = a.getDimension(R.styleable.DonutView_radius, 20.0f);
        } finally {
            a.recycle();
        }

        paint = new Paint();
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(radius / 14.0f);

        shadowPaint = new Paint();
        shadowPaint.setColor(0xf0000000);
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setAntiAlias(true);
        shadowPaint.setStrokeWidth(6.0f);
        shadowPaint.setMaskFilter(new BlurMaskFilter(4, BlurMaskFilter.Blur.SOLID));


        myPath = new Path();
        shadowPath = new Path();


        outterCircle = new RectF();
        innerCircle = new RectF();
        shadowRectF = new RectF();

        float adjust = (.019f*radius);
        shadowRectF.set(adjust, adjust, radius*2-adjust, radius*2-adjust);

        adjust = .038f * radius;
        outterCircle.set(adjust, adjust, radius*2-adjust, radius*2-adjust);

        adjust = .276f * radius;
        innerCircle.set(adjust, adjust, radius*2-adjust, radius*2-adjust);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw shadow
        paint.setShader(null);
        float adjust = (.0095f*radius);
        paint.setShadowLayer(8, adjust, -adjust, 0xaa000000);
        drawDonut(canvas, paint, 0, 359.9f);

        paint.setTextSize(50);
//
//        // green
//        setGradient(0xff84BC3D,0xff5B8829);
//        drawDonut(canvas,paint, 0,60);
//
//        //red
//        setGradient(0xffe04a2f,0xffB7161B);
//        drawDonut(canvas,paint, 60,60);
//
//        // blue
//        setGradient(0xff4AB6C1,0xff2182AD);
//        drawDonut(canvas,paint, 120,60);
//
//        // yellow
//        setGradient(0xffFFFF00,0xfffed325);
//        drawDonut(canvas,paint, 180,180);

        //red
        float redStart = 45 - (red * 1.8f);
        float redLength = red * 3.6f;
        if( redLength > 0f )    {
            setGradient(0xffe04a2f, 0xffB7161B);
            drawDonut(canvas, paint, redStart, redLength);
            canvas.drawText("R/H", radius / 12f * 13f, radius / 10f * 13f, paint);
            canvas.drawText(String.valueOf(red), radius / 5f * 9f, radius / 5f * 9f, paint);
        }

        // blue
        float blueStart = redStart + redLength;
        float blueLength = blue * 3.6f;
        if( blueLength > 0f )   {
            setGradient(0xff4AB6C1, 0xff2182AD);
            drawDonut(canvas, paint, blueStart, blueLength);
            canvas.drawText("L/H", radius / 3f * 2f, radius / 10f * 13f, paint);
            canvas.drawText(String.valueOf(blue), 0f, radius / 5f * 9f, paint);
        }

        // yellow
        float yellowStart = blueStart + blueLength;
        float yellowLength = yellow * 3.6f;
        if( yellowLength > 0f ) {
            setGradient(0xffFFFF00, 0xfffed325);
            drawDonut(canvas, paint, yellowStart, yellowLength);
            canvas.drawText("L/Q", radius / 3f * 2f, radius / 5f * 4f, paint);
            canvas.drawText(String.valueOf(yellow), 0f, radius / 5f, paint);
        }

        // green
        float greenStart = yellowStart + yellowLength;
        float greenLength = green * 3.6f;
        if( greenLength > 0f )  {
            setGradient(0xff84BC3D, 0xff5B8829);
            drawDonut(canvas, paint, greenStart, greenLength);
            canvas.drawText("R/Q", radius / 12f * 13f, radius / 5f * 4f, paint);
            canvas.drawText(String.valueOf(green), radius / 5f * 9f, radius / 5f, paint);
        }
    }

    public void drawDonut(Canvas canvas, Paint paint, float start,float sweep){

        myPath.reset();
        myPath.arcTo(outterCircle, start, sweep, false);
        myPath.arcTo(innerCircle, start+sweep, -sweep, false);
        myPath.close();
        canvas.drawPath(myPath, paint);
    }

    public void setGradient(int sColor, int eColor){
        paint.setShader(new RadialGradient(radius, radius, radius - 5,
                new int[]{sColor, eColor},
                new float[]{.6f, .95f}, Shader.TileMode.CLAMP) );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = (int) radius*2;
        int desiredHeight = (int) radius*2;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //70dp exact
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }else if (widthMode == MeasureSpec.AT_MOST) {
            //wrap content
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    public void setPercent(int yellow, int blue, int green, int red)    {
        if( yellow != this.yellow ||
            blue != this.blue ||
            green != this.green ||
            red != this.red )
        {
            this.yellow = yellow;
            this.blue = blue;
            this.green = green;
            this.red = red;

            invalidate();
        }
    }
}
