package com.example.hw2_rewrite;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private ArrayList<Uri> imageUriList;
    private FragmentManager fragmentManager;

    public ImageAdapter(ArrayList<Uri> imageUriList, FragmentManager fragmentManager) {
        this.imageUriList = imageUriList;
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
        holder.imageView.setImageURI(imageUri); // 設置圖片

        holder.imageView.setOnClickListener(v -> {
            // 創建並顯示 ImageDialogFragment
            ImageDialogFragment dialogFragment = ImageDialogFragment.newInstance(imageUri.toString());
            dialogFragment.show(fragmentManager, "ImageDialogFragment");
        });
    }

    @Override
    public int getItemCount() {
        return imageUriList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}