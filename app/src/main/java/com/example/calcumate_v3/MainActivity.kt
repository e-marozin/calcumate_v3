package com.example.calcumate_v3

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.calcumate_v3.ui.navigation.Navigation
import com.example.calcumate_v3.ui.theme.Calcumate_v3Theme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RequestPermissions()
        setContent {
            Calcumate_v3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
//                    color = MaterialTheme.colors.background
                color = colorResource(R.color.pink_0)
                ) {
                    CalcumateApp()
                }
            }
        }
    }

    //?? how to move out of main? useful?
    @RequiresApi(Build.VERSION_CODES.M)
    private fun RequestPermissions() {
        //Handle camera permission response
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.i("CameraPermissions", "Permission granted")
            } else {
                Log.i("CameraPermissions", "Permission denied")
            }
        }

        //Request camera permission
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.i("CameraPermissions", "Permission previously granted")
                //Add UI?
            }
            shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            ) -> {
                Log.i("CameraPermissions", "Add UI -> why, ok and cancel button")
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.i("CameraPermissions", "Permission previously granted")
                //Add UI?
            }
            shouldShowRequestPermissionRationale(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                Log.i("CameraPermissions", "Add UI -> why, ok and cancel button")
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }
}

@Composable
private fun CalcumateApp() {
    val navController = rememberNavController()
    Navigation(navController)
}