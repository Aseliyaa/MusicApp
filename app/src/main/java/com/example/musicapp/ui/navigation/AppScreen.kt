package com.example.musicapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


import androidx.navigation.compose.rememberNavController
import com.example.musicapp.ui.screen.home.ArtistDetailsScreen
import com.example.musicapp.ui.screen.home.components.ArtistsByGenreScreen
import com.example.musicapp.ui.screen.home.HomeViewModel

import com.example.musicapp.ui.screen.home.components.ArtistsScreen
import com.example.musicapp.ui.screen.home.CommonScreenComponents

import com.example.musicapp.ui.screen.home.components.GenresScreen
import com.example.musicapp.ui.screen.home.components.TracksByAlbumIdScreen
import com.example.musicapp.ui.screen.home.components.TracksScreen
import com.example.musicapp.ui.screen.player.PlayerScreen

import com.example.musicapp.ui.screen.welcome.WelcomeScreen
import com.example.musicapp.ui.viewmodel.AppViewModelProvider

enum class AppScreen {
    WELCOME,
    HOME,
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ScreenNavigation(
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val navController = rememberNavController()
    val selectedItemIndex = viewModel.selectedItemIndex.collectAsState()

    NavHost(navController = navController, startDestination = AppScreen.WELCOME.name) {
        composable(AppScreen.WELCOME.name) {
            WelcomeScreen(navController)
        }
        composable(AppScreen.HOME.name) {
            CommonScreenComponents(
                selectedItemIndex,
                navHostController = navController
            ) { GenresScreen(navHostController = navController) }
        }
        composable("artists/{genreId}") { backStackEntry ->
            val genreId = backStackEntry.arguments?.getString("genreId")
            if (genreId != null) {
                CommonScreenComponents(
                    selectedItemIndex,
                    navHostController = navController
                ) { ArtistsByGenreScreen(navController, genreId) }
            }
        }
        composable("home/{menuIndex}") { backStackEntry ->
            val menuIndex = backStackEntry.arguments?.getString("menuIndex")?.toInt()
            if (menuIndex != null) {
                viewModel.selectItem(menuIndex)

                when (menuIndex) {
                    0 -> CommonScreenComponents(
                        selectedItemIndex,
                        navHostController = navController,
                    ) { GenresScreen(navHostController = navController) }

                    1 -> CommonScreenComponents(
                        selectedItemIndex,
                        navHostController = navController
                    ) { ArtistsScreen(navHostController = navController) }

                    2 -> CommonScreenComponents(
                        selectedItemIndex,
                        navHostController = navController
                    ) { TracksScreen(navHostController = navController) }
                }
            }
        }
        composable("artist/{artistId}") { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId")
            if (artistId != null) {
                CommonScreenComponents(
                    selectedItemIndex = selectedItemIndex,
                    navHostController = navController
                ) {
                    ArtistDetailsScreen(navController, artistId)
                }
            }
        }
        composable("album/{albumId}") { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId")
            if (albumId != null){
                CommonScreenComponents(
                    selectedItemIndex = selectedItemIndex,
                    navHostController = navController
                ) {
                    TracksByAlbumIdScreen(navHostController = navController, albumId)
                }
            }
        }
        composable("track/{trackId}"){ backStackEntry ->
            val trackId = backStackEntry.arguments?.getString("trackId")
            if (trackId != null){
                PlayerScreen(navHostController = navController, trackId = trackId)
            }
            
        }
    }
}