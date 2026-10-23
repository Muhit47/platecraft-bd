package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.PlateEditorScreen
import com.example.ui.PlateEditorViewModel
import com.example.ui.theme.PlateCraftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlateCraftTheme {
                val viewModel: PlateEditorViewModel = viewModel()
                PlateEditorScreen(viewModel = viewModel)
            }
        }
    }
}
