package com.kelly.sosapp.presentation.screens

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.kelly.sosapp.data.model.Location
import com.kelly.sosapp.data.model.UpdateLocationRequest
import com.kelly.sosapp.utils.LayoutWrapper
import com.otaliastudios.cameraview.CameraException
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


@Composable
fun CameraScreen(
    isLoading: Boolean,
    onUiEvent: (CameraScreenEvent) -> Unit
) {
    val lifeCycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val cameraView = remember {
        CameraView(context)
    }

    var gLatitude by remember {
        mutableStateOf("")
    }

    var gLongitude by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = gLongitude) {
        if (gLatitude.isNotBlank() && gLongitude.isNotBlank()) {
            cameraView.setLocation(gLatitude.toDouble(), gLongitude.toDouble())
        }
    }


    fun Bitmap.toBase64(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    LayoutWrapper(
        snackbarHostState = snackbarHostState
    ) { padding, _, latitude, longitude ->

        gLatitude = latitude
        gLongitude = longitude

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    cameraView.apply {
                        setLifecycleOwner(lifeCycleOwner)
                        mode = Mode.PICTURE
                        audio = Audio.OFF
                        mapGesture(Gesture.PINCH, GestureAction.ZOOM)
                        mapGesture(Gesture.TAP, GestureAction.AUTO_FOCUS)
                        mapGesture(Gesture.LONG_TAP, GestureAction.TAKE_PICTURE)
                    }
                },
                update = {
                    it.run {
                        addCameraListener(object : CameraListener() {
                            override fun onPictureTaken(result: PictureResult) {
                                result.run {
                                    toBitmap { rootBitMap ->
                                        rootBitMap?.let { bitMap ->
                                            onUiEvent(
                                                CameraScreenEvent.OnSendLocationDetails(
                                                    locationUpdateRequest = UpdateLocationRequest(
                                                        image = bitMap.toBase64(),
                                                        location = Location(
                                                            latitude = location?.latitude.toString(),
                                                            longitude = location?.longitude.toString()
                                                        ),
                                                        phoneNumbers = listOf(
                                                            "87532878290",
                                                            "09987654321"
                                                        )
                                                    )
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            override fun onCameraClosed() {
                                removeCameraListener(this)
                            }

                            override fun onCameraError(exception: CameraException) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = exception.message ?: "Unable to take picture",
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                }
                            }
                        })
                    }
                }
            )

            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {

                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { cameraView.takePictureSnapshot() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Take Photo",
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        IconButton(
                            onClick = { cameraView.toggleFacing() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Take Photo",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed interface CameraScreenEvent {
    data class OnSendLocationDetails(
        val locationUpdateRequest: UpdateLocationRequest
    ) : CameraScreenEvent
}