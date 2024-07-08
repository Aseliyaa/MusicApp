package com.example.musicapp.ui.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.musicapp.ui.screen.home.HeaderRow
import com.example.musicapp.ui.screen.home.HomeMenuRow
import com.example.musicapp.ui.screen.home.HomeViewModel
import com.example.musicapp.ui.screen.home.ProfileAndSearchRow
import com.example.musicapp.ui.screen.home.SelectedItemScreen
import com.example.musicapp.ui.viewmodel.AppViewModelProvider

@Composable
fun CommonScreenComponents(
    navHostController: NavHostController, content: @Composable () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val selectedItemIndex by viewModel.selectedItemIndex.collectAsState()
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .padding(20.dp)

            ) {
                ProfileAndSearchRow()
                Spacer(modifier = Modifier.height(12.dp))
                HomeMenuRow(
                    selectedItemIndex = selectedItemIndex,
                    onItemSelected = { viewModel.selectItem(it) }
                )
                Spacer(modifier = Modifier.height(20.dp))
                HeaderRow()
            }
        },
        content = { padding ->
            Box(modifier = Modifier
                .padding(padding)
                .background(Color.White)) {
                content()
            }
        },
        bottomBar = {

        }
    )
}
