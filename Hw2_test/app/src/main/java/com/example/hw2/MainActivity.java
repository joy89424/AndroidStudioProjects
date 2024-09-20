package com.example.hw2;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Uri> imageUriList = new ArrayList<>();

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 設定狀態欄的顏色
        getWindow().setStatusBarColor(Color.parseColor("#46A3FF"));

        // 處理 WindowInsets 來確保內容不被遮擋
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 每行顯示兩個圖片
        imageAdapter = new ImageAdapter(this, imageUriList);
        recyclerView.setAdapter(imageAdapter);


        // 註冊 ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            imageUriList.add(imageUri);
                            imageAdapter.notifyItemInserted(imageUriList.size() - 1);
                        }
                    }
                }
        );

        // 在 PopupMenu 選取圖片
        Button btnShowMemu = findViewById(R.id.btn_show_menu);
        btnShowMemu.setOnClickListener(view -> {
            showPopupMenu(view);
        });



    }

    private void showPopupMenu(View view) {
        // 建立 PopupMenu
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        // 將選單項目加入到 PopupMenu 中
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        // 設定選單項目點擊事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.option1) {
                    Toast.makeText(MainActivity.this, "選擇了開啟相機", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.option2) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    pickImageLauncher.launch(intent);
                    //startActivityForResult(intent, PICK_IMAGE_REQUEST);
                    //Toast.makeText(MainActivity.this, "選擇了選擇相片", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });

        // 顯示 PopupMenu
        popupMenu.show();
    }
}