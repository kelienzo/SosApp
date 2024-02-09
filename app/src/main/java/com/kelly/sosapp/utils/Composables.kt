package com.kelly.sosapp.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayoutWrapper(
    topBarText: String = "",
    snackbarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    },
    content: @Composable (PaddingValues, Boolean, String, String) -> Unit
) {

    val networkState by NetworkUtils.getNetworkState(LocalContext.current)
        .collectAsStateWithLifecycle()

    val locationState by LocationUtils.getCurrentLocation(LocalContext.current)
        .collectAsStateWithLifecycle()

    val coroutine = rememberCoroutineScope()

    fun showSnackBar(snackBar: suspend () -> Unit) {
        coroutine.launch {
            snackBar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            if (topBarText.isNotBlank()) {
                CenterAlignedTopAppBar(
                    title = { Text(text = topBarText) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }
    ) {

        content(it, networkState.first, locationState.first, locationState.second)

        showSnackBar {
            snackbarHostState.run {
                if (!networkState.first) {
                    showSnackbar(
                        message = networkState.second,
                        duration = SnackbarDuration.Indefinite,
                        withDismissAction = true
                    )
                } else {
                    currentSnackbarData?.dismiss()
                }
            }
        }
    }
}