package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.ui.AppViewModel
import com.example.ui.components.MainAppView
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: AppViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          MainAppView(
              viewModel = viewModel,
              modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }
}

@androidx.compose.runtime.Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

