package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ui.BankingApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BankingViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: BankingViewModel by viewModels {
    ViewModelProvider.AndroidViewModelFactory.getInstance(application)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        BankingApp(viewModel = viewModel)
      }
    }
  }
}
