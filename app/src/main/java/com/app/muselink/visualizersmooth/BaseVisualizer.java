/*
 * Copyright (C) 2017 Gautam Chibde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.muselink.visualizersmooth;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.muselink.R;

/**
 * Base class that contains common implementation for all
 * visualizers.
 * Created by gautam chibde on 28/10/17.
 */

abstract public class BaseVisualizer extends View {
    protected byte[] bytes;
    protected Paint paint;
    protected Visualizer visualizer;

    int radiusDynamic = 85;

    protected int color = Color.RED;

    public BaseVisualizer(Context context) {
        super(context);
        init(null);
        init();
    }

    public BaseVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        init();
    }

    public BaseVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        init();
    }

    private void init(AttributeSet attributeSet) {
        paint = new Paint();
    }

    /**
     * Set color to visualizer with color resource id.
     *
     * @param color color resource id.
     */
    public void setColor(int color) {
        this.color = color;
        this.paint.setColor(this.color);
    }

    /**
     * @param mediaPlayer MediaPlayer
     * @deprecated will be removed in next version use {@link BaseVisualizer#setPlayer(int)} instead
     */
    @Deprecated
    public void setPlayer(MediaPlayer mediaPlayer) {
        setPlayer(mediaPlayer.getAudioSessionId());
    }

    public void setPlayer(int audioSessionId) {
//        radiusDynamic = this.getResources().getDimensionPixelOffset(R.dimen._30sdp);
//        visualizer = new Visualizer(audioSessionId);
//        visualizer.setEnabled(false);
//        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
//        bytes = new byte[visualizer.getCaptureSize()];
//
//        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
//            @Override
//            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,int samplingRate) {
////                BaseVisualizer.this.bytes = bytes;
////                BaseVisualizer.this.bytes = getFft();
////                invalidate();
//            }
//            @Override
//            public void onFftDataCapture(Visualizer visualizer, byte[] bytes,int samplingRate) {
//                BaseVisualizer.this.bytes = bytes;
//                invalidate();
//            }
//        }, Visualizer.getMaxCaptureRate() / 2, false, true);
//        visualizer.setEnabled(true);


        radiusDynamic = this.getResources().getDimensionPixelOffset(R.dimen._31sdp);
        visualizer = new Visualizer(audioSessionId);
        visualizer.setEnabled(false);

        visualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        bytes = new byte[visualizer.getCaptureSize()];

        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,int samplingRate) {
//                BaseVisualizer.this.bytes = bytes;
//                BaseVisualizer2.this.bytes = getFft();
//                invalidate();
            }
            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes,int samplingRate) {
                BaseVisualizer.this.bytes = bytes;
                invalidate();
            }
        }, Visualizer.getMaxCaptureRate(), false, true);
        visualizer.setEnabled(true);

    }

    public void release() {
        //will be null if setPlayer hasn't yet been called
        if (visualizer == null)
            return;
        visualizer.release();
        bytes = null;
        invalidate();
    }

    public Visualizer getVisualizer() {
        return visualizer;
    }

    protected abstract void init();
}
