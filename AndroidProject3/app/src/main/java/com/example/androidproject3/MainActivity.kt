package com.example.androidproject3

import android.os.Bundle
import android.text.Editable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidproject3.ui.theme.AndroidProject3Theme
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberImagePainter
import coil.compose.AsyncImage
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidProject3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    // 定義 scrollState
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(top = 50.dp, start = 20.dp)
            .verticalScroll(scrollState)      // 添加垂直滾動
    ){
        SimpleText()                // 顯示簡單的文字內容。
        SimpleButton()              // 按鈕元件，用於用戶點擊互動。
        EditableTextField()         // 可編輯的文本輸入框，通常用於表單或用戶輸入。
        SimpleImage()               // 顯示圖片，可以是本地資源或是網絡圖片。
        SimpleCard()                // 用來顯示包含內容的卡片，帶有陰影和圓角。
    }
}

@Composable
fun SimpleText() {
    Text(
        text = "1.Text",
        fontWeight = FontWeight.Bold
    )

    Text(text = "This is a text",)
}

@Composable
fun SimpleButton() {
    Text(
        text = "2.Button",
        modifier = Modifier.padding(top = 10.dp),
        fontWeight = FontWeight.Bold
    )

    var ClickNum by remember { mutableStateOf(0) }
    Button(
        onClick = { ClickNum += 1 }
    ) {
        Text("按了 $ClickNum 下了")
    }
}

@Composable
fun EditableTextField() {
    Text(
        text = "3.TextField",
        modifier = Modifier.padding(top = 10.dp),
        fontWeight = FontWeight.Bold
    )

    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { newText -> text = newText },
        label = { Text("Enter text") }
    )
}

@Composable
fun SimpleImage() {
    Text(
        text = "4.Image",
        modifier = Modifier.padding(top = 10.dp),
        fontWeight = FontWeight.Bold
    )

    Text(text = "<方法1>")
    Image(
        painter = painterResource(R.drawable.my_image),
        contentDescription = "Sample Image"
    )

    // 使用 Coil 加載網路圖片
    Text(text = "<方法2>")
    Image(
        painter = rememberImagePainter("https://www.biosmonthly.com/storage/upload/article/tw_article_coverphoto_20240315155001_f5c.jpg"),
        contentDescription = "Example Image",
        modifier = Modifier
    )

    // 使用 Coil 加載網路圖片
    Text(text = "<方法3>")
    AsyncImage(
        model = "https://japanshopping.org/files/shopping_article_contents/-_2023-11-13-041605.JPG",
        contentDescription = "Sample Image 3",
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Composable
fun SimpleCard() {
    Card(
        modifier = Modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)  // 使用 `CardDefaults.cardElevation`
    ) {
        Text(text = "This is a card")
    }
    }
