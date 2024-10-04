package com.example.hw2_rewrite;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ImageDialogFragment extends DialogFragment {

    private ImageView fragmentImageView;
    private Button newButton;
    private Button recordButton;
    private Button editButton;

    private static final String ARG_IMAGE_URI = "image_uri";
    private static final String ARG_UNIQUE_ID = "uniqueId";

    private String uniqueId; // 將 uniqueId 定義為類的成員變數

    public static ImageDialogFragment newInstance(String imageUri, String uniqueId) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URI, imageUri);
        args.putString(ARG_UNIQUE_ID, uniqueId); // 傳遞唯一 ID
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        // 初始化視圖
        fragmentImageView = view.findViewById(R.id.fragmentImageView);
        newButton = view.findViewById(R.id.newButton);
        recordButton = view.findViewById(R.id.recordButton);
        editButton = view.findViewById(R.id.editButton);

        // 從 Bundle 中獲取傳遞過來的 URI，並顯示圖片
        if (getArguments() != null) {
            String imageUri = getArguments().getString(ARG_IMAGE_URI);
            uniqueId = getArguments().getString(ARG_UNIQUE_ID); // 獲取唯一 ID
            fragmentImageView.setImageURI(Uri.parse(imageUri));
        }

        // 設置按鈕點擊事件
        newButton.setOnClickListener(v -> {
            // 實現新增功能
            dismiss();
        });

        recordButton.setOnClickListener(v -> {
            // 實現錄音功能
            Intent intent = new Intent(getActivity(), RecordActivity.class);
            intent.putExtra("imageUri", getArguments().getString(ARG_IMAGE_URI));
            intent.putExtra("uniqueId", uniqueId); // 傳遞唯一 ID
            startActivity(intent);
        });

        editButton.setOnClickListener(v -> {
            // 實現編輯相片功能
            Intent intent = new Intent(getActivity(), EditPhotoActivity.class);
            intent.putExtra("imageUri", getArguments().getString(ARG_IMAGE_URI));
            intent.putExtra("uniqueId", uniqueId); // 傳遞唯一 ID
            startActivity(intent);
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90); // 90% 螢幕寬度
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90); // 90% 螢幕高度
            dialog.getWindow().setLayout(width, height);
        });

        return dialog;
    }
}
