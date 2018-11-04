package com.example.dell.prm391x_alarmclock_trungnqfx00077;

import java.util.Locale;

public class Alarm {
    private int mHourOfDay;
    private int mMinute;
    private boolean isOn;

    public Alarm(int hourOfDay, int minute) {
        mHourOfDay = hourOfDay;
        mMinute = minute;
        isOn = false;
    }

    public int getHourOfDay() {
        return mHourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        mHourOfDay = hourOfDay;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%02d:%02d", mHourOfDay, mMinute);
    }
}
