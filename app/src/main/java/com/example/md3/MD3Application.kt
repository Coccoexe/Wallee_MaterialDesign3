package com.example.md3

import android.app.Application
import com.google.android.material.color.DynamicColors

class MD3Application: Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply dynamic color
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}