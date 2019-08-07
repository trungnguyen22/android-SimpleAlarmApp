package services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.dell.alarm.activities.ClockAlarmActivity;

import com.example.dell.alarm.utils.AlarmManagerUtil;
import com.example.dell.alarm.utils.Constants;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        String msg = "";
        long intervalMillis = 0;
        int soundOrVibrator = 0;
        long id = 0;
        if (bundle != null) {
            msg = bundle.getString(Constants.MSG);
            intervalMillis = bundle.getLong(Constants.INTERVAL_MILLIS, 0);
            soundOrVibrator = bundle.getInt(Constants.SOUND_OR_VIBRATOR);
            id = bundle.getLong(Constants.ID);
        }

        if (intervalMillis != 0) {
            AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis, intent);
        }

        Intent clockIntent = new Intent(context, ClockAlarmActivity.class);
        clockIntent.putExtra(Constants.MSG, msg);
        clockIntent.putExtra(Constants.SOUND_OR_VIBRATOR, soundOrVibrator);
        clockIntent.putExtra(Constants.ID, id);
        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(clockIntent);
    }
}
