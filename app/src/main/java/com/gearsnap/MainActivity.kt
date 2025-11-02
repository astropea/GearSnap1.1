package com.gearsnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gearsnap.navigation.GSNavGraph
import com.gearsnap.navigation.BottomDestinations
import com.gearsnap.theme.GearSnapTheme
import com.gearsnap.ui.activities.LanguageManager
import com.gearsnap.ui.activities.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved language and theme BEFORE super.onCreate()
        LanguageManager.applyLanguage(this)
        ThemeManager.applyTheme(this)

        super.onCreate(savedInstanceState)

        setContent {
            val isDarkTheme = ThemeManager.isDarkTheme(this)
            GearSnapTheme(useDark = isDarkTheme) {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        NavigationBar {
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
                                    label = { Text(stringResource(dest.labelResId)) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    GSNavGraph(Modifier.padding(innerPadding), navController)
                }
            }
        }
    }
}