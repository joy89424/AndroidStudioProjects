package com.example.project_4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SecondActivity extends AppCompatActivity {

    private boolean isFirstFragmentLoaded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // 加載第一個 Fragment 按鈕
        loadFragment(new FirstFragment());

        // 切換 Fragment 按鈕
        Button buttonSwitchFragment = findViewById(R.id.buttonSwitchFragment);
        buttonSwitchFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFirstFragmentLoaded) {
                    loadFragment(new SecondFragment());
                } else {
                    loadFragment(new FirstFragment());
                }
                isFirstFragmentLoaded = !isFirstFragmentLoaded;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}