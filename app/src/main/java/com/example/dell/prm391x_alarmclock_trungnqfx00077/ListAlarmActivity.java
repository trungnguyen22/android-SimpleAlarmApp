package com.example.dell.prm391x_alarmclock_trungnqfx00077;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListAlarmActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    public static int REQUEST_CODE_ALARM_ADDED = 22;
    public static int REQUEST_CODE_EDIT_ALARM = 33;

    public static String RETURN_HOUR_PICKED_EXTRA = "RETURN_HOUR_PICKED_EXTRA";
    public static String RETURN_MINUTE_PICKED_EXTRA = "RETURN_MINUTE_PICKED_EXTRA";
    public static String RETURN_POSITION_ITEM_EXTRA = "RETURN_POSITION_ITEM_EXTRA";
    public static String BOOLEAN_EXTRA = "BOOLEAN_EXTRA";

    public static int MENU_MODE_EDIT = 0;
    public static int MENU_MODE_DELETE = 1;

    // Views
    Toolbar mListAlarmToolbar;
    FloatingActionButton mAddAlarmFAB;
    TimePickerDialog mTimePickerDialog;
    RecyclerView mRecyclerView;
    Switch mSwitchTimePickerDialog;

    // Data
    List<Alarm> mAlarmList;

    // Adapter
    AlarmAdapter mAlarmAdapter;

    // Logic
    boolean isShowDialog;
    int positionOfEditItem;
    boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_alarm_activity);
        initData();
        bindViews();
    }

    private void initData() {
        mAlarmList = new ArrayList<>();
        mAlarmAdapter = new AlarmAdapter(this, mAlarmList);
    }

    private void bindViews() {
        mListAlarmToolbar = findViewById(R.id.mListAlarmToolbar);
        setSupportActionBar(mListAlarmToolbar);

        mRecyclerView = findViewById(R.id.mListAlarmRV);
        mAddAlarmFAB = findViewById(R.id.mAddAlarmFAB);

        mSwitchTimePickerDialog = findViewById(R.id.mSwitchTimePickerDialog);
        mSwitchTimePickerDialog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isShowDialog = isChecked;
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAlarmAdapter);

        mAddAlarmFAB.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mAddAlarmFAB:
                if (isShowDialog) {
                    addAlarm();
                } else {
                    AddAlarmActivity.openAddAlarmForResult(ListAlarmActivity.this);
                }
                break;
        }
    }

    private void addAlarm() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        mTimePickerDialog = new TimePickerDialog(this, this, hourOfDay, minute, false);
        mTimePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (isEditMode) {
            mAlarmList.get(positionOfEditItem).setHourOfDay(hourOfDay);
            mAlarmList.get(positionOfEditItem).setMinute(minute);
            mAlarmAdapter.notifyItemChanged(positionOfEditItem);
        } else {
            mAlarmList.add(new Alarm(hourOfDay, minute));
            mAlarmAdapter.notifyItemInserted(mAlarmList.size() - 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_ALARM_ADDED) {
            if (resultCode == Activity.RESULT_OK) {
                int hourOfDay = data != null ? data.getIntExtra(RETURN_HOUR_PICKED_EXTRA, 0) : 0;
                int minute = data != null ? data.getIntExtra(RETURN_MINUTE_PICKED_EXTRA, 0) : 0;
                mAlarmList.add(new Alarm(hourOfDay, minute));
                mAlarmAdapter.notifyItemInserted(mAlarmList.size() - 1);
            }
        } else {
            if (resultCode == Activity.RESULT_OK) {
                int hourOfDay = data != null ? data.getIntExtra(RETURN_HOUR_PICKED_EXTRA, 0) : 0;
                int minute = data != null ? data.getIntExtra(RETURN_MINUTE_PICKED_EXTRA, 0) : 0;
                mAlarmList.get(positionOfEditItem).setHourOfDay(hourOfDay);
                mAlarmList.get(positionOfEditItem).setMinute(minute);
                mAlarmAdapter.notifyItemChanged(positionOfEditItem);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getItemId();

        if (item.getOrder() == MENU_MODE_EDIT) {
            positionOfEditItem = position;
            Alarm alarm = mAlarmList.get(position);
            if (isShowDialog) {
                isEditMode = true;
                mTimePickerDialog = new TimePickerDialog(this, this, alarm.getHourOfDay(), alarm.getMinute(), false);
                mTimePickerDialog.show();
            } else {
                AddAlarmActivity.openEdit(this, alarm.getHourOfDay(), alarm.getMinute(), true);
            }
        } else {
            mAlarmList.remove(position);
            mAlarmAdapter.notifyItemRemoved(position);
        }
        return true;
    }
}
