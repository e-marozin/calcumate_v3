package com.example.calcumate_v3.ui.screen.camera

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.calcumate_v3.R

//ImageAnalyzerScreen composables/ui lives here
@Composable
fun CameraScreen(navController: NavController) {
    val cameraViewModel: CameraViewModel = viewModel()
    //General
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    //Start Camera
    cameraViewModel.initCamera(context,lifeCycleOwner)
    CameraScreenContent(cameraViewModel, navController)
}

@Composable
private fun CameraScreenContent(viewModel: CameraViewModel, navController: NavController){
    val viewState by viewModel.viewState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()){
        CameraPreview(viewModel)

        //Take picture
        Column(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(dimensionResource(R.dimen.padding_3x))){
            //Camera button
            Button(onClick = {viewModel.takePhoto(navController)},
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.white),
                    contentColor = colorResource(R.color.pink_3)
                )){
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_photo_camera_24),
                    contentDescription = "Camera",
                    modifier = Modifier
                        .alpha(100f)
                        .size(dimensionResource(R.dimen.icon_min))
                )
            }
        }
    }
}

@Composable
private fun CameraPreview(viewModel: CameraViewModel) {
    //Inflate view
    AndroidView(
        factory = { viewModel.setPreviewView() },
        modifier = Modifier.fillMaxSize()
    ) {
        //!!!: No logic required here, way to optimize view inflation?
    }
}
