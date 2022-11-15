package com.app.muselink.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

public class VisualizerView  extends RelativeLayout {

    private int width;
    private int height;

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    private int padingTop = 0;
    private int min = 0;
    private int max = 100;
    private float last = min;

    float scale = 270f / 500f;


    private void init(Context context, AttributeSet attributeSet) {
        setWillNotDraw(false);
        initValueAnimator();
        initInternalCirclePainter();
    }


    public void updateProgress(int value,Context context) {
        this.context = context;
        this.progressValue = Float.parseFloat(value+"");
        invalidate();
    }

    private Paint progressPaint;

    private RectF mRcf;

    int strokeWidth = 40;

    private void initInternalCirclePainter() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
//        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setColor(Color.parseColor("#FFFFFF"));
        progressPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }


    private static final int COLOR_TRANSPARENT = Color.parseColor("#00000000");


    private static final int COLOR_SKY = Color.parseColor("#4BE7E4");
    private static final int COLOR_BLUE = Color.parseColor("#2A43FF");
    private static final int COLOR_PINK = Color.parseColor("#E900F8");
    private static final int COLOR_RED = Color.parseColor("#F03527");
    private static final int COLOR_MUSTARD = Color.parseColor("#F09328");
    private static final int COLOR_YELLOW = Color.parseColor("#F8E100");
    private static final int COLOR_GREEN = Color.parseColor("#71E714");


    float startDegree = -90f;
    float addDegree = 1f;
    Canvas canvas;
    Context context;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        progressValue = 100;
        this.canvas = canvas;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("", "");
        this.width = w;
        this.height = h;

    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int count = getChildCount();
        int maxWidth = getWidth() / 2;
        int maxHeight = getHeight() / 2;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            int mesaureWidth = child.getMeasuredWidth();
            int measureHeight = child.getMeasuredWidth();
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            child.setTranslationY(padingTop);
            RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) child.getLayoutParams();
            relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            child.setLayoutParams(relativeLayoutParams);
            if (mesaureWidth > maxWidth) {
                layoutParams.width = maxWidth;
            }
            if (measureHeight > maxHeight) {
                layoutParams.height = maxHeight;
            }
        }
    }


    float progressValue = 0;

    private void animateValue() {
        if (valueAnimator != null) {
            valueAnimator.setFloatValues(last, progressValue);
            valueAnimator.setDuration(duration);
            valueAnimator.start();
        }
    }

    private int duration = 12000;


    private ValueAnimator valueAnimator;
    private Interpolator interpolator = new AccelerateDecelerateInterpolator();

    private void initValueAnimator() {
        valueAnimator = new ValueAnimator();
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimatorListenerImp());
    }

    private class ValueAnimatorListenerImp implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {

            Float value = (Float) valueAnimator.getAnimatedValue();

            last = value;

        }
    }
}
