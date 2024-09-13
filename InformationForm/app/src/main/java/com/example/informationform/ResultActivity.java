package com.example.informationform;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private RecyclerView resultRecyclerView; // RecyclerView 控件，用於顯示搜尋結果
    private PersonAdapter resultAdapter; // Adapter，用於處理 RecyclerView 的數據顯示
    private ArrayList<Person> searchResult; // 存儲搜尋結果的列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result); // 設置活動的佈局檔案

        // 初始化 RecyclerView 並設置其布局管理器
        resultRecyclerView = findViewById(R.id.resultRecyclerView);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化 SharedPreferences
        // 1. 獲取 SharedPreferences 物件
        SharedPreferences sharedPreferences = getSharedPreferences("Person", MODE_PRIVATE);
        // 2. 獲取 Editor 物件
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 從 Intent 中獲取搜尋結果
        Intent intent = getIntent();
        searchResult = (ArrayList<Person>) intent.getSerializableExtra("searchResult");

        // 初始化 Adapter 並設置刪除按鈕的點擊事件處理器
        resultAdapter = new PersonAdapter(searchResult, new PersonAdapter.OnDeleteClickListenr() {
            @Override
            public void onDeleteClick(int position) {
                // 刪除點擊位置的項目
                searchResult.remove(position);

                // 將 SharedPreferences 的資料刪除
                // 3.儲存數據
                editor.remove(searchResult.get(position).getName());
                editor.apply(); // 儲存變更

                resultAdapter.notifyItemRemoved(position); // 通知 Adapter 刪除了某項目
                resultAdapter.notifyDataSetChanged(); // 通知 Adapter 列表已更改
            }
        });
        resultRecyclerView.setAdapter(resultAdapter); // 設置 RecyclerView 的 Adapter
    }
}
