package com.example.hw2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 101;
    private Uri photoUri; // 儲存拍攝的圖片 URI

    private ActivityResultLauncher<Intent> getImageLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    private List<Uri> imageUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 恢復保存的狀態
        if (savedInstanceState != null) {
            imageUris = savedInstanceState.getParcelableArrayList("imageUris");
        } else {
            // 從 SharePreference 載入圖片 URI
            loadImageUrisFromPreference(); // 從 SharedPreferences 中恢復圖片 URI
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
                    // 檢查權限，有的話就開啟相機
                    checkCameraPermission();
                    return true;
                } else if (item.getItemId() == R.id.action_choose_image) {
                    // 檢查權限，有的話就開啟圖片選擇器
                    checkStoragePermission();
                    return true;
                } else {
                    return false;
                }
            });

            // 顯示 PopupMenu
            popupMenu.show();
        });

        // 初始化圖片適配器並設置點擊監聽器
        ImageAdapter imageAdapter = new ImageAdapter(imageUris);
        imageAdapter.setOnItemClickListener(this); // 設置點擊事件

        // 初始化圖片選擇器的 ActivityResultLauncher
        getImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
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

        // 初始化相機的 ActivityResultLauncher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (photoUri != null) {
                            // 將拍攝的圖片 URI 加入列表
                            imageUris.add(photoUri);
                            // 通知適配器有新項目插入
                            imageAdapter.notifyItemInserted(imageUris.size() - 1);

                            Toast.makeText(MainActivity.this, "照片已儲存", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "拍照失敗", Toast.LENGTH_SHORT).show();
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
        outState.putParcelable("photoUri", photoUri);  // 保存 photoUri
        outState.putParcelableArrayList("imageUris", new ArrayList<>(imageUris));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageUris = savedInstanceState.getParcelableArrayList("imageUris");
        if (savedInstanceState != null) {
            photoUri = savedInstanceState.getParcelable("photoUri");
            if (photoUri == null) {
                // 必須進行處理，防止 photoUri 為 null 導致崩潰
            }
            imageUris = savedInstanceState.getParcelableArrayList("imageUris");
            if (imageUris == null) {
                imageUris = new ArrayList<>();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveImageUrisToPreferences();
    }

    // 儲存 imageUris 到 SharedPreferences
    private void saveImageUrisToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 儲存 imageUris 列表中的所有 URI
        Set<String> uriSet = new HashSet<>();
        for (Uri uri : imageUris) {
            uriSet.add(uri.toString());
        }

        editor.putStringSet("imageUris", uriSet);
        editor.apply();  // 提交變更
    }

    // 從 SharedPreferences 中恢復圖片 URI
    private void loadImageUrisFromPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        Set<String> uriSet = sharedPreferences.getStringSet("imageUris", null);

        if (uriSet != null) {
            for (String uriString : uriSet) {
                imageUris.add(Uri.parse(uriString));  // 將字串轉換為 URI 並加入 imageUris 列表
            }
        }
    }

    // 運行時動態請求權限
    // 用來檢查應用程式是否已獲得讀取外部儲存空間的權限，有的話就開啟圖片選擇器
    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果尚未獲得權限，則請求權限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            // 如果權限已經獲得，啟動圖片選擇器
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            getImageLauncher.launch(intent);
        }
    }

    // 用來檢查應用程式是否已獲得相機的權限，有的話就啟動相機
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            // 準備拍照
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                photoUri = createImageFile(); // 創建圖片檔案
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);  // 傳遞 URI 給相機
                cameraLauncher.launch(cameraIntent); // 啟動相機
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "無法創建檔案", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 處理使用者對權限請求的回應
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION || requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"權限已獲得", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "權限被拒絕", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 初始化 DialogFragment，並傳遞圖片給 DialogFragment，再顯示出來
    @Override
    public void onItemClick(int position, Uri imageUri) {
        // 創建 DialogFragment 的實例
        ImageDialogFragment imageDialogFragment = new ImageDialogFragment();

        // 傳遞圖片 URI 給 DialogFragment
        Bundle bundle = new Bundle();
        bundle.putString("imageUri", imageUri.toString());
        imageDialogFragment.setArguments(bundle);

        // 顯示 DialogFragment
        imageDialogFragment.show(getSupportFragmentManager(), "imageDialog");
    }

    // 儲存圖片到指定路徑並返回 Uri
    private Uri createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_"; // 圖片檔名
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // 儲存圖片的目錄
        File image = File.createTempFile(imageFileName, ".jpg", storageDir); // 創建臨時文件
        if (image.exists()) {
            Toast.makeText(this, "檔案已創建" + image.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }
        return FileProvider.getUriForFile(this, "com.example.hw2.fileprovider", image); // 返回 URI
    }

}
