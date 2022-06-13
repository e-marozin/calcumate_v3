package com.example.calcumate_v3.ui.screen.imageanalyzer

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.calcumate_v3.ui.navigation.Screens
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

//Business logic of ImageAnlyzerScreen lives here - state hoisting, make composables stateless (value: Type, onValueChange: (Type) -> Unit)

data class ImageAnalyzerViewState( //?? What's actually needed here, only things that'll be re-used?
    val context: Context? = null,
    val lifeCycleOwner: LifecycleOwner? = null,
    val imageCapture: MutableState<ImageCapture?> = mutableStateOf(null),
    val executor: MutableState<Executor?> = mutableStateOf(null),
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null,
    val previewView: PreviewView? = null,
//    var extractedText: MutableState<String> = mutableStateOf(""),
    var photoUri: Uri? = null
)

class ImageAnalyzerViewModel : ViewModel(){
    private val _viewState = MutableStateFlow(ImageAnalyzerViewState())
    val viewState: StateFlow<ImageAnalyzerViewState> = _viewState


    fun initCamera(context: Context, lifeCycleOwner: LifecycleOwner){
        //Set values in view state
        _viewState.value = _viewState.value.copy(
            context = context,
            lifeCycleOwner = lifeCycleOwner,
            executor = mutableStateOf(ContextCompat.getMainExecutor(context)),
            cameraProviderFuture =  (ProcessCameraProvider.getInstance(context))
        )
    }

    fun setPreviewView(): PreviewView{
        val previewView = PreviewView(_viewState.value.context!!)
        val executor =  _viewState.value.executor
        _viewState.value.cameraProviderFuture!!.addListener({
            val cameraProvider = _viewState.value.cameraProviderFuture!!.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()


            try {
                cameraProvider.unbindAll()

                //Image Analyzer
                    ///CODE HERE

                //Image capture
                _viewState.value = _viewState.value.copy(
                    imageCapture = mutableStateOf(
                        ImageCapture.Builder()
                            .setTargetRotation(previewView.display.rotation)
                            .build()
                    )
                )
                cameraProvider.bindToLifecycle(
                    _viewState.value.lifeCycleOwner!!,
                    cameraSelector,
                    preview,
                    _viewState.value.imageCapture.value //?
                //imageAnalyzer
                )
            } catch (exc: Exception){
                Log.e("CAMERA PREVIEW", "Use case binding failed", exc)
            }
        }, _viewState.value.executor.value)
        return previewView
    }

    fun takePhoto(navController: NavController){
//        var photoUri: Uri? = null
        //MEDIA STORE VARIABLES
        val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(viewState.value.context!!.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()


        _viewState.value.imageCapture.value!!.takePicture( //!! ?
            outputOptions,
            _viewState.value.executor.value!!,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("FAILURE", "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Log.d("SUCCESS", msg)
                    //Nav to view photo
                    _viewState.value = _viewState.value.copy(photoUri = output.savedUri!!)
                    Log.d("photoUriAsString", "${_viewState.value.photoUri.toString()}")
                    Log.d("urlEncoder", URLEncoder.encode("/${_viewState.value.photoUri.toString()}", StandardCharsets.UTF_8.toString()))
                    val encodedUrl = URLEncoder.encode("${_viewState.value.photoUri.toString()}", StandardCharsets.UTF_8.toString())
                    navController.navigate(Screens.CapturedPhotoScreen.route + "/$encodedUrl")
                }
            })
    }

}