package com.kelly.sosapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkUtils : ConnectivityManager.NetworkCallback() {
    private val networkState = MutableStateFlow(Pair(false, "Internet connection is unavailable"))

    private var isNetworkCallbackRegistered = false
    fun getNetworkState(context: Context): StateFlow<Pair<Boolean, String>> {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (isNetworkCallbackRegistered.not()) {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(), this)
            isNetworkCallbackRegistered = true
        }

        return networkState
    }

    override fun onAvailable(network: Network) {
        setNetworkInfo(true, "Internet connection available")
    }

    override fun onLost(network: Network) {
        setNetworkInfo(false, "Internet connection lost")
    }

    override fun onLosing(network: Network, maxMsToLive: Int) {
        setNetworkInfo(false, "Internet connection is unstable")
    }

    override fun onUnavailable() {
        setNetworkInfo(false, "Internet connection is unavailable")
    }

    private fun setNetworkInfo(isInternet: Boolean, message: String) {
        networkState.value = Pair(isInternet, message)
    }
}