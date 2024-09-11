package com.example.project_3;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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

        // 1. 初始化 RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // 2. 創建資料
        List<News> newsList = new ArrayList<>();
        newsList.add(new News("Breaking News", "This is a summary of breaking news.", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c4/Eo_circle_red_number-1.svg/768px-Eo_circle_red_number-1.svg.png"));
        newsList.add(new News("Tech News", "This is a summary of tech news.", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Eo_circle_red_number-2.svg/2048px-Eo_circle_red_number-2.svg.png"));
        newsList.add(new News("World News", "This is a summary of world news.", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/62/Eo_circle_red_number-3.svg/2048px-Eo_circle_red_number-3.svg.png"));

        // 3. 創建 NewsAdapter 並設置資料
        NewsAdapter newsAdapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(newsAdapter);

        // 4. 設定 RecyclerView 的 LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}