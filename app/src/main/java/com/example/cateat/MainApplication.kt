package com.example.cateat

import android.app.Application
import com.example.cateat.utils.CatAppLifecycleTracker

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(CatAppLifecycleTracker())
    }
}