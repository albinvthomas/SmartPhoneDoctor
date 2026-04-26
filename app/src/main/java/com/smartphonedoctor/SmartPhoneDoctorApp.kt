package com.smartphonedoctor

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartPhoneDoctorApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
