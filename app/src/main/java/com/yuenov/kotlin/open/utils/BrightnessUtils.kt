package com.yuenov.kotlin.open.utils

import android.app.Activity
import android.content.Context
import android.provider.Settings

object BrightnessUtils {

    fun getScreenBrightness(context: Context): Int {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            50
        )
    }

    fun setAppScreenBrightness(activity: Activity, brightness: Int) {
        try {
            val window = activity.window
            val lp = window.attributes
            lp.screenBrightness = brightness / 255f
            window.attributes = lp
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}