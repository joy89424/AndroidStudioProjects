package com.example.project_2;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 找到 RecyclerView 元件
        // 1. 初始化 RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // 准备数据源
        // 2. 準備資料
        List<String> dataList = Arrays.asList("Item 1", "Item 2", "Item 3", "Item 4");

        // 创建 Adapter 并设置给 RecyclerView
        // 3. 創建 Adapter 並設置資料
        MyAdapter adapter = new MyAdapter(dataList);

        // 4. 設定 RecyclerView 的 Adapter
        recyclerView.setAdapter(adapter);

        // 设置 LayoutManager，这里使用 LinearLayoutManager 来实现垂直列表
        // 5. 設定 RecyclerView 的 LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}