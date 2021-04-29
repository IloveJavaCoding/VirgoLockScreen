package com.nepalese.virgolockscreen.data;

import com.nepalese.virgosdk.Beans.BaseBean;

/**
 * @author nepalese on 2021/4/29 17:38
 * @usage
 */
public class clockBean extends BaseBean {
    private String colorSelect;
    private String colorDefault;
    // tvSizeCenter, tvSizeClock, tvOffset, tvRH, tvRM, tvRS;
    private int sizeCenter;
    private int sizeClock;
    private int offset;
    private int rHour;
    private int rMinute;
    private int rSecond;

    public clockBean() {
    }

    public String getColorSelect() {
        return colorSelect;
    }

    public void setColorSelect(String colorSelect) {
        this.colorSelect = colorSelect;
    }

    public String getColorDefault() {
        return colorDefault;
    }

    public void setColorDefault(String colorDefault) {
        this.colorDefault = colorDefault;
    }

    public int getSizeCenter() {
        return sizeCenter;
    }

    public void setSizeCenter(int sizeCenter) {
        this.sizeCenter = sizeCenter;
    }

    public int getSizeClock() {
        return sizeClock;
    }

    public void setSizeClock(int sizeClock) {
        this.sizeClock = sizeClock;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getrHour() {
        return rHour;
    }

    public void setrHour(int rHour) {
        this.rHour = rHour;
    }

    public int getrMinute() {
        return rMinute;
    }

    public void setrMinute(int rMinute) {
        this.rMinute = rMinute;
    }

    public int getrSecond() {
        return rSecond;
    }

    public void setrSecond(int rSecond) {
        this.rSecond = rSecond;
    }
}
