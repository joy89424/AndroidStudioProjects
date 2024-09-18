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

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView; // RecyclerView 控件，用於顯示列表
    private PersonAdapter adapter; // Adapter，用於處理 RecyclerView 的數據顯示
    private List<Person> personList; // 存儲 Person 物件的列表

    // 請求碼常量
    private static final int REQUEST_CODE = 1;

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

        // 初始化 personList 並從 SharedPreferences 中讀取資料
        personList = new ArrayList<>();
        readData(this); // 使用 readData 讀取 SharedPreferences 中的資料

        // 初始化 Adapter 並設置刪除按鈕的點擊事件處理器
        adapter = new PersonAdapter(personList, new PersonAdapter.OnDeleteClickListenr() {
            @Override
            public void onDeleteClick(int position) {
                // 檢查點擊位置的合法性
                if (position >= 0 && position < personList.size()) {
                    String idToRemove = personList.get(position).getId();
                    removeData(MainActivity.this, idToRemove); // 更新 SharedPreferences 中的資料
                    personList.remove(position); // 從列表中刪除指定位置的項目
                    adapter.notifyDataSetChanged(); // 通知 Adapter 列表已更改
                    adapter.notifyItemRemoved(position); // 通知 Adapter 刪除了某項目

                    // 刪除後保存最新資料
                    saveData(MainActivity.this, new ArrayList<>(personList));

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

        // 初始化添加按鈕和編輯文本控件
        EditText editName = findViewById(R.id.editTextText);
        EditText editAge = findViewById(R.id.editTextNumber);
        Button addbutton = findViewById(R.id.button);

        // 設置添加按鈕的點擊事件
        addbutton.setOnClickListener(view -> {
            String name = editName.getText().toString();
            String ageString = editAge.getText().toString();

            // 檢查輸入是否為空
            if(!name.isEmpty() && !ageString.isEmpty()) {
                int age = Integer.parseInt(ageString); // 將年齡字符串轉換為整數
                Person newPerson = new Person(name, age);
                String id = newPerson.getId();
                personList.add(newPerson); // 將新 Person 物件添加到列表
                adapter.notifyItemInserted(personList.size() - 1); // 通知 Adapter 插入了新項目
                recyclerView.scrollToPosition(personList.size() - 1); // 滾動到新添加的項目

                // 清空編輯框
                editName.setText("");
                editAge.setText("");

                // 使用 JSON 格式儲存資料
                addData(MainActivity.this, name, age, id);

                // 新增後保存最新資料
                saveData(MainActivity.this, new ArrayList<>(personList));
            }
        });

        // 初始化搜尋按鈕和編輯文本控件
        Button serchButton = findViewById(R.id.button2);
        EditText searchEditText = findViewById(R.id.editTextText2);

        serchButton.setOnClickListener(view -> {
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
            startActivityForResult(intent, REQUEST_CODE); // 使用 startActivityForResult 來啟動 ResultActivity
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("有進來onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        // 確認是從 ResultActivity 返回的結果
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            System.out.println("有進來IF");
            // 清空當前列表並重新讀取資料
            personList.clear();
            readData(this);
            adapter.notifyDataSetChanged(); // 通知 Adapter 更新資料
        }
    }

    public void addData(Context context, String name, int age, String id) {
        // 取得 SharedPreferences 和 Editor
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 讀取已存在的 JSON 字串
        String jsonString = sharedPreferences.getString("data_list","[]");

        try {
            // 解析 JSON 字串為 JSONArray
            JSONArray jsonArray = new JSONArray(jsonString);

            // 創建新資料的 JSON 物件
            JSONObject newObject = new JSONObject();
            newObject.put("name", name);
            newObject.put("age", age);
            newObject.put("id", id);

            // 將新資料加入 JSONArray
            jsonArray.put(newObject);

            // 儲存更新後的 JSON 列表
            editor.putString("data_list",jsonArray.toString());
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void readData(Context context) {
        // 取得 SharedPreferences 和 Editor
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // 讀取已存在的 JSON 字串
        String jsonString = sharedPreferences.getString("data_list","[]");

        try {
            // 解析 JSON 字串為 JSONArray
            JSONArray jsonArray = new JSONArray(jsonString);

            // 遍歷 JSONArray
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                int age = jsonObject.getInt("age");
                String id = jsonObject.getString("id"); // 讀取 ID

                // 從 SharedPreferences 中讀取資料，並更新到 personList
                personList.add(new Person(name, age, id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void removeData(Context context, String idToRemove) {
        // 取得 SharedPreferences 和 Editor
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 讀取儲存的 JSON 字串
        String jsonString = sharedPreferences.getString("data_list", "[]");

        try {
            // 解析 JSON 字串為 JSONArray
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONArray updateArray = new JSONArray();

            // 遍歷 JSONArray，保留不符合條件的項目
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");

                // 如果 id 不符合要刪除的條件，則保留該項
                if (!id.equals(idToRemove)) {
                    updateArray.put(jsonObject);
                }
            }

            // 儲存更新後的 JSON 列表
            editor.putString("data_list", updateArray.toString());
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveData(Context context, ArrayList<Person> personList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        JSONArray jsonArray = new JSONArray();
        for(Person person : personList) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("id", person.getId());
                jsonObject.put("name", person.getName());
                jsonObject.put("age", person.getAge());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }

        editor.putString("data_list", jsonArray.toString());
        editor.apply();
    }

}
