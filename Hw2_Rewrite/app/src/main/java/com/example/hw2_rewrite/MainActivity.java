package com.example.hw2_rewrite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int PICK_IMAGE_CODE = 200;

    private RecyclerView recyclerView;
    private Button showPopMenuButton;
    PopupMenu popupMenu;

    // 定義 ActivityResultLauncher 來處理圖片選擇結果
    private ActivityResultLauncher<Intent> imagePickerLauncher;


    private ArrayList<Uri> imageUriList = new ArrayList<>();
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 設定狀態欄的顏色
        getWindow().setStatusBarColor(Color.parseColor("#46A3FF"));  // getWindow().setStatusBarColor(只能放入int))，所以需要Color.parseColor()

        // 處理 WindowInsets 來確保內容不被遮擋
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 恢復保存的狀態
        if (savedInstanceState != null) {
            ArrayList<Uri> savedUriList = savedInstanceState.getParcelableArrayList("imageUriList");
            if (savedUriList != null) {
                imageUriList = savedUriList;
            }
        }

        // 初始化
        showPopMenuButton = findViewById(R.id.showPopMenuButton);
        recyclerView = findViewById(R.id.recyclerView);
        imageAdapter = new ImageAdapter(imageUriList);


        // 設置RecyclerView佈局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 每行兩列
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(imageAdapter); // 設置適配器

        // 初始化 PopupMenu
        showPopMenuButton.setOnClickListener(v -> {
            showPopMenu(v); // 按下按鈕後的行為
        });

        // 初始化圖片選擇器
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    imageUriList.add(selectedImageUri); // 將圖片 URI 加入列表
                    imageAdapter.notifyItemInserted(imageUriList.size()-1);
                    Toast.makeText(this, "圖片選擇成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showPopMenu(View view) {
        popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());

        // 設置選單項目的點擊事件
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.actionOpenCamera) {
                // 在這裡處理開啟相機
                openCamera();
                return true;
            } else if (item.getItemId() == R.id.actionChooseImage) {
                // 在這裡處理選擇圖片
                chooseImage();
                return true;
            } else {
                return false;
            }
        });

        // 顯示選單
        popupMenu.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("imageUriList", imageUriList);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Uri> savedUriList = savedInstanceState.getParcelableArrayList("imageUriList");
            if (savedUriList != null) {
                imageUriList = savedUriList;
                if (imageUriList == null) {
                   imageUriList = new ArrayList<>(); // 確保不為 null
                }
                imageAdapter.notifyDataSetChanged(); // 通知 Adapter 資料已更新
            }
        }
    }

    // 檢查並處理相機權限
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 如果已有權限，執行相機功能
            Toast.makeText(this, "相機權限已授予", Toast.LENGTH_SHORT).show();
            // 開啟相機的代碼放在這裡
        } else {
            // 如果沒有權限，請求權限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    // 檢查並處理存儲權限
    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // 如果已有權限，執行圖片選擇器
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);

        } else {
            // 如果沒有權限，請求權限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 使用者已授予相機權限
                    Toast.makeText(this, "相機權限已授予", Toast.LENGTH_SHORT).show();
                } else {
                    // 使用者拒絕相機權限
                    Toast.makeText(this, "相機權限被拒絕", Toast.LENGTH_SHORT).show();
                }
                break;
            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 使用者已授予存儲權限
                    Toast.makeText(this, "存取權限已授予", Toast.LENGTH_SHORT).show();
                } else {
                    // 使用者拒絕存儲權限
                    Toast.makeText(this, "存取權限被拒絕", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}