package com.example.alarmclock.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmclock.R;
import com.example.alarmclock.listcomponent.ListAdapter;
import com.example.alarmclock.listcomponent.ListItem;
import com.example.alarmclock.util.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ConfirmationActivity extends AppCompatActivity {
    final static public int NEW_REQ_CODE = 1;
    final static public int EDIT_REQ_CODE = 2;
    UtilCommon common = (UtilCommon) getApplication();
    float xhis = 0;
    float yhis = 0;
    float zhis = 0;
    float xval = 0;
    float yval = 0;
    float zval = 0;
    float active = 0;
    float stable = 0;
    float total = 0;
    int flag = 0;
    RecyclerView rv = null;
    RecyclerView.Adapter adapter = null;
    String text = "0";
    TextView stableTextView;
    TextView activeTextView;
    TextView xTextView;
    TextView yTextView;
    TextView zTextView;
    private DatabaseHelper helper = null;
    private String fileName = "test.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_main);
        saveFile(fileName, text);
        // アラームのデータを取得
        ArrayList<ListItem> data = this.loadAlarms();

        // RecyclerViewに設定
        this.setRV(data);

        // フローティングアクションボタンの設定
        FloatingActionButton fbt = findViewById(R.id.fbtn);
        fbt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(ConfirmationActivity.this, InputActivity.class);
                        i.putExtra(getString(R.string.request_code), NEW_REQ_CODE);
                        startActivityForResult(i, NEW_REQ_CODE);
                    }
                });


        stableTextView = findViewById(R.id.stable);
        activeTextView = findViewById(R.id.active);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // リクエストコードと結果コードをチェック
        if (requestCode == RESULT_CANCELED) {
            // 何もしない
        } else if ((requestCode == NEW_REQ_CODE || requestCode == EDIT_REQ_CODE) && resultCode == RESULT_OK) {
            ArrayList<ListItem> dataAlarms = this.loadAlarms();
            this.updateRV(dataAlarms);
        }
    }

    private ArrayList<ListItem> loadAlarms() {
        helper = DatabaseHelper.getInstance(this);

        ArrayList<ListItem> data = new ArrayList<>();

        try (SQLiteDatabase db = helper.getReadableDatabase()) {

            String[] cols = {"alarmid", "name", "alarttime"};

            Cursor cs = db.query("alarms", cols, null, null,
                    null, null, "alarmid", null);
            boolean eol = cs.moveToFirst();
            while (eol) {
                ListItem item = new ListItem();
                item.setAlarmID(cs.getInt(0));
                item.setAlarmName(cs.getString(1));
                item.setTime(cs.getString(2));
                data.add(item);
                eol = cs.moveToNext();
            }
        }
        return data;
    }

    private void setRV(ArrayList<ListItem> data) {
        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter = new ListAdapter(data);
        rv.setAdapter(adapter);
    }

    private void updateRV(ArrayList<ListItem> data) {
        adapter = new ListAdapter(data);
        rv.setAdapter(adapter);

    }


    public void saveFile(String file, String str) {

        // try-with-resources
        try (FileOutputStream fileOutputstream = openFileOutput(file,
                Context.MODE_PRIVATE)) {

            fileOutputstream.write(str.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
