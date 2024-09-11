package com.example.project_2;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<String> dataList;

    // 构造函数，用来传入数据列表
    public MyAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    // 创建 ViewHolder，item_layout.xml 被填充进来作为 ViewHolder 的视图
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    // 将数据绑定到 ViewHolder 上
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.itemText.setText(dataList.get(position));
        // 此处可以进一步绑定更多数据或设置点击事件
    }

    // 返回列表项的数量
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // 定义 ViewHolder，保存 item 的视图
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemText;
        ImageView itemImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.itemText);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }
}
