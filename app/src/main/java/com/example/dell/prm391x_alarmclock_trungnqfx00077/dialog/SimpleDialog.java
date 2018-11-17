package com.example.dell.prm391x_alarmclock_trungnqfx00077.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.prm391x_alarmclock_trungnqfx00077.R;

public class SimpleDialog extends Dialog {

    private Context mContext;

    // View members
    public TextView dialog_title;
    public EditText dialog_message;
    public Button bt_cancel, bt_confirm;
    public ImageView icon;
    private View view;


    public SimpleDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;

        bindViews(context);
    }

    @SuppressLint("InflateParams")
    private void bindViews(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.dialog_simple, null);
        icon = view.findViewById(R.id.mSimpleDialogIcon);
        dialog_title = view.findViewById(R.id.mSimpleDialogTitle);
        setTitle("Prompt message");
        dialog_message = view.findViewById(R.id.mSimpleDialogMessage);
        dialog_message.clearFocus();
        bt_confirm = view.findViewById(R.id.mSimpleDialogConfirm);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        setTitle(mContext.getString(R.string.alarm_dialog_title));
    }


    public void setClickListener(View.OnClickListener listener) {
        bt_confirm.setOnClickListener(listener);
    }

    public SimpleDialog setMessage(String message) {
        dialog_message.setText(message);
        return this;
    }

    public SimpleDialog setTitle(String title) {
        dialog_title.setText(title);
        return this;
    }

    public SimpleDialog setIcon(int iconResId) {
        dialog_title.setVisibility(View.GONE);
        icon.setVisibility(View.VISIBLE);
        icon.setBackgroundResource(iconResId);
        return this;
    }

}
