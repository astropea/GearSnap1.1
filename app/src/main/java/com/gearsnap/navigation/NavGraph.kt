package com.gearsnap.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gearsnap.ui.screens.*

sealed class BottomDestinations(val route: String, val icon: ImageVector, val label: String) {
    data object Home: BottomDestinations("home", Icons.Default.Home, "Accueil")
    data object Map: BottomDestinations("map", Icons.Default.Map, "Carte")
    data object Rent: BottomDestinations("rent", Icons.Default.Handyman, "Prêt")
    data object Events: BottomDestinations("events", Icons.Default.Event, "Événements")
    data object Social: BottomDestinations("social", Icons.Default.Chat, "Social")
    data object Profile: BottomDestinations("profile", Icons.Default.Person, "Profil")

    companion object {
        val all = listOf(Home, Map, Rent, Events, Social, Profile)
    }
}

@Composable
fun GSNavGraph(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomDestinations.Home.route, modifier = modifier) {
        composable(BottomDestinations.Home.route) { HomeScreen() }
        composable(BottomDestinations.Map.route) { MapScreen() }
        composable(BottomDestinations.Rent.route) { RentScreen() }
        composable(BottomDestinations.Events.route) { EventsScreen() }
        composable(BottomDestinations.Social.route) { SocialScreen() }
        composable(BottomDestinations.Profile.route) { ProfileScreen() }
    }
}