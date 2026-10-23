package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.PlateEditorScreen
import com.example.ui.PlateEditorViewModel
import com.example.ui.SplashScreen
import com.example.ui.theme.PlateCraftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlateCraftTheme {
                var isSplashScreenVisible by remember { mutableStateOf(true) }

                Crossfade(
                    targetState = isSplashScreenVisible,
                    animationSpec = tween(durationMillis = 400),
                    label = "splash_transition"
                ) { showSplash ->
                    if (showSplash) {
                        SplashScreen(
                            onTimeoutOrSkip = { isSplashScreenVisible = false }
                        )
                    } else {
                        val viewModel: PlateEditorViewModel = viewModel()
                        PlateEditorScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
