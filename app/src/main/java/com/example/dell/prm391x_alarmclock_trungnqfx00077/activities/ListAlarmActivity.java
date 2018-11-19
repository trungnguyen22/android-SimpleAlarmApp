package com.example.dell.prm391x_alarmclock_trungnqfx00077.activities;

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
import android.widget.Toast;

import com.example.dell.prm391x_alarmclock_trungnqfx00077.R;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.adapters.AlarmAdapter;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.database.DatabaseHelper;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.enums.EDatabaseOperation;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.models.Alarm;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.utils.AlarmManagerUtil;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.utils.Constants;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.utils.SharedPrefs;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListAlarmActivity extends AppCompatActivity
        implements TimePickerDialog.OnTimeSetListener, AlarmAdapter.onItemClick {

    public static int REQUEST_CODE_ALARM_ADDED = 22;
    public static int REQUEST_CODE_EDIT_ALARM = 33;

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
    DatabaseHelper mDatabaseHelper;

    // Adapter
    AlarmAdapter mAlarmAdapter;

    // Logic
    boolean isShowDialog;
    int positionOfEditItem;
    boolean isEditMode;
    boolean is24hrFormat;
    boolean needUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_alarm_activity);

        initData();
        bindViews();
        setupAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListAlarmFromDB();
    }

    private void getListAlarmFromDB() {
        mAlarmList = mDatabaseHelper.getAllAlarms();
        mAlarmAdapter.setAlarmList(mAlarmList);
        resetAdapter();
        toggleEmptyMessageTV();
    }

    private void initData() {
        mDatabaseHelper = new DatabaseHelper(this);

        // SharedPreferences
        isShowDialog = SharedPrefs.getInstance().get(Constants.SHARED_PREFS_BOOLEAN, Boolean.class);
        is24hrFormat = SharedPrefs.getInstance().get(Constants.SHARED_PREFS_BOOLEAN_2ND, Boolean.class);
    }

    private void bindViews() {
        mListAlarmToolbar = findViewById(R.id.mListAlarmToolbar);
        mEmptyMessageTV = findViewById(R.id.mEmptyMessageTV);
        mRecyclerView = findViewById(R.id.mListAlarmRV);
        mAddAlarmFAB = findViewById(R.id.mAddAlarmFAB);

        setSupportActionBar(mListAlarmToolbar);

        mAddAlarmFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowDialog) {
                    showTimePickerDialog();
                } else {
                    AddAlarmActivity.openAddAlarmForResult(ListAlarmActivity.this);
                }
            }
        });
    }

    private void setupAdapter() {
        mAlarmAdapter = new AlarmAdapter(this, mAlarmList, this);
        mAlarmAdapter.setIs24HrFormat(is24hrFormat);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAlarmAdapter);
    }

    /* ================================ TIME PICKER DIALOG ================================*/

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        showTimePickerDialog(hourOfDay, minute);
    }

    private void showTimePickerDialog(int hourOfDay, int minute) {
        mTimePickerDialog = new TimePickerDialog(this, this, hourOfDay, minute, is24hrFormat);
        mTimePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (isEditMode) {
            isEditMode = false;
            updateAlarm(hourOfDay, minute, positionOfEditItem);
            resetAdapter();
        } else {
            createAlarm(hourOfDay, minute);
            getListAlarmFromDB();
        }
    }

    /* ================================ MENU ================================*/

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getItemId();
        if (item.getOrder() == MENU_MODE_EDIT) {
            positionOfEditItem = position;
            Alarm alarm = mAlarmList.get(position);
            if (isShowDialog) {
                isEditMode = true;
                showTimePickerDialog(alarm.getHourOfDay(), alarm.getMinute());
            } else {
                AddAlarmActivity.openEdit(this, alarm.getHourOfDay(), alarm.getMinute(), true);
            }
        } else {
            onDatabaseOperation(EDatabaseOperation.DELETE_ITEM_TO_DB, mAlarmList.get(position));
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
                mAlarmAdapter.setIs24HrFormat(is24hrFormat);
                mAlarmAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* ================================ BUSINESS METHODS ================================*/

    private void createAlarm(int hourOfDay, int minute) {
        Alarm alarm = new Alarm(0, hourOfDay, minute, false);
        onDatabaseOperation(EDatabaseOperation.INSERT_ITEM_TO_DB, alarm);
    }

    private void resetAdapter() {
        sortingAlarmListASC(); // Sorting the list alarm in ASC order
        mAlarmAdapter.notifyDataSetChanged();
    }

    private void updateAlarm(int hourOfDay, int minute, int pos) {
        Alarm alarm = mAlarmList.get(pos);
        alarm.setHourOfDay(hourOfDay);
        alarm.setMinute(minute);
        onDatabaseOperation(EDatabaseOperation.UPDATE_ITEM_TO_DB, alarm);
        resetAdapter();
        if (alarm.isEnable()) {
            AlarmManagerUtil.setAlarm(this, 0, 0, 2, alarm);
        }
    }

    private void sortingAlarmListASC() {
        Collections.sort(mAlarmList, new Comparator<Alarm>() {
            @Override
            public int compare(Alarm o1, Alarm o2) {
                try {
                    String pattern = "hh:mm a";
                    Date date1 = new SimpleDateFormat(pattern, Locale.getDefault()).parse(Utils.getTimeIn12HrFormat(o1.getHourOfDay(), o1.getMinute()));
                    Date date2 = new SimpleDateFormat(pattern, Locale.getDefault()).parse(Utils.getTimeIn12HrFormat(o2.getHourOfDay(), o2.getMinute()));
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
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

    /* ================================ DATABASE OPERATION ================================*/

    private boolean onDatabaseOperation(EDatabaseOperation eDatabaseOperation, Alarm alarm) {
        long result;
        switch (eDatabaseOperation) {
            case INSERT_ITEM_TO_DB:
                result = mDatabaseHelper.insertAlarm(alarm);
                alarm.setID(result);
                if (result != -1) {
                    showMessageDB(getString(R.string.inserted_db_success_message));
                    return true;
                }
                return false;
            case UPDATE_ITEM_TO_DB:
                result = mDatabaseHelper.updateAlarm(alarm);
                if (result != -1) {
                    showMessageDB(getString(R.string.updated_db_success_message));
                    return true;
                }
                return false;
            case DELETE_ITEM_TO_DB:
                result = mDatabaseHelper.deleteAlarm(alarm);
                if (result != -1) {
                    showMessageDB(getString(R.string.deleted_db_success_message));
                    return true;
                }
                return false;
        }
        return false;
    }

    private void showMessageDB(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    /* ================================ END OF BUSINESS METHODS ================================*/

    @Override
    protected void onPause() {
        super.onPause();
        SharedPrefs.getInstance().putArrayList(Constants.SHARED_PREFS_ARRAY_LIST_KEY, mAlarmList);
        SharedPrefs.getInstance().put(Constants.SHARED_PREFS_BOOLEAN, isShowDialog);
        SharedPrefs.getInstance().put(Constants.SHARED_PREFS_BOOLEAN_2ND, is24hrFormat);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Add a new alarm
        if (requestCode == REQUEST_CODE_ALARM_ADDED) {
            if (resultCode == Activity.RESULT_OK) {
                needUpdate = false;
                int hourOfDay = data != null ? data.getIntExtra(RETURN_HOUR_PICKED_EXTRA, 0) : 0;
                int minute = data != null ? data.getIntExtra(RETURN_MINUTE_PICKED_EXTRA, 0) : 0;
                createAlarm(hourOfDay, minute);
            }
        }
        // Edit an alarm
        if (requestCode == REQUEST_CODE_EDIT_ALARM) {
            if (resultCode == Activity.RESULT_OK) {
                isEditMode = false;
                int hourOfDay = data != null ? data.getIntExtra(RETURN_HOUR_PICKED_EXTRA, 0) : 0;
                int minute = data != null ? data.getIntExtra(RETURN_MINUTE_PICKED_EXTRA, 0) : 0;
                updateAlarm(hourOfDay, minute, positionOfEditItem);
                resetAdapter();
            }
        }
    }

    /* ================================ ADAPTER LISTENER ================================*/
    @Override
    public void onSwitchClick(Alarm alarm) {
        if (alarm.isEnable()) {
            AlarmManagerUtil.setAlarm(this, 0, 0, 2, alarm);
        } else {
            AlarmManagerUtil.cancelAlarm(this, alarm.getID());
        }
        mDatabaseHelper.updateAlarm(alarm);
    }
}
