package com.example.androidproject1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.androidproject1.ui.theme.AndroidProject1Theme
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidProject1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Joy",
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var numClick by remember { mutableStateOf(0) }
    var text by remember { mutableStateOf("$name click $numClick times.") }
    // 使用 Column 排列 Text 和 Button

    androidx.compose.foundation.layout.Column(
        modifier = modifier.padding(start = 20.dp, top = 50.dp)
    ) {
        // 顯示按鈕，按下按鈕後改變文字
        Button(
            onClick = {
                numClick += 1
                text = "$name click $numClick times."
            },
            modifier = modifier.padding(bottom = 8.dp)
        ) {
            Text(text = "Click Me")
        }

        // 顯示初始文字
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidProject1Theme {
        Greeting("Android")
    }
}