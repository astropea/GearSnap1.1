package com.gearsnap.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gearsnap.ui.components.AddSpotDialog
import com.gearsnap.ui.components.SpotDetailSheet
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    spotsViewModel: SpotsViewModel = viewModel()
) {
    val addSpotState by spotsViewModel.addSpotState.collectAsState()
    val spots by spotsViewModel.spots.collectAsState()
    val detailState by spotsViewModel.detail.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSpot by remember { mutableStateOf<SpotUi?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var longPressGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    val context = LocalContext.current

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasLocationPermission = granted
            if (!granted) {
                Toast.makeText(context, "Permission de localisation refusee.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val spotId = selectedSpot?.id
        if (uri != null && spotId != null) {
            spotsViewModel.addPhoto(spotId, uri)
        }
    }

    LaunchedEffect(addSpotState) {
        when (val state = addSpotState) {
            is AddSpotUIState.Success -> {
                Toast.makeText(context, "Spot '${state.spot.name}' ajoute avec succes !", Toast.LENGTH_SHORT).show()
                showAddDialog = false
                longPressGeoPoint = null
                spotsViewModel.resetAddSpotState()
            }
            is AddSpotUIState.Error -> {
                Toast.makeText(context, "Erreur: ${state.message}", Toast.LENGTH_LONG).show()
                spotsViewModel.resetAddSpotState()
            }
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", 0))
                    MapView(ctx).apply {
                        setMultiTouchControls(true)
                        controller.setZoom(12.0)
                        controller.setCenter(GeoPoint(48.8566, 2.3522))

                        if (hasLocationPermission) {
                            val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                            myLocationOverlay.enableMyLocation()
                            overlays.add(myLocationOverlay)
                        }

                        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false
                            override fun longPressHelper(p: GeoPoint?): Boolean {
                                p?.let {
                                    longPressGeoPoint = it
                                    showAddDialog = true
                                }
                                return true
                            }
                        })
                        overlays.add(0, mapEventsOverlay)
                    }
                },
                update = { mapView ->
                    mapView.overlays.removeAll { it is Marker }
                    spots.forEach { spot ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(spot.lat, spot.lng)
                            title = spot.name
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            setOnMarkerClickListener { _, _ ->
                                selectedSpot = spot
                                spotsViewModel.selectSpot(spot.id)
                                showSheet = true
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                }
            )

            if (showAddDialog) {
                val geoPoint = longPressGeoPoint
                if (geoPoint != null) {
                    AddSpotDialog(
                        initialLat = geoPoint.latitude,
                        initialLng = geoPoint.longitude,
                        onDismiss = {
                            showAddDialog = false
                            longPressGeoPoint = null
                        },
                        isAdding = addSpotState is AddSpotUIState.Loading,
                        onConfirm = { name, category, difficulty, description ->
                            spotsViewModel.addSpot(
                                name = name,
                                category = category,
                                difficulty = difficulty,
                                description = description,
                                lat = geoPoint.latitude,
                                lng = geoPoint.longitude
                            )
                        }
                    )
                }
            }

            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showSheet = false
                        selectedSpot = null
                    },
                    sheetState = sheetState
                ) {
                    SpotDetailSheet(
                        spot = detailState.spot,
                        photos = detailState.photos,
                        reviews = detailState.reviews,
                        isLoading = detailState.isLoading,
                        onAddPhotoClick = { photoPicker.launch("image/*") },
                        onAddReview = { rating, comment ->
                            selectedSpot?.id?.let { spotsViewModel.addReview(it, rating, comment) }
                        }
                    )
                }
            }
        }
    }
}
