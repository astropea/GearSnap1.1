package com.gearsnap.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gearsnap.ui.screens.*
import com.gearsnap.R

sealed class BottomDestinations(val route: String, val icon: ImageVector, val labelResId: Int) {
    data object Home: BottomDestinations("home", Icons.Default.Home, R.string.nav_home)
    data object Map: BottomDestinations("map", Icons.Default.Map, R.string.nav_map)
    data object Rent: BottomDestinations("rent", Icons.Default.Handyman, R.string.nav_rent)
    // Keep Events defined (not in bottom bar) for potential future usage
    data object Events: BottomDestinations("events", Icons.Default.Event, R.string.nav_events)
    data object Planning: BottomDestinations("planning", Icons.Default.Event, R.string.nav_planning)
    data object Social: BottomDestinations("social", Icons.Default.Chat, R.string.nav_social)
    data object Profile: BottomDestinations("profile", Icons.Default.Person, R.string.nav_profile)

    companion object {
        val all = listOf(Home, Map, Rent, Planning, Social, Profile)
    }
}

@Composable
fun GSNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onLogout: () -> Unit = {}
) {
    NavHost(navController = navController, startDestination = BottomDestinations.Home.route, modifier = modifier) {
        composable(BottomDestinations.Home.route) { HomeScreen() }
        composable(BottomDestinations.Map.route) { MapScreen() }
        composable(BottomDestinations.Rent.route) { RentScreen() }
        composable(BottomDestinations.Events.route) { EventsScreen() }
        composable(BottomDestinations.Planning.route) { PlanningScreen() }
        composable(BottomDestinations.Social.route) { SocialScreen() }
        composable(BottomDestinations.Profile.route) { ProfileScreen(onLogout = onLogout) }
    }
}