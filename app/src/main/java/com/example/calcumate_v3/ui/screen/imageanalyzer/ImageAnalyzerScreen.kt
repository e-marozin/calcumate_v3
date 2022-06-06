package com.example.calcumate_v3.ui.screen.imageanalyzer

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.calcumate_v3.R
import com.example.calcumate_v3.ui.base.CreateIcon
import com.google.common.util.concurrent.ListenableFuture

//ImageAnalyzerScreen composables/ui lives here
//RENAME to CameraScreen, CameraViewModel
@Composable
fun ImageAnalyzerScreen(navController: NavController) {
    val imageAnalyzerViewModel: ImageAnalyzerViewModel = viewModel()
    //General
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    //Start Camera
    imageAnalyzerViewModel.initCamera(context,lifeCycleOwner)
    ImageAnalyzerScreenContent(imageAnalyzerViewModel, navController)
}

@Composable
private fun ImageAnalyzerScreenContent(viewModel: ImageAnalyzerViewModel, navController: NavController){
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
                CreateIcon(
                    "Take Photo",
                    R.drawable.ic_baseline_photo_camera_24,
                    dimensionResource(R.dimen.icon_min),
                    100f
                    )
            }
        }
    }
}

@Composable
private fun CameraPreview(viewModel: ImageAnalyzerViewModel) {
    //Inflate view
    AndroidView(
        factory = { viewModel.setPreviewView() },
        modifier = Modifier.fillMaxSize()
    ) {
        //PreviewView logic?
    }
}
