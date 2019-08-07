package com.example.dell.alarm.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.dell.alarm.R;
import com.example.dell.alarm.models.Alarm;

import java.util.Calendar;

import services.AlarmReceiver;

public class AlarmManagerUtil {

    public static final String ALARM_ACTION = "com.example.dell.alarm.clock";

    public static void setAlarmTime(Context context, long timeInMillis, Intent intent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(context, intent.getIntExtra("id", 0),
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = (int) intent.getLongExtra("intervalMillis", 0);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, timeInMillis, interval, sender);
    }

    public static void cancelAlarm(Context context, long id) {
        Intent intent = new Intent(ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, (int) id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }

    public static void setAlarm(Context context, int flag, int week, int soundOrVibrator, Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Set up calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                alarm.getHourOfDay(), alarm.getMinute(), 2
        );

        long intervalMillis = 0;
        if (flag == 0) {
            intervalMillis = 0;
        } else if (flag == 1) {
            intervalMillis = 24 * 3600 * 1000;
        } else if (flag == 2) {
            intervalMillis = 24 * 3600 * 1000 * 7;
        }

        String tips = context.getString(R.string.alarm_reminder_message)
                + String.format(context.getString(R.string.time_format), alarm.getHourOfDay(), alarm.getMinute());

        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.ID, alarm.getID());
        bundle.putString(Constants.MSG, tips);
        bundle.putInt(Constants.SOUND_OR_VIBRATOR, soundOrVibrator);
        bundle.putLong(Constants.INTERVAL_MILLIS, intervalMillis);
        intent.putExtras(bundle);

        PendingIntent sender = PendingIntent.getBroadcast(
                context,
                (int) alarm.getID(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP,
                calMethod(week, calendar.getTimeInMillis()),
                intervalMillis,
                sender
        );
    }

    /**
     * @param weekFlag Incoming is the day of the week
     * @param dateTime The timestamp is passed in (set the date of the day + the hour, minute, and second from the selection box)
     * @return Returns the timestamp of the start alarm time
     */
    private static long calMethod(int weekFlag, long dateTime) {
        long time = 0;
        // weekFlag == 0 means that the time interval is periodic or one time, and weekfalg is not 0, which means several alarms per week and is divided by week.
        if (weekFlag != 0) {
            Calendar calendar = Calendar.getInstance();
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            if (1 == week) {
                week = 7;
            } else if (2 == week) {
                week = 1;
            } else if (3 == week) {
                week = 2;
            } else if (4 == week) {
                week = 3;
            } else if (5 == week) {
                week = 4;
            } else if (6 == week) {
                week = 5;
            } else if (7 == week) {
                week = 6;
            }

            if (weekFlag == week) {
                if (dateTime > System.currentTimeMillis()) {
                    time = dateTime;
                } else {
                    time = dateTime + 7 * 24 * 3600 * 1000;
                }
            } else if (weekFlag > week) {
                time = dateTime + (weekFlag - week) * 24 * 3600 * 1000;
            } else if (weekFlag < week) {
                time = dateTime + (weekFlag - week + 7) * 24 * 3600 * 1000;
            }
        } else {
            if (dateTime > System.currentTimeMillis()) {
                time = dateTime;
            } else {
                time = dateTime + 24 * 3600 * 1000;
            }
        }
        return time;
    }
}