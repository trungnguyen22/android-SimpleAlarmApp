package com.example.dell.prm391x_alarmclock_trungnqfx00077;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import adapters.AlarmAdapter;
import models.Alarm;
import utils.Constants;
import utils.SharedPrefs;

public class ListAlarmActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    public static int REQUEST_CODE_ALARM_ADDED = 22;
    public static int REQUEST_CODE_EDIT_ALARM = 33;

    // SHARED PREFS KEY
    public static String SHARED_PREFS_ARRAY_LIST_KEY = "SHARED_PREFS_ARRAY_LIST_KEY";

    // Intent Extra Key
    public static String RETURN_HOUR_PICKED_EXTRA = "RETURN_HOUR_PICKED_EXTRA";
    public static String RETURN_MINUTE_PICKED_EXTRA = "RETURN_MINUTE_PICKED_EXTRA";
    public static String RETURN_POSITION_ITEM_EXTRA = "RETURN_POSITION_ITEM_EXTRA";
    public static String BOOLEAN_EXTRA = "BOOLEAN_EXTRA";

    public static int MENU_MODE_EDIT = 0;

    // Views
    Toolbar mListAlarmToolbar;
    FloatingActionButton mAddAlarmFAB;
    TimePickerDialog mTimePickerDialog;
    RecyclerView mRecyclerView;
    TextView mEmptyMessageTV;

    // Data
    List<Alarm> mAlarmList;

    // Adapter
    AlarmAdapter mAlarmAdapter;

    // Logic
    boolean isShowDialog;
    int positionOfEditItem;
    boolean isEditMode;
    boolean is24hrFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_alarm_activity);
        initData();
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleEmptyMessageTV();
    }

    private void initData() {
        isShowDialog = SharedPrefs.getInstance().get(Constants.SHARED_PREFS_BOOLEAN, Boolean.class);
        is24hrFormat = SharedPrefs.getInstance().get(Constants.SHARED_PREFS_BOOLEAN_2ND, Boolean.class);

        mAlarmList = SharedPrefs.getInstance().getArrayList(SHARED_PREFS_ARRAY_LIST_KEY);
        if (mAlarmList == null) mAlarmList = new ArrayList<>();

        mAlarmAdapter = new AlarmAdapter(this, mAlarmList);
    }

    private void bindViews() {
        mListAlarmToolbar = findViewById(R.id.mListAlarmToolbar);
        mEmptyMessageTV = findViewById(R.id.mEmptyMessageTV);
        mRecyclerView = findViewById(R.id.mListAlarmRV);
        mAddAlarmFAB = findViewById(R.id.mAddAlarmFAB);

        setSupportActionBar(mListAlarmToolbar);

        // Setup RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAlarmAdapter);

        mAddAlarmFAB.setOnClickListener(this);
    }

    /**
     * Checking if it's empty list or not, then show/hide the message
     */
    private void toggleEmptyMessageTV() {
        if (mAlarmList.size() == 0) {
            mEmptyMessageTV.setVisibility(View.VISIBLE);
        } else {
            mEmptyMessageTV.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mAddAlarmFAB:
                if (isShowDialog) {
                    showTimePickerDialog();
                } else {
                    AddAlarmActivity.openAddAlarmForResult(ListAlarmActivity.this);
                }
                break;
        }
    }


    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        mTimePickerDialog = new TimePickerDialog(this, this, hourOfDay, minute, false);
        mTimePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (isEditMode) {
            isEditMode = false;
            mAlarmList.get(positionOfEditItem).setHourOfDay(hourOfDay);
            mAlarmList.get(positionOfEditItem).setMinute(minute);
            resetAdapter();
        } else {
            mAlarmList.add(new Alarm(mAlarmList.size(), hourOfDay, minute, is24hrFormat));
            toggleEmptyMessageTV();
            resetAdapter();
        }
    }

    private void resetAdapter() {
        sortingAlarmListASC(); // Sorting the list alarm in ASC order
        mAlarmAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_ALARM_ADDED) {
            if (resultCode == Activity.RESULT_OK) {
                int hourOfDay = data != null ? data.getIntExtra(RETURN_HOUR_PICKED_EXTRA, 0) : 0;
                int minute = data != null ? data.getIntExtra(RETURN_MINUTE_PICKED_EXTRA, 0) : 0;
                mAlarmList.add(new Alarm(mAlarmList.size(), hourOfDay, minute, is24hrFormat));
                resetAdapter();
            }
        } else {
            if (resultCode == Activity.RESULT_OK) {
                isEditMode = false;
                int hourOfDay = data != null ? data.getIntExtra(RETURN_HOUR_PICKED_EXTRA, 0) : 0;
                int minute = data != null ? data.getIntExtra(RETURN_MINUTE_PICKED_EXTRA, 0) : 0;
                mAlarmList.get(positionOfEditItem).setHourOfDay(hourOfDay);
                mAlarmList.get(positionOfEditItem).setMinute(minute);
                resetAdapter();
            }
        }
    }

    private void sortingAlarmListASC() {
        Collections.sort(mAlarmList, new Comparator<Alarm>() {
            @Override
            public int compare(Alarm o1, Alarm o2) {
                try {
                    return new SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(o1.convert24hrTo12hrFormat()).compareTo(new SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(o2.convert24hrTo12hrFormat()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
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
            toggleEmptyMessageTV();
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_switch_show_time_picker).setChecked(isShowDialog);
        menu.findItem(R.id.action_switch_24_hour_format).setChecked(is24hrFormat);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch_show_time_picker:
                item.setChecked(!item.isChecked());
                isShowDialog = item.isChecked();
                return true;
            case R.id.action_switch_24_hour_format:
                item.setChecked(!item.isChecked());
                is24hrFormat = item.isChecked();
                setShowTimeFormat();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setShowTimeFormat() {
        for (Alarm alarm : mAlarmList) {
            alarm.setIs24hrFormat(is24hrFormat);
            mAlarmAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPrefs.getInstance().putArrayList(Constants.SHARED_PREFS_ARRAY_LIST_KEY, mAlarmList);
        SharedPrefs.getInstance().put(Constants.SHARED_PREFS_BOOLEAN, isShowDialog);
        SharedPrefs.getInstance().put(Constants.SHARED_PREFS_BOOLEAN_2ND, is24hrFormat);
    }
}
