package com.example.calcumate_v3.ui.screen.capturedphoto

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.compose.material.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.calcumate_v3.CurrencyValues
import com.example.calcumate_v3.currencyValues
import com.example.calcumate_v3.getCurrencyValuesByRegionCode
import com.example.calcumate_v3.ui.screen.home.HomeViewState
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
//import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
//import com.google.mlkit.vision.objects.defaults.PredefinedCategory
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.lang.reflect.Type
import java.util.Collections.addAll

data class CapturedPhotoViewState(
    val context: Context? = null, //Bad to re-init? Do we need to pass from IAViewModel?
    val photoUri: Uri? = null,
    val image: InputImage? = null, //need to declare at this level or fun enough?
    val currencyValues: CurrencyValues? = null,
    val total: Int? = 0,
    val coinTotal: Double? = 0.00,
    val debugMode: MutableState<Boolean> = mutableStateOf(false),
    val debugLog: MutableState<String> = mutableStateOf(""),
    val displayX: MutableState<Boolean> = mutableStateOf(false),
    val displayCoinInput: MutableState<Boolean> = mutableStateOf(false),
    val valueStateList: MutableState<String> = mutableStateOf("")
)

class CapturedPhotoViewModel : ViewModel(){
    private val _viewState = MutableStateFlow(CapturedPhotoViewState())
    val viewState: StateFlow<CapturedPhotoViewState> = _viewState

    fun initView(context: Context, photoUri: Uri) {
        _viewState.value = _viewState.value.copy(
            context = context,
            photoUri = photoUri,
            currencyValues = getCurrencyValuesByRegionCode("AU"), //!!UPLIFT: retrieve currency values via dynamic region code/user input

        )
        Log.d("!!!currencyValues", "${_viewState.value.currencyValues}")

    }

    fun textRecognition() {
        //Create instance of recognizer
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        //Create InputImage from Uri
        try {
            createInputImageFromURI() //UPLIFT: don't recall, check if Image null, if not use existing InputImage
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //Process Image
        val result = recognizer.process(_viewState.value.image!!)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                Log.d("Text Recognition", "success")
                val resultText = visionText.text
                //Popualte debugLog
                _viewState.value = _viewState.value.copy(debugLog = (mutableStateOf(resultText)))
                for (block in visionText.textBlocks) {
                    val blockText = block.text
                    if (isNumeric(blockText) && _viewState.value.currencyValues!!.notes.contains(
                            blockText
                        )
                    ) { //Process numeric results within currency values ONLY
                        val blockCornerPoints = block.cornerPoints
                        val blockFrame = block.boundingBox
                        for (line in block.lines) {
                            val lineText = line.text
                            val lineCornerPoints = line.cornerPoints
                            val lineFrame = line.boundingBox
                            for (element in line.elements) {
                                val elementText = element.text
                                _viewState.value = _viewState.value.copy(
                                    total = _viewState.value.total!!.plus(elementText.toInt())
                                ) //Add elementText to running total
                                val elementCornerPoints = element.cornerPoints
                                val elementFrame = element.boundingBox
                            }
                        }
                    }
                }
                Log.d("!!!total", "${_viewState.value.total}")
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Log.d("Text Recognition", "failure")

            }

        //UPLIFT: Wait for total to be updated...??
        _viewState.value = _viewState.value.copy(displayX = mutableStateOf(true))
    }

    fun imageLabeling() {
        //Create instance of image labeling
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        //Create InputImage from Uri
        try {
            createInputImageFromURI()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //Process Image
        labeler.process(_viewState.value.image!!) //UPLIFT: don't recall, check if Image null, if not use existing InputImage
            .addOnSuccessListener { labels ->
                // Task completed successfully
                Log.d("!!!imageLabelling", "success")
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    val index = label.index
                }

            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Log.d("!!!imageLabelling", "failure")
            }
    }

    fun objectRecognition(){
        val localModel = LocalModel.Builder()
            .setAssetFilePath("lite-model_object_detection_mobile_object_labeler_v1_1.tflite")
            .build()

        //CUSTOM MODEL
        // Multiple object detection in static images
        val customObjectDetectorOptions =
            CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .setClassificationConfidenceThreshold(0.5f)
                .setMaxPerObjectLabelCount(10)
                .build()

        //Create instance of object detector
        val objectDetector =
            ObjectDetection.getClient(customObjectDetectorOptions)

        //Create InputImage from Uri
        try {
            createInputImageFromURI() //UPLIFT: don't recall, check if Image null, if not use existing InputImage
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //Process image
        objectDetector.process(_viewState.value.image!!)
            .addOnSuccessListener { detectedObjects ->
                // Task completed successfully
                Log.d("Object Recognition", "success, $detectedObjects")
                for (detectedObject in detectedObjects) {
                    val boundingBox = detectedObject.boundingBox
                    val trackingId = detectedObject.trackingId
//                    viewState.value.boundingBoxes.add(boundingBox) //??? correct way to add to list, no .copy()?
                    for (label in detectedObject.labels) {
                        val text = label.text
                        val confidence = label.confidence
                        if(text == "Coin" && _viewState.value.displayCoinInput != mutableStateOf(true)){
                            _viewState.value = _viewState.value.copy(displayCoinInput = mutableStateOf(true))
                        }
                        Log.d("!!!text", "${text}}")
                    }
//                    Log.d("!!!boundingBoxes", "${_viewState.value.boundingBoxes}}")
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Log.d("Object Recognition", "failure")
            }
    }

    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }

    fun createInputImageFromURI(){
        _viewState.value = _viewState.value.copy(
            image = InputImage.fromFilePath(
                _viewState.value.context!!,
                _viewState.value.photoUri!!
            )
        )
    }

    fun setList(list: List<String>){
        val textFieldInitValues = List(list.size){ "" }

    }
}
