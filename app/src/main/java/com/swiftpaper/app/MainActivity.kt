package com.swiftpaper.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.swiftpaper.app.navigation.SwiftPaperNavHost
import com.swiftpaper.app.ui.theme.SwiftPaperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as SwiftPaperApp
        // Yandex App Open Ad (реклама при входе) — cold start.
        app.adsManager.onColdStart(this)
        setContent {
            SwiftPaperTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SwiftPaperNavHost()
                }
            }
        }
    }
}
