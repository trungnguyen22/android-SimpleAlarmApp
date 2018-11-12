package adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import models.Alarm;
import utils.AlarmManagerUtil;

import com.example.dell.prm391x_alarmclock_trungnqfx00077.R;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private List<Alarm> mAlarmList;
    private Context mContext;

    public AlarmAdapter(Context context, List<Alarm> alarmList) {
        mAlarmList = alarmList;
        mContext = context;
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
            itemView.setOnCreateContextMenuListener(this);
        }

        private void bindViews() {
            mAlarmTimeTV = itemView.findViewById(R.id.mAlarmTimeTV);
            mOnOffAlarmSwitch = itemView.findViewById(R.id.mOnOffAlarmSwitch);
            mOnOffAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Alarm alarm = mAlarmList.get(getAdapterPosition());
                    alarm.setOn(isChecked);
                    if (isChecked) {
                        AlarmManagerUtil.setAlarm(mContext, 0, alarm.getHourOfDay(), alarm.getMinute(),
                                alarm.getID(), 0, "Alert Rings", 2);
                    } else {
                        AlarmManagerUtil.cancelAlarm(mContext, alarm.getID());
                    }
                }
            });
        }

        private void bindData(int position) {
            Alarm alarm = mAlarmList.get(position);
            mAlarmTimeTV.setText(alarm.toString());
            if (alarm.isOn()) {
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
