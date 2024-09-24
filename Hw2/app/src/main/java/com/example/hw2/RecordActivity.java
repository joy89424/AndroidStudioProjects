package com.example.hw2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;

public class RecordActivity extends AppCompatActivity {

    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // 啟用邊到邊的佈局
        setContentView(R.layout.activity_record);

        // 設定狀態欄的顏色
        getWindow().setStatusBarColor(Color.parseColor("#46A3FF"));

        // 處理 WindowInsets 來確保內容不被遮擋
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化 UI 元件
        initUI();

        // 接收並顯示圖片
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String imageUriString = bundle.getString("imageUri");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                loadImage(imageUri);
            } else {
                Toast.makeText(this, "未接收到圖片 URI", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initUI() {
        // 初始化 UI 元件的邏輯
        ivImage = findViewById(R.id.ivImage);
    }

    private void loadImage(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            ivImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Fail to load image", Toast.LENGTH_SHORT).show();
        }
    }
}