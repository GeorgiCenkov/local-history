package com.example.localhistory.utils

import android.content.Context
import java.util.Locale
import android.content.res.Configuration

// Utility function to change the app's locale ( language )
fun updateLocale(context: Context, languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    return context.createConfigurationContext(config)
}