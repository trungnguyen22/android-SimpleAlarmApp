package com.example.dell.prm391x_alarmclock_trungnqfx00077.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

public class Alarm implements Serializable {

    public static final String TABLE_NAME = "alarms";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_HOUR_OF_DAY = "hour_of_day";
    public static final String COLUMN_MINUTE = "minute";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    /* Create Table statement
    CREATE TABLE alarms (
         id integer primary key autoincrement,
         hour_of_day integer,
         minute integer,
         status integer default 0,
         timestamp datetime default current_timestamp
        )
    */

    // +----+-------------+--------+--------+------------+
    // | id | hour_of_day | minute | status | timestamp  |
    // +----+-------------+--------+--------+------------+
    // |  1 |          23 |     20 |      1 | 1542351764 |
    // |  2 |           9 |      0 |      1 | 1542351764 |
    // |  3 |           6 |     30 |      0 | 1542351764 |
    // +----+-------------+--------+--------+------------+

    // Create table query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME
                    + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_HOUR_OF_DAY + " INTEGER,"
                    + COLUMN_MINUTE + " INTEGER,"
                    + COLUMN_STATUS + " INTEGER DEFAULT 0,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    private long mID;
    private int mHourOfDay;
    private int mMinute;
    private boolean isEnable;

    public Alarm() {
    }

    public Alarm(long id, int hourOfDay, int minute, boolean isEnable) {
        this.mID = id;
        this.mHourOfDay = hourOfDay;
        this.mMinute = minute;
        this.isEnable = isEnable;
    }


    public long getID() {
        return mID;
    }

    public void setID(long ID) {
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

    public boolean isEnable() {
        return isEnable;
    }

    public int getAlarmStatus() {
        if (isEnable)
            return 1;
        else
            return 0;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%02d:%02d", mHourOfDay, mMinute);
    }
}
