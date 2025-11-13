package com.gearsnap.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
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
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        // Apply to the activity context
        if (context is Activity) {
            val config = context.resources.configuration
            config.setLocale(locale)
            context.createConfigurationContext(config)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    fun restartActivity(context: Context) {
        if (context is ComponentActivity) {
            context.recreate()
        } else if (context is Activity) {
            context.recreate()
        }
    }
}

// Theme management
object ThemeManager {
    private const val PREF_NAME = "gearsnap_prefs"
    private const val KEY_THEME = "selected_theme"

    /**
     * Sauvegarde le choix de thème de l'utilisateur
     * @param isDark true pour mode sombre, false pour mode clair
     */
    fun setTheme(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_THEME, isDark).apply()
    }

    /**
     * Récupère le thème sauvegardé
     * @return false (mode clair) par défaut, true si mode sombre sélectionné
     */
    fun getSavedTheme(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        // Par défaut : mode clair (false)
        return prefs.getBoolean(KEY_THEME, false)
    }

    /**
     * Vérifie si le thème sombre est activé
     * @return true si mode sombre, false si mode clair
     */
    fun isDarkTheme(context: Context): Boolean {
        // Retourne directement le thème sauvegardé (par défaut : clair)
        return getSavedTheme(context)
    }

    /**
     * Applique le thème sélectionné à l'application
     * Force MODE_NIGHT_NO pour le mode clair (ignore le thème système)
     * Force MODE_NIGHT_YES pour le mode sombre
     */
    fun applyTheme(context: Context) {
        val isDark = getSavedTheme(context)
        val mode = if (isDark) {
            // Mode sombre : Force le mode nuit
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            // Mode clair : Force le mode jour (ignore le thème système)
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * Applique le thème et redémarre l'activité pour appliquer les changements
     */
    fun applyThemeAndRestart(context: Context) {
        applyTheme(context)
        restartActivity(context)
    }

    /**
     * Redémarre l'activité courante pour appliquer les changements de thème
     */
    fun restartActivity(context: Context) {
        if (context is ComponentActivity) {
            context.recreate()
        } else if (context is Activity) {
            context.recreate()
        }
    }
}