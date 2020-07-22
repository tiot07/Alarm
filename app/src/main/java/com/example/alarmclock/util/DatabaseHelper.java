package com.example.alarmclock.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final private String DBNAME = "alarm.sqlite";
    static final private int VERSION = 1;
    private static DatabaseHelper sSingleton = null;


    private DatabaseHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new DatabaseHelper(context);
        }
        return sSingleton;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE alarms (alarmid integer PRIMARY KEY, name text , alarttime text );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Alarm;");
        onCreate(db);
    }
}
