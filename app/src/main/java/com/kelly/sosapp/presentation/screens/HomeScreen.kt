package com.kelly.sosapp.presentation.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelly.sosapp.utils.LayoutWrapper
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    image: Pair<String?, String?>?,
    onOpenCamera: () -> Unit
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    fun String.base64ToBitmap(): Bitmap {
        val decodedString = Base64.decode(this, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    LayoutWrapper(
        topBarText = "Home",
        snackbarHostState = snackbarHostState
    ) { padding, _, _, _ ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            image?.let {
                it.first?.let { imageString ->
                    Image(
                        bitmap = imageString.base64ToBitmap().asImageBitmap(),
                        contentDescription = "Image Taken",
                        modifier = Modifier.weight(1f)
                    )
                }
                it.second?.let { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }

            Button(
                onClick = { onOpenCamera() }
            ) {
                Text(text = "Open Camera")
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_3)
@Composable
fun MainScreenPreview() {
    HomeScreen(image = null, onOpenCamera = {})
}