package com.example.testa_new;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class MyFragment extends Fragment {

    private ArrayList<Uri> imageUriList = new ArrayList<>();
    private Integer position;

    private ImageView fragmentImageView;

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
            System.out.println("Fragment imageUriList = " + imageUriList);
            System.out.println("Fragment position = " + position);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_my, container, false);

        fragmentImageView = fragmentView.findViewById(R.id.fragmentImageView);

        // 設定 ImageView 的圖片，如果有 imageUriList 和 position
        if (imageUriList != null && position != null && position < imageUriList.size()) fragmentImageView.setImageURI(imageUriList.get(position));

        return fragmentView;
    }
}