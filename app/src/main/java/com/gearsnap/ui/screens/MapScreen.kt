package com.gearsnap.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
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
import kotlin.random.Random
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    // Local simulated state
    var spots by remember { mutableStateOf(sampleSpots()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedSpot by remember { mutableStateOf<SpotUi?>(null) }
    var centerLatLng by remember { mutableStateOf(48.8566 to 2.3522) }
    var longPressLatLng by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // Search and Filters
    var searchQuery by remember { mutableStateOf("") }
    var hikingOnly by remember { mutableStateOf(false) }
    var selectedDifficulties by remember { mutableStateOf<Set<SpotDifficulty>>(emptySet()) }
    var selectedRadius by remember { mutableStateOf<Int?>(null) } // en km
    var showSearchBar by remember { mutableStateOf(false) }
    var showDifficultyMenu by remember { mutableStateOf(false) }
    var showRadiusMenu by remember { mutableStateOf(false) }

    // Fonction de calcul de distance (Haversine)
    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371.0 // Rayon de la Terre en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    // Filtrage combiné
    val filtered = remember(spots, hikingOnly, searchQuery, selectedDifficulties, selectedRadius, userLocation) {
        var result = spots

        // Filtre par catégorie (hiking)
        if (hikingOnly) {
            result = result.filter { it.category == SpotCategory.HIKING }
        }

        // Filtre par nom (recherche)
        if (searchQuery.isNotBlank()) {
            result = result.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

        // Filtre par difficulté
        if (selectedDifficulties.isNotEmpty()) {
            result = result.filter { it.difficulty in selectedDifficulties }
        }

        // Filtre par distance (rayon)
        if (selectedRadius != null) {
            val centerPoint = userLocation ?: centerLatLng
            result = result.filter { spot ->
                val distance = calculateDistance(centerPoint.first, centerPoint.second, spot.lat, spot.lng)
                distance <= selectedRadius!!
            }
        }

        result
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
                        painter = painterResource(R.drawable.logosnd),
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
                IconButton(onClick = { showSearchBar = !showSearchBar }) {
                    Icon(
                        imageVector = if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = stringResource(R.string.map_search_hint),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Barre de recherche (affichée sous l'AppBar)
        AnimatedVisibility(
            visible = showSearchBar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.map_search_hint),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Effacer",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // Filters row under app bar - Style conforme aux maquettes
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = if (showSearchBar) 120.dp else 56.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 8.dp
                ),
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

            // Filtre de difficulté avec menu déroulant
            Box {
                FilterChip(
                    selected = selectedDifficulties.isNotEmpty(),
                    onClick = { showDifficultyMenu = !showDifficultyMenu },
                    label = {
                        Text(
                            if (selectedDifficulties.isEmpty()) "Difficulté"
                            else "Difficulté (${selectedDifficulties.size})"
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedDifficulties.isNotEmpty(),
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderColor = MaterialTheme.colorScheme.outline
                    )
                )

                DropdownMenu(
                    expanded = showDifficultyMenu,
                    onDismissRequest = { showDifficultyMenu = false }
                ) {
                    SpotDifficulty.values().forEach { difficulty ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = difficulty in selectedDifficulties,
                                        onCheckedChange = null
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(difficulty.display)
                                }
                            },
                            onClick = {
                                selectedDifficulties = if (difficulty in selectedDifficulties) {
                                    selectedDifficulties - difficulty
                                } else {
                                    selectedDifficulties + difficulty
                                }
                            }
                        )
                    }
                    if (selectedDifficulties.isNotEmpty()) {
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Réinitialiser", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                selectedDifficulties = emptySet()
                                showDifficultyMenu = false
                            }
                        )
                    }
                }
            }

            // Filtre de distance avec menu déroulant
            Box {
                FilterChip(
                    selected = selectedRadius != null,
                    onClick = { showRadiusMenu = !showRadiusMenu },
                    label = {
                        Text(
                            if (selectedRadius == null) "Distance"
                            else "< ${selectedRadius}km"
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedRadius != null,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderColor = MaterialTheme.colorScheme.outline
                    )
                )

                DropdownMenu(
                    expanded = showRadiusMenu,
                    onDismissRequest = { showRadiusMenu = false }
                ) {
                    listOf(5, 10, 20, 50, 100).forEach { radius ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (selectedRadius == radius) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_map_pin),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.width(8.dp))
                                    } else {
                                        Spacer(Modifier.width(24.dp))
                                    }
                                    Text("${radius} km")
                                }
                            },
                            onClick = {
                                selectedRadius = radius
                                showRadiusMenu = false
                            }
                        )
                    }
                    if (selectedRadius != null) {
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Réinitialiser", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                selectedRadius = null
                                showRadiusMenu = false
                            }
                        )
                    }
                }
            }
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
                onAdd = { name: String, category: SpotCategory, spotLat: Double, spotLng: Double ->
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
                        painter = painterResource(R.drawable.logosnd),
                        contentDescription = stringResource(R.string.cd_logo),
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
    SpotUi("1", "Buttes Chaumont", 48.8809, 2.3819, SpotCategory.HIKING, difficulty = SpotDifficulty.EASY),
    SpotUi("2", "Fontainebleau", 48.4047, 2.7016, SpotCategory.CLIMBING, difficulty = SpotDifficulty.HARD),
    SpotUi("3", "Petite Ceinture", 48.8566, 2.3522, SpotCategory.URBEX, difficulty = SpotDifficulty.MEDIUM),
    SpotUi("4", "Bois de Vincennes", 48.8275, 2.4324, SpotCategory.HIKING, difficulty = SpotDifficulty.EASY),
    SpotUi("5", "Parc des Buttes", 48.8800, 2.3850, SpotCategory.EXPLORATION, difficulty = SpotDifficulty.MEDIUM),
    SpotUi("6", "Montmartre", 48.8867, 2.3431, SpotCategory.URBEX, difficulty = SpotDifficulty.HARD)
)