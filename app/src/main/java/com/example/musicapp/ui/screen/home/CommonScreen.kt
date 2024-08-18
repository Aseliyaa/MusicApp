package com.example.musicapp.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun CommonScreenComponents(
    selectedItemIndex: State<Int>,
    navHostController: NavHostController, content: @Composable () -> Unit,
) {
    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.background(Color.White),
        topBar = {
            Column(
                modifier = Modifier
                    .padding(20.dp)

            ) {
                Spacer(modifier = Modifier.height(12.dp))
                HomeMenuRow(selectedItemIndex, navHostController)
                Spacer(modifier = Modifier.height(20.dp))
                HeaderRow()
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
            ) {
                content()
            }
        },
        bottomBar = {

        }
    )
}
