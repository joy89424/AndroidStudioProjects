package com.example.informationform;

// 引入所需的 Android 和 Java 類別
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView; // RecyclerView 控件，用於顯示列表
    private PersonAdapter adapter; // Adapter，用於處理 RecyclerView 的數據顯示
    private List<Person> personList; // 存儲 Person 物件的列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 設置活動的佈局檔案

        // 設置工具列
        Toolbar toolbar = findViewById(R.id.toolbar_1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("顧客資料系統"); // 設置工具列的標題

        // 初始化 RecyclerView 和設置其布局管理器
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化 personList 並設置為空列表
        personList = new ArrayList<>();

        // 初始化 SharedPreferences
        // 1. 獲取 SharedPreferences 物件
        SharedPreferences sharedPreferences = getSharedPreferences("Person", MODE_PRIVATE);
        // 2. 獲取 Editor 物件
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 讀取 SharedPreferences 中的所有資料
        // 取得所有的鍵（person names）
        for (String key : sharedPreferences.getAll().keySet()) {
            int age = sharedPreferences.getInt(key, -1); // 讀取對應的年齡
            if (age != -1) {
                personList.add(new Person(key, age)); // 將 Person 物件添加到列表
            }
        }

        // 初始化添加按鈕和編輯文本控件
        EditText editName = findViewById(R.id.editTextText);
        EditText editAge = findViewById(R.id.editTextNumber);
        Button addbutton = findViewById(R.id.button);

        // 初始化 Adapter 並設置刪除按鈕的點擊事件處理器
        adapter = new PersonAdapter(personList, new PersonAdapter.OnDeleteClickListenr() {
            @Override
            public void onDeleteClick(int postion) {
                // 檢查點擊位置的合法性
                if (postion >= 0 && postion < personList.size()) {
                    personList.remove(postion); // 從列表中刪除指定位置的項目
                    adapter.notifyDataSetChanged(); // 通知 Adapter 列表已更改
                    adapter.notifyItemRemoved(postion); // 通知 Adapter 刪除了某項目

                    // 將 SharedPreferences 的資料刪除
                    // 3.儲存數據
                    editor.remove(personList.get(postion).getName());
                    editor.apply(); // 儲存變更

                    // 如果列表為空，顯示提示訊息
                    if (personList.isEmpty()){
                        Toast.makeText(MainActivity.this, "資料已清空", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 索引不合法時顯示錯誤提示
                    Toast.makeText(MainActivity.this, "無法刪除，索引超出範圍", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(adapter); // 設置 RecyclerView 的 Adapter

        // 設置添加按鈕的點擊事件
        addbutton.setOnClickListener(view -> {
            String name = editName.getText().toString();
            String ageString = editAge.getText().toString();

            // 檢查輸入是否為空
            if(!name.isEmpty() && !ageString.isEmpty()) {
                int age = Integer.parseInt(ageString); // 將年齡字符串轉換為整數
                personList.add(new Person(name, age)); // 將新 Person 物件添加到列表
                adapter.notifyItemInserted(personList.size() - 1); // 通知 Adapter 插入了新項目
                recyclerView.scrollToPosition(personList.size() - 1); // 滾動到新添加的項目

                // 清空編輯框
                editName.setText("");
                editAge.setText("");

                // 將檔案存入 SharedPreferences
                // 3.儲存數據
                editor.putInt(name, age); // 鍵為 name，值為 age
                editor.apply(); // 儲存變更
            }
        });

        // 初始化搜尋按鈕和編輯文本控件
        Button serchButton = findViewById(R.id.button2);
        EditText searchEditText = findViewById(R.id.editTextText2);
        serchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchName = searchEditText.getText().toString().trim();
                ArrayList<Person> searchResults = new ArrayList<>();

                // 遍歷 personList，尋找匹配的名稱
                for (Person person : personList) {
                    if (person.getName().equalsIgnoreCase(searchName)) {
                        searchResults.add(person); // 將匹配的 Person 物件添加到搜尋結果列表
                    }
                }

                // 如果找到結果，跳轉至 ResultActivity，並傳遞搜尋結果
                if (searchResults.isEmpty()) {
                    Toast.makeText(MainActivity.this, "未找到相關人員", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("searchResult", searchResults); // 將搜尋結果傳遞給 ResultActivity
                startActivity(intent); // 啟動 ResultActivity
            }
        });

    }
}
