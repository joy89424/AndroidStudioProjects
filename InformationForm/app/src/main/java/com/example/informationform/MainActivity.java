package com.example.informationform;

import android.content.Intent;
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
    private RecyclerView recyclerView;
    private PersonAdapter adapter;
    private List<Person> personList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("顧客資料系統");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        personList = new ArrayList<>();

        // 初始化 Adapter
        adapter = new PersonAdapter(personList, new PersonAdapter.OnDeleteClickListenr() {
            @Override
            public void onDeleteClick(int postion) {
                // 檢查列表的大小，確保索引合法
                if (postion >= 0 && postion < personList.size()) {
                    personList.remove(postion);  // 刪除點擊位置的資料
                    adapter.notifyDataSetChanged(); // 強制更新 RecyclerView
                    adapter.notifyItemRemoved(postion);  // 通知 Adapter 更新

                    // 檢查列表是否為空
                    if (personList.isEmpty()){
                        Toast.makeText(MainActivity.this, "資料已清空", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 索引不合法時的錯誤處理
                    Toast.makeText(MainActivity.this, "無法刪除，索引超出範圍", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(adapter);

        EditText editName = findViewById(R.id.editTextText);
        EditText editAge = findViewById(R.id.editTextNumber);
        Button addbutton = findViewById(R.id.button);

        // 設定添加按鈕點擊事件
        addbutton.setOnClickListener(view -> {
            String name = editName.getText().toString();
            String ageString = editAge.getText().toString();

            if(!name.isEmpty() && !ageString.isEmpty()) {
                int age = Integer.parseInt(ageString);
                personList.add(new Person(name, age));
                adapter.notifyItemInserted(personList.size() - 1);
                recyclerView.scrollToPosition(personList.size() - 1);

                editName.setText("");
                editAge.setText("");
            }
        });

        // 搜尋按鈕的實作
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
                        searchResults.add(person);
                    }
                }

                // 如果找到結果，跳轉至 ResultActivity，並傳遞搜尋結果
                if (searchResults.isEmpty()) {
                    Toast.makeText(MainActivity.this, "未找到相關人員", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("searchResult", searchResults);
                startActivity(intent);
            }
        });

    }
}