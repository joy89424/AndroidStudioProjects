package com.example.pratice1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Activity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_2);

        // 接收從 MainActivity 傳遞過來的 Intent
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        // 顯示接收到的數據
        EditText editText2 = findViewById(R.id.editText2);
        editText2.setText(message);

        Button buttonTransmit2 = findViewById(R.id.buttonTransmit2);
        buttonTransmit2.setOnClickListener(view -> {
            // 取得 EditText 的內容
            String message2 = editText2.getText().toString();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("message2", message2);

            // 設定結果為 RESULT_OK，並附帶數據
            setResult(RESULT_OK, resultIntent);

            // 關閉 Activity2，返回 MainActivity
            finish();
        });
    }
}