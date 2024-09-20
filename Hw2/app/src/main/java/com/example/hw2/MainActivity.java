package com.example.hw2;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE = 100;
    private ActivityResultLauncher<Intent> getImageLauncher;
    private List<Uri> imageUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 恢復保存的狀態
        if (savedInstanceState != null) {
            imageUris = savedInstanceState.getParcelableArrayList("imageUris");
            if (imageUris == null) {
                imageUris = new ArrayList<>();
            }
        }

        // 設定狀態欄的顏色
        getWindow().setStatusBarColor(Color.parseColor("#46A3FF"));

        // 處理 WindowInsets 來確保內容不被遮擋
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 取得加號按鈕的引用
        Button btnShowMenu = findViewById(R.id.btn_show_menu);
        // 為加號按鈕設置點擊監聽器
        btnShowMenu.setOnClickListener(view -> {
            // 創建一個 PopupMenu
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);

            // 載入選單項目
            popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());

            // 處理選單項目的點擊事件
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_open_camera) {
                    Toast.makeText(MainActivity.this, "開啟相機功能 (未實作)", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.action_choose_image) {
                    // 檢查權限
                    checkStoragePermission();

                    // 啟動圖片選擇器
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*"); // 設定選擇類型為圖片
                    getImageLauncher.launch(intent);
                    return true;
                } else {
                    return false;
                }
            });

            // 顯示 PopupMenu
            popupMenu.show();
        });

        // 初始化 RecyclerView 和適配器
        ImageAdapter imageAdapter = new ImageAdapter(imageUris);
        getImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                // 獲取選擇的圖片URI
                Uri selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    // 將選擇的圖片Uri加入到列表中
                    imageUris.add(selectedImageUri);
                    // 通知適配器有新項目插入
                    imageAdapter.notifyItemInserted(imageUris.size() - 1);
                }
            }
        });

        // 初始化RecyclerView並設置佈局管理器
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 每行兩列
        recyclerView.setLayoutManager(gridLayoutManager);

        // 創建一個ArrayList<Uri>來保存圖片Uri
        recyclerView.setAdapter(imageAdapter);

        // 通知適配器數據集已更改
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("imageUris", new ArrayList<>(imageUris));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageUris = savedInstanceState.getParcelableArrayList("imageUris");
        if (imageUris == null) {
            imageUris = new ArrayList<>(); // 如果為 null，初始化為空的 ArrayList
        }
    }

    // 運行時動態請求權限
    // 用來檢查應用程式是否已獲得讀取外部儲存空間的權限
    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果尚未獲得權限，則請求權限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
        }
    }

    // 處理使用者對權限請求的回應
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"權限已獲得", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "權限被拒絕", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
