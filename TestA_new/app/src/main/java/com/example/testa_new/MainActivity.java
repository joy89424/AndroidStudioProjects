package com.example.testa_new;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.WindowInsetsController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_READ_PERMISSION_CODE = 100;

    private ArrayList<Uri> imageUriList = new ArrayList<>();
    private String[] spinnerArray = new String[]{"Fragment", "DialogFragment", "Activity"};;

    private Button buttonAdd;
    private EditText editText;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private Spinner spinner;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(Color.parseColor("#404040"));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 設置狀態欄字體顏色為白色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            //getWindow().getInsetsController().setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS); // 黑色
        } else {
            getWindow().getDecorView().setSystemUiVisibility(0); // 清除 SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 標誌
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 黑色
        }

        // 恢復保存的狀態
        if (savedInstanceState != null) {
            imageUriList = savedInstanceState.getParcelableArrayList("imageUriList");
        }

        // 初始化
        buttonAdd = findViewById(R.id.buttonAdd);
        editText = findViewById(R.id.editText);
        recyclerView = findViewById(R.id.recyclerView);
        spinner = findViewById(R.id.spinner);

        imageAdapter = new ImageAdapter(imageUriList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(imageAdapter);

        buttonAdd.setOnClickListener(v -> {
            Toast.makeText(this, "AddPicture", Toast.LENGTH_SHORT).show();
            checkPermissionAndAddImage();
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, spinnerArray);
        // 指定自定義的下拉菜單佈局樣式
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData() != null) {
                String output = "";
                Uri selectedImageUri = result.getData().getData();
                imageUriList.add(selectedImageUri);
                for (Uri uri : imageUriList) {
                    output = output + "\n" + uri;
                }

                // 刪除第一排的換行
                if (output.substring(0, 1).equals("\n")) {
                    output = output.substring(1, output.length());
                }

                // 將圖片位址顯示在 editText 上
                editText.setText(output);
            } else {
                Toast.makeText(this, "操作取消", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkPermissionAndAddImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14 及以上
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_READ_PERMISSION_CODE);
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            // Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_READ_PERMISSION_CODE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0 - 12
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_PERMISSION_CODE);
            }
        } else {
            // Android 6.0 以下，無需請求權限
            launchImagePicker();
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case STORAGE_READ_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 使用者已授予存儲權限
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        Toast.makeText(this, "存取權限全部允許", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "存取權限已授予", Toast.LENGTH_SHORT).show();
                    }
                    launchImagePicker();
                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                        // 使用者已授予有限存取
                        Toast.makeText(this, "您已選擇有限存取，請選擇全部允許以繼續使用該功能", Toast.LENGTH_SHORT).show();
                        // 重新請求完整存取的權限
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_READ_PERMISSION_CODE);
                    } else {
                        // 使用者拒絕存儲權限
                        Toast.makeText(this, "存取權限被拒絕", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("imageUriList", imageUriList);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            imageUriList = savedInstanceState.getParcelableArrayList("imageUriList");
            if (imageUriList == null) imageUriList = new ArrayList<>();
        }
    }
}
