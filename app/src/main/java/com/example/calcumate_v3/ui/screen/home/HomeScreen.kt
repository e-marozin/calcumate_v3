package com.example.calcumate_v3.ui.screen.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.calcumate_v3.R
import com.example.calcumate_v3.ui.base.CreateDivider
import com.example.calcumate_v3.ui.base.CreateIcon
import com.example.calcumate_v3.ui.navigation.Screens
import kotlinx.coroutines.launch

//HomeScreen composables/ui lives here
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    HomeScreenContent(viewModel, navController)
}

@Composable
private fun HomeScreenContent(viewModel: HomeViewModel, navController: NavController){
    val viewState by viewModel.viewState.collectAsState()

    Box (
        Modifier
            .background(colorResource(R.color.pink_0))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        LazyColumn(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            item{
                CreateText( //header
                    "Calcu-mate",
                    MaterialTheme.typography.h1,
                    dimensionResource(R.dimen.spacing_3x),
                    dimensionResource(R.dimen.spacing_3x)
                    )
            }
            item{
                CreateButton(
                    "Camera",
                    R.drawable.ic_baseline_photo_camera_24,
                    dimensionResource(R.dimen.icon_2x),
                    0.95f,
                    dimensionResource(R.dimen.spacing_min),
                    navController
                )
            }
            item{
                CreateButton(
                    "Camera",
                    R.drawable.ic_baseline_insert_photo_24,
                    dimensionResource(R.dimen.icon_2x),
                    0.95f,
                    dimensionResource(R.dimen.spacing_min),
                    navController
                )
            }
            item{
                CreateButton(
                    "Camera",
                    R.drawable.ic_baseline_settings_24,
                    dimensionResource(R.dimen.icon_2x),
                    0.95f,
                    dimensionResource(R.dimen.spacing_min),
                    navController
                )
            }
            item{
                CreateText( //header
                    "Get Started",
                    MaterialTheme.typography.body1,
                    dimensionResource(R.dimen.spacing_3x),
                    dimensionResource(R.dimen.spacing_min)
                )
            }
            item{
                CreateDivider(
                    colorResource(R.color.grey_2),
                    dimensionResource(R.dimen.thickness_min),
                    Modifier
                        .width(dimensionResource(R.dimen.footer_length))
                )
            }
        }
    }

}

@Composable
fun CreateText(contentLabel: String, textStyle: TextStyle, topSpacerHeight: Dp, bottomSpacerHeight: Dp){
    Spacer(Modifier.height(topSpacerHeight))
    Text(
        text = contentLabel,
        style = textStyle, //replace with theme?
        textAlign = TextAlign.Center,
        color = colorResource(R.color.pink_3) //replace with theme?
    )
    Spacer(Modifier.height(bottomSpacerHeight))
}

//MOVE TO APPUTILS?
@Composable
fun CreateButton(contentDesc: String, iconImage: Int, iconDimen: Dp, iconAlpha: Float, spacerHeight: Dp, navController: NavController){
    Spacer(Modifier.height(spacerHeight))
    Button(
        onClick = {navController.navigate(Screens.ImageAnalyzerScreen.route)},
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(R.color.white),
            contentColor = colorResource(R.color.pink_3)
        ),
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_min))
            .size(dimensionResource(R.dimen.btn_max)),
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            CreateIcon(
                contentDesc,
                iconImage,
                iconDimen,
                iconAlpha
            )
        }

    }
    Spacer(Modifier.height(spacerHeight))
}
