package com.example.hw2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

public class ImageFragment extends Fragment {

    private ImageView imageView;
    private Button recordButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        // 初始化 imageView 和 recordButton
        imageView = view.findViewById(R.id.fragmentImageView);
        recordButton = view.findViewById(R.id.recordButton);


        // 設定按鈕的點擊事件（未來實作錄音功能）
        recordButton.setOnClickListener(view1 -> {
            // 錄音功能邏輯（未來步驟）
            Toast.makeText(getContext(), "Record button clicked", Toast.LENGTH_SHORT).show();
        });

        // 從 Bundle 中獲取圖片 URI，並顯示圖片
        if (getArguments() != null) {
            String imageUriString = getArguments().getString("imageUri");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                loadImage(imageUri);
            }
        }

        return view;
    }

    private void loadImage(Uri imageUri) {
        try {
            InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Fail to load image", Toast.LENGTH_SHORT).show();
        }
    }
}