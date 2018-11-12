package com.example.dell.prm391x_alarmclock_trungnqfx00077;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import utils.AlarmManagerUtil;

public class AddAlarmActivity extends AppCompatActivity implements View.OnClickListener {

    TimePicker mTimePicker;

    Button mCancelBtn;
    Button mDoneBtn;

    int mHourOfDay;
    int mMinute;
    int mPosition;
    boolean isEditMode;

    Calendar mCalendar;

    public static void openAddAlarmForResult(Activity activity) {
        Intent intent = new Intent(activity, AddAlarmActivity.class);
        activity.startActivityForResult(intent, ListAlarmActivity.REQUEST_CODE_ALARM_ADDED);
    }

    public static void openEdit(Activity activity, int hourOfDay, int minute, boolean isEditMode) {
        Intent intent = new Intent(activity, AddAlarmActivity.class);
        intent.putExtra(ListAlarmActivity.RETURN_HOUR_PICKED_EXTRA, hourOfDay);
        intent.putExtra(ListAlarmActivity.RETURN_MINUTE_PICKED_EXTRA, minute);
        intent.putExtra(ListAlarmActivity.BOOLEAN_EXTRA, isEditMode);
        activity.startActivityForResult(intent, ListAlarmActivity.REQUEST_CODE_EDIT_ALARM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        initData();
        bindViews();
        findViewById(R.id.mTestBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManagerUtil.setAlarm(AddAlarmActivity.this, 0, mHourOfDay, mMinute,
                        0, 0, "Alert Rings", 2);
                String message = "Set Alarm Successfully: " + mHourOfDay + ":" + mMinute;
                Toast.makeText(AddAlarmActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        mHourOfDay = getIntent().getIntExtra(ListAlarmActivity.RETURN_HOUR_PICKED_EXTRA, 0);
        mMinute = getIntent().getIntExtra(ListAlarmActivity.RETURN_MINUTE_PICKED_EXTRA, 0);
        mPosition = getIntent().getIntExtra(ListAlarmActivity.RETURN_POSITION_ITEM_EXTRA, 0);
        isEditMode = getIntent().getBooleanExtra(ListAlarmActivity.BOOLEAN_EXTRA, false);
    }

    private void bindViews() {
        mCancelBtn = findViewById(R.id.mAddAlarmCancleBtn);
        mDoneBtn = findViewById(R.id.mAddAlarmDoneBtn);
        mCancelBtn.setOnClickListener(this);
        mDoneBtn.setOnClickListener(this);
        setUpTimePicker();
    }

    private void setUpTimePicker() {
        mTimePicker = findViewById(R.id.mAddAlarmTimePicker);

        if (isEditMode) {
            // Do nothing, but update later
        } else {
            mCalendar = Calendar.getInstance();
            mHourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
            mMinute = mCalendar.get(Calendar.MINUTE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(mHourOfDay);
            mTimePicker.setMinute(mMinute);
        } else {
            mTimePicker.setCurrentHour(mHourOfDay);
            mTimePicker.setCurrentMinute(mMinute);
        }

        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHourOfDay = hourOfDay;
                mMinute = minute;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mAddAlarmCancleBtn:
                onBackPressed();
                break;
            case R.id.mAddAlarmDoneBtn:
                Intent returnIntent = new Intent();
                returnIntent.putExtra(ListAlarmActivity.RETURN_HOUR_PICKED_EXTRA, mHourOfDay);
                returnIntent.putExtra(ListAlarmActivity.RETURN_MINUTE_PICKED_EXTRA, mMinute);
                returnIntent.putExtra(ListAlarmActivity.RETURN_MINUTE_PICKED_EXTRA, mMinute);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
        }
    }
}
