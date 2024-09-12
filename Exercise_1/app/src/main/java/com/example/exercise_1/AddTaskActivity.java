package com.example.exercise_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Button initial
        Button buttonSaveData = findViewById(R.id.buttonSaveData);
        // EditText initial
        EditText editText_1 = findViewById(R.id.editText_1);

        buttonSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 取得 EditText 中的文字
                String newTask = editText_1.getText().toString();

                // 準備回傳的 Intent
                Intent intent = new Intent();
                intent.putExtra("new_task", newTask);

                // 設置結果為 OK 並附加回傳資料
                setResult(RESULT_OK, intent);

                // 結束當前 Activity 並返回 MainActivity
                finish();
            }
        });
    }
}