package com.example.calcumate_v3.ui.screen.capturedphoto

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.calcumate_v3.CurrencyValues
import com.example.calcumate_v3.currencyValues
import com.example.calcumate_v3.getCurrencyValuesByRegionCode
import com.example.calcumate_v3.ui.screen.home.HomeViewState
import com.google.mlkit.vision.common.InputImage
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
    val total: Int? = 0,
    val debugMode: MutableState<Boolean> = mutableStateOf(false),
    val debugLog: MutableState<String> = mutableStateOf(""),
    val displayX: MutableState<Boolean> = mutableStateOf(false)
)

class CapturedPhotoViewModel : ViewModel(){
    private val _viewState = MutableStateFlow(CapturedPhotoViewState())
    val viewState: StateFlow<CapturedPhotoViewState> = _viewState

    //    init{
    //
    //    }

    fun initView(context: Context, photoUri: Uri) {
        _viewState.value = _viewState.value.copy(
            context = context,
            photoUri = photoUri,
            currencyValues = getCurrencyValuesByRegionCode("AU"), //!!UPLIFT: retrieve currency values via dynamic region code/user input
        )
    }

    fun textRecognition() {
        Log.d("!!!currencyValues", "${_viewState.value.currencyValues}")
        //Create instance of recognizer
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        //Create InputImage from Uri
        try {
            _viewState.value = _viewState.value.copy(
                image = InputImage.fromFilePath(
                    _viewState.value.context!!,
                    _viewState.value.photoUri!!
                )
            )
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

    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }

    fun displayTotal() {

    }

    fun debug_displayText(displayText: String) {  //can I toggle this view on/off

    }

}
