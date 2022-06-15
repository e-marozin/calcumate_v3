package com.example.calcumate_v3.ui.screen.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.calcumate_v3.R
import com.example.calcumate_v3.ui.base.CreateDivider
import com.example.calcumate_v3.ui.navigation.Screens

//HomeScreen composables/ui lives here
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    HomeScreenContent(viewModel, navController)
}

@Composable
private fun HomeScreenContent(viewModel: HomeViewModel, navController: NavController){
    val viewState by viewModel.viewState.collectAsState()

    //!!!UPLIFT: move to ViewModel?
    //Init launcher for loading gallery
    val launcher  = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.setImageUri(it) } //!!!UPLIFT: set directly without method???
    }

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
                    onClick = {navController.navigate(Screens.CameraScreen.route)}
                )
            }
            item{
                CreateButton(
                    "Gallery",
                    R.drawable.ic_baseline_insert_photo_24,
                    dimensionResource(R.dimen.icon_2x),
                    0.95f,
                    dimensionResource(R.dimen.spacing_min),
                    onClick = { launcher.launch("image/*") }
                )
            }
            item{
                CreateButton(
                    "Settings",
                    R.drawable.ic_baseline_settings_24,
                    dimensionResource(R.dimen.icon_2x),
                    0.95f,
                    dimensionResource(R.dimen.spacing_min),
                    onClick = {navController.navigate(Screens.SettingsScreen.route)}
                )
            }
            item{
                Box(modifier = Modifier.clickable { viewModel.toggleBoolean(viewState.showGetStartedMenu) }){
                    CreateText( //header
                        "Get Started",
                        MaterialTheme.typography.body2,
                        dimensionResource(R.dimen.spacing_3x),
                        dimensionResource(R.dimen.spacing_min)
                    )
                }
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

        //TO-DO: Redirect to ImageAnalyzerScreen after image selection
        //!!!BUG: URI ACCESS LIMITED, CANNOT REDIRECT WITH ENCODEDURL WITH EXTENDING ACCESS TO URI
        viewState.imageUri?.let{
//            GlideImage(viewState.imageUri!!.value)
//            Log.d("!!!imageUriHOME", "${viewState.imageUri!!.value}")
//            val encodedUrl = URLEncoder.encode(viewState.imageUri!!.value.toString(), StandardCharsets.UTF_8.toString())
//            Log.d("!!!encodedUrlHOME", encodedUrl)
//            navController.navigate(Screens.ImageAnalyzerScreen.route + "/${viewState.imageUri!!.value}")

//            navController.navigate(Screens.ImageAnalyzerScreen.route + "/$encodedUrl")
        }
    }

}

@Composable
fun CreateText(contentLabel: String, textStyle: TextStyle, topSpacerHeight: Dp, bottomSpacerHeight: Dp){
    Spacer(Modifier.height(topSpacerHeight))
    Text(
        text = contentLabel,
        style = textStyle, //!!!UPLIFT: Replace with theme
        textAlign = TextAlign.Center,
        color = colorResource(R.color.pink_3) //!!!UPLIFT: Replace with theme
    )
    Spacer(Modifier.height(bottomSpacerHeight))
}

@Composable
fun CreateButton(contentDesc: String, iconImage: Int, iconDimen: Dp, iconAlpha: Float, spacerHeight: Dp, onClick: () -> Unit = {}){
    Spacer(Modifier.height(spacerHeight))
    Button(
        onClick = onClick,
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
            Icon(
                painter = painterResource(iconImage),
                contentDescription = contentDesc,
                modifier = Modifier
                    .alpha(iconAlpha)
                    .size(iconDimen)
            )
        }
    }
    Spacer(Modifier.height(spacerHeight))
}