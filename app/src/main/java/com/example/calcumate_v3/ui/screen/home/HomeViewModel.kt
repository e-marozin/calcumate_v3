package com.example.calcumate_v3.ui.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//Business logic of HomeScreen lives here - state hoisting, make composables stateless (value: Type, onValueChange: (Type) -> Unit)
data class HomeViewState (
    val showGetStartedMenu: MutableState<Boolean> = mutableStateOf(false),
    // Declaring a Boolean value to store bottom sheet collapsed state
)

class HomeViewModel : ViewModel (){
    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState

    init{
        //Set values in view state
//        _viewState.value = _viewState.value.copy()
    }

    //Is there an OOTB fun for this?
    //should it just directly toggle viewstate value rather than param ??
    fun toggleBoolean(bool: MutableState<Boolean>){
        bool.value = !bool.value
    }

}