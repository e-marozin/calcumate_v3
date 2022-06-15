package com.example.calcumate_v3.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

//SettingScreen composables/ui lives here
@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel: SettingsViewModel = viewModel()
    SettingScreenContent(viewModel, navController)
}

@Composable
private fun SettingScreenContent(viewModel: SettingsViewModel, navController: NavController) {
    val viewState by viewModel.viewState.collectAsState()
}
