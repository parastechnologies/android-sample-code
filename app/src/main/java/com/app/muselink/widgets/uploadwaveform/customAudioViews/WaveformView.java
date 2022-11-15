/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.muselink.widgets.uploadwaveform.customAudioViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.LinearLayout;

import com.app.muselink.R;

import java.util.ArrayList;


/**
 * WaveformView is an Android view that displays a visual representation
 * of an audio waveform.  It retrieves the frame gains from a CheapSoundFile
 * object and recomputes the shape contour at several zoom levels.
 * <p>
 * This class doesn't handle selection or any of the touch interactions
 * directly, so it exposes a listener interface.  The class that embeds
 * this view should add itself as a listener and make the view scroll
 * and respond to other events appropriately.
 * <p>
 * WaveformView doesn't actually handle selection, but it will just display
 * the selected part of the waveform in a different color.
 */
public class WaveformView extends LinearLayout {

    private boolean isDrawBorder = true;

    public boolean isDrawBorder() {
        return isDrawBorder;
    }

    public void setIsDrawBorder(boolean isDrawBorder) {
        this.isDrawBorder = isDrawBorder;
    }

    public interface WaveformListener {
        public void waveformTouchStart(float x);

        public void waveformTouchMove(float x);

        public void waveformTouchEnd();

        public void waveformFling(float x);

        public void waveformDraw();

        public void waveformZoomIn();

        public void waveformZoomOut();
    }

    ;

    // Colors
    private Paint mGridPaint;
    private Paint mSelectedLinePaint;
    private Paint mLineBottomColor;
    private Paint mSelectedLinePaintTop;

    private Paint mUnselectedLinePaint;
    private Paint mUnselectedLinePaintTransparent;
    private Paint mUnselectedBkgndLinePaint;
    private Paint mBorderLinePaint;
    private Paint mBorderLinePaintStart;
    private Paint mPlaybackLinePaint;
    private Paint mTimecodePaint;

    private SoundFile mSoundFile;
    private int[] mLenByZoomLevel;
    private double[][] mValuesByZoomLevel;
    private double[] mZoomFactorByZoomLevel;
    private int[] mHeightsAtThisZoomLevel;
    private int mZoomLevel;
    private int mNumZoomLevels;
    private int mSampleRate;
    private int mSamplesPerFrame;
    private int mOffset;
    private int mSelectionStart;
    private int mSelectionEnd;
    private int mPlaybackPos;
    private float mInitialScaleSpan;
    private WaveformListener mListener;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mInitialized;

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // We don't want keys, the markers get these
        setFocusable(false);

        Resources res = getResources();
        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(false);
        mGridPaint.setColor(res.getColor(R.color.colorGridLine));
        mSelectedLinePaint = new Paint();
        mSelectedLinePaint.setAntiAlias(false);
        mSelectedLinePaint.setColor(res.getColor(R.color.colorSelectionSong));


        mLineBottomColor = new Paint();
        mLineBottomColor.setAntiAlias(false);
        mLineBottomColor.setColor(res.getColor(R.color.colorBottomUpload));

        mSelectedLinePaintTop = new Paint();
        mSelectedLinePaintTop.setAntiAlias(false);
        mSelectedLinePaintTop.setColor(res.getColor(R.color.colorSelectionTopSky));

        mUnselectedLinePaint = new Paint();

        mUnselectedLinePaintTransparent = new Paint();
        mUnselectedLinePaintTransparent.setAntiAlias(false);
        mUnselectedLinePaintTransparent.setAntiAlias(false);
        mUnselectedLinePaintTransparent.setColor(res.getColor(R.color.color_black_100));

        mUnselectedLinePaint.setAntiAlias(false);
        mUnselectedLinePaint.setColor(res.getColor(R.color.white));
        mUnselectedBkgndLinePaint = new Paint();
        mUnselectedBkgndLinePaint.setAntiAlias(false);
        mUnselectedBkgndLinePaint.setColor(res.getColor(R.color.transparent));

