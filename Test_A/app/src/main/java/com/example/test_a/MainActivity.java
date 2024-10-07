package com.example.test_a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {

    private Button goToB;
    public ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    System.out.println("result = " + result);
                    if (result.getResultCode() == RESULT_OK) {
                        // 處理來自 ActivityC 的返回資料
                        Intent data = result.getData();
                        if (data != null) {
                            boolean resultData = data.getBooleanExtra("isEdited", false);
                            // 更新資料或進行操作
                            System.out.println("resultData = " + resultData);
                        }
                    }
                }
        );

        goToB = findViewById(R.id.goToB);
        goToB.setOnClickListener(v -> {
            Intent intentB = new Intent(this, ActivityB.class);
            launcher.launch(intentB);
        });
    }
}

