package com.example.dell.alarm.activities;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.dell.alarm.R;
import com.example.dell.alarm.database.DatabaseHelper;
import com.example.dell.alarm.dialog.SimpleDialog;
import com.example.dell.alarm.models.Alarm;
import com.example.dell.alarm.utils.Constants;

public class ClockAlarmActivity extends AppCompatActivity {

    String message;
    int soundOrVibrator;
    long id;

    private DatabaseHelper mDatabaseHelper;
    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;

    private Alarm mAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm);
        initData();
        setRingtoneAndVibration(soundOrVibrator);
        showReminderDialog(message, soundOrVibrator);
    }

    private void initData() {
        // Getting data from AlarmReceiver
        mDatabaseHelper = new DatabaseHelper(this);

        message = getIntent().getStringExtra(Constants.MSG);
        soundOrVibrator = getIntent().getIntExtra(Constants.SOUND_OR_VIBRATOR, 0);
        id = getIntent().getLongExtra(Constants.ID, 0);

        mAlarm = mDatabaseHelper.getAlarm(id);
        mAlarm.setEnable(false);
        mDatabaseHelper.updateAlarm(mAlarm);
    }

    /**
     * @param message         a message to show in dialog
     * @param soundOrVibrator 0 - only vibration
     *                        1 - only ringtone
     *                        2 - both
     */
    private void showReminderDialog(String message, final int soundOrVibrator) {
        final SimpleDialog simpleDialog = new SimpleDialog(this, R.style.Theme_Dialog);
        simpleDialog.setTitle(getString(R.string.alarm_reminder_title));
        simpleDialog.setMessage(message);
        simpleDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (simpleDialog.bt_confirm == v || simpleDialog.bt_cancel == v) {
                    if (soundOrVibrator == 1 || soundOrVibrator == 2) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                    }
                    if (soundOrVibrator == 0 || soundOrVibrator == 2) {
                        mVibrator.cancel();
                    }
                    simpleDialog.dismiss();
                    finish();
                }
            }
        });
        simpleDialog.show();
    }

    private void setRingtoneAndVibration(int soundOrVibrator) {
        if (soundOrVibrator == 1 || soundOrVibrator == 2) {
            Uri currentRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                    this.getApplicationContext(),
                    RingtoneManager.TYPE_RINGTONE
            );
            mMediaPlayer = MediaPlayer.create(this, currentRingtoneUri);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
        if (soundOrVibrator == 0 || soundOrVibrator == 2) {
            mVibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
            mVibrator.vibrate(new long[]{100, 10, 100, 600}, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
