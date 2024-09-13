package com.example.informationform;

// 引入所需的 Android 和 Java 類別
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

    private List<Person> personList; // 存儲 Person 物件的列表
    private OnDeleteClickListenr onDeleteClickListenr; // 刪除按鈕點擊事件的監聽器

    // 定義接口，供 Activity 或 Fragment 實現以處理刪除按鈕的點擊事件
    public interface OnDeleteClickListenr {
        void onDeleteClick(int position); // 當刪除按鈕被點擊時調用的方法
    }

    // PersonAdapter 的建構子，初始化 personList 和 onDeleteClickListenr
    public PersonAdapter(List<Person> personList, OnDeleteClickListenr listener) {
        this.personList = personList;
        this.onDeleteClickListenr = listener;
    }

    @NonNull
    @Override
    public PersonAdapter.PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 為 RecyclerView 創建新視圖
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item, parent, false);
        return new PersonViewHolder(view); // 返回新創建的 ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull PersonAdapter.PersonViewHolder holder, int position) {
        // 獲取列表中當前位置的 Person 物件
        Person person = personList.get(position);
        // 設置視圖中的文本內容
        holder.nameTextView.setText(person.getName());
        holder.ageTextView.setText(String.valueOf(person.getAge()));
        // 設置刪除按鈕的點擊事件處理器
        holder.btnDelete.setOnClickListener(view -> onDeleteClickListenr.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        // 返回列表中項目的數量
        return personList == null ? 0 : personList.size();
    }

    // 定義 ViewHolder 類，用於持有每個項目的視圖
    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, ageTextView; // 顯示人員姓名和年齡的 TextView
        Button btnDelete; // 刪除按鈕

        // ViewHolder 的建構子，初始化視圖中的控件
        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textName); // 設置姓名 TextView
            ageTextView = itemView.findViewById(R.id.textAge); // 設置年齡 TextView
            btnDelete = itemView.findViewById(R.id.btnDelete); // 設置刪除按鈕
        }
    }
}
