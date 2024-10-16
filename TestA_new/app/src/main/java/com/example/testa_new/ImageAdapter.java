package com.example.testa_new;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private ArrayList<Uri> imageUriList;
    private Integer spinnerSelect;

    public ImageAdapter(ArrayList<Uri> imageUriList, Integer spinnerSelect) {
        this.imageUriList = imageUriList;
        this.spinnerSelect = spinnerSelect;
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
        holder.imageView.setImageURI(imageUri); // 設置 imageView 圖片

        String[] splitImageString = imageUri.toString().split("/");
        holder.textView.setText("Picture " + (position + 1) + " : " + splitImageString[splitImageString.length-1]); // 修改 textView 文字

        holder.imageView.setOnClickListener(v -> {
            switch (spinnerSelect) {
                case 0:
                    System.out.println("按下圖片，並已選擇了 Fragment");
                    // open Fragment
                    MyFragment myFragment = MyFragment.newInstance(imageUriList, position);
                    FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, myFragment)
                            .addToBackStack(null)
                            .commit();
                    break;
                case 1:
                    // open DialogFragment

                    break;
                case 2:
                    // open Activity

                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUriList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
