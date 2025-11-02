package com.gearsnap.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.gearsnap.MainActivity
import java.util.*

// Language management
object LanguageManager {
    private const val PREF_NAME = "gearsnap_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    fun setLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "fr") ?: "fr"
    }

    fun applyLanguage(context: Context) {
        val languageCode = getSavedLanguage(context)
        setLanguage(context, languageCode)
    }

    fun restartActivity(context: Context) {
        if (context is androidx.activity.ComponentActivity) {
            context.recreate()
        } else {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
            if (context is android.app.Activity) {
                context.finish()
            }
        }
    }
}

// Theme management
object ThemeManager {
    private const val PREF_NAME = "gearsnap_prefs"
    private const val KEY_THEME = "selected_theme"

    fun setTheme(context: Context, isDark: Boolean?) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (isDark == null) {
            prefs.edit().remove(KEY_THEME).apply() // Remove to use system default
        } else {
            prefs.edit().putBoolean(KEY_THEME, isDark).apply()
        }
    }

    fun getSavedTheme(context: Context): Boolean? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return if (prefs.contains(KEY_THEME)) {
            prefs.getBoolean(KEY_THEME, false)
        } else {
            null // Use system default
        }
    }

    fun isDarkTheme(context: Context): Boolean {
        val savedTheme = getSavedTheme(context)
        return savedTheme ?: false
    }

    fun applyTheme(context: Context) {
        val savedTheme = getSavedTheme(context)
        val mode = when {
            savedTheme == null -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            savedTheme -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}