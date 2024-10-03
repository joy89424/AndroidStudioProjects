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

    public ImageAdapter(ArrayList<Uri> imageUriList, ArrayList<String> uniqueIds, FragmentManager fragmentManager) {
        this.imageUriList = imageUriList;
        this.uniqueIds = uniqueIds; // 新增的參數
        this.fragmentManager = fragmentManager;
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
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioPath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
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