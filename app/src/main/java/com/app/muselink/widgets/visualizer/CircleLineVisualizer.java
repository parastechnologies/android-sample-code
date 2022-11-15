package com.app.muselink.widgets.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;

import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.app.muselink.R;
import com.app.muselink.widgets.visualizer.base.BaseVisualizer;


/**
 * @author maple on 2019/4/24 15:17.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class CircleLineVisualizer extends BaseVisualizer {

    private static final int BAR_MAX_POINTS = 300;
    private static final int BAR_MIN_POINTS = 10;
    private Rect mClipBounds;
    private int mPoints;
    private int mPointRadius;
    private float[] mSrcY;
    private int mRadius;
    private Paint mGPaint;
    private boolean drawLine;
    public CircleLineVisualizer(Context context) {
        super(context);
    }
    public CircleLineVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public CircleLineVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public boolean isDrawLine() {
        return drawLine;
    }
    /**
     * control the display of drawLine
     *
     * @param drawLine is show drawLine
     */
    public void setDrawLine(boolean drawLine) {
        this.drawLine = drawLine;
    }
    int cx = 0;
    int cy = 0;
    @Override
    protected void init() {
        mPoints = (int) (BAR_MAX_POINTS * mDensity);
        if (mPoints < BAR_MIN_POINTS) mPoints = BAR_MIN_POINTS;
        mSrcY = new float[mPoints];
        mClipBounds = new Rect();
        setAnimationSpeed(mAnimSpeed);
        mPaint.setAntiAlias(true);
        mGPaint = new Paint();
        mGPaint.setAntiAlias(true);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (int) (this.getResources().getDimensionPixelOffset(R.dimen._80sdp) / getResources().getDisplayMetrics().density) - (int) (this.getResources().getDimensionPixelOffset(R.dimen._15sdp) / getResources().getDisplayMetrics().density);

//        mRadius = Math.min(w, h) / 2;
//        cx = w/2;
//        cy = h/2;
        mPointRadius = 1;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.getClipBounds(mClipBounds);
        updateData();

//        drawLines(canvas);

        for (int i = 0; i < 360; i++) {

            Path path = new Path();

//            if (mSrcY[i * mPoints / 360] == 0) continue;
            if(i%5==0) {
                canvas.save();
                canvas.rotate(-i, getWidth() / 2, getHeight() / 2);
                float cx = (float) (getWidth() / 2 + mRadius);
                float cy = (float) (getHeight() / 2);

                mPaint = new Paint();
                mPaint.setStrokeWidth(5.0f);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeCap(Paint.Cap.ROUND);

                if (i < 90) {
                    float index = Float.parseFloat(String.valueOf(i / 3.0f));
                    float red = (248 - (5.7f) * index) / 255;
                    float green = ((225 + (0.2f * index)) / 255);
                    float blue = ((0 + (7.6f * index)) / 255);
                    mPaint.setColor(Color.argb(1.0f, red, green, blue));
                } else if (i < 180) {
                    float index = ((i - 90) / 3);
                    float red = (75 + (5) * index) / 255;
                    float green = ((231 + (7.5f * index)) / 255);
                    float blue = ((228 + (0.67f * index)) / 255);
                    mPaint.setColor(Color.argb(1.0f, red, green, blue));
                } else if (i < 270) {
                    float index = ((i - 180) / 3);
                    float red = (233 + ((0.23f) * index)) / 255;
                    float green = (0 + (4.9f * index)) / 255;
                    float blue = (248 - (6.94f * index)) / 255;
                    mPaint.setColor(Color.argb(1.0f, red, green, blue));
                } else {
                    float index = ((i - 270) / 3);
                    float red = (240 + ((0.27f) * index)) / 255;
                    float green = (147 + (2.8f * index)) / 255;
                    float blue = (40 - (1.33f * index)) / 255;
                    mPaint.setColor(Color.argb(1.0f, red, green, blue));
                }

//                float x = (float) ((cx) + Math.cos(degreesToRadians(i)) * mRadius);
//                float y = (float) ((cy) + Math.sin(degreesToRadians(i)) * (mRadius));

                path.moveTo(cx,cy-mPointRadius);
                path.lineTo(cx + mSrcY[i * mPoints / 360],cy + mPointRadius);
                canvas.drawPath(path,mPaint);
//                canvas.drawRect(cx, cy - mPointRadius, cx + mSrcY[i * mPoints / 360],
//                        cy + mPointRadius, mPaint);
//            canvas.drawCircle(cx + mSrcY[i * mPoints / 360], cy, mPointRadius, mPaint);
                canvas.restore();
            }
        }
    }

    public double degreesToRadians(float degrees){
        return degrees * Math.PI / 180;
    }

    /**
     * Draw a translucent ray
     *
     * @param canvas target canvas
     */
    private void drawLines(Canvas canvas) {
        int lineLen = 60 * mPointRadius;//default len,

        //UpperLine
        for (int i = 0; i < 360; i++) {

            if(i%3==0) {

                mGPaint = new Paint();
                mGPaint.setStrokeWidth(5.0f);
//            mGPaint.setStrokeWidth(30.0f);
                mGPaint.setStyle(Paint.Style.STROKE);
                mGPaint.setStrokeCap(Paint.Cap.ROUND);

//                canvas.save();
//                canvas.rotate(-i, getWidth() / 2, getHeight() / 2);
//            float cx = (float) (getWidth() / 2 + mRadius) + mSrcY[i * mPoints / 360] ;
//            float cy = (float) (getHeight() / 2);
                Path path = new Path();
                ;

                float x = (float) ((cx) + Math.cos(degreesToRadians(i)) * mRadius);
                float y = (float) ((cy) + Math.sin(degreesToRadians(i)) * mRadius);

                path.moveTo(cx, cy);
                path.lineTo(x, y);
//            path.lineTo(cx + lineLen, cy);



                if (i < 90) {
                    float index = Float.parseFloat(String.valueOf(i / 3.0f));
                    float red = (248 - (5.7f) * index) / 255;
                    float green = ((225 + (0.2f * index)) / 255);
                    float blue = ((0 + (7.6f * index)) / 255);
                    mGPaint.setColor(Color.argb(1.0f, red, green, blue));
                } else if (i < 180) {
                    float index = ((i - 90) / 3);
                    float red = (75 + (5) * index) / 255;
                    float green = ((231 + (7.5f * index)) / 255);
                    float blue = ((228 + (0.67f * index)) / 255);
                    mGPaint.setColor(Color.argb(1.0f, red, green, blue));
                } else if (i < 270) {
                    float index = ((i - 180) / 3);
                    float red = (233 + ((0.23f) * index)) / 255;
                    float green = (0 + (4.9f * index)) / 255;
                    float blue = (248 - (6.94f * index)) / 255;
                    mGPaint.setColor(Color.argb(1.0f, red, green, blue));
                } else {
                    float index = ((i - 270) / 3);
                    float red = (240 + ((0.27f) * index)) / 255;
                    float green = (147 + (2.8f * index)) / 255;
                    float blue = (40 - (1.33f * index)) / 255;
                    mGPaint.setColor(Color.argb(1.0f, red, green, blue));
                }

//            if (i < 90) {
//                if(i<30) {
//                    mGPaint.setColor(COLOR_BLUE);
//                    mGPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_BLUE, COLOR_PINK, Shader.TileMode.MIRROR));
//                }else if(i<60){
//                    mGPaint.setColor(COLOR_PINK);
//                    mGPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_PINK, COLOR_BLUE, Shader.TileMode.MIRROR));
//
//                }else {
//                    mGPaint.setColor(COLOR_SKY);
//                    mGPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_SKY, COLOR_SKY, Shader.TileMode.MIRROR));
//                }
//
//            } else if (i < 180) {
//                if(i<120) {
//                    mGPaint.setColor(COLOR_SKY);
//                    mGPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_GREEN, COLOR_SKY, Shader.TileMode.MIRROR));
//                }else if(i<150){
//                    mGPaint.setColor(COLOR_GREEN);
//                    mGPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_GREEN, COLOR_YELLOW, Shader.TileMode.MIRROR));
//
//                }else {
//                    mGPaint.setColor(COLOR_YELLOW);
//                    mGPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_YELLOW, COLOR_GREEN, Shader.TileMode.MIRROR));
//
//                }
//            } else if (i < 270) {
//                if(i<210) {
//                    mGPaint.setColor(COLOR_GREEN);
//                    mGPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_GREEN, COLOR_YELLOW, Shader.TileMode.MIRROR));
//                }else if(i<240){
//                    mGPaint.setColor(COLOR_YELLOW);
//                    mGPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_YELLOW, COLOR_MUSTARD, Shader.TileMode.MIRROR));
//
//                }else {
//                    mGPaint.setColor(COLOR_MUSTARD);
//                    mPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_MUSTARD, COLOR_MUSTARD, Shader.TileMode.MIRROR));
//
//                }
//            } else {
//                if(i<310) {
//                    mGPaint.setColor(COLOR_MUSTARD);
//                    mGPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_RED, COLOR_MUSTARD, Shader.TileMode.MIRROR));
//
////                    mPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_SKY, COLOR_GREEN, Shader.TileMode.MIRROR));
//                }else if(i<330){
//                    mGPaint.setColor(COLOR_RED);
//                    mGPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_RED, COLOR_MUSTARD, Shader.TileMode.MIRROR));
//
////                    mPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), COLOR_GREEN, COLOR_SKY, Shader.TileMode.MIRROR));
//
//                }else {
//                    mGPaint.setColor(COLOR_PINK);
//                    mGPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), COLOR_PINK, COLOR_RED, Shader.TileMode.MIRROR));
//
////                    mPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), COLOR_SKY, COLOR_BLUE, Shader.TileMode.MIRROR));
//
//                }
////                    paint.setAlpha(1);
//            }


                canvas.drawPath(path, mGPaint);
            }
//            canvas.restore();
        }
    }

    private void updateData() {
        if (isVisualizationEnabled && mRawAudioBytes != null) {
            if (mRawAudioBytes.length == 0) return;
            for (int i = 0; i < mSrcY.length; i++) {
                int x = (int) Math.ceil((i + 1) * (mRawAudioBytes.length / mPoints));
                int t = 0;
                if (x < 1024) {
                    t = ((byte) (Math.abs(mRawAudioBytes[x]) + 128)) * mRadius / 128;
                }
                mSrcY[i] = -t;
            }
        }
    }
}
