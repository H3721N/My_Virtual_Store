package com.gomez.herlin.mi_tiendita_virtual

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyAplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}