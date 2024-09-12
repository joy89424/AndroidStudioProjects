package com.example.exercise_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;  // 添加這行來檢查 log
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> dataArray = new ArrayList<>(); // 保留資料

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button initial
        Button buttonNewToDoItem = findViewById(R.id.buttonNewToDoItem);
        // TextView dataShow
        TextView dataShow = findViewById(R.id.dataShow);

        // 檢查 TextView 初始是否正確顯示資料
        Log.d("MainActivity", "onCreate: dataArray size = " + dataArray.size()); // Log 顯示資料數量
        updateDataShow(dataShow);  // 一開始顯示所有儲存的資料

        buttonNewToDoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    // 接收從 AddTaskActivity 回傳的資料
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 從 Intent 中取得新資料
            String newTask = data.getStringExtra("new_task");

            if (newTask != null && !newTask.trim().isEmpty()) { // 確認資料非空
                // 將新資料加入 dataArray
                dataArray.add(newTask);

                // Log 確認資料是否成功加入
                Log.d("MainActivity", "onActivityResult: New task added = " + newTask);
                Log.d("MainActivity", "onActivityResult: dataArray size = " + dataArray.size()); // Log 顯示資料數量

                // 更新 TextView 顯示
                TextView dataShow = findViewById(R.id.dataShow);
                updateDataShow(dataShow);  // 更新顯示的內容
            }
        }
    }


    // 用來更新 TextView 顯示所有的資料
    private void updateDataShow(TextView dataShow) {
        StringBuilder sb = new StringBuilder();
        for (String task : dataArray) {
            sb.append(task).append("\n");  // 確保每一項任務顯示在新行
        }

        // Log 檢查顯示的資料
        Log.d("MainActivity", "updateDataShow: Full text = " + sb.toString());

        // 更新 TextView 顯示
        dataShow.setText(sb.toString());  // 顯示資料
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("dataArray", dataArray);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            dataArray = (ArrayList<String>) savedInstanceState.getSerializable("dataArray");

            // 获取 TextView
            TextView dataShow = findViewById(R.id.dataShow);

            // 调用 updateDataShow() 并传入 TextView
            updateDataShow(dataShow);
        }
    }

}
