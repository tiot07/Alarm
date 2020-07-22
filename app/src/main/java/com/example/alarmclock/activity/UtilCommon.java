package com.example.alarmclock.activity;

import android.app.Application;
import android.util.Log;

/**
 * グローバル変数を扱うクラス
 * Created by sample on 2016/11/18.
 */
public class UtilCommon extends Application {

    private static final String TAG = "UtilCommon";
    private boolean mGlobal;  // boolean型のグローバル変数
    private int flag; // boolean型のグローバル変数

    /**
     * アプリケーションの起動時に呼び出される
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mGlobal = false;
        flag=0;
    }

    /**
     * アプリケーション終了時に呼び出される
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate");
        mGlobal = false;
        flag=0;
    }

    /**
     * グローバル変数の値を変更
     * @param global 変更する値
     */
    public void setGlobal(int global) {
        Log.d(TAG, "setGlobal");
        //mGlobal = global;
        flag=global;
    }

    /**
     * グローバル変数の値を取得
     * @return グローバル変数（mGlobal）
     */
    public int getGlobal() {
        Log.d(TAG, "getGlobal");
        return flag;
    }

}