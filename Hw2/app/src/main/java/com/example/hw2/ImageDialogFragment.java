package com.example.hw2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.InputStream;

public class ImageDialogFragment extends DialogFragment {

    private ImageView imageView;
    private Button recordButton;
    private Button editPhotoButton;
    private Button newButton;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        // 初始化
        imageView = view.findViewById(R.id.fragmentImageView);
        recordButton = view.findViewById(R.id.recordButton);
        editPhotoButton = view.findViewById(R.id.editButton);
        newButton = view.findViewById(R.id.newButton);

        // 從 Bundle 中獲取圖片 URI，並顯示圖片
        if (getArguments() != null) {
            String imageUriString = getArguments().getString("imageUri");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                loadImage(imageUri);
            }
        }

        // 設定錄音按鈕的點擊事件
        recordButton.setOnClickListener(view1 -> {
            String imageUriString = getArguments().getString("imageUri");
            Uri imageUri = Uri.parse(imageUriString);

            if (imageUri != null) {
                Intent recordIntent = new Intent(getActivity(), RecordActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("imageUri", imageUri.toString());
                recordIntent.putExtras(bundle);
                startActivity(recordIntent);
            } else {
                Toast.makeText(getContext(), "圖片未加載", Toast.LENGTH_SHORT).show();
            }
        });

        // 設定編輯相片按鈕的點擊事件
        editPhotoButton.setOnClickListener(view1 -> {
            String imageUriString = getArguments().getString("imageUri");
            Uri imageUri = Uri.parse(imageUriString);

            if (imageUri != null) {
                Intent editPhotoIntent = new Intent(getActivity(), EditPhotoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("imageUri", imageUri.toString());
                editPhotoIntent.putExtras(bundle);
                startActivity(editPhotoIntent);
            } else {
                Toast.makeText(getContext(), "圖片未加載", Toast.LENGTH_SHORT).show();
            }
        });

        // 設定新增相片按鈕的點擊事件
        newButton.setOnClickListener(view1 -> {
            dismiss();
        });

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90); // 90% 螢幕寬度
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90); // 90% 螢幕高度
            dialog.getWindow().setLayout(width, height);
        });
        /*
        // 設置對話框的樣式，這裡設置為全屏
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        */
        return dialog;
    }
}
