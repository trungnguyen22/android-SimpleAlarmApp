package com.example.dell.alarm.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String getTimeIn12HrFormat(int hourOfDay, int minute) {
        final String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.getDefault());
            final Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("K:mm a", Locale.getDefault()).format(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }
}
