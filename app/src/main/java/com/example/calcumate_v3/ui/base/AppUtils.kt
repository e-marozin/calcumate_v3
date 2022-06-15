package com.example.calcumate_v3.ui.base

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun CreateDivider(color: Color, thickness: Dp, modifier: Modifier){
    Divider(color = color, thickness = thickness, modifier = modifier)
}
