package com.gearsnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gearsnap.navigation.BottomDestinations
import com.gearsnap.navigation.GSNavGraph
import com.gearsnap.theme.GearSnapTheme
import com.gearsnap.ui.activities.LanguageManager
import com.gearsnap.ui.activities.ThemeManager
import com.gearsnap.auth.AuthViewModel
import com.gearsnap.auth.AuthRepository
import com.gearsnap.auth.GoogleSignInService
import com.gearsnap.auth.screens.AuthScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ ÉTAPE 1 : Appliquer la langue et le thème AVANT super.onCreate()
        // Cela garantit que le bon thème est actif dès le démarrage
        LanguageManager.applyLanguage(this)
        ThemeManager.applyTheme(this)

        super.onCreate(savedInstanceState)

        // ✅ ÉTAPE 2 : Appliquer le contenu avec le bon thème
        // Le thème est déjà forcé par ThemeManager.applyTheme() ci-dessus
        setContent {
            // Récupère le thème sauvegardé (false = clair par défaut)
            val isDarkTheme = ThemeManager.isDarkTheme(this)

            // ⚠️ IMPORTANT : GearSnapTheme ignore isSystemInDarkTheme()
            // et utilise uniquement la préférence utilisateur
            GearSnapTheme(useDark = isDarkTheme) {
                val context = this
                val navController = rememberNavController()

                // Initialiser les services d'authentification
                val authViewModel = viewModel {
                    AuthViewModel(
                        authRepository = AuthRepository(),
                        googleSignInService = GoogleSignInService(context)
                    )
                }

                val isAuthenticated = authViewModel.isAuthenticated

                if (isAuthenticated) {
                    // Utilisateur connecté : Affichage de l'app complète avec bottom navigation
                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ) {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.destination?.route
                                BottomDestinations.all.forEach { dest ->
                                    NavigationBarItem(
                                        selected = currentRoute == dest.route,
                                        onClick = {
                                            navController.navigate(dest.route) {
                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(dest.icon, contentDescription = stringResource(dest.labelResId)) },
                                        label = { Text(stringResource(dest.labelResId)) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                                            indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        GSNavGraph(
                            modifier = Modifier.padding(innerPadding),
                            navController = navController,
                            onLogout = {
                                authViewModel.signOut()
                            }
                        )
                    }
                } else {
                    // Utilisateur non connecté : Affichage de l'écran d'authentification
                    AuthScreen(
                        viewModel = authViewModel,
                        onAuthSuccess = {
                            // La navigation est gérée automatiquement via l'état isAuthenticated
                        }
                    )
                }
            }
        }
    }
}