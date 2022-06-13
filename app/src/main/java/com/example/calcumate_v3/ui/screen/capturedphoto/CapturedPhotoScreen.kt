package com.example.calcumate_v3.ui.screen.capturedphoto

import android.net.Uri
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

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

        if(viewState.debugMode.value){
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
//                onClick = {viewModel.textRecognition()}) {
                onClick = {viewModel.detectCurrencyTotal()}) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_check_24) ,contentDescription = "Process image")
            }
        }

        //Display X - slide x in
        Box(
            modifier = Modifier
                .align(Alignment.Center),
//                .height(screenHeight/2)
//                .width(screenWidth/2),
            contentAlignment = Alignment.Center
        ){
            //Remove repetition??, simplify???
            if(viewState.displayCoinInput.value){
                Toast(viewState, viewModel, viewState.displayCoinInput.value)
            }else{
                Toast(viewState, viewModel, viewState.displayX.value)
            }
        }

    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Toast(viewState: CapturedPhotoViewState, viewModel: CapturedPhotoViewModel, visible: Boolean){
    AnimatedVisibility(
        visible = visible,
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
//        ToastContent(viewState, viewModel)
        if(viewState.displayCoinInput.value){
            CoinContent(viewState, viewModel)
        }else{
            XContent(viewState)
        }

    }
}

@Composable
private fun ToastContent(viewState: CapturedPhotoViewState) {

}

@Composable
private fun XContent(viewState: CapturedPhotoViewState) {
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
            if(viewState.total != 0.00){ //or null?
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

@Composable
private fun CoinContent(viewState: CapturedPhotoViewState, viewModel: CapturedPhotoViewModel){
    val shape = RoundedCornerShape(4.dp)
    val coinList = viewState.currencyValues?.coinValues
    coinList?.let {
        //UPLIFT: make box scrollable?/set height?
        Box(
            modifier = Modifier
                .clip(shape)
                .background(colorResource(id = R.color.white)), //replace with theme
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "COINS",
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_min)),
                    style = MaterialTheme.typography.body2,
                    color = colorResource(R.color.pink_3) //replace with theme
                )
                viewModel.setList(coinList)
                coinList.forEachIndexed { index, item ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CoinImage()
                        OutlinedTextField(
                            value = viewState.valueStateList[index],
                            onValueChange = {
                                viewModel.onTextChange(it,index, item)
                            }
                        )
                    }
                }
                Button(onClick = {viewModel.onEnter()},
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(R.color.white), //theme???
                        contentColor = colorResource(R.color.pink_3)
                    )) {
                    Text(
                        text = "Enter",
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun CoinImage(){
    Box(
        modifier = Modifier
            .height(32.dp)
            .width(32.dp),
        contentAlignment = Alignment.Center
    ) { //UPLIFT: DYNAMIC SIZING H/W
        GlideImage(
            imageModel = R.drawable.coin_32px,
            contentScale = ContentScale.Fit,
        )
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

////icon: <a href="https://www.flaticon.com/free-icons/coin" title="coin icons">Coin icons created by Freepik - Flaticon</a>