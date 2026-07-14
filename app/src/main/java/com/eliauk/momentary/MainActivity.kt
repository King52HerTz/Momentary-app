package com.eliauk.momentary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eliauk.momentary.ui.MomentaryApp
import com.eliauk.momentary.ui.theme.MomentaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MomentaryTheme {
                MomentaryApp(viewModel())
            }
        }
    }
}
