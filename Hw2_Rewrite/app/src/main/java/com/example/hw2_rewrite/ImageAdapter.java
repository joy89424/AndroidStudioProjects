package com.example.hw2_rewrite;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private ArrayList<Uri> imageUriList;
    private ArrayList<String> uniqueIds; // 存儲每張圖片的唯一 ID
    private FragmentManager fragmentManager;
    MediaPlayer mediaPlayer;
    private boolean isMediaPlayerPrepared = false; // 用來追踪 MediaPlayer 是否已經準備好

    public ImageAdapter(ArrayList<Uri> imageUriList, ArrayList<String> uniqueIds, FragmentManager fragmentManager) {
        this.imageUriList = imageUriList;
        this.uniqueIds = uniqueIds;
        this.fragmentManager = fragmentManager;
        this.mediaPlayer = new MediaPlayer(); // 初始化 MediaPlayer
        this.mediaPlayer.setOnPreparedListener(mp -> {
            isMediaPlayerPrepared = true; // 更新標誌
            mediaPlayer.start(); // 當音頻準備好後開始播放
        });
        this.mediaPlayer.setOnCompletionListener(mp -> {isMediaPlayerPrepared = false;}); // 當播放結束時，重置標誌
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        Uri imageUri = imageUriList.get(position);
        String uniqueId = uniqueIds.get(position); // 獲取每張圖片的唯一 ID
        holder.imageView.setImageURI(imageUri); // 設置圖片

        holder.imageView.setOnClickListener(v -> {
            // 創建並顯示 ImageDialogFragment
            ImageDialogFragment dialogFragment = ImageDialogFragment.newInstance(imageUri.toString(), uniqueId);
            dialogFragment.show(fragmentManager, "ImageDialogFragment");
        });

        holder.playAudioButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("audio_records", Context.MODE_PRIVATE);
            String audioPath = sharedPreferences.getString("audio_for_image_" + uniqueId, null); // 使用唯一 ID 來獲取音頻路徑

            if (audioPath != null) {
                // 如果 MediaPlayer 正在播放或尚未準備好，先停止
                if (mediaPlayer.isPlaying() || isMediaPlayerPrepared) {
                    mediaPlayer.stop();
                    mediaPlayer.reset(); // 重置 MediaPlayer 以便播放新文件
                    isMediaPlayerPrepared = false;
                }

                try {
                    mediaPlayer.setDataSource(audioPath);
                    mediaPlayer.prepareAsync(); // 異步準備 MediaPlayer
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(holder.itemView.getContext(), "此圖片沒有聲音", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUriList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button playAudioButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            playAudioButton = itemView.findViewById(R.id.playAudioButton);
        }
    }
}