package com.example.calcumate_v3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.calcumate_v3.ui.screen.capturedphoto.CapturedPhotoScreen
import com.example.calcumate_v3.ui.screen.home.HomeScreen
import com.example.calcumate_v3.ui.screen.imageanalyzer.ImageAnalyzerScreen
import java.net.URLDecoder

//NavHost lives here

sealed class Screens(val route: String) {
    object HomeScreen : Screens("home")
    object ImageAnalyzerScreen : Screens("imageanalyzer")
    object CapturedPhotoScreen : Screens("capturedphoto")
}

@Composable
fun Navigation(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route){

        composable(route = Screens.HomeScreen.route){
            HomeScreen(navController)
        }
        composable(route = Screens.ImageAnalyzerScreen.route) {
            ImageAnalyzerScreen(navController)
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