package com.example.musicapp.ui.screen.welcome


import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.musicapp.ui.navigation.AppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WelcomeViewModel : ViewModel() {
    var _currentPage = MutableStateFlow(0)
    private val currentPage: StateFlow<Int> = _currentPage

    suspend fun scrollNext(pagerState: PagerState, pageCount: Int, navHostController: NavHostController) {
        if (currentPage.value != pageCount - 1) {
            pagerState.animateScrollToPage(_currentPage.value + 1)
        } else {
            navHostController.navigate(AppScreen.HOME.name)
        }
    }
}