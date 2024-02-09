package com.kelly.sosapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kelly.sosapp.presentation.navigation.SOSNavHost
import com.kelly.sosapp.ui.theme.SOSAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val checkPermission = hasNoPermission()
            var shouldRelaunchPermissionRequest by remember { mutableStateOf(false) }
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { result ->
                    // Handle permission results here if needed
                    for ((permission, isGranted) in result) {
                        if (!isGranted) {
                            shouldRelaunchPermissionRequest = true
                        }
                    }
                }
            )
            SOSAppTheme {
                LaunchedEffect(shouldRelaunchPermissionRequest) {
                    if (checkPermission) {
                        launcher.launch(permissions)
                    }
                }
                SOSNavHost()
            }
        }
    }

    private fun hasNoPermission(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
    }
}