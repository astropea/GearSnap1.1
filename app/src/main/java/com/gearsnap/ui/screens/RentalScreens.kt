package com.gearsnap.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.gearsnap.R
import com.gearsnap.data.RentalItem
import androidx.compose.foundation.background
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gearsnap.ui.viewmodel.RentalViewModel
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LouerScreen(
    viewModel: RentalViewModel = viewModel(),
    onCreateClick: () -> Unit = {},
    onOpenDetail: (String) -> Unit = {}
) {
    val state by viewModel.filteredItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val search by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Search bar
            OutlinedTextField(
                value = search,
                onValueChange = { viewModel.search(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Rechercher un article…") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            // Filters row
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Category dropdown simplified as text button to open menu
                var expandedCat by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expandedCat, onExpandedChange = { expandedCat = it }) {
                    OutlinedTextField(
                        value = selectedCategory ?: "Toutes les catégories",
                        onValueChange = {},
                        modifier = Modifier
                            .weight(1f),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) }
                    )
                    ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                        val categories = listOf("Toutes", "Vélo", "Camping", "Escalade", "Autre")
                        categories.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat) }, onClick = {
                                viewModel.selectedCategory.value = if (cat == "Toutes") null else cat
                                expandedCat = false
                            })
                        }
                    }
                }

                // Sort menu
                var expandedSort by remember { mutableStateOf(false) }
                IconButton(onClick = { expandedSort = true }) { Icon(painter = painterResource(id = R.drawable.ic_map_pin), contentDescription = "Trier") }
                DropdownMenu(expanded = expandedSort, onDismissRequest = { expandedSort = false }) {
                    DropdownMenuItem(text = { Text("Prix croissant") }, onClick = { viewModel.sortByPriceAsc(); expandedSort = false })
                    DropdownMenuItem(text = { Text("Prix décroissant") }, onClick = { viewModel.sortByPriceDesc(); expandedSort = false })
                    DropdownMenuItem(text = { Text("Plus récent") }, onClick = { viewModel.sortByDate(); expandedSort = false })
                    DropdownMenuItem(text = { Text("Meilleure note") }, onClick = { viewModel.sortByRating(); expandedSort = false })
                }
            }

            // Price range slider
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                val range by viewModel.priceRange.collectAsState()
                Text(text = "Prix / jour: ${range.start.toInt()}€ - ${range.endInclusive.toInt()}€", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp))
                RangeSlider(value = range, onValueChange = { newRange -> viewModel.priceRange.value = newRange }, valueRange = 0f..200f)
            }

            // List
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state) { item ->
                        RentalCard(item = item, onClick = { onOpenDetail(item.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun RentalCard(item: RentalItem, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick), shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Placeholder image
            Box(modifier = Modifier.size(96.dp).background(MaterialTheme.colorScheme.surfaceVariant)) {
                Image(painter = painterResource(id = R.drawable.logosnd), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("${item.pricePerDay}€/j", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text(item.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.ownerName, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.width(8.dp))
                    Text("⭐ ${item.ownerRating}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun CreateRentalScreen(
    viewModel: RentalViewModel = viewModel(),
    onPublished: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf(0.0) }
    var category by remember { mutableStateOf("Autre") }
    var condition by remember { mutableStateOf("Bon") }
    var pickedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isPublishing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Image picker (GetMultipleContents) pour compatibilité large
    val fallbackLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        pickedUris = uris.take(4)
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        OutlinedTextField(value = if (price == 0.0) "" else price.toString(), onValueChange = { price = it.toDoubleOrNull() ?: 0.0 }, label = { Text("Prix / jour") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Catégorie") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = condition, onValueChange = { condition = it }, label = { Text("État") }, modifier = Modifier.weight(1f))
        }

        // Photo picker: open gallery to select up to 4 images
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { fallbackLauncher.launch("image/*") }, modifier = Modifier.weight(1f)) { Text("Sélectionner des photos") }
            Text("${pickedUris.size}/4")
        }

        // Preview selected local images
        if (pickedUris.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pickedUris.forEach { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.logosnd),
                        error = painterResource(R.drawable.logosnd)
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        val snackbarHostState = remember { SnackbarHostState() }
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                if (isPublishing) {
                    Button(onClick = { }, enabled = false, modifier = Modifier.fillMaxWidth()) { Text("Publication en cours...") }
                } else {
                    Button(onClick = {
                        // Publier: uploader les photos puis créer l'annonce
                        val uris = pickedUris
                        isPublishing = true
                        coroutineScope.launch {
                            try {
                                val uploadedUrls = mutableListOf<String>()
                                for (u in uris.take(4)) { // max 4
                                    try {
                                        val url = viewModel.uploadPhoto(u)
                                        uploadedUrls.add(url)
                                    } catch (e: Exception) {
                                        // Afficher le message d'erreur et arrêter la publication
                                        val msg = e.message ?: "Erreur lors de l'upload d'une image"
                                        snackbarHostState.showSnackbar(msg)
                                        isPublishing = false
                                        return@launch
                                    }
                                }

                                // Récupérer l'utilisateur courant via le ViewModel helpers
                                val currentUserId = viewModel.getCurrentUserId()
                                val currentUserName = viewModel.getCurrentUserName()

                                val item = RentalItem(
                                    id = "",
                                    title = title,
                                    description = description,
                                    pricePerDay = price,
                                    category = category,
                                    condition = condition,
                                    photos = uploadedUrls,
                                    location = "Inconnu",
                                    ownerId = currentUserId ?: "anonymous",
                                    ownerName = currentUserName ?: "Moi",
                                    ownerRating = 0.0
                                )

                                val success = viewModel.createRental(item)
                                if (success) {
                                    snackbarHostState.showSnackbar("Annonce publiée")
                                    onPublished()
                                } else {
                                    snackbarHostState.showSnackbar("Erreur: impossible de publier")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                snackbarHostState.showSnackbar("Erreur lors de l'upload")
                            } finally {
                                isPublishing = false
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Publier") }
                }
            }
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
fun RentalDetailScreen(
    itemId: String,
    viewModel: RentalViewModel = viewModel(),
    onContact: (String) -> Unit = {}
) {
    val items by viewModel.allItems.collectAsState()
    val item = items.firstOrNull { it.id == itemId } ?: return

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Carousel: use HorizontalPager to show all photos (swipeable)
        val photos = item.photos
        if (photos.isNotEmpty()) {
            val pagerState = rememberPagerState(initialPage = 0) { photos.size }
            HorizontalPager(state = pagerState, modifier = Modifier.height(260.dp).fillMaxWidth()) { page ->
                AsyncImage(
                    model = photos[page],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.logosnd),
                    error = painterResource(R.drawable.logosnd)
                )
            }
            // pager indicator can be added here if desired
        } else {
            Box(modifier = Modifier.height(220.dp).fillMaxWidth()) {
                Image(painter = painterResource(R.drawable.logosnd), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
        }

        Text(item.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(item.description, style = MaterialTheme.typography.bodyMedium)
        Text("${item.pricePerDay}€/jour", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Text(item.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(item.ownerName, style = MaterialTheme.typography.titleMedium)
                Text("⭐ ${item.ownerRating}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            Text(sdf.format(Date(item.datePosted)), style = MaterialTheme.typography.bodySmall)
        }

        Button(onClick = { onContact(item.ownerId) }, modifier = Modifier.fillMaxWidth()) { Text("Contacter le loueur") }
    }
}

@Composable
fun ContactOwnerScreen(ownerId: String, viewModel: RentalViewModel = viewModel(), onSent: () -> Unit = {}) {
    var message by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Contact: $ownerId", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth(), minLines = 4)
        Spacer(Modifier.weight(1f))
        if (isSending) {
            Button(onClick = { }, enabled = false, modifier = Modifier.fillMaxWidth()) { Text("Envoi...") }
        } else {
            Button(onClick = {
                val fromId = viewModel.getCurrentUserId() ?: "anonymous"
                isSending = true
                coroutineScope.launch {
                    try {
                        val ok = viewModel.sendMessage(toId = ownerId, fromId = fromId, text = message)
                        if (ok) onSent()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        isSending = false
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Envoyer") }
        }
    }
}

@Composable
fun RentScreen(navToDetail: (String) -> Unit = {}, navToCreate: () -> Unit = {}) {
    LouerScreen(onCreateClick = navToCreate, onOpenDetail = navToDetail)
}
