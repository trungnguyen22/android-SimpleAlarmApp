package utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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

    public static void cancelAlarm(Context context, int id) {
        Intent intent = new Intent(ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }

    /**
     * @param flag            flag = 0 for one-time alarm,
     *                        flag = 1 for daily reminders (1 day interval)
     *                        flag = 2 for weekly reminders (a periodic interval of one week)
     * @param hour            hour
     * @param minute          minute
     * @param id              Alarm's id
     * @param week            Week=0 means a one-time alarm or a periodic alarm by day.
     *                        In the case of non-zero, it is an alarm that represents the week of the week.
     * @param tips            Alarm's message
     * @param soundOrVibrator 2 means that both sound and vibration are executed, 1 means only ringtone reminder, 0 means only vibration reminder
     */
    public static void setAlarm(Context context, int flag, int hour, int minute, int id,
                                int week, String tips, int soundOrVibrator) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), hour, minute, 10);

        long intervalMillis = 0;
        if (flag == 0) {
            intervalMillis = 0;
        } else if (flag == 1) {
            intervalMillis = 24 * 3600 * 1000;
        } else if (flag == 2) {
            intervalMillis = 24 * 3600 * 1000 * 7;
        }

        tips += " at " + hour + ":" + minute;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Constants.INTERVAL_MILLIS, intervalMillis);
        intent.putExtra(Constants.MSG, tips);
        intent.putExtra(Constants.ID, id);
        intent.putExtra(Constants.SOUND_OR_VIBRATOR, soundOrVibrator);

        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // setWindow(int type of alarm, long windowStartMillis, long windowLengthMillis, PendingIntent operation)
        // windowStartMillis - The earliest time, in milliseconds, that the alarm should be delivered, expressed in the appropriate clock's units (depending on the alarm type).
        // windowLengthMillis - The length of the requested delivery window, in milliseconds.
        //                      The alarm will be delivered no later than this many milliseconds after windowStartMillis.
        //                      Note that this parameter is a duration, not the timestamp of the end of the window.
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, calMethod(week, calendar.getTimeInMillis()),
                intervalMillis, sender);

        /*Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);*/
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
