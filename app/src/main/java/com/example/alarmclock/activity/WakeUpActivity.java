//動いていないと判定された場合の画面
//本体のアラームを鳴らし、GoogleHomeに対してのリクエストを行う
//加速度センサーを用いて動いているかを判定し、フラグファイルの編集を行う

package com.example.alarmclock.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.alarmclock.R;
import com.example.alarmclock.service.SoundService;

import java.io.FileOutputStream;
import java.io.IOException;


public class WakeUpActivity extends AppCompatActivity implements SensorEventListener {
    //加速度センサー
    float xhis = 0;
    float yhis = 0;
    float zhis = 0;
    float xval = 0;
    float yval = 0;
    float zval = 0;
    float active = 0;
    float stable = 0;
    float total = 0;

    String flag = "0";
    SensorManager manager;
    Sensor sensor;
    TextView stableTextView;
    TextView activeTextView;
    WebView GoogleHomeWebView;
    WebView WeatherWebView;
    Button stopBtn;
    private String fileName = "flag.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_wake_up);
        stableTextView = findViewById(R.id.stable);
        activeTextView = findViewById(R.id.active);

        Toolbar toolbar = findViewById(R.id.toolbarWakeUp);
        setSupportActionBar(toolbar);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        startService(new Intent(this, SoundService.class));


        //GoogleHomeへHTTP通信
        GoogleHomeWebView = findViewById(R.id.GoogleHomeWebView);
        GoogleHomeWebView.getSettings().setJavaScriptEnabled(true);
        GoogleHomeWebView.loadUrl("http://192.168.2.158:8091/google-home-notifier?text=テスト");
        GoogleHomeWebView.setWebViewClient(new MyWebViewClient());

        //天気予報画面
        WeatherWebView = findViewById(R.id.WeatherWebView);
        WeatherWebView.getSettings().setJavaScriptEnabled(true);
        WeatherWebView.loadUrl("https://weathernews.jp/onebox/");
        WeatherWebView.setWebViewClient(new MyWebViewClient());
        //

        stopBtn = findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(WakeUpActivity.this, SoundService.class));
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //加速度を用いて動いているかを判定
        xval = event.values[0];
        yval = event.values[1];
        zval = event.values[2];
        if ((Math.abs(xval - xhis) > 0.2) && (Math.abs(yval - yhis) > 0.2) && (Math.abs(zval - zhis) > 0.2)) {
            active += 1;
        } else {
            stable += 1;
        }
        total += 1;
        if ((int) ((active / total) * 100) >= 10) {
            flag = "1";
        } else {
            flag = "0";
        }

        activeTextView.setText((int) ((active / total) * 100) + "%");
        stableTextView.setText((int) ((stable / total) * 100) + "%");
        xhis = xval;
        yhis = yval;
        zhis = zval;

        //スヌーズを鳴らすかどうかのフラッグ保存
        saveFile(fileName, flag);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }

    public void saveFile(String file, String str) {

        try (FileOutputStream fileOutputstream = openFileOutput(file,
                Context.MODE_PRIVATE)) {

            fileOutputstream.write(str.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}
