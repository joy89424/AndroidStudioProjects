package com.example.test_a;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActivityB extends AppCompatActivity {

    private Button goToC;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_b);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 設定 ActivityResultLauncher
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // 處理來自 ActivityC 的返回資料
                        Intent data = result.getData();
                        if (data != null) {
                            boolean resultData = data.getBooleanExtra("isEdited", false);
                            // 將資料傳回 MainActivity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("isEdited", resultData);
                            setResult(RESULT_OK, resultIntent);
                            finish(); // 關閉 ActivityB
                        }
                    }
                }
        );

        goToC = findViewById(R.id.goToC);
        goToC.setOnClickListener(v -> {
            Intent intentC = new Intent(this, ActivityC.class);
            launcher.launch(intentC); // 使用註冊的 launcher 啟動 ActivityC
        });
    }
}
