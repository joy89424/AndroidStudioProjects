package com.example.testa_new;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyFragment extends Fragment {

    private static final String TAG = "MyFragment";
    private ArrayList<Uri> imageUriList = new ArrayList<>();
    private Integer position;
    private Button fragmentButton;

    private ImageView fragmentImageView;
    private TextView fragmentEditText;

    public MyFragment() {
        // Required empty public constructor
    }

    public static MyFragment newInstance(ArrayList<Uri> imageUriList, Integer position) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("imageUriList", imageUriList);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUriList = getArguments().getParcelableArrayList("imageUriList");
            position = getArguments().getInt("position");
            Log.d(TAG, "onCreate: imageUriList = " + imageUriList);
            Log.d(TAG, "onCreate: position = " + position);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_my, container, false);

        fragmentImageView = fragmentView.findViewById(R.id.fragmentImageView);
        fragmentEditText = fragmentView.findViewById(R.id.fragmentEditText);
        fragmentButton = fragmentView.findViewById(R.id.fragmentButton);

        // 設定 ImageView 的圖片，如果有 imageUriList 和 position
        if (imageUriList != null && position != null && position < imageUriList.size()) fragmentImageView.setImageURI(imageUriList.get(position));

        // 按下回傳按鈕後
        fragmentButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "回傳", Toast.LENGTH_SHORT).show();
            String fragmentText = (String) fragmentEditText.getText();

            // 取得 FragmentManager 和 FragmentTransaction
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // 移除當前的 Fragment
            fragmentTransaction.remove(MyFragment.this);

            // 提交變更
            fragmentTransaction.commit();

        });

        return fragmentView;
    }
}