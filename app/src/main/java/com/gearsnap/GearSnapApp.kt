package com.gearsnap

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.gearsnap.ui.activities.ThemeManager

/**
 * Classe Application principale de GearSnap
 *
 * IMPORTANT : Le thème est appliqué ICI, AVANT toute activité.
 * Cela garantit que le bon mode (clair/sombre) est actif dès le démarrage.
 */
class GearSnapApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // ✅ ÉTAPE CRITIQUE : Appliquer le thème AVANT tout le reste
        // Cela force le mode clair ou sombre selon les préférences utilisateur
        // et IGNORE complètement le thème système
        applyUserTheme()
    }

    /**
     * Applique le thème sauvegardé par l'utilisateur
     * - Mode clair par défaut (si aucune préférence)
     * - Force MODE_NIGHT_NO pour le clair (ignore le système)
     * - Force MODE_NIGHT_YES pour le sombre
     */
    private fun applyUserTheme() {
        val isDarkTheme = ThemeManager.getSavedTheme(this)

        val nightMode = if (isDarkTheme) {
            // Mode sombre sélectionné : Force le mode nuit
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            // Mode clair sélectionné (ou par défaut) : Force le mode jour
            // ⚠️ Ceci IGNORE le thème système, même si le téléphone est en mode sombre
            AppCompatDelegate.MODE_NIGHT_NO
        }

        // Applique le mode de nuit à toute l'application
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}