package com.app.muselink.viualizer;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class PathView extends RelativeLayout
{
    Path path;
    Paint paint;
    float length;

    public PathView(Context context)
    {
        super(context);
    }

    public PathView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void init(Paint paint,float cx,float cy,float x,float y)
    {
//        paint = new Paint();
//        paint.setColor(Color.BLUE);
//        paint.setStrokeWidth(10);
//        paint.setStyle(Paint.Style.STROKE);
        this.paint = paint;
        path = new Path();
        path.moveTo(cx, cy);
        path.lineTo(x, y);
        invalidate();

        PathMeasure measure = new PathMeasure(path, false);
        length = measure.getLength();

//        float[] intervals = new float[]{length, length};
//
//        ObjectAnimator animator = ObjectAnimator.ofFloat(PathView.this, "phase", 1.0f, 0.0f);
//        animator.setDuration(3000);
//        animator.start();
    }

    //is called by animtor object
    public void setPhase(float phase)
    {
        Log.d("pathview","setPhase called with:" + String.valueOf(phase));
        paint.setPathEffect(createPathEffect(length-100, phase, 0.5f));
        invalidate();//will calll onDraw
    }

    private static PathEffect createPathEffect(float pathLength, float phase, float offset)
    {
        return new DashPathEffect(new float[] { pathLength, pathLength },
                Math.max(phase * pathLength, offset));
    }

    @Override
    public void onDraw(Canvas c)
    {
        super.onDraw(c);
        c.drawPath(path, paint);
    }
}