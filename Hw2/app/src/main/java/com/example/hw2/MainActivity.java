package com.example.hw2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {

    // 定義請求權限的代碼
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 101;

    // 儲存拍攝的圖片 URI
    private Uri photoUri;

    // 用於啟動圖片選擇器、相機和錄音的 ActivityResultLauncher
    private ActivityResultLauncher<Intent> getImageLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> recordLauncher;

    // 儲存圖片 URI 的列表
    private List<Uri> imageUris = new ArrayList<>();
    private List<String> audioFilePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // 啟用邊到邊的佈局
        setContentView(R.layout.activity_main);

        // 恢復保存的狀態
        if (savedInstanceState != null) {
            imageUris = savedInstanceState.getParcelableArrayList("imageUris");
        } else {
            // 從 SharedPreferences 載入圖片 URI
            loadImageUrisFromPreference();
            loadAudioFilePathsFromPreference();
        }
        audioFilePaths = new ArrayList<>(Collections.nCopies(imageUris.size(), null));

        // 設定狀態欄的顏色
        getWindow().setStatusBarColor(Color.parseColor("#46A3FF"));

        // 處理 WindowInsets 來確保內容不被遮擋
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 取得加號按鈕的引用並設置點擊監聽器
        Button btnShowMenu = findViewById(R.id.btn_show_menu);
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

        // 初始化圖片適配器
        ImageAdapter imageAdapter = new ImageAdapter(imageUris);

        // 設置點擊監聽器
        imageAdapter.setOnItemClickListener(this);

        // 設置播放按鈕點擊監聽器
        imageAdapter.setOnPlayButtonClickListener(((position, imageUri) -> {
            // 從 SharedPreferences 中取得圖片對應的錄音檔路徑
            SharedPreferences sharedPreferences = getSharedPreferences("RecordingData", MODE_PRIVATE);
            String imageUriString = imageUri.toString();
            String audioFilepath = sharedPreferences.getString(imageUriString, null);

            if (audioFilepath != null) {
                // 開始播放對應的錄音檔
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    // 設置錄音檔路徑為數據源
                    mediaPlayer.setDataSource(audioFilepath);

                    // 准備播放器
                    mediaPlayer.prepare();

                    // 開始播放
                    mediaPlayer.start();
                    Toast.makeText(MainActivity.this, "正在播放錄音", Toast.LENGTH_SHORT).show();

                    // 播放完畢後自動釋放資源
                    mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
                        mediaPlayer.release();
                        Toast.makeText(MainActivity.this, "錄音播放完畢", Toast.LENGTH_SHORT).show();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "無法播放錄音", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "未找到對應的錄音檔", Toast.LENGTH_SHORT).show();
            }
        }));


        // 初始化圖片選擇器的 ActivityResultLauncher
        getImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // 獲取選擇的圖片 URI
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // 將選擇的圖片 URI 加入到列表中
                            imageUris.add(selectedImageUri);
                            // 將 audioFilePaths 相應位置設為 null
                            audioFilePaths.add(null);
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
                            // 將 audioFilePaths 相應位置設為 null
                            audioFilePaths.add(null);
                            // 通知適配器有新項目插入
                            imageAdapter.notifyItemInserted(imageUris.size() - 1);
                            Toast.makeText(MainActivity.this, "照片已儲存", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "拍照失敗", Toast.LENGTH_SHORT).show();
                    }
                });

        // 在錄音完成後
        recordLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        String audioFilePath = result.getData().getStringExtra("audioFilePath");
                        Log.d("MainActivity", "Audio File Path: " + audioFilePath);
                        int imagePosition = result.getData().getIntExtra("imagePosition", -1);
                        if (imagePosition != -1) {
                            audioFilePaths.set(imagePosition, audioFilePath);

                            // 儲存到 SharedPreferences，使用 imageUri 作為鍵
                            SharedPreferences sharedPreferences = getSharedPreferences("RecordingData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String imageUriString = imageUris.get(imagePosition).toString(); // 獲取對應的 URI
                            editor.putString(imageUriString, audioFilePath);
                            editor.apply(); // 提交變更
                        }
                    }
                });



        // 初始化 RecyclerView 並設置佈局管理器
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 每行兩列
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(imageAdapter); // 設置適配器

        // 通知適配器數據集已更改
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("photoUri", photoUri);  // 保存 photoUri
        outState.putParcelableArrayList("imageUris", new ArrayList<>(imageUris));
        outState.putStringArrayList("audioFilePaths", new ArrayList<>(audioFilePaths));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // 恢復 photoUri 和 imageUris
        photoUri = savedInstanceState.getParcelable("photoUri");
        imageUris = savedInstanceState.getParcelableArrayList("imageUris");
        if (imageUris == null) {
            imageUris = new ArrayList<>(); // 確保不為 null
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveImageUrisToPreferences(); // 暫停時保存圖片 URI
        saveAudioFilePathsToPreferences();
    }

    // 儲存 imageUris 到 SharedPreferences
    private void saveImageUrisToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 將 imageUris 轉換為 String 列表
        List<String> uriStrings = new ArrayList<>();
        for (Uri uri : imageUris) {
            uriStrings.add(uri.toString());
        }

        // 使用 Gson 將 String 列表轉換為 JSON
        Gson gson = new Gson();
        String json = gson.toJson(uriStrings);

        editor.putString("imageUris", json);
        editor.apply();  // 提交變更
    }


    // 從 SharedPreferences 中恢復圖片 URI
    private void loadImageUrisFromPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("imageUris", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {}.getType(); // 使用 List<String>
            List<String> uriStrings = gson.fromJson(json, type); // 將 JSON 轉換回 List<String>

            // 將 String 列表轉換為 Uri 列表
            imageUris = new ArrayList<>();
            if (uriStrings != null) {
                for (String uriString : uriStrings) {
                    imageUris.add(Uri.parse(uriString)); // 將 String 轉換回 Uri
                }
            }
        }

        if (imageUris == null) {
            imageUris = new ArrayList<>();  // 確保不為 null
        }
    }

    private void saveAudioFilePathsToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(audioFilePaths);
        editor.putString("audioFilePaths", json);
        editor.apply();  // 提交變更
    }

    private void loadAudioFilePathsFromPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("audioFilePaths", null);
        Type type = new TypeToken<List<String>>() {}.getType();
        audioFilePaths = new Gson().fromJson(json, type);
        if (audioFilePaths == null) {
            audioFilePaths = new ArrayList<>(Collections.nCopies(imageUris.size(), null)); // 確保不為 null
        }
    }

    // 檢查版本並請求儲存權限，獲得權限後開啟圖片選擇器
    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 使用 READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                openImagePicker(); // 權限已被允許，直接開啟圖片選擇器
            }
        } else {
            // Android 13 以下的版本使用 READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                openImagePicker(); // 權限已被允許，直接開啟圖片選擇器
            }
        }
    }

    // 開啟圖片選擇器
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        getImageLauncher.launch(intent); // 使用已註冊的 ActivityResultLauncher
    }

    // 開啟相機
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoUri = createImageFile(); // 創建一個圖片檔案並獲取其 URI
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); // 將 URI 傳遞給相機
            cameraLauncher.launch(cameraIntent); // 使用已註冊的 ActivityResultLauncher
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "無法創建圖片檔案", Toast.LENGTH_SHORT).show();
        }
    }

    // 創建一個圖片檔案
    private Uri createImageFile() throws IOException {
        String fileName = "photo_" + System.currentTimeMillis() + ".jpg"; // 生成檔名
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // 獲取儲存路徑
        File imageFile = File.createTempFile(fileName, ".jpg", storageDir); // 創建檔案
        return FileProvider.getUriForFile(this, "com.example.hw2.fileprovider", imageFile); // 轉換為 URI
    }

    // 請求相機權限，獲得權限後開啟相機
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            openCamera(); // 權限已被允許，開啟相機
        }
    }

    // 處理權限請求結果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker(); // 儲存權限被允許，開啟圖片選擇器
                } else {
                    Toast.makeText(this, "儲存權限被拒絕", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera(); // 相機權限被允許，開啟相機
                } else {
                    Toast.makeText(this, "相機權限被拒絕", Toast.LENGTH_SHORT).show();
                }
                break;
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
}
