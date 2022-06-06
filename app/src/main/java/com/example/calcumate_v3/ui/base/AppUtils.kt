package com.example.calcumate_v3.ui.base

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.calcumate_v3.ui.screen.capturedphoto.CapturedPhotoViewState

@Composable
//REPLACE WITH OOTB ICON SEE CAPTUREPHOTOSCREEN - or need it for home screen alpha??
fun CreateIcon(contentDesc: String, iconImage: Int, iconSize: Dp, iconAlpha: Float){
    Icon(
        painter = painterResource(iconImage),
        contentDescription = contentDesc,
        modifier = Modifier
            .alpha(iconAlpha)
            .size(iconSize)
    )
}

@Composable
fun CreateDivider(color: Color, thickness: Dp, modifier: Modifier){
    Divider(color = color, thickness = thickness, modifier = modifier)

}
