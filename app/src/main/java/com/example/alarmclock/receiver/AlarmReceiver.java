package com.example.alarmclock.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;

import com.example.alarmclock.activity.WakeUpActivity;
import com.example.alarmclock.listcomponent.ListItem;
import com.example.alarmclock.util.DatabaseHelper;
import com.example.alarmclock.util.Util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    private AlarmManager alarmMgr = null;
    private TimePicker timePicker = null;
    private PendingIntent alarmIntent = null;
    private String fileName = "test.txt";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        TextView activeTextView;

        String requestCode = intent.getData().toString();
        DatabaseHelper helper = DatabaseHelper.getInstance(context);

        LocalDateTime date1 = LocalDateTime.now();
        DateTimeFormatter dtformat1 = DateTimeFormatter.ofPattern("HH");
        DateTimeFormatter dtformat2 = DateTimeFormatter.ofPattern("mm");
        String hour = dtformat1.format(date1);
        String minute = dtformat2.format(date1);
        int minutes = Integer.parseInt(minute);

        String alarmTime = hour + ":"
                + String.format("%02d", minutes + 1);


        ListItem listItem = new ListItem();

        listItem.setAlarmID(minutes + 1);
        listItem.setAlarmName("無題");
        listItem.setTime(alarmTime);
        Util.setAlarm(context, listItem);

        String flag = readFile(context, fileName);
        Intent startActivityIntent = new Intent(context, WakeUpActivity.class);
        startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        if (Integer.parseInt(flag) != 1) {
            context.startActivity(startActivityIntent);
        }
    }

    // ファイルを読み出し
    public String readFile(Context context, String file) {
        String text = null;


        try (FileInputStream fileInputStream = context.openFileInput(file);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))) {

            String lineBuffer;
            while ((lineBuffer = reader.readLine()) != null) {
                text = lineBuffer;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }
}