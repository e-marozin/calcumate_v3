package com.example.calcumate_v3.ui.screen.capturedphoto

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.calcumate_v3.CurrencyValues
import com.example.calcumate_v3.getCurrencyValuesByRegionCode
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

data class CapturedPhotoViewState(
    val context: Context? = null, //Bad to re-init? Do we need to pass from IAViewModel?
    val photoUri: Uri? = null,
    val image: InputImage? = null, //need to declare at this level or fun enough?
    val currencyValues: CurrencyValues? = null,
    var total: Double? = 0.00,
    val debugMode: MutableState<Boolean> = mutableStateOf(false),
    val debugLog: MutableState<String> = mutableStateOf(""),
    val displayX: MutableState<Boolean> = mutableStateOf(false),
    val displayCoinInput: MutableState<Boolean> = mutableStateOf(false),
    val valueStateList: SnapshotStateList<String> = mutableStateListOf(),
    val mapTest: MutableMap<Int, Double> = mutableMapOf()
)

class CapturedPhotoViewModel : ViewModel(){
    private val _viewState = MutableStateFlow(CapturedPhotoViewState())
    val viewState: StateFlow<CapturedPhotoViewState> = _viewState

    fun initView(context: Context, photoUri: Uri) {
        _viewState.value = _viewState.value.copy(
            context = context,
            photoUri = photoUri,
            currencyValues = getCurrencyValuesByRegionCode("AU") //!!UPLIFT: retrieve currency values via dynamic region code/user input
        )
    }

    fun detectCurrencyTotal(){
        //Check for coins
        objectRecognition()

        //Process notes
        textRecognition()

        //If there are no coins, just show final screen
        if(!_viewState.value.displayCoinInput.value){
            _viewState.value = _viewState.value.copy(displayX = mutableStateOf(true))
//            toggleBoolean(_viewState.value.displayX)
        }
    }

    private fun textRecognition() {
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
                val resultText = visionText.text
                //Populate debugLog
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
                Log.d("!!!TextRecogTotal", "${_viewState.value.total}")
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Log.d("Text Recognition", "failure")

            }
        //UPLIFT: Wait for total to be updated...??
    }

    private fun objectRecognition(){
        //Setup custom model to detect coins
        val localModel = LocalModel.Builder()
            .setAssetFilePath("lite-model_object_detection_mobile_object_labeler_v1_1.tflite")
            .build()

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
                for (detectedObject in detectedObjects) {
                    Log.d("!!!Object Recognition", "success, $detectedObject")
//                    val boundingBox = detectedObject.boundingBox
//                    val trackingId = detectedObject.trackingId
                    for (label in detectedObject.labels) {
                        val text = label.text
                        Log.d("!!!Object Recognition", "label, $text")
                        val confidence = label.confidence
                        if(text == "Coin" && _viewState.value.displayCoinInput != mutableStateOf(true)){
                            _viewState.value = _viewState.value.copy(displayCoinInput = mutableStateOf(true))
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Log.d("Object Recognition", "failure")
            }
    }

    private fun createInputImageFromURI(){
        _viewState.value = _viewState.value.copy(
            image = InputImage.fromFilePath(
                _viewState.value.context!!,
                _viewState.value.photoUri!!
            )
        )
    }

    private fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }

    fun setList(list: List<Double>){
        val textFieldInitValues = List(list.size){ "" }
        _viewState.value.valueStateList.apply { addAll(textFieldInitValues) }
    }

    //!!!UPLIFT: validate input, handle: removal of items from map if user updates
    fun onTextChange(text: String, index: Int, coinValue: Double){
        _viewState.value.valueStateList[index] = text
        if(isNumeric(text) && text.isNotBlank()) {
            _viewState.value.mapTest[text.toInt()] = coinValue
        }else{
            Log.d("!!!CoinInpt", "NOT NUMERIC, ERROR")
        }
    }

    fun onEnter(){
        if(viewState.value.mapTest.isNotEmpty()){
            var currentTotal = 0.00
            _viewState.value.mapTest.forEach { item ->
                if(isNumeric(item.key.toString())){
                    currentTotal += (item.key * item.value)
                }
            }
            _viewState.value = _viewState.value.copy(total = _viewState.value.total?.plus(currentTotal), displayCoinInput = mutableStateOf(false), displayX = mutableStateOf(true))
        }else{
            Log.d("!!!CoinInput", "NO/NON-NUMBER ENTRIES, ERROR")
        }
    }

//    //!!!UPLIFT: Move to app utils? re-init in HomeScreenViewModel
    fun toggleBoolean(bool: MutableState<Boolean>){
        bool.value = !bool.value
//        _viewState.value = _viewState.value.copy()
    }
}
