package com.example.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.musicapp.ui.navigation.ScreenNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }
    }


    @Composable
    private fun MainScreen() {
        ScreenNavigation()
    }
}


