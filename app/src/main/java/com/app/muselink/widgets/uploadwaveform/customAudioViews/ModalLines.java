package com.app.muselink.widgets.uploadwaveform.customAudioViews;

import java.io.Serializable;

public class ModalLines implements Serializable {

    boolean IsSelected = false;
    int color  = 0;
    int i = 0;
    int y0 = 0;
    int y1 = 0;

    public boolean isSelected() {
        return IsSelected;
    }

    public void setSelected(boolean selected) {
        IsSelected = selected;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getY0() {
        return y0;
    }

    public void setY0(int y0) {
        this.y0 = y0;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }
}
