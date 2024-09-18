package com.example.pratice1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1; // 定義請求代碼
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 初始化 EditText
        editText = findViewById(R.id.editText);

        // 按下傳送按鈕
        Button buttonTransmit = findViewById(R.id.buttonTransmit);
        buttonTransmit.setOnClickListener(view -> {
            // 建立 Intent，指定目標 Activity 是 Activity2
            Intent intent = new Intent(MainActivity.this, Activity2.class);
            String message = editText.getText().toString();

            // 可選：傳遞數據到目標 Activity
            intent.putExtra("message", message);

            // 啟動 Activity2 ，並等待回傳結果
            startActivityForResult(intent, REQUEST_CODE);
        });

    }

    // 接收來自 Activity2 的結果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // 從返回的 Intent 中取得數據
            String returnedMessage = data.getStringExtra("message2");
            // 顯示在 EditText 中
            editText.setText(returnedMessage);
        }
    }
}