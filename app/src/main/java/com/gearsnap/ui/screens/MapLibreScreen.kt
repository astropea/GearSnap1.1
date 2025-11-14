package com.gearsnap.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.gearsnap.R

@Composable
fun MapLibreScreen(
    spots: List<SpotUi>,
    modifier: Modifier = Modifier,
    onMarkerClick: (SpotUi) -> Unit = {},
    onCenterChanged: (lat: Double, lng: Double) -> Unit = { _, _ -> },
    onLongPress: (lat: Double, lng: Double) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasLocationPermission = granted
        }
    )

    // Demander les permissions au démarrage
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Configuration OSMDroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        onCenterChanged(48.8566, 2.3522)
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    // Configuration de la carte
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    // Position initiale (Paris)
                    controller.setZoom(12.0)
                    controller.setCenter(GeoPoint(48.8566, 2.3522))

                    // Activer la localisation si permission accordée
                    if (hasLocationPermission) {
                        isClickable = true
                    }

                    // Ajouter un listener pour l'appui long
                    overlays.add(object : org.osmdroid.views.overlay.Overlay() {
                        override fun onLongPress(e: android.view.MotionEvent?, mapView: org.osmdroid.views.MapView?): Boolean {
                            if (e != null && mapView != null) {
                                val projection = mapView.projection
                                val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
                                onLongPress(geoPoint.latitude, geoPoint.longitude)
                                return true
                            }
                            return false
                        }
                    })
                }
            },
            update = { mapView ->
                // Mettre à jour la position centrale
                val center = mapView.mapCenter
                if (center != null) {
                    onCenterChanged(center.latitude, center.longitude)
                }

                // Supprimer tous les marqueurs existants (sauf le listener d'appui long)
                val longPressOverlay = mapView.overlays.firstOrNull { it is org.osmdroid.views.overlay.Overlay && it !is Marker }
                mapView.overlays.clear()
                if (longPressOverlay != null) {
                    mapView.overlays.add(longPressOverlay)
                }

                // Ajouter les nouveaux marqueurs filtrés
                spots.forEach { spot ->
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(spot.lat, spot.lng)
                        title = spot.name
                        snippet = "${spot.category.display} - ${spot.difficulty.display}"

                        // Couleur du marqueur selon la catégorie
                        val iconRes = when (spot.category) {
                            SpotCategory.HIKING -> R.drawable.ic_map_pin
                            SpotCategory.CLIMBING -> R.drawable.ic_map_pin
                            SpotCategory.URBEX -> R.drawable.ic_map_pin
                            SpotCategory.EXPLORATION -> R.drawable.ic_map_pin
                        }

                        // Définir l'icône
                        try {
                            val drawable = ContextCompat.getDrawable(mapView.context, iconRes)
                            if (drawable != null) {
                                // Colorer le marqueur selon la catégorie
                                val color = when (spot.category) {
                                    SpotCategory.HIKING -> android.graphics.Color.parseColor("#2D5016")
                                    SpotCategory.CLIMBING -> android.graphics.Color.parseColor("#D97706")
                                    SpotCategory.URBEX -> android.graphics.Color.parseColor("#0891B2")
                                    SpotCategory.EXPLORATION -> android.graphics.Color.parseColor("#EAB308")
                                }
                                drawable.setTint(color)
                                icon = drawable
                            }
                        } catch (e: Exception) {
                            // Utiliser l'icône par défaut si erreur
                        }

                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                        setOnMarkerClickListener { clickedMarker, _ ->
                            onMarkerClick(spot)
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }

                // Rafraîchir la carte
                mapView.invalidate()
            }
        )
    }
}
