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
    var longPressLatLng by remember { mutableStateOf<Pair<Double, Double>?>(null) }

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
        // Map - Affichage en plein écran sans padding
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            MapLibreScreen(
                spots = filtered,
                modifier = Modifier.fillMaxSize(),
                onMarkerClick = { spot: SpotUi ->
                    selectedSpot = spot
                    showSheet = true
                },
                onCenterChanged = { lat: Double, lng: Double -> centerLatLng = Pair(lat, lng) },
                onLongPress = { lat: Double, lng: Double ->
                    longPressLatLng = Pair(lat, lng)
                    showAddDialog = true
                }
            )
        }

        // Top AppBar (green) with logo and search - Style conforme aux maquettes
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo + Texte GearSnap
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_gearsnap_logo),
                        contentDescription = stringResource(R.string.cd_logo),
                        modifier = Modifier.size(32.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "GearSnap",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Icône de recherche
                IconButton(onClick = { /* TODO search */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.map_search_hint),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Filters row under app bar - Style conforme aux maquettes
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = hikingOnly,
                onClick = { hikingOnly = !hikingOnly },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_map_pin),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Hiking")
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = hikingOnly,
                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                    borderColor = MaterialTheme.colorScheme.outline
                )
            )
            FilterChip(
                selected = distanceFilter,
                onClick = { distanceFilter = !distanceFilter },
                label = { Text("Distance") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color.White,
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    containerColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = distanceFilter,
                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                    borderColor = MaterialTheme.colorScheme.outline
                )
            )
            FilterChip(
                selected = difficultyFilter,
                onClick = { difficultyFilter = !difficultyFilter },
                label = { Text("Difficulty") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color.White,
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    containerColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = difficultyFilter,
                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                    borderColor = MaterialTheme.colorScheme.outline
                )
            )
        }

        // FAB retiré - Création de spot uniquement par appui long sur la carte

        // Floating button "Ajouter aux favoris" - Style conforme aux maquettes
        AnimatedVisibility(
            visible = selectedSpot != null && selectedSpot?.isFavorite == false,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 96.dp)
        ) {
            Surface(
                onClick = {
                    val s = selectedSpot ?: return@Surface
                    spots = spots.map { if (it.id == s.id) it.copy(isFavorite = true) else it }
                    selectedSpot = spots.firstOrNull { it.id == s.id }
                },
                color = Color.White,
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 4.dp,
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_map_pin),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Ajouter aux favoris",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // AddSpotDialog
        if (showAddDialog) {
            val (lat, lng) = longPressLatLng ?: centerLatLng
            AddSpotDialog(
                initialLat = lat,
                initialLng = lng,
                onDismiss = {
                    showAddDialog = false
                    longPressLatLng = null
                },
                onAdd = { name, category, spotLat, spotLng ->
                    val newSpot = SpotUi(
                        id = Random.nextLong().toString(),
                        name = name,
                        lat = spotLat,
                        lng = spotLng,
                        category = category
                    )
                    spots = spots + newSpot
                    showAddDialog = false
                    longPressLatLng = null
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