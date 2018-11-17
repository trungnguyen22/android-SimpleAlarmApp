package com.example.dell.prm391x_alarmclock_trungnqfx00077.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.dell.prm391x_alarmclock_trungnqfx00077.R;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.models.Alarm;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.utils.AlarmManagerUtil;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.utils.Utils;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private static final String TAG = AlarmAdapter.class.getSimpleName();

    private onItemClick mListener;

    public interface onItemClick {
        void onSwitchClick(Alarm alarm);
    }

    private List<Alarm> mAlarmList;
    private Context mContext;

    // Logic
    private boolean is24HrFormat;

    public AlarmAdapter(Context context, List<Alarm> alarmList, onItemClick listener) {
        mAlarmList = alarmList;
        mContext = context;
        mListener = listener;
    }

    public void setIs24HrFormat(boolean is24HrFormat) {
        this.is24HrFormat = is24HrFormat;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_alarm, viewGroup, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder alarmViewHolder, int i) {
        alarmViewHolder.bindData(i);
    }

    @Override
    public int getItemCount() {
        return mAlarmList.size();
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView mAlarmTimeTV;
        Switch mOnOffAlarmSwitch;

        AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            bindViews();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alarm alarm = mAlarmList.get(getAdapterPosition());
                    String alarmInfo = "Alarm_ID: " + alarm.getID()
                            + "--Alarm_Status: " + alarm.getAlarmStatus()
                            + "--Alarm_isEnable:" + alarm.isEnable();
                    Log.d(TAG, alarmInfo);
                }
            });
            itemView.setOnCreateContextMenuListener(this);
        }

        private void bindViews() {
            mAlarmTimeTV = itemView.findViewById(R.id.mAlarmTimeTV);
            mOnOffAlarmSwitch = itemView.findViewById(R.id.mOnOffAlarmSwitch);
            mOnOffAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Alarm alarm = mAlarmList.get(getAdapterPosition());
                    alarm.setEnable(isChecked);
                    mListener.onSwitchClick(alarm);
                }
            });
        }

        private void bindData(int position) {
            Alarm alarm = mAlarmList.get(position);
            if (is24HrFormat) {
                mAlarmTimeTV.setText(alarm.toString());
            } else {
                mAlarmTimeTV.setText(Utils.getTimeIn12HrFormat(alarm.getHourOfDay(), alarm.getMinute()));
            }
            if (alarm.isEnable()) {
                mOnOffAlarmSwitch.setChecked(true);
            } else {
                mOnOffAlarmSwitch.setChecked(false);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, getAdapterPosition(), 0, mContext.getString(R.string.edit));
            menu.add(Menu.NONE, getAdapterPosition(), 1, mContext.getString(R.string.delete));
        }
    }
}
