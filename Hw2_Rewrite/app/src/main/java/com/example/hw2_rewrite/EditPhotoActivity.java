package com.example.hw2_rewrite;

import static java.lang.Character.getType;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EditPhotoActivity extends AppCompatActivity {

    private String imageUriString;
    private String uniqueId;
    private ImageView showPhoto;
    private SeekBar zoomSeekbar;
    private Button saveButton;
    private FrameLayout frameLayout;
    private float scaleFactor;

    private ArrayList<Uri> imageUriList = new ArrayList<>(); // 初始化 imageUriList
    private static final int STORAGE_WRITE_PERMISSION_CODE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_photo);
        getWindow().setStatusBarColor(Color.parseColor("#46A3FF"));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化
        showPhoto = findViewById(R.id.showPhoto);
        zoomSeekbar = findViewById(R.id.zoomSeekBar);
        saveButton = findViewById(R.id.saveButton);
        frameLayout = findViewById(R.id.frameLayout);

        // 從 Intent 獲取圖片 URI 和唯一 ID
        Intent intent = getIntent();
        imageUriString = intent.getStringExtra("imageUri");
        uniqueId = intent.getStringExtra("uniqueId"); // 獲取唯一 ID
        Uri imageUri = Uri.parse(imageUriString);

        // 顯示圖片
        showPhoto.setImageURI(imageUri);

        // 從 SharedPreferences 獲取 imageUriList
        loadImageUriListFromPreferences();

        // 設置 SeekBar 改變圖片大小
        zoomSeekbar.setMax(200); // 最大值
        zoomSeekbar.setProgress(100); // 初始比例 (100%)
        zoomSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                scaleFactor = progress / 100f; // 設置比例範圍為 0.5x 到 2x
                showPhoto.setScaleX(scaleFactor);
                showPhoto.setScaleY(scaleFactor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 不需要實作此方法
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 不需要實作此方法
            }
        });

        // 設置儲存按鈕的點擊事件
        saveButton.setOnClickListener(v -> {
            saveEditPhoto();
        });
    }

    // 儲存裁切過的圖片
    private void saveEditPhoto() {
        // 檢查是否需要權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上版本直接儲存，不需要請求權限
            saveBitmapToStorage();
        } else {
            // Android 9 及以下版本需要請求權限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_PERMISSION_CODE);
            } else {
                saveBitmapToStorage();
            }
        }
    }

    private void saveBitmapToStorage() {
        // 獲取原始的 Bitmap
        Bitmap originalBitmap = ((BitmapDrawable) showPhoto.getDrawable()).getBitmap();

        // 創建放大或縮小的 Bitmap
        int new_Width = Math.round(originalBitmap.getWidth()*scaleFactor);
        int new_Height = Math.round(originalBitmap.getHeight()*scaleFactor);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, new_Width, new_Height, true);

        // 創建新的 Bitmap，並以白色填充背景
        int frameWidth = frameLayout.getWidth();
        int frameHeight = frameLayout.getHeight();
        Bitmap editedBitmap = Bitmap.createBitmap(frameWidth, frameHeight, scaledBitmap.getConfig());
        Canvas canvas = new Canvas(editedBitmap);
        canvas.drawColor(Color.WHITE); // 填充白色背景

        // 計算繪製位置，確保圖片置中
        int left = (frameWidth - scaledBitmap.getWidth()) / 2;
        int top = (frameHeight - scaledBitmap.getHeight()) / 2;

        // 繪製圖片，處理放大和縮小的情況
        if (scaleFactor > 1) {
            // 放大圖片，裁剪超出部分
            Rect srcRect = new Rect(
                    Math.max(0, -left),
                    Math.max(0, -top),
                    Math.min(scaledBitmap.getWidth(), frameWidth - left),
                    Math.min(scaledBitmap.getHeight(), frameHeight - top)
            );
            Rect dstRect = new Rect(
                    Math.max(0, left),
                    Math.max(0, top),
                    Math.min(frameWidth, left + scaledBitmap.getWidth()),
                    Math.min(frameHeight, top + scaledBitmap.getHeight())
            );
            canvas.drawBitmap(scaledBitmap, srcRect, dstRect, null);
        } else {
            // 縮小圖片，填充白色背景
            canvas.drawBitmap(scaledBitmap, left, top, null);
        }

        // 確定儲存檔案的檔名，使用 uniqueId 來避免覆蓋
        String fileName = "edit_image_" + uniqueId + ".jpg";

        OutputStream fos;

        try {
            Uri imageUri = null;
            // Android 10 (API 29) 以上使用 MediaStore 儲存圖片
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EditedImages");

                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri);
                } else {
                    throw new IOException("Failed to create new MediaStore record.");
                }
            } else {
                // Android 10 以下使用傳統方式儲存至外部存儲
                File imagesDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "EditedImages");
                if (!imagesDir.exists()) {
                    imagesDir.mkdir();
                }
                File imageFile = new File(imagesDir, fileName);
                fos = new FileOutputStream(imageFile);
                imageUri = Uri.fromFile(imageFile);
            }

            // 將 Bitmap 儲存為 JPEG
            editedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // 更新 imageUriList
            updateImageUriList(imageUri);

            // 通知圖片已成功儲存
            Toast.makeText(this, "圖片已儲存成功", Toast.LENGTH_SHORT).show();

            // 回傳 Intent ，告知圖片已被更改
            Intent resultIntent = new Intent();
            resultIntent.putExtra("isEdited", true); // 根據實際情況設置
            setResult(RESULT_OK, resultIntent);
            finish(); // 結束 EditPhotoActivity
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "儲存失敗", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateImageUriList(Uri imageUri) {
        // 確保 imageUriList 和 uniqueId 都已讀取
        if (imageUriList != null && uniqueId != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
            String jsonUniqueIds = sharedPreferences.getString("uniqueIds", null);
            if (jsonUniqueIds != null) {
                Type type = new TypeToken<ArrayList<String>>() {}.getType();
                ArrayList<String> uniqueIdList = new Gson().fromJson(jsonUniqueIds, type);

                // 根據 uniqueId 找到對應的 index
                if (uniqueIdList.contains(uniqueId)) {
                    int index = uniqueIdList.indexOf(uniqueId);
                    // 使用 index 更新對應的 imageUri
                    imageUriList.set(index, imageUri);
                    // 更新 SharedPreferences
                    saveImageUriListToPreference();
                } else {
                    Toast.makeText(this, "無法找到對應的 uniqueId", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void saveImageUriListToPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 將 ArrayList<Uri> 轉換為 ArrayList<String>
        ArrayList<String> uriStringList = new ArrayList<>();
        for (Uri uri : imageUriList) {
            uriStringList.add(uri.toString());
        }

        // 將 List<String> 轉換為 JSON 字串
        String json = new Gson().toJson(uriStringList);
        editor.putString("image_uri_list", json);
        editor.apply();

        Log.d("MainActivity", "Image URI list saved: " + json); // 日誌輸出
    }

    private void loadImageUriListFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("image_uri_list", null);
        Log.d("MainActivity", "Loading Image URI list: " + json); // 日誌輸出

        // 讀取 imageUriList
        if (json != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> uriStringList = new Gson().fromJson(json, type);
            for (String uriString : uriStringList) {
                imageUriList.add(Uri.parse(uriString));
            }
        }

        // 讀取 uniqueIds
        String jsonUniqueIds = sharedPreferences.getString("uniqueIds", null);
        if (jsonUniqueIds != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> uniqueIdList = new Gson().fromJson(jsonUniqueIds, type);

            // 使用 uniqueId 尋找對應的索引
            if (uniqueIdList.contains(uniqueId)) {
                int index = uniqueIdList.indexOf(uniqueId);
                // 找到對應的 index 後繼續進行操作
            } else {
                Toast.makeText(this, "無法找到對應的 uniqueId", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_WRITE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "儲存權限已授予", Toast.LENGTH_SHORT).show();
                saveBitmapToStorage(); // 權限獲得後儲存圖片
            } else {
                Toast.makeText(this, "需要儲存權限才能使用此功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (showPhoto != null) {
            BitmapDrawable drawable = (BitmapDrawable) showPhoto.getDrawable();
            if (drawable != null) {
                Bitmap bitmap = drawable.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle(); // 釋放位圖資源
                }
            }
        }
    }
}