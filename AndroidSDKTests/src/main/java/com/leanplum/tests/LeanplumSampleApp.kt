package com.leanplum.tests

import android.app.Application
import com.leanplum.tests.LP

class LeanplumSampleApp:Application() {

    override fun onCreate() {
        super.onCreate()
        LP.initSDKInApplication(this)
    }
}