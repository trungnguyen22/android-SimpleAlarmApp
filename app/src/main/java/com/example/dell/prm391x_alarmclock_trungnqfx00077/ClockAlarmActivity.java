package com.example.dell.prm391x_alarmclock_trungnqfx00077;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import utils.Constants;

public class ClockAlarmActivity extends AppCompatActivity {

    String message;
    int soundOrVibrator;

    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm);
        initData();
    }

    private void initData() {
        message = getIntent().getStringExtra(Constants.MSG);
        soundOrVibrator = getIntent().getIntExtra(Constants.SOUND_OR_VIBRATOR, 0);
        showDialogInBroadcastReceiver(message, soundOrVibrator);
    }

    /**
     * @param message  a message to show in dialog
     * @param soundOrVibrator to check if only sound, or sound and vibration or only vibration
     * */
    private void showDialogInBroadcastReceiver(String message, final int soundOrVibrator) {
        /* soundOrVibrator: 2 means that both sound and vibration are executed, 1 means only ringtone reminder, 0 means only vibration reminder */
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

        final SimpleDialog simpleDialog = new SimpleDialog(this, R.style.Theme_Dialog);
        simpleDialog.show();
        simpleDialog.setTitle(getString(R.string.alarm_remider_title));
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
    }
}
