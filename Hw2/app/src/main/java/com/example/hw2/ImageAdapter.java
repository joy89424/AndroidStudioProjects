package com.example.hw2;

import android.net.Uri;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Uri> imageUris;
    private OnItemClickListener listener;

    // 定義點擊事件的接口
    public interface OnItemClickListener {
        void onItemClick(int position, Uri imageUri);
    }

    // 設置點擊事件的監聽器
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ImageAdapter(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position); // 確保從正確的 Uri 列表中取得圖片
        holder.imageView.setImageURI(imageUri); // 將圖片設置到 ImageView

        // 設置圖片的點擊事件
        holder.imageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, imageUri);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size(); // 返回圖片列表的大小
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
