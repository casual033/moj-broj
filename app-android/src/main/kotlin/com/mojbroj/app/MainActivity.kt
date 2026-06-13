package com.mojbroj.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mojbroj.app.ui.MojBrojApp
import com.mojbroj.app.ui.MojBrojViewModel
import com.mojbroj.app.ui.MojBrojViewModelFactory
import com.mojbroj.app.ui.theme.MojBrojTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MojBrojTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val vm: MojBrojViewModel = viewModel(
                        factory = MojBrojViewModelFactory(applicationContext)
                    )
                    MojBrojApp(viewModel = vm)
                }
            }
        }
    }
}
