package com.example.dell.prm391x_alarmclock_trungnqfx00077.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dell.prm391x_alarmclock_trungnqfx00077.App;
import com.example.dell.prm391x_alarmclock_trungnqfx00077.models.Alarm;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SharedPrefs {
    private static final String PREFS_NAME = "share_prefs";
    private static SharedPrefs mInstance;
    private SharedPreferences mSharedPreferences;

    private SharedPrefs() {
        mSharedPreferences = App.self().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefs getInstance() {
        if (mInstance == null) {
            mInstance = new SharedPrefs();
        }
        return mInstance;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> anonymousClass) {
        if (anonymousClass == String.class) {
            return (T) mSharedPreferences.getString(key, "");
        } else if (anonymousClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPreferences.getBoolean(key, false));
        } else if (anonymousClass == Float.class) {
            return (T) Float.valueOf(mSharedPreferences.getFloat(key, 0));
        } else if (anonymousClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPreferences.getInt(key, 0));
        } else if (anonymousClass == Long.class) {
            return (T) Long.valueOf(mSharedPreferences.getLong(key, 0));
        } else {
            return App.self().getGson().fromJson(mSharedPreferences.getString(key, ""), anonymousClass);
        }
    }

    public <T> void put(String key, T data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (data instanceof String) {
            editor.putString(key, (String) data);
        } else if (data instanceof Boolean) {
            editor.putBoolean(key, (Boolean) data);
        } else if (data instanceof Float) {
            editor.putFloat(key, (Float) data);
        } else if (data instanceof Integer) {
            editor.putInt(key, (Integer) data);
        } else if (data instanceof Long) {
            editor.putLong(key, (Long) data);
        } else {
            editor.putString(key, App.self().getGson().toJson(data));
        }
        editor.apply();
    }

    public <T> void putArrayList(String key, List<T> data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, App.self().getGson().toJson(data));
        editor.apply();
    }

    public List<Alarm> getAlarmArrayList(String key) {
        String json = mSharedPreferences.getString(key, "");
        Type token = new TypeToken<List<Alarm>>() {
        }.getType();
        return App.self().getGson().fromJson(json, token);
    }

    /*public List<Alarm> getAlarmArrayList(String key) {
        String json = mSharedPreferences.getString(key, "");
        Type token = new TypeToken<List<Alarm>>() {}.getType();
        return App.self().getGson().fromJson(json, token);
    }*/

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

}