        mBorderLinePaint = new Paint();
        mBorderLinePaint.setAntiAlias(true);
        mBorderLinePaint.setStrokeWidth(strokeWidth);
        mBorderLinePaint.setPathEffect(new DashPathEffect(new float[]{3.0f, 2.0f}, 0.0f));
        mBorderLinePaint.setColor(res.getColor(R.color.colorSelectionBorder));

        mBorderLinePaintStart = new Paint();
        mBorderLinePaintStart.setAntiAlias(true);
        mBorderLinePaintStart.setStrokeWidth(strokeWidth);
        mBorderLinePaintStart.setPathEffect(new DashPathEffect(new float[]{3.0f, 2.0f}, 0.0f));
        mBorderLinePaintStart.setColor(res.getColor(R.color.colorSelectionSong));

        mPlaybackLinePaint = new Paint();
        mPlaybackLinePaint.setAntiAlias(false);
        mPlaybackLinePaint.setStrokeWidth(3f);
        mPlaybackLinePaint.setColor(res.getColor(R.color.colorGreen));
        mTimecodePaint = new Paint();
        mTimecodePaint.setTextSize(12);
        mTimecodePaint.setAntiAlias(true);
        mTimecodePaint.setColor(res.getColor(R.color.colorGreen));
        mTimecodePaint.setShadowLayer(2, 1, 1, res.getColor(R.color.colorGreen));

        mGestureDetector = new GestureDetector(
                context,
                new GestureDetector.SimpleOnGestureListener() {
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                        mListener.waveformFling(vx);
                        return true;
                    }
                }
        );

        mScaleGestureDetector = new ScaleGestureDetector(
                context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    public boolean onScaleBegin(ScaleGestureDetector d) {
                        Log.v("Ringdroid", "ScaleBegin " + d.getCurrentSpanX());
                        mInitialScaleSpan = Math.abs(d.getCurrentSpanX());
                        return true;
                    }

                    public boolean onScale(ScaleGestureDetector d) {
                        float scale = Math.abs(d.getCurrentSpanX());
                        Log.v("Ringdroid", "Scale " + (scale - mInitialScaleSpan));
                        if (scale - mInitialScaleSpan > 40) {
                            mListener.waveformZoomIn();
                            mInitialScaleSpan = scale;
                        }
                        if (scale - mInitialScaleSpan < -40) {
                            mListener.waveformZoomOut();
                            mInitialScaleSpan = scale;
                        }
                        return true;
                    }

                    public void onScaleEnd(ScaleGestureDetector d) {
                        Log.v("Ringdroid", "ScaleEnd " + d.getCurrentSpanX());
                    }
                }
        );

        mSoundFile = null;
        mLenByZoomLevel = null;
        mValuesByZoomLevel = null;
        mHeightsAtThisZoomLevel = null;
        mOffset = 0;
        mPlaybackPos = -1;
        mSelectionStart = 0;
        mSelectionEnd = 0;
        mInitialized = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        mScaleGestureDetector.onTouchEvent(event);
