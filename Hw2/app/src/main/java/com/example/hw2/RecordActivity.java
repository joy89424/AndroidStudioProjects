package com.example.hw2;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;

import android.media.MediaRecorder;
import android.media.MediaPlayer;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_RECORD_AUDIO_PERMISSION = 102;
    private boolean permissionToRecordAccepted = false;
    private String[] permission = {Manifest.permission.RECORD_AUDIO};

    // 顯示圖片的變數
    private ImageView ivImage;

    // 宣告錄音相關變數
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private Button recordButton;

    // 播放錄音的變數
    private MediaPlayer mediaPlayer;
    private Button playButton;

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

        checkAudioPermission();

        // 初始化 UI 元件
        // 圖片
        ivImage = findViewById(R.id.ivImage);

        // 按鈕
        recordButton = findViewById(R.id.btnStartStopRecording);
        playButton = findViewById(R.id.btnPlayRecording);
        Button saveButton = findViewById(R.id.btnSaveRecording);

        // 取得存儲錄音的檔案路徑
        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/trmp_audio.3dp";

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

        // 開始錄音/停止錄音按鈕的邏輯
        recordButton.setOnClickListener(view -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
            isRecording = !isRecording;
        });

        // 播放錄音的邏輯
        playButton.setOnClickListener(view -> {
            if (mediaPlayer == null) {
                startPlaying();
            } else {
                stopPlaying();
            }
        });

        // 儲存錄音的邏輯
        saveButton.setOnClickListener(view -> {
            if (!isRecording) {
                saveRecordWithImage();
            } else {
                Toast.makeText(this, "請先停止錄音再保存", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // 請求麥克風權限
    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_RECORD_AUDIO_PERMISSION);
        }
    }

    // 處理權限請求結果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 執行錄音功能
                } else {
                    Toast.makeText(this, "錄音被拒絕", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
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

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 使用麥克風作為音源
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // 錄音格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 音頻編碼
        mediaRecorder.setOutputFile(audioFilePath); // 設定錄音檔案的儲存路徑

        try {
            mediaRecorder.prepare(); // 準備錄音
            mediaRecorder.start(); // 開始錄音
        } catch (IOException e) {
            e.printStackTrace();
        }

        recordButton.setText("停止錄音"); // 開始錄音後按鈕文字變更
    }

    private void stopRecording() {
        mediaRecorder.stop();  // 停止錄音
        mediaRecorder.release();  // 釋放資源
        mediaRecorder = null;
        recordButton.setText("開始錄音"); // 停止錄音後按鈕文字變更
    }

    private void startPlaying() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePath); // 設定音源路徑
            mediaPlayer.prepare(); // 準備播放
            mediaPlayer.start(); // 開始播放
            playButton.setText("停止播放"); // 播放時按鈕文字變更為“停止播放”
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 播放完後自動停止並釋放資源
        mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
            stopPlaying();
        });
    }

    private void stopPlaying() {
        mediaPlayer.release(); // 釋放 MediaPlayer 資源
        mediaPlayer = null; // 重置 mediaPlayer
        playButton.setText("播放錄音"); // 停止後按鈕文字變更為“播放錄音”
    }

    // 定義一個方法來儲存圖片URI與錄音檔的路徑
    private void saveRecordWithImage() {
        // 確保有圖片和錄音檔的路徑
        if (ivImage.getDrawable() != null && audioFilePath != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String imageUriString = bundle.getString("imageUri");
                if (imageUriString != null) {
                    // 使用 SharedPreferences 儲存圖片 URI 和錄音檔路徑
                    SharedPreferences sharedPreferences = getSharedPreferences("RecordingData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(imageUriString, audioFilePath); // 將圖片的 URI 與對應的錄音檔路徑進行綁定
                    editor.apply();

                    Toast.makeText(this, "錄音已儲存", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "無法儲存，未找到圖片 URI", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "請確保錄音和圖片已完成", Toast.LENGTH_SHORT).show();
        }
    }

}