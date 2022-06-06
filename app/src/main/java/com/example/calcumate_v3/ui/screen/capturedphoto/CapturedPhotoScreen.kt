package com.example.calcumate_v3.ui.screen.capturedphoto

import android.net.Uri
import android.util.Log
import androidx.annotation.ColorRes
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.calcumate_v3.R
import com.skydoves.landscapist.glide.GlideImage

//CapturedPhotoScreen composables/ui lives here
@Composable
fun CapturedPhotoScreen(navController: NavController, photoUriAsString: String?) {
    // Variables
    val viewModel: CapturedPhotoViewModel = viewModel()
    val photoUri = photoUriAsString!!.toUri()
    val context = LocalContext.current

    //Init View
    viewModel.initView(context,photoUri)
    CapturedPhotoScreenContent(viewModel, navController, photoUri)
}

@Composable
private fun CapturedPhotoScreenContent(viewModel: CapturedPhotoViewModel, navController: NavController, photoUri: Uri) {
    val viewState by viewModel.viewState.collectAsState()

    //Modifiers
    val btnModifier = Modifier
        .clip(CircleShape)
    //THEME????
    val btnColors =  ButtonDefaults.outlinedButtonColors(
        backgroundColor = colorResource(R.color.white),
        contentColor = colorResource(R.color.pink_3)
    )
    val btnBorderStroke = BorderStroke(dimensionResource(R.dimen.borderstroke_min),
        colorResource(R.color.pink_3))

    //Display captured photo
    Box(modifier = Modifier.fillMaxSize()){
        //Captured photo
        GlideImage(
            imageModel = photoUri,
            contentScale = ContentScale.FillWidth
        )

        //!---DEBUG MODE---!
        // !!!UPLIFT: Control visibility of toggle via global app setting
        Row(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter), horizontalArrangement = Arrangement.End){
            DebugModeToggle(viewState)
        }

        if(viewState.debugMode.value == true){
            OnDebugMode(viewState)
        }
        //!---DEBUG MODE ---!

        //Buttons
        Row(modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_3x))
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ){ //?? TO DO: Make buttons circular, clickable box instead??
            OutlinedButton( //Close image button
                border= btnBorderStroke,
                shape = CircleShape,
                colors = btnColors,
                contentPadding = PaddingValues(0.dp),
                onClick = {navController.popBackStack()}) { //go back
                Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_close_24) ,contentDescription = "Close image")
            }
            OutlinedButton( //Process image button
                border= btnBorderStroke,
                shape = CircleShape,
                colors = btnColors,
                contentPadding = PaddingValues(0.dp),
                onClick = {viewModel.textRecognition()}) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_check_24) ,contentDescription = "Process image")
            }
        }

        //Display X - slide x in
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = dimensionResource(id = R.dimen.padding_3x))
        ){
            Toast(viewState)
        }

    }
}

@Composable
private fun DebugModeToggle(viewState: CapturedPhotoViewState){
    Switch(
        checked = viewState.debugMode.value,
        onCheckedChange = { viewState.debugMode.value = it },
        colors = SwitchDefaults.colors(
            checkedThumbColor = colorResource(R.color.pink_3), //UPLIFT:replace with theme
            uncheckedThumbColor = colorResource(R.color.white),
            checkedTrackColor = colorResource(R.color.pink_3),
            uncheckedTrackColor = colorResource(R.color.pink_2)
        )
    )
}

@Composable
private fun OnDebugMode(viewState: CapturedPhotoViewState){
    Box(modifier = Modifier
        .fillMaxWidth()){
        Text(text = "debugLog: " + viewState.debugLog.value, color = colorResource(R.color.grey_2), modifier = Modifier.align(Alignment.TopStart))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Toast(viewState: CapturedPhotoViewState){
    AnimatedVisibility(
        visible = viewState.displayX.value,
        enter = slideInVertically(
            // Start the slide from 40 (pixels) above where the content is supposed to go, to
            // produce a parallax effect
            initialOffsetY = { -40 }
        ) + expandVertically(
            expandFrom = Alignment.Top
        ) + scaleIn(
            // Animate scale from 0f to 1f using the top center as the pivot point.
            transformOrigin = TransformOrigin(0.5f, 0f)
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
    ) {
        // Content that needs to appear/disappear goes here:
//            Text("Content to appear/disappear", Modifier.fillMaxWidth().requiredHeight(200.dp))
        ToastContent(viewState)
    }
}

@Composable
private fun ToastContent(viewState: CapturedPhotoViewState) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(colorResource(id = R.color.white))
            .border(1.dp, colorResource(id = R.color.pink_3), shape)
            .height(40.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if(viewState.total != 0){ //or null?
                Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_sentiment_very_satisfied_24) ,contentDescription = "Total")
                Text(text = "Total: ${viewState.total}")
            }else{
                Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_outline_sentiment_dissatisfied_24) ,contentDescription = "Total")
                Text(text = "Total: ${viewState.total}")
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}