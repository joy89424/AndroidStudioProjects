package com.example.hw2_rewrite;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class RecordActivity extends AppCompatActivity {
    private Button startRecordingButton;
    private Button playRecordingButton;
    private Button saveRecordingButton;
    private ImageView recordImageView;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private boolean isRecording = false;
    private boolean isPlaying = false;

    private String audioFileName = null;
    private String imageUriString;
    private String uniqueId; // 用來存儲唯一 ID

    // 權限請求代碼
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 102;

    // 權限相關
    private boolean permissionToRecordAccepted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // 初始化
        startRecordingButton = findViewById(R.id.startRecordingButton);
        playRecordingButton = findViewById(R.id.playRecordingButton);
        saveRecordingButton = findViewById(R.id.saveRecordingButton);
        recordImageView = findViewById(R.id.recordImageView);

        // 從 Intent 獲取圖片 URI 和唯一 ID
        Intent intent = getIntent();
        imageUriString = intent.getStringExtra("imageUri");
        uniqueId = intent.getStringExtra("uniqueId"); // 獲取唯一 ID
        Uri imageUri = Uri.parse(imageUriString);

        // 顯示圖片
        recordImageView.setImageURI(imageUri);

        // 請求錄音權限
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);

        // 設置按鈕點擊事件
        startRecordingButton.setOnClickListener(view -> {
            onRecord();
        });

        playRecordingButton.setOnClickListener(view -> {
            onPlay();
        });

        saveRecordingButton.setOnClickListener(view -> {
            onSave();
        });
    }

    private void onRecord() {
        if (!isRecording) {
            // 開始錄音
            startRecording();
            startRecordingButton.setText("停止錄音");
        } else {
            // 停止錄音
            stopRecording();
            startRecordingButton.setText("開始錄音");
        }
        isRecording = !isRecording;
    }

    private void startRecording() {
        // 設定錄音檔案的名稱和路徑
        String fileName = "audio_" + System.currentTimeMillis() + ".mp3";
        audioFileName = getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + fileName;

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(audioFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        try {
            mediaRecorder.stop();
        } catch (RuntimeException stopException) {
            // Handle the exception
        }
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void onPlay() {
        if (!isPlaying) {
            // 開始播放
            startPlaying();
            playRecordingButton.setText("停止播放");
        } else {
            // 停止播放
            stopPlaying();
            playRecordingButton.setText("播放錄音");
        }
        isPlaying = !isPlaying;
    }

    private void startPlaying() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(mediaPlayer1 ->{
            stopPlaying();
            playRecordingButton.setText("播放錄音");
        });
    }

    private void stopPlaying() {
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop(); // 停止播放
            }
            mediaPlayer.reset(); // 重置 mediaPlayer 以便重用
        }
    }

    private void onSave() {
        if (audioFileName != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("audio_records", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // 使用唯一 ID 來存儲錄音
            editor.putString("audio_for_image_" + uniqueId, audioFileName); // 使用唯一 ID 存儲
            editor.apply();

            Toast.makeText(this, "錄音已保存", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "沒有錄音可保存", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "錄音權限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "需要錄音權限才能使用此功能", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
