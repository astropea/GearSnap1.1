package com.gearsnap.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.launch
import coil.compose.rememberAsyncImagePainter
import com.gearsnap.model.Photo
import com.gearsnap.model.Review
import com.gearsnap.model.Spot
import com.gearsnap.ui.screens.SpotUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpotDetailSheet(
    spot: Spot?,
    fallbackSpot: SpotUi?,
    photos: List<Photo>,
    reviews: List<Review>,
    isLoading: Boolean,
    onAddPhotoClick: () -> Unit,
    onAddReview: (rating: Int, comment: String) -> Unit
) {
    val displaySpot = spot ?: fallbackSpot?.let {
        Spot(
            id = it.id,
            name = it.name,
            description = it.description,
            lat = it.lat,
            lng = it.lng,
            sport = it.category.name,
            difficulty = it.difficulty.name,
            rating = it.rating,
            ratingCount = it.ratingCount
        )
    }

    val reviewsCount = if (reviews.isNotEmpty()) reviews.size else displaySpot?.ratingCount ?: 0
    val reviewsAverage = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else displaySpot?.rating ?: 0.0

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentSpotId = displaySpot?.id ?: fallbackSpot?.id ?: "unknown"
    val reviewSectionIndex = 2 // items: card=0, photos=1, title avis=2

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = listState
    ) {
        if (isLoading && reviews.isEmpty() && displaySpot == null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    val headerPhotos = photos
                    val configuration = LocalConfiguration.current
                    val imageWidth = (configuration.screenWidthDp.dp - 48.dp).coerceAtLeast(160.dp)
                    val pagerState = rememberPagerState(
                        initialPage = 0,
                        pageCount = { headerPhotos.size.coerceAtLeast(1) }
                    )
                    LaunchedEffect(currentSpotId, headerPhotos.size) {
                        pagerState.scrollToPage(0)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(240.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        if (headerPhotos.isNotEmpty()) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .width(imageWidth)
                                    .height(220.dp)
                            ) { page ->
                                val photo = headerPhotos[page]
                                Image(
                                    painter = rememberAsyncImagePainter(photo.url),
                                    contentDescription = "Photo du spot",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .clip(MaterialTheme.shapes.large),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            if (headerPhotos.size > 1) {
                                IconButton(
                                    onClick = {
                                        val target = if (pagerState.currentPage > 0) pagerState.currentPage - 1 else headerPhotos.lastIndex
                                        scope.launch { pagerState.animateScrollToPage(target) }
                                    },
                                    modifier = Modifier.align(Alignment.CenterStart)
                                ) {
                                    Icon(Icons.Default.ChevronLeft, contentDescription = "Photo precedente")
                                }
                                IconButton(
                                    onClick = {
                                        val target = if (pagerState.currentPage < headerPhotos.lastIndex) pagerState.currentPage + 1 else 0
                                        scope.launch { pagerState.animateScrollToPage(target) }
                                    },
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                ) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = "Photo suivante")
                                }
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    headerPhotos.forEachIndexed { dotIndex, _ ->
                                        val selected = pagerState.currentPage == dotIndex
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                                .height(6.dp)
                                                .width(6.dp)
                                                .clip(MaterialTheme.shapes.small)
                                                .background(
                                                    if (selected) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            displaySpot?.name ?: "Spot",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RatingRow(rating = reviewsAverage)
                            Text(
                                text = String.format("%.1f / 5 (%d avis)", reviewsAverage, reviewsCount),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = displaySpot?.sport ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        Text(
                            displaySpot?.description?.ifBlank { "Pas de description." }
                                ?: "Pas de description.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TextButton(onClick = {
                                displaySpot?.let { s ->
                                    val uri = Uri.parse("geo:${s.lat},${s.lng}?q=${s.lat},${s.lng}(${Uri.encode(s.name)})")
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    context.startActivity(intent)
                                }
                            }) {
                                Text("Itineraire")
                            }
                            TextButton(onClick = {
                                scope.launch { listState.animateScrollToItem(2) }
                            }) {
                                Text("Avis")
                            }
                        }
                    }
                }
            }
        }

        // Photos
        item {
            Text("Photos", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(photos) { photo ->
                    Image(
                        painter = rememberAsyncImagePainter(photo.url),
                        contentDescription = "Photo",
                        modifier = Modifier
                            .height(120.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }
                item {
                    Button(onClick = onAddPhotoClick) {
                        Text("Ajouter une photo")
                    }
                }
            }
        }

        // Avis
        item { Text("Avis (${reviewsCount})", style = MaterialTheme.typography.titleMedium) }

        // Ajouter un avis
        item {
            var rating by remember { mutableIntStateOf(4) }
            var comment by remember { mutableStateOf("") }
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Ajouter un avis", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        value = rating.toFloat(),
                        onValueChange = { rating = it.toInt().coerceIn(1, 5) },
                        valueRange = 1f..5f,
                        steps = 3,
                        modifier = Modifier.weight(1f)
                    )
                    Text("$rating/5", modifier = Modifier.padding(start = 8.dp))
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Commentaire") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        onAddReview(rating, comment.trim())
                        comment = ""
                        rating = 4
                    },
                    enabled = !isLoading && comment.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.height(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Publier")
                    }
                }
            }
        }

        // Liste des avis
        if (reviews.isEmpty()) {
            item { Text("Aucun avis pour ce spot. Soyez le premier a en laisser un !", style = MaterialTheme.typography.bodySmall) }
        } else {
            items(reviews, key = { it.id }) { review ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val dateText = formatReviewDate(review.createdAt)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    RatingRow(rating = review.rating.toDouble())
                                    Text(
                                        "${review.rating}/5",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                dateText?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        review.authorName?.takeIf { it.isNotBlank() }?.let { author ->
                            Text(
                                "par $author",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                            if (review.comment.isNotBlank()) {
                                Text(
                                    review.comment,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            review.photoUrl?.let {
                                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentDescription = "Photo avis",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingRow(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(5) { idx ->
            val filled = rating >= idx + 1
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

private fun formatReviewDate(timestamp: Long?): String? {
    if (timestamp == null || timestamp <= 0) return null
    return try {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        formatter.format(Date(timestamp))
    } catch (_: Exception) {
        null
    }
}
