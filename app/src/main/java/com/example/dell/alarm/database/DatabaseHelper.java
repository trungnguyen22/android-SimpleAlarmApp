package com.example.dell.alarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dell.alarm.models.Alarm;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "alarms_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create alarm table
        db.execSQL(Alarm.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Alarm.TABLE_NAME);

        // Create table again
        onCreate(db);
    }

    public long insertAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        // This class is used to store a set of values that the ContentResolver
        ContentValues values = new ContentValues();

        // `id` and `timestamp` will be inserted automatically.
        values.put(Alarm.COLUMN_HOUR_OF_DAY, alarm.getHourOfDay());
        values.put(Alarm.COLUMN_MINUTE, alarm.getMinute());
        values.put(Alarm.COLUMN_STATUS, alarm.getAlarmStatus());

        // insert now
        long id = db.insert(Alarm.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public Alarm getAlarm(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        /*
        * public android.database.Cursor query(String table,
                                     String[] columns,
                                     String selection,
                                     String[] selectionArgs,
                                     String groupBy,
                                     String having,
                                     String orderBy,
                                     String limit)
        *
        * */
        Cursor cursor = db.query(
                Alarm.TABLE_NAME,
                new String[]{Alarm.COLUMN_ID, Alarm.COLUMN_HOUR_OF_DAY, Alarm.COLUMN_MINUTE, Alarm.COLUMN_STATUS, Alarm.COLUMN_TIMESTAMP},
                Alarm.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );

        Alarm alarm = new Alarm();
        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            cursor.moveToFirst(); // move to first column
            alarm = new Alarm(
                    cursor.getInt(cursor.getColumnIndex(Alarm.COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndex(Alarm.COLUMN_HOUR_OF_DAY)),
                    cursor.getInt(cursor.getColumnIndex(Alarm.COLUMN_MINUTE)),
                    (cursor.getInt(cursor.getColumnIndex(Alarm.COLUMN_STATUS)) == 1)
            );
            cursor.close();
        }
        return alarm;
    }

    public List<Alarm> getAllAlarms() {
        List<Alarm> alarmList = new ArrayList<>();

        // Select all query
        String selectAllQuery =
                "SELECT * FROM "
                        + Alarm.TABLE_NAME
                        + " ORDER BY "
                        + Alarm.COLUMN_TIMESTAMP
                        + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAllQuery, null);

        // looping through all row and adding to list
        // moveToFirst  -Move the cursor to the first row. This method will return false if the cursor is empty.
        // moveToNext   -Move the cursor to the next row. This method will return false if the cursor is already past the last entry in the result set.
        if (cursor.moveToFirst()) {
            do {
                Alarm alarm = new Alarm();
                alarm.setID(cursor.getInt(cursor.getColumnIndex(Alarm.COLUMN_ID)));
                alarm.setHourOfDay(cursor.getInt(cursor.getColumnIndex(Alarm.COLUMN_HOUR_OF_DAY)));
                alarm.setMinute(cursor.getInt(cursor.getColumnIndex(Alarm.COLUMN_MINUTE)));
                alarm.setEnable((cursor.getInt(cursor.getColumnIndex(Alarm.COLUMN_STATUS)) == 1));
                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return alarmList;
    }

    public int getAlarmsCount() {
        String countQuery = "SELECT * FROM " + Alarm.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Alarm.COLUMN_HOUR_OF_DAY, alarm.getHourOfDay());
        values.put(Alarm.COLUMN_MINUTE, alarm.getMinute());
        values.put(Alarm.COLUMN_STATUS, alarm.getAlarmStatus());

        return db.update(Alarm.TABLE_NAME, values, Alarm.COLUMN_ID + " = ?",
                new String[]{String.valueOf(alarm.getID())});
    }

    public int deleteAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(Alarm.TABLE_NAME, Alarm.COLUMN_ID + " = ?", new String[]{String.valueOf(alarm.getID())});
        db.close();
        return result;
    }

}