//        if (mGestureDetector.onTouchEvent(event)) {
//            return true;
//        }

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mListener.waveformTouchStart(event.getX());
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mListener.waveformTouchMove(event.getX());
//                break;
//            case MotionEvent.ACTION_UP:
//                mListener.waveformTouchEnd();
//                break;
//        }
        return true;
    }

    public void setSoundFile(SoundFile soundFile) {
        mSoundFile = soundFile;
        mSampleRate = mSoundFile.getSampleRate();
        mSamplesPerFrame = mSoundFile.getSamplesPerFrame();
        computeDoublesForAllZoomLevels();
        mHeightsAtThisZoomLevel = null;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public boolean canZoomIn() {
        return (mZoomLevel > 0);
    }

    public void zoomIn() {
        if (canZoomIn()) {
            mZoomLevel--;
            mSelectionStart *= 2;
            mSelectionEnd *= 2;
            mHeightsAtThisZoomLevel = null;
            int offsetCenter = mOffset + getMeasuredWidth() / 2;
            offsetCenter *= 2;
            mOffset = offsetCenter - getMeasuredWidth() / 2;
            if (mOffset < 0)
                mOffset = 0;
            invalidate();
        }
    }

    public boolean canZoomOut() {
        return (mZoomLevel < mNumZoomLevels - 1);
    }

    public int maxPos() {
        return mLenByZoomLevel[mZoomLevel];
    }

    public int secondsToFrames(double seconds) {
        return (int) (1.0 * seconds * mSampleRate / mSamplesPerFrame + 0.5);
    }

    public int secondsToPixels(double seconds) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int) (z * seconds * mSampleRate / mSamplesPerFrame + 0.5);
    }

    public double pixelsToSeconds(int pixels) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (pixels * (double) mSamplesPerFrame / (mSampleRate * z));
    }

    public int millisecsToPixels(int msecs) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int) ((msecs * 1.0 * mSampleRate * z) /
                (1000.0 * mSamplesPerFrame) + 0.5);
    }

    public int pixelsToMillisecs(int pixels) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int) (pixels * (1000.0 * mSamplesPerFrame) /
                (mSampleRate * z) + 0.5);
    }

    int songSeconds = 0;
    int viewWidth = 0;

    public void setSongSeconds(int seconds) {
        songSeconds = seconds;
    }

    public void setViewWidth(int width) {
        viewWidth = width;
    }

    public void setParameters(int start, int end, int offset) {
        mSelectionStart = start;
        mSelectionEnd = end;
        mOffset = offset;
    }

    boolean IsScrollMove = false;

    public void setWavformScrollMove(boolean IsScrollMove) {
        this.IsScrollMove = IsScrollMove;
    }

    public int getStart() {
        return mSelectionStart;
    }

    public int getEnd() {
        return mSelectionEnd;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setPlayback(int pos) {
        mPlaybackPos = pos;
    }

    public void setListener(WaveformListener listener) {
        mListener = listener;
    }

    public void recomputeHeights(float density) {
        mHeightsAtThisZoomLevel = null;
        mTimecodePaint.setTextSize((int) (12 * density));
        invalidate();
    }

    int spacebetweenselection = 0;

    public void setSpaceBetweenSelection(int spacebetweenselection) {
        this.spacebetweenselection = spacebetweenselection;
    }

    protected void drawWaveformLine(Canvas canvas,
                                    int x, int y0, int y1,
                                    Paint paint) {
        canvas.drawLine(x, y0, x, y1, paint);
    }

    int minusHeightLine = 30;
    int strokeWidth = 6;
    boolean IsFirstTimeRandomNumber = false;
    ArrayList<Integer> list = new ArrayList<>();

    boolean IsFirstTime = true;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (mSoundFile == null)
//            return;

//        if (mHeightsAtThisZoomLevel == null)
//            computeIntsForThisZoomLevel();

        // Draw waveform
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int oneSecondPixel = 3;
        int totalWidth = oneSecondPixel * songSeconds;

        for(int i=0;i<totalWidth/3;i++) {
            View view = new View(this.getContext());
            view.setLayoutParams(new LayoutParams(3, measuredHeight));
            view.setBackgroundColor(R.color.color_watermelon);
            addView(view);
        }




//        Paint paint;
//        paint = mSelectedLinePaint;
//        paint.setStrokeWidth(viewWidth*2);
//        canvas.drawLine(
//                0, 0,
//                0, measuredHeight,
//                paint);
//
//        Paint paint1 = mUnselectedLinePaint;
//        paint1.setStrokeWidth(viewWidth);
//        canvas.drawLine(
//                viewWidth, 0,
//                0, measuredHeight,
//                paint1);

//        paint = mSelectedLinePaint;
//        paint.setStrokeWidth(viewWidth*2);
//        canvas.drawLine(
//                0, 0,
//                0, measuredHeight,
//                paint);

//        paint = mUnselectedLinePaint;
//        paint.setStrokeWidth(viewWidth*3);
//        canvas.drawLine(
//                0, 0,
//                0, measuredHeight,
//                paint);


//        mSoundFile.getSamples()


//        int start = mOffset;
//        int start = mOffset;
//        int width = mHeightsAtThisZoomLevel.length - start;
//        int ctr = measuredHeight / 2;
//
//        if (width > measuredWidth)
//            width = measuredWidth;
//
//        // Draw grid
//        double onePixelInSecs = pixelsToSeconds(1);
//        double fractionalSecs = mOffset * onePixelInSecs;
//        int i = 0;
//        RectF mWaveRect = new RectF();
//
//        boolean linetypeUnSelected = false;
//        boolean linetypeStarted = true;
//        int starti = 0;
//        int endi = 0;
//        int strokeWidth = 0;
//        ArrayList<ModalLines> modalLinesArrayList = new ArrayList<>();
//
//        int valueEnd = 0;
//        int value  = 0 ;
//        if(IsFirstTime) {
//            IsFirstTime = false;
//            value = getWidth()/2;
//            valueEnd = getWidth();
//            for (i = 0; i < getWidth() / 2; i++) {
//                Paint paint;
//                paint = mUnselectedBkgndLinePaint;
//                paint.setStrokeWidth(this.strokeWidth);
//                drawWaveformLine(
//                        canvas, i,
//                        ctr - mHeightsAtThisZoomLevel[start + i] - 20,
//                        ctr + 1 + mHeightsAtThisZoomLevel[start + i] + 20,
//                        paint);
//            }
//        }else {
//            value = getWidth()/2 - mOffset;
//            valueEnd = getWidth() + value;
//        }
//        // Draw waveform
//        for (i = value; i < valueEnd; i++) {
//
//            ModalLines modalLines = new ModalLines();
//            Resources res = getResources();
//
//            Paint paint;
////            if (i + start >= mSelectionStart &&
////                    i + start < mSelectionEnd) {
////
////                modalLines.setSelected(true);
////                modalLines.setColor(res.getColor(R.color.white));
////
////                paint = mSelectedLinePaint;
////                if (linetypeStarted) {
////                    starti = i;
////                    linetypeStarted = false;
////                } else {
////                    endi = i;
////                }
////                strokeWidth = strokeWidth + 1;
////
////                linetypeUnSelected = false;
////
////            } else {
////                modalLines.setSelected(false);
////                modalLines.setColor(res.getColor(R.color.colorSelectionBox));
////                paint = mUnselectedLinePaint;
////                linetypeUnSelected = true;
////            }
//
//            modalLines.setSelected(false);
//            modalLines.setColor(res.getColor(R.color.colorSelectionBox));
//            paint = mUnselectedLinePaint;
//            linetypeUnSelected = true;
//
//            paint.setStrokeWidth(this.strokeWidth);
//            if (i % 12 == 0) {
//
//                if(linetypeUnSelected) {
//                        drawWaveformLine(
//                                canvas, i,
//                                0,
//                                getHeight(),
//                                paint);
//
//                }else {
//                    drawWaveformLine(
//                            canvas, i,
//                            0,
//                            getHeight(),
//                            paint);
//                }
//
//                modalLines.setI(i);
//                modalLines.setY0(measuredHeight / 2);
////                modalLines.setY1(ctr + 1 + mHeightsAtThisZoomLevel[start + i]);
//
//                if (linetypeUnSelected) {
//
//                    mLineBottomColor.setStrokeWidth(this.strokeWidth);
//                    drawWaveformLine(
//                            canvas, i,
//                            measuredHeight / 2,
//                            getHeight(),
//                            mLineBottomColor);
//                }
//
//                modalLinesArrayList.add(modalLines);
//
//            }
//
////            if (i + start == mPlaybackPos) {
////                canvas.drawLine(i, 0, i, measuredHeight - minusHeightLine, mPlaybackLinePaint);
////            }
//
//        }
//
//        mWaveRect.set(starti, 0, endi, measuredHeight / 2);
//        canvas.drawRoundRect(mWaveRect, 0, 0, mSelectedLinePaintTop);
//
//
//        if (isDrawBorder()) {
//            canvas.drawLine(
//                    mSelectionStart - mOffset + 0.5f, 0,
//                    mSelectionStart - mOffset + 0.5f, measuredHeight - minusHeightLine,
//                    mBorderLinePaintStart);
//
//            Resources res = getResources();
//            Paint paint = new Paint();
//            paint.setAntiAlias(false);
//            paint.setStrokeWidth(this.strokeWidth);
//            paint.setColor(res.getColor(R.color.colorBackgroundBlack));
//            canvas.drawLine(0, measuredHeight / 2, measuredWidth, measuredHeight / 2, paint);
//
//            canvas.drawLine(
//                    mSelectionEnd - mOffset + 0.5f, 0,
//                    mSelectionEnd - mOffset + 0.5f, measuredHeight,
//                    mBorderLinePaint);
//        }
//
//
//        if (modalLinesArrayList.size() > 0) {
//
//            if(!IsFirstTimeRandomNumber) {
//                IsFirstTimeRandomNumber = true;
//                for (int k = 0; k < (modalLinesArrayList.size() / 2) - 5; k++) {
//                    int random = new Random().nextInt((modalLinesArrayList.size() - 0) + 1) + 0;
//                    list.add(random);
//                }
//            }
//
//            for (int j = 0; j < modalLinesArrayList.size(); j++) {
//                if (list.contains(j)) {
//                    ModalLines modalLines = modalLinesArrayList.get(j);
//                    Paint paint = new Paint();
//                    paint.setAntiAlias(false);
//                    paint.setStrokeWidth(this.strokeWidth);
//                    paint.setColor(modalLines.color);
//                    drawWaveformLine(canvas, modalLines.i, modalLines.y0 - 5, modalLines.y0 + 7, paint);
//                }
//            }
//
//        }
//
//
//        if (mListener != null) {
//            mListener.waveformDraw();
//        }
    }

    /**
     * Called once when a new sound file is added
     */
    private void computeDoublesForAllZoomLevels() {
        int numFrames = mSoundFile.getNumFrames();
        int[] frameGains = mSoundFile.getFrameGains();
        double[] smoothedGains = new double[numFrames];
        if (numFrames == 1) {
            smoothedGains[0] = frameGains[0];
        } else if (numFrames == 2) {
            smoothedGains[0] = frameGains[0];
            smoothedGains[1] = frameGains[1];
        } else if (numFrames > 2) {
            smoothedGains[0] = (double) (
                    (frameGains[0] / 2.0) +
                            (frameGains[1] / 2.0));
            for (int i = 1; i < numFrames - 1; i++) {
                smoothedGains[i] = (double) (
                        (frameGains[i - 1] / 3.0) +
                                (frameGains[i] / 3.0) +
                                (frameGains[i + 1] / 3.0));
            }
            smoothedGains[numFrames - 1] = (double) (
                    (frameGains[numFrames - 2] / 2.0) +
                            (frameGains[numFrames - 1] / 2.0));
        }

        // Make sure the range is no more than 0 - 255
        double maxGain = 1.0;
        for (int i = 0; i < numFrames; i++) {
            if (smoothedGains[i] > maxGain) {
                maxGain = smoothedGains[i];
            }
        }
        double scaleFactor = 1.0;
        if (maxGain > 255.0) {
            scaleFactor = 255 / maxGain;
        }

        // Build histogram of 256 bins and figure out the new scaled max
        maxGain = 0;
        int gainHist[] = new int[256];
        for (int i = 0; i < numFrames; i++) {
            int smoothedGain = (int) (smoothedGains[i] * scaleFactor);
            if (smoothedGain < 0)
                smoothedGain = 0;
            if (smoothedGain > 255)
                smoothedGain = 255;

            if (smoothedGain > maxGain)
                maxGain = smoothedGain;

            gainHist[smoothedGain]++;
        }

        // Re-calibrate the min to be 5%
        double minGain = 0;
        int sum = 0;
        while (minGain < 255 && sum < numFrames / 20) {
            sum += gainHist[(int) minGain];
            minGain++;
        }

        // Re-calibrate the max to be 99%
        sum = 0;
        while (maxGain > 2 && sum < numFrames / 100) {
            sum += gainHist[(int) maxGain];
            maxGain--;
        }

        // Compute the heights
        double[] heights = new double[numFrames];
        double range = maxGain - minGain;
        for (int i = 0; i < numFrames; i++) {
            double value = (smoothedGains[i] * scaleFactor - minGain) / range;
            if (value < 0.0)
                value = 0.0;
            if (value > 1.0)
                value = 1.0;
            heights[i] = value * value;
        }

        mNumZoomLevels = 5;
        mLenByZoomLevel = new int[5];
        mZoomFactorByZoomLevel = new double[5];
        mValuesByZoomLevel = new double[5][];

        // Level 0 is doubled, with interpolated values
        mLenByZoomLevel[0] = numFrames * 2;
        mZoomFactorByZoomLevel[0] = 2.0;
        mValuesByZoomLevel[0] = new double[mLenByZoomLevel[0]];

        if (numFrames > 0) {
            mValuesByZoomLevel[0][0] = 0.5 * heights[0];
            mValuesByZoomLevel[0][1] = heights[0];
        }

        for (int i = 1; i < numFrames; i++) {
            mValuesByZoomLevel[0][2 * i] = 0.5 * (heights[i - 1] + heights[i]);
            mValuesByZoomLevel[0][2 * i + 1] = heights[i];
        }

        // Level 1 is normal
        mLenByZoomLevel[1] = numFrames;
        mValuesByZoomLevel[1] = new double[mLenByZoomLevel[1]];
        mZoomFactorByZoomLevel[1] = 1.0;
        for (int i = 0; i < mLenByZoomLevel[1]; i++) {
            mValuesByZoomLevel[1][i] = heights[i];
        }

        // 3 more levels are each halved
        for (int j = 2; j < 5; j++) {
            mLenByZoomLevel[j] = mLenByZoomLevel[j - 1] / 2;
            mValuesByZoomLevel[j] = new double[mLenByZoomLevel[j]];
            mZoomFactorByZoomLevel[j] = mZoomFactorByZoomLevel[j - 1] / 2.0;
            for (int i = 0; i < mLenByZoomLevel[j]; i++) {
                mValuesByZoomLevel[j][i] =
                        0.5 * (mValuesByZoomLevel[j - 1][2 * i] +
                                mValuesByZoomLevel[j - 1][2 * i + 1]);
            }
        }

        if (numFrames > 5000) {
            mZoomLevel = 3;
        } else if (numFrames > 1000) {
            mZoomLevel = 3;
        } else if (numFrames > 300) {
            mZoomLevel = 1;
        } else {
            mZoomLevel = 0;
        }

        mInitialized = true;
    }

    /**
     * Called the first time we need to draw when the zoom level has changed
     * or the screen is resized
     */
    private void computeIntsForThisZoomLevel() {
        int halfHeight = (getMeasuredHeight() / 2) - 1;
        mHeightsAtThisZoomLevel = new int[mLenByZoomLevel[mZoomLevel]];
        for (int i = 0; i < mLenByZoomLevel[mZoomLevel]; i++) {
            mHeightsAtThisZoomLevel[i] =
                    (int) (mValuesByZoomLevel[mZoomLevel][i] * halfHeight);
        }
    }

}
