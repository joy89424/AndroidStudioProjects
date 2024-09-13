package com.example.informationform;

import android.content.Intent;
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

public class ResultActivity extends AppCompatActivity {

    private RecyclerView resultRecyclerView;
    private PersonAdapter resultAdapter;
    private ArrayList<Person> searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultRecyclerView = findViewById(R.id.resultRecyclerView);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        // 從 Intent 中獲取搜尋結果
        searchResult = (ArrayList<Person>) getIntent().getSerializableExtra("searchResult");

        // 設置 RecyclerView
        resultAdapter = new PersonAdapter(searchResult, new PersonAdapter.OnDeleteClickListenr() {
            @Override
            public void onDeleteClick(int postion) {
                searchResult.remove(postion);
                resultAdapter.notifyItemRemoved(postion);
                resultAdapter.notifyDataSetChanged();
            }
        });
        resultRecyclerView.setAdapter(resultAdapter);
    }
}