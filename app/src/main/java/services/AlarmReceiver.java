package services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.dell.prm391x_alarmclock_trungnqfx00077.ClockAlarmActivity;

import utils.AlarmManagerUtil;
import utils.Constants;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Received");

        String msg = intent.getStringExtra(Constants.MSG);
        long intervalMillis = intent.getLongExtra(Constants.INTERVAL_MILLIS, 0);
        int soundOrVibrator = intent.getIntExtra(Constants.SOUND_OR_VIBRATOR, 0);

        if (intervalMillis != 0) {
            AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis, intent);
        }

        Intent clockIntent = new Intent(context, ClockAlarmActivity.class);
        clockIntent.putExtra(Constants.MSG, msg);
        clockIntent.putExtra(Constants.SOUND_OR_VIBRATOR, soundOrVibrator);
        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(clockIntent);
    }
}
