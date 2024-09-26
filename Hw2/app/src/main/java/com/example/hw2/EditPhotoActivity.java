package com.example.hw2;

import android.Manifest;
import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.OutputStream;

public class EditPhotoActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_PERMISSION = 103;

    private ImageView photoImageView;
    private SeekBar zoomSeekbar;
    private Button saveButton;
    private FrameLayout frameLayout;
    private Uri imageUri;

    private final ActivityResultLauncher<IntentSenderRequest> intentSenderLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    saveImage();
                } else {
                    Toast.makeText(this, "需要寫入權限來儲存圖片", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // 啟用邊到邊的佈局
        setContentView(R.layout.activity_edit_photo);

        // 設定狀態欄的顏色
        getWindow().setStatusBarColor(Color.parseColor("#46A3FF"));

        // 處理 WindowInsets 來確保內容不被遮擋
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化 Imageview
        photoImageView = findViewById(R.id.showPhoto);
        // 初始化 Seekbar
        zoomSeekbar = findViewById(R.id.zoomSeekBar);
        // 初始化 Button
        saveButton = findViewById(R.id.saveButton);
        // 初始化 FrameLayout
        frameLayout = findViewById(R.id.frameLayout);

        // 接收並顯示圖片
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String imageUriString = bundle.getString("imageUri");
            if (imageUriString != null) {
                imageUri = Uri.parse(imageUriString);
                loadImage(imageUri);
            } else {
                Toast.makeText(this, "未接收到圖片 URI", Toast.LENGTH_SHORT).show();
            }
        }

        // 設定拖曳按鈕
        zoomSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int process, boolean fromUser) {
                // 計算縮放比例
                float scale = 1 + (process / 100.0f);

                // 設定圖片縮放
                photoImageView.setScaleX(scale);
                photoImageView.setScaleY(scale);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 不需要實作
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 不需要實作
            }
        });

        // 設定存檔按鈕
        saveButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestWritePermission();
            } else {
                saveImage();
            }
        });
    }

    private void loadImage(Uri imageUri) {
        photoImageView.setImageURI(imageUri);
    }

    private void saveImage() {
        if (imageUri == null) {
            Toast.makeText(this, "未接收到圖片 URI", Toast.LENGTH_SHORT).show();
            return;
        }

        // 取得 FrameLayout 的 Bitmap
        frameLayout.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(frameLayout.getDrawingCache());
        frameLayout.setDrawingCacheEnabled(false);

        // 裁切 Bitmap 到 FrameLayout 的範圍
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, frameLayout.getWidth(), frameLayout.getHeight());

        // 儲存 Bitmap 到原本的檔案路徑
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(imageUri);
            if (outputStream != null) {
                croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
                Toast.makeText(this, "圖片已儲存", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "無法打開輸出流", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                RecoverableSecurityException recoverableSecurityException = (RecoverableSecurityException) e;
                IntentSender intentSender = recoverableSecurityException.getUserAction().getActionIntent().getIntentSender();
                IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
                intentSenderLauncher.launch(request);
            } else {
                e.printStackTrace();
                Toast.makeText(this, "儲存圖片失敗", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "儲存圖片失敗", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            }, REQUEST_WRITE_PERMISSION);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                Toast.makeText(this, "需要寫入權限來儲存圖片", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
