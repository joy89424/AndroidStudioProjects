package com.example.project_4;

import android.content.Intent;
import android.net.wifi.aware.PublishConfig;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    /* --------------------------------------------------------------------------
        onCreate() → onStart() → onResume()
        （Activity開始運行並顯示在前台）

        onPause() → onStop()
        （Activity進入背景）

        onRestart() → onStart() → onResume()
        （從背景返回前台）

        onDestroy()
        （Activity被銷毀）
    -------------------------------------------------------------------------- */
    // 當Activity被創建時調用。這是應用初始化的地方，通常用來設置佈局、初始化元件、加載資料等
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化代碼
        // 初始化 Spinner
        Spinner Spinner_1 = findViewById(R.id.Spinner_1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.早餐, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        Spinner_1.setAdapter(adapter);

        // 初始化 Button
        Button Button_1 = findViewById(R.id.Button_1);
        Button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
                //finish(); // 結束 MainActivity，防止其佔用資源
            }
        });
    }

    // 當Activity即將顯示給用戶時調用。此時Activity還不可與用戶互動，但已準備好顯示在螢幕上。
    @Override
    protected void onStart() {
        super.onStart();
        // 例如刷新UI元件數據
    }

    // 當Activity準備與用戶互動時調用。這是Activity處於"前景"狀態的時候，用戶可以與它互動。
    @Override
    protected void onResume() {
        super.onResume();
        // 例如啟動相機、感應器
    }

    // 當Activity即將停止與用戶互動時調用。通常是在Activity失去焦點、即將進入背景但未完全隱藏的狀態時調用，例如當系統顯示一個新的Activity時。
    @Override
    protected void onPause() {
        super.onPause();
        // 例如暫停播放音樂、保存草稿
    }

    // 當Activity完全不可見時調用，表示Activity已經進入背景。這是釋放資源、停止繁重作業的好時機。
    @Override
    protected void onStop() {
        super.onStop();
        // 例如停止長時間運行的作業
    }

    // 當Activity被銷毀時調用，這是Activity生命週期的最後一個階段。通常是由系統自動調用，或者在Activity被手動銷毀時調用。
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 例如清理任何資源或暫存數據
    }

    // 當Activity從停止狀態重新啟動時調用，通常是當用戶再次打開應用並重新回到之前的Activity時。
    @Override
    protected void onRestart() {
        super.onRestart();
        // 例如重新載入數據
    }
}