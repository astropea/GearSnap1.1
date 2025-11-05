package com.gearsnap.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gearsnap.R
import com.gearsnap.ui.components.AddSpotDialog
import com.gearsnap.ui.components.GSButton
import com.gearsnap.ui.components.GSButtonVariant
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    // Local simulated state
    var spots by remember { mutableStateOf(sampleSpots()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedSpot by remember { mutableStateOf<SpotUi?>(null) }
    var centerLatLng by remember { mutableStateOf(48.8566 to 2.3522) }

    // Filters
    var hikingOnly by remember { mutableStateOf(false) }
    var distanceFilter by remember { mutableStateOf(false) } // placeholder
    var difficultyFilter by remember { mutableStateOf(false) } // placeholder

    val filtered = remember(spots, hikingOnly) {
        if (hikingOnly) spots.filter { it.category == SpotCategory.HIKING } else spots
    }

    // Bottom sheet for spot details
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(Modifier.fillMaxSize()) {
        // Map
        MapLibreScreen(
            spots = filtered,
            modifier = Modifier.fillMaxSize(),
            onMarkerClick = { spot: SpotUi ->
                selectedSpot = spot
                showSheet = true
            },
            onCenterChanged = { lat: Double, lng: Double -> centerLatLng = Pair(lat, lng) }
        )

        // Top AppBar (green) with logo and search
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_gearsnap_logo),
                        contentDescription = stringResource(R.string.cd_logo),
                        modifier = Modifier.size(28.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("GearSnap", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            actions = {
                IconButton(onClick = { /* TODO search */ }) {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.map_search_hint), tint = MaterialTheme.colorScheme.onPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
        )

        // Filters row under app bar
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 64.dp, start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = hikingOnly,
                onClick = { hikingOnly = !hikingOnly },
                label = { Text("Hiking") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = hikingOnly,
                    selectedBorderColor = MaterialTheme.colorScheme.secondary
                )
            )
            FilterChip(
                selected = distanceFilter,
                onClick = { distanceFilter = !distanceFilter },
                label = { Text("Distance") },
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = distanceFilter,
                    selectedBorderColor = MaterialTheme.colorScheme.secondary
                )
            )
            FilterChip(
                selected = difficultyFilter,
                onClick = { difficultyFilter = !difficultyFilter },
                label = { Text("Difficulty") },
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = difficultyFilter,
                    selectedBorderColor = MaterialTheme.colorScheme.secondary
                )
            )
        }

        // Primary FAB (add spot)
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 96.dp)
                .shadow(8.dp, CircleShape)
        ) { Icon(Icons.Default.Add, contentDescription = "Add") }

        // Secondary floating button (add to favorites)
        GSButton(
            text = "Ajouter aux favoris",
            icon = Icons.Default.Favorite,
            onClick = {
                val s = selectedSpot ?: return@GSButton
                spots = spots.map { if (it.id == s.id) it.copy(isFavorite = true) else it }
                selectedSpot = spots.firstOrNull { it.id == s.id }
            },
            enabled = selectedSpot != null && selectedSpot?.isFavorite == false,
            variant = GSButtonVariant.SECONDARY,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp)
        )

        // AddSpotDialog
        if (showAddDialog) {
            AddSpotDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, category ->
                    val (lat, lng) = centerLatLng
                    val newSpot = SpotUi(
                        id = Random.nextLong().toString(),
                        name = name,
                        lat = lat,
                        lng = lng,
                        category = category
                    )
                    spots = spots + newSpot
                    showAddDialog = false
                }
            )
        }

        // Spot details sheet
        if (showSheet && selectedSpot != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                val s = selectedSpot!!
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.logoavnd),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(s.name, style = MaterialTheme.typography.titleMedium)
                        Text(s.category.display, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    FilledTonalButton(onClick = { /* TODO route */ }) { Text("Itinéraire") }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { /* TODO avis */ }) { Text("Avis") }
                    Button(
                        onClick = {
                            spots = spots.map { if (it.id == s.id) it.copy(isFavorite = true) else it }
                            selectedSpot = spots.firstOrNull { it.id == s.id }
                        },
                        enabled = !s.isFavorite
                    ) { Text("❤ Ajouter aux favoris") }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

private fun sampleSpots(): List<SpotUi> = listOf(
    SpotUi("1", "Buttes Chaumont", 48.8809, 2.3819, SpotCategory.HIKING),
    SpotUi("2", "Fontainebleau", 48.4047, 2.7016, SpotCategory.CLIMBING),
    SpotUi("3", "Petite Ceinture", 48.8566, 2.3522, SpotCategory.URBEX)
)