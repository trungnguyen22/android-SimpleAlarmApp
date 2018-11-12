package models;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Alarm {
    private int mID;
    private int mHourOfDay;
    private int mMinute;
    private boolean isOn;
    private boolean is24hrFormat;

    public Alarm(int id, int hourOfDay, int minute, boolean is24hrFormat) {
        mID = id;
        mHourOfDay = hourOfDay;
        mMinute = minute;
        isOn = false;
        this.is24hrFormat = is24hrFormat;
    }

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
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

    public boolean isIs24hrFormat() {
        return is24hrFormat;
    }

    public void setIs24hrFormat(boolean is24hrFormat) {
        this.is24hrFormat = is24hrFormat;
    }

    public String convert24hrTo12hrFormat() {
        final String time = String.format(Locale.getDefault(), "%02d:%02d", mHourOfDay, mMinute);
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.getDefault());
            final Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("K:mm a", Locale.getDefault()).format(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }

    @NonNull
    @Override
    public String toString() {
        if (is24hrFormat) {
            return String.format(Locale.getDefault(), "%02d:%02d", mHourOfDay, mMinute);
        } else {
            return convert24hrTo12hrFormat();
        }
    }
}
