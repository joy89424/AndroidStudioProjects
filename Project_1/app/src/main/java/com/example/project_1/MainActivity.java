package com.example.project_1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //
    private EditText EditText1;
    private String input = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // 載入佈局
        setContentView(R.layout.activity_main);

        // 初始化視圖必須在 setContentView() 之後
        EditText1 = findViewById(R.id.edittext_1);
        Button Button1 = findViewById(R.id.button1);
        Button Button2 = findViewById(R.id.button2);
        Button Button3 = findViewById(R.id.button3);
        Button Button4 = findViewById(R.id.button4);
        Button Button5 = findViewById(R.id.button5);
        Button Button6 = findViewById(R.id.button6);
        Button Button7 = findViewById(R.id.button7);
        Button Button8 = findViewById(R.id.button8);
        Button Button9 = findViewById(R.id.button9);
        Button Button10 = findViewById(R.id.button10);
        Button Button11= findViewById(R.id.button11);
        Button Button12= findViewById(R.id.button12);
        Button Button13 = findViewById(R.id.button13);
        Button Button14 = findViewById(R.id.button14);
        Button Button15 = findViewById(R.id.button15);
        Button Button16 = findViewById(R.id.button16);


        // 設置邊界調整
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("1");
            }
        });

        Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("2");
            }
        });

        Button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("3");
            }
        });

        Button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("+");
            }
        });

        Button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("4");
            }
        });

        Button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("5");
            }
        });

        Button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("6");
            }
        });

        Button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("-");
            }
        });

        Button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("7");
            }
        });

        Button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("8");
            }
        });

        Button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("9");
            }
        });

        Button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("*");
            }
        });

        Button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("C");
            }
        });

        Button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("0");
            }
        });

        Button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("=");
            }
        });

        Button16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendNumber("/");
            }
        });
    }

    private void appendNumber(String number) {
        if (number.equals("C")) {
            // 清除輸入
            input = "";
            EditText1.setText(input);
        } else if (number.equals("=")) {
            // 如果輸入不為空
            if (!input.isEmpty()) {
                // 檢查最後一個字符是否是運算符號
                if (input.endsWith("+") || input.endsWith("-") || input.endsWith("*") || input.endsWith("/")) {
                    // 刪除最後一個運算符號
                    input = input.substring(0, input.length()-1);
                }
                // 計算結果
                double result = calculateResult(input);
                // 顯示結果
                input = String.valueOf(result); // 將結果作為新的輸入
                EditText1.setText(input);
            }
        } else if (input.isEmpty()) {
            // 不允許以運算符號開頭
            if (!(number.equals("+") || number.equals("-") || number.equals("*") || number.equals("/"))) {
                input += number;
                EditText1.setText(input);
                EditText1.setSelection(input.length());
            }
        }
        else {
            // 如果最後一個字符是運算符號且輸入了新的運算符號，替換它
            if ((input.endsWith("+") || input.endsWith("-") || input.endsWith("*") || input.endsWith("/")) && (number.equals("+") || number.equals("-") || number.equals("*") || number.equals("/"))) {
                input = input.substring(0, input.length()-1);
            }
            input += number;
            EditText1.setText(input);
            EditText1.setSelection(input.length());
        }
    }

    private double calculateResult(String input) {
        try {
            // 先處理乘除
            String[] addSubtractParts = input.split("(?=[+-])|(?<=[+-])"); // 以加減分割字串，但保留運算符號
            double result = 0;
            double currentValue = 0;
            String currentOperation = "+"; // 初始化為加法

            for (String part : addSubtractParts) {
                System.out.println(part);
                if (part.equals("+") || part.equals("-")) {
                    currentOperation = part; // 更新當前運算符
                } else {
                    // 處理乘除
                    double value = calculateMultiplyDivide(part.trim());

                    if (currentOperation.equals("+")) {
                        result += value;
                    } else if (currentOperation.equals("-")) {
                        result -= value;
                    }
                }
            }

            return result;
        } catch (Exception e) {
            Log.e("", "Error calculating result", e);
            return 0; // 返回0表示錯誤
        }
    }

    // 處理乘除法
    private double calculateMultiplyDivide(String input) {
        String[] multiplyDivideParts = input.split("(?=[*/])|(?<=[*/])"); // 以乘除分割字串，但保留運算符號
        double result = Double.parseDouble(multiplyDivideParts[0].trim()); // 初始化為第一個數字

        for (int i = 1; i < multiplyDivideParts.length; i += 2) {
            String operator = multiplyDivideParts[i];
            double value = Double.parseDouble(multiplyDivideParts[i + 1].trim());

            if (operator.equals("*")) {
                result *= value;
            } else if (operator.equals("/")) {
                result /= value;
            }
        }

        return result;
    }
}