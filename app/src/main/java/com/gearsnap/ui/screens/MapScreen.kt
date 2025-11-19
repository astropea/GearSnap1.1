package com.gearsnap.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.drawable.toBitmap
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gearsnap.R
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
    var newSpotPhotoUris by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSpot by remember { mutableStateOf<SpotUi?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var longPressGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    val context = LocalContext.current
    val pinBitmap = remember { buildPinBitmap(context) }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasLocationPermission = granted
            if (!granted) {
                Toast.makeText(context, "Permission de localisation refusee.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val addSpotPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        newSpotPhotoUris = uris.filterNotNull()
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        val spotId = selectedSpot?.id
        val cleaned = uris.filterNotNull()
        if (spotId != null && cleaned.isNotEmpty()) {
            spotsViewModel.addPhotos(spotId, cleaned)
        } else if (spotId == null) {
            Toast.makeText(context, "Spot introuvable pour la photo.", Toast.LENGTH_SHORT).show()
        }
    }

    // Permissions pour lire/ajouter une photo (READ_MEDIA_IMAGES ou READ_EXTERNAL_STORAGE) et appareil photo
    val photoPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = result.values.all { it }
        if (granted) {
            photoPicker.launch("image/*")
        } else {
            Toast.makeText(context, "Permission photo/camera refusee.", Toast.LENGTH_SHORT).show()
        }
    }
    val photoPermissions: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    }

    LaunchedEffect(addSpotState) {
        when (val state = addSpotState) {
            is AddSpotUIState.Success -> {
                Toast.makeText(context, "Spot '${state.spot.name}' ajoute avec succes !", Toast.LENGTH_SHORT).show()
                showAddDialog = false
                longPressGeoPoint = null
                newSpotPhotoUris = emptyList()
                spotsViewModel.resetAddSpotState()
            }
            is AddSpotUIState.Error -> {
                Toast.makeText(context, "Erreur: ${state.message}", Toast.LENGTH_LONG).show()
                newSpotPhotoUris = emptyList()
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
                            icon = BitmapDrawable(context.resources, pinBitmap)
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
                newSpotPhotoUris = emptyList()
            },
            isAdding = addSpotState is AddSpotUIState.Loading,
            onPickPhoto = { addSpotPhotoPicker.launch("image/*") },
            selectedPhotoCount = newSpotPhotoUris.size,
            onConfirm = { name, category, difficulty, description ->
                spotsViewModel.addSpot(
                    name = name,
                    category = category,
                    difficulty = difficulty,
                    description = description,
                    lat = geoPoint.latitude,
                    lng = geoPoint.longitude,
                    photoUris = newSpotPhotoUris
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
                        fallbackSpot = selectedSpot,
                        photos = detailState.photos,
                        reviews = detailState.reviews,
                        isLoading = detailState.isLoading,
                        onAddPhotoClick = {
                            val hasPermission = photoPermissions.all { perm ->
                                ContextCompat.checkSelfPermission(context, perm) ==
                                    android.content.pm.PackageManager.PERMISSION_GRANTED
                            }
                            if (hasPermission) {
                                photoPicker.launch("image/*")
                            } else {
                                photoPermissionLauncher.launch(photoPermissions)
                            }
                        },
                        onAddReview = { rating, comment ->
                            selectedSpot?.id?.let { spotsViewModel.addReview(it, rating, comment) }
                        }
                    )
                }
            }
        }
    }

    // À chaque sélection de spot, recharge le détail depuis la DB pour avoir avis/desc frais
    LaunchedEffect(selectedSpot?.id) {
        selectedSpot?.id?.let { spotsViewModel.selectSpot(it) }
    }
}

// Couleur unique pour tous les pins (image fournie)
private fun buildPinBitmap(context: Context): Bitmap {
    val drawable = ContextCompat.getDrawable(context, R.drawable.ic_pin_green)
        ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val density = context.resources.displayMetrics.density
    val width = (24 * density).toInt().coerceAtLeast(1)
    val height = (32 * density).toInt().coerceAtLeast(1)
    return drawable.toBitmap(width, height, Bitmap.Config.ARGB_8888)
}
