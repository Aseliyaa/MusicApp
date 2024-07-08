package com.example.musicapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


import androidx.navigation.compose.rememberNavController
import com.example.musicapp.ui.screen.home.ArtistsByGenreScreen
import com.example.musicapp.ui.screen.home.HomeScreen
import com.example.musicapp.ui.screen.home.components.ArtistsGrid
import com.example.musicapp.ui.screen.home.components.CommonScreenComponents
import com.example.musicapp.ui.screen.welcome.WelcomeScreen

enum class AppScreen {
    WELCOME,
    HOME,
}

@Composable
fun ScreenNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreen.WELCOME.name) {
        composable(AppScreen.WELCOME.name){
            WelcomeScreen(navController)
        }
        composable(AppScreen.HOME.name){
            HomeScreen(navController)
        }
        composable("artists/{genreId}"){ backStackEntry ->
            val genreId =  backStackEntry.arguments?.getString("genreId")
//            ArtistsByGenreScreen(navController, genreId)
            CommonScreenComponents(navHostController = navController, content = {
                ArtistsByGenreScreen(navController, genreId)
            })
        }
    }
}