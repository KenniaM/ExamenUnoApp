package com.moviles.examenuno

import android.content.Context
import android.net.ConnectivityManager
import android.app.Application


class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        fun hasInternet(): Boolean {
            val connectivityManager =
                instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}