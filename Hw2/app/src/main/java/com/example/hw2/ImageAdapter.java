package com.example.hw2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private final List<Uri> imageUriList;
    private final Context context;

    public ImageAdapter(Context context, List<Uri> imageUriList) {
        this.context = context;
        this.imageUriList = imageUriList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageUriList.get(position);
        Log.d("ImageAdapter", "Binding image URI: " + imageUri.toString());

        // 使用 Glide 等庫將圖片顯示為方形縮圖
        Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.placeholder) // 可選，設置佔位圖
                .error(R.drawable.error) // 可選，設置錯誤圖
                .into(holder.imageThumbnail);

        // 根據位置交錯左右排列
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.imageThumbnail.getLayoutParams();
        if (position % 2 == 0) {
            params.gravity = Gravity.START;
        } else {
            params.gravity = Gravity.END;
        }
        holder.imageThumbnail.setLayoutParams(params);

        // 點擊縮圖顯示大圖
        holder.imageThumbnail.setOnClickListener(v -> {
            // 在這裡處理顯示大圖的邏輯
        });
    }

    @Override
    public int getItemCount() {
        return imageUriList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageThumbnail;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageThumbnail = itemView.findViewById(R.id.image_thumbnail);
        }
    }
}
