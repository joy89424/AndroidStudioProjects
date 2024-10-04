package com.example.hw2_rewrite;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {

    // 定義相機和存儲權限的請求代碼
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_READ_PERMISSION_CODE = 101;

    // 定義 Uri 來儲存拍照或選擇的圖片
    private Uri photoUri;
    private ArrayList<Uri> imageUriList = new ArrayList<>();
    private ArrayList<String> uniqueIds = new ArrayList<>(); // 用來存放唯一 ID 的新列表

    // 定義 RecyclerView 來顯示圖片列表
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    // 定義按鈕來顯示彈出菜單
    private Button showPopMenuButton;
    private PopupMenu popupMenu;

    // 定義 ActivityResultLauncher 來處理圖片選擇結果
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // 定義儲存的 key
    private static final String IMAGE_URI_LIST_KEY = "image_uri_list";

    // 定義 Gson 變量
    private Gson gson;

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

        // 初始化 Gson
        gson = new Gson();  // 新增這行以初始化 gson

        // 註冊廣播接收器
        IntentFilter filter = new IntentFilter("com.example.hw2_rewrite.PHOTO_UPDATED");

        // 恢復保存的狀態
        if (savedInstanceState != null) {
            imageUriList = savedInstanceState.getParcelableArrayList("imageUriList");
            uniqueIds = savedInstanceState.getStringArrayList("uniqueIds");
        } else {
            // 恢復保存的圖片 Uri 列表
            loadImageUriList();
        }

        // 初始化
        showPopMenuButton = findViewById(R.id.showPopMenuButton);
        recyclerView = findViewById(R.id.recyclerView);
        imageAdapter = new ImageAdapter(imageUriList, uniqueIds, getSupportFragmentManager()); // 傳入 FragmentManager

        // 設置RecyclerView佈局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 每行兩列
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(imageAdapter); // 設置適配器

        // 初始化 PopupMenu
        showPopMenuButton.setOnClickListener(v -> {
            showPopMenu(v); // 按下按鈕後的行為
        });

        // 初始化圖片選擇器、相機拍攝結果、編輯照片的處理
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Uri selectedImageUri = null;

                // 處理圖片選擇結果
                if (result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                }

                // 如果有選擇的圖片
                if ( selectedImageUri != null) {
                    // 獲取實際路徑，主要是為了配合小米手機問題
                    Uri imagePath = getRealPathFromURI(selectedImageUri);
                    if (imagePath != null) {
                        imageUriList.add(imagePath); // 將圖片 URI 加入列表
                        String newUniqueId = "unique_id_for_image_" + System.currentTimeMillis(); // 生成唯一 ID
                        uniqueIds.add(newUniqueId); // 將唯一 ID 加入列表
                        imageAdapter.notifyItemInserted(imageUriList.size()-1);
                        imageAdapter.notifyItemInserted(uniqueIds.size()-1);
                        Toast.makeText(this, "圖片選擇成功", Toast.LENGTH_SHORT).show();
                    }
                } else if (photoUri != null) {
                    // 處理拍攝結果
                    imageUriList.add(photoUri);
                    String newUniqueId = "unique_id_for_image_" + System.currentTimeMillis(); // 生成唯一 ID
                    uniqueIds.add(newUniqueId); // 將唯一 ID 加入列表
                    imageAdapter.notifyItemInserted(imageUriList.size()-1);
                    imageAdapter.notifyItemInserted(uniqueIds.size()-1);
                    Toast.makeText(this, "圖片拍攝成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "操作失敗", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "操作取消", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 活動暫停時保存圖片 Uri
        saveImageUriList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 活動停止時保存圖片 Uri
        saveImageUriList();
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
        outState.putParcelable("photoUri", photoUri);  // 保存 photoUri
        outState.putStringArrayList("uniqueIds", uniqueIds);  // 保存 uniqueIds 列表
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            imageUriList = savedInstanceState.getParcelableArrayList("imageUriList");
            uniqueIds = savedInstanceState.getStringArrayList("uniqueIds"); // 恢復 uniqueIds 列表
            if (imageUriList == null) {
                imageUriList = new ArrayList<>(); // 確保不為 null
            }
            photoUri = savedInstanceState.getParcelable("photoUri");
            imageAdapter.notifyDataSetChanged(); // 通知 Adapter 資料已更新
        }
    }

    // 在儲存圖片時將列表保存到 SharedPreferences
    private void saveImageUriList() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 使用 Gson 將 ArrayList<Uri> 轉換成 JSON
        Gson gson = new Gson();

        // 將 imageUris 轉換為 String 列表
        List<String> uriStrings = new ArrayList<>();
        for (Uri uri : imageUriList) {
            uriStrings.add(uri.toString());
        }

        String json = gson.toJson(uriStrings);

        // 將 JSON 字符串儲存到 SharedPreferences
        editor.putString(IMAGE_URI_LIST_KEY, json);

        // 保存唯一 ID
        String uniqueIdJson = gson.toJson(uniqueIds);
        editor.putString("uniqueIds", uniqueIdJson);

        editor.apply();
    }

    // 從 SharedPreferences 讀取 Uri 列表
    private void loadImageUriList() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);

        // 加載圖片 URI
        String json = sharedPreferences.getString(IMAGE_URI_LIST_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> uriStrings = gson.fromJson(json, type);

            imageUriList = new ArrayList<>();
            if (uriStrings != null) {
                for (String uriString : uriStrings) {
                    imageUriList.add(Uri.parse(uriString));
                }
            }
        } else {
            imageUriList = new ArrayList<>(); // 如果沒有數據，則初始化
        }

        // 加載唯一 ID
        String uniqueIdJson = sharedPreferences.getString("uniqueIds", null);
        if (uniqueIdJson != null) {
            Type uniqueIdType = new TypeToken<List<String>>() {}.getType();
            List<String> uniqueIdStrings = gson.fromJson(uniqueIdJson, uniqueIdType);

            uniqueIds = new ArrayList<>();
            if (uniqueIdStrings != null) {
                uniqueIds.addAll(uniqueIdStrings);
            }
        } else {
            uniqueIds = new ArrayList<>(); // 如果沒有數據，則初始化
        }
    }

    // 檢查並處理相機權限
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 如果已有權限，執行相機功能
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                photoUri = createImageFile(); // 創建檔案用於保存照片
                if (photoUri != null){
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); // 將 URI 傳遞給相機應用
                    imagePickerLauncher.launch(cameraIntent); // 啟動相機
                } else {
                    Toast.makeText(this, "無法創建圖像文件", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "無法啟動相機", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 如果沒有權限，請求權限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private Uri createImageFile() {
        // 創建一個臨時文件用於保存圖片
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile("image_",".jpg", storageDir);
            return FileProvider.getUriForFile(this, "com.example.hw2_rewrite.fileprovider", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 檢查並處理存儲權限
    private void chooseImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14 及以上
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker(false);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_READ_PERMISSION_CODE);
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            // Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker(false);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_READ_PERMISSION_CODE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0 - 12
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker(false);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_PERMISSION_CODE);
            }
        } else {
            // Android 6.0 以下，無需請求權限
            launchImagePicker(false);
        }
    }

    // 執行圖片選擇器
    private void launchImagePicker(boolean limitedAccess) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (limitedAccess) {
            // 指定特定的目錄
            Uri uri = Uri.parse("content://media/external/images/media");
            intent.setDataAndType(uri, "image/*");
        }
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 使用者已授予相機權限
                    Toast.makeText(this, "相機權限已授予", Toast.LENGTH_SHORT).show();
                    openCamera();
                } else {
                    // 使用者拒絕相機權限
                    Toast.makeText(this, "相機權限被拒絕", Toast.LENGTH_SHORT).show();
                }
                break;
            case STORAGE_READ_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 使用者已授予存儲權限
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        Toast.makeText(this, "存取權限全部允許", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "存取權限已授予", Toast.LENGTH_SHORT).show();
                    }
                    launchImagePicker(false);
                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                        // 使用者已授予有限存取
                        Toast.makeText(this, "您已選擇有限存取，請選擇全部允許以繼續使用該功能", Toast.LENGTH_SHORT).show();
                        // 重新請求完整存取的權限
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_READ_PERMISSION_CODE);
                    } else {
                        // 使用者拒絕存儲權限
                        Toast.makeText(this, "存取權限被拒絕", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    // 獲取圖片的路徑
    public Uri getRealPathFromURI(Uri uri) {
        String path = null;
        // 判斷是否為 MIUI 手機
        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
            // 使用 MIUI 的方法獲取圖片路徑
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
                cursor.close();
            }
        } else {
            // 使用標準方法獲取圖片路徑
            return uri;
        }
        return Uri.parse(path);
    }
}