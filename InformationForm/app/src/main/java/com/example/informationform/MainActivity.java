package com.example.informationform;

import android.os.Bundle;
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
        personList.add(new Person("張三", 25));
        personList.add(new Person("李四", 30));

        adapter = new PersonAdapter(personList);
        recyclerView.setAdapter(adapter);
    }
}