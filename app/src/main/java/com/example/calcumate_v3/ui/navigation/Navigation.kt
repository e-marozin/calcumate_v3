package com.example.calcumate_v3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.calcumate_v3.ui.screen.camera.CameraScreen
import com.example.calcumate_v3.ui.screen.capturedphoto.CapturedPhotoScreen
import com.example.calcumate_v3.ui.screen.home.HomeScreen
import com.example.calcumate_v3.ui.screen.settings.SettingsScreen

//NavHost lives here
sealed class Screens(val route: String) {
    object HomeScreen : Screens("home")
    object CameraScreen : Screens("camera")
    object CapturedPhotoScreen : Screens("capturedphoto")
    object SettingsScreen : Screens("settings")
}

@Composable
fun Navigation(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route){

        composable(route = Screens.HomeScreen.route){
            HomeScreen(navController)
        }
        composable(route = Screens.CameraScreen.route) {
            CameraScreen(navController)
        }
        composable(route = Screens.SettingsScreen.route) {
            SettingsScreen(navController)
        }
        composable(
            route = Screens.CapturedPhotoScreen.route + "/{photoUriAsString}",
            arguments = listOf(navArgument(name = "photoUriAsString") {
                type = NavType.StringType
            })
        ) { navStackEntry ->
            CapturedPhotoScreen(
                navController,
                navStackEntry.arguments?.getString("photoUriAsString")
            )
        }
    }
}