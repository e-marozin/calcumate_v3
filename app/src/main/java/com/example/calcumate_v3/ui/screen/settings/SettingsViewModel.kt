package com.example.calcumate_v3.ui.screen.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//Business logic of SettingScreen lives here - state hoisting, make composables stateless (value: Type, onValueChange: (Type) -> Unit)
data class SettingsViewState (
    val placeholder: String = ""
)

class SettingsViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(SettingsViewState())
    val viewState: StateFlow<SettingsViewState> = _viewState
}