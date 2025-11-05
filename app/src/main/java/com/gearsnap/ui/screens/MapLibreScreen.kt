package com.gearsnap.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun MapLibreScreen(
    spots: List<SpotUi>,
    modifier: Modifier = Modifier,
    onMarkerClick: (SpotUi) -> Unit = {},
    onCenterChanged: (lat: Double, lng: Double) -> Unit = { _, _ -> }
) {
    // Placeholder composable to keep the screen working while MapLibre dependency syncs.
    // Reports a default center (Paris) once so newly added spots use that center.
    LaunchedEffect(Unit) {
        onCenterChanged(48.8566, 2.3522)
    }
    // Intentionally empty Box; the map layer will be added once MapLibre is ready.
}
