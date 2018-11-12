package com.example.dell.prm391x_alarmclock_trungnqfx00077;

import android.app.Application;

import com.google.gson.Gson;

public class App extends Application {
    private static App mSelf;
    private Gson mGson;

    public static App self() {
        return mSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSelf = this;
        mGson = new Gson();
    }

    public Gson getGson() {
        return mGson;
    }
}
