package com.example.calcumate_v3.ui.screen.home

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//Business logic of HomeScreen lives here - state hoisting, make composables stateless (value: Type, onValueChange: (Type) -> Unit)
data class HomeViewState (
    val showGetStartedMenu: MutableState<Boolean> = mutableStateOf(false),
    val imageUri: MutableState<Uri>? = null
)

class HomeViewModel : ViewModel (){
    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState

    init{
        //Set values in view state
    }

    //!!!UPLIFT: is there an OOTB version of this?
    fun toggleBoolean(bool: MutableState<Boolean>){
        bool.value = !bool.value
    }

    //!!!UPLIFT: cannot set value directly in Screen, hitting issues with nullable
    fun setImageUri(uri: Uri){
        _viewState.value = _viewState.value.copy(imageUri = mutableStateOf(uri))
    }
}