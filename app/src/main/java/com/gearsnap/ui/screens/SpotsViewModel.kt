package com.gearsnap.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gearsnap.data.SpotRepository
import com.gearsnap.model.Photo
import com.gearsnap.model.Review
import com.gearsnap.model.Spot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AddSpotUIState {
    object Idle : AddSpotUIState()
    object Loading : AddSpotUIState()
    data class Success(val spot: Spot) : AddSpotUIState()
    data class Error(val message: String) : AddSpotUIState()
}

data class SpotDetailUi(
    val spot: Spot? = null,
    val reviews: List<Review> = emptyList(),
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SpotsViewModel(
    private val repository: SpotRepository = SpotRepository()
) : ViewModel() {
    private val _addSpotState = MutableStateFlow<AddSpotUIState>(AddSpotUIState.Idle)
    val addSpotState: StateFlow<AddSpotUIState> = _addSpotState

    private val _spots = MutableStateFlow<List<SpotUi>>(emptyList())
    val spots: StateFlow<List<SpotUi>> = _spots

    private val _detail = MutableStateFlow(SpotDetailUi())
    val detail: StateFlow<SpotDetailUi> = _detail

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    init {
        refreshSpots()
    }

    fun refreshSpots() {
        viewModelScope.launch {
            try {
                val fromRemote = repository.getSpots()
                _spots.value = fromRemote.map { toSpotUi(it) }
            } catch (e: Exception) {
                // ignore for now
            }
        }
    }

    fun selectSpot(spotId: String) {
        viewModelScope.launch {
            _detail.value = _detail.value.copy(isLoading = true, error = null)
            try {
                val spot = runCatching { repository.getSpotById(spotId) }.getOrNull()
                val reviews = runCatching { repository.getReviews(spotId) }.getOrDefault(emptyList())
                val photos = runCatching { repository.getPhotos(spotId) }.getOrDefault(emptyList())
                _reviews.value = reviews
                _detail.value = SpotDetailUi(
                    spot = spot,
                    reviews = reviews,
                    photos = photos,
                    isLoading = false
                )
            } catch (e: Exception) {
                _reviews.value = emptyList()
                _detail.value = SpotDetailUi(error = e.message, isLoading = false)
            }
        }
    }

    fun addSpot(
        name: String,
        category: SpotCategory,
        difficulty: String,
        lat: Double,
        lng: Double,
        description: String = "",
        photoUris: List<android.net.Uri> = emptyList()
    ) {
        viewModelScope.launch {
            _addSpotState.value = AddSpotUIState.Loading
            try {
                val normalizedDifficulty = mapDifficultyLabel(difficulty)
                val spot = Spot(
                    id = "",
                    name = name,
                    description = description,
                    lat = lat,
                    lng = lng,
                    sport = category.name,
                    difficulty = normalizedDifficulty.name,
                    photos = emptyList(),
                    rating = 0.0
                )
                val created = repository.createSpot(spot)
                created?.let { newSpot ->
                    if (photoUris.isNotEmpty()) {
                        addPhotosInternal(newSpot.id, photoUris)
                    }
                    val refreshedSpot = repository.getSpotById(newSpot.id) ?: newSpot
                    _spots.value = _spots.value + toSpotUi(refreshedSpot)
                    _addSpotState.value = AddSpotUIState.Success(refreshedSpot)
                } ?: run {
                    _addSpotState.value = AddSpotUIState.Error("Impossible de creer le spot.")
                }
            } catch (e: Exception) {
                _addSpotState.value = AddSpotUIState.Error(e.message ?: "Une erreur est survenue.")
            }
        }
    }

    fun addReview(spotId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            try {
                repository.addReview(spotId, rating, comment)
                selectSpot(spotId)
            } catch (_: Exception) { }
        }
    }

    fun addPhoto(spotId: String, uri: Uri) {
        viewModelScope.launch {
            try {
                val added = repository.addPhoto(spotId, uri)
                // Mise à jour optimiste de l'UI pendant que l'on recharge
                _detail.value = _detail.value.copy(photos = listOf(added) + _detail.value.photos)
                selectSpot(spotId)
            } catch (_: Exception) { }
        }
    }

    fun addPhotos(spotId: String, uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            try {
                addPhotosInternal(spotId, uris)
                selectSpot(spotId)
            } catch (_: Exception) { }
        }
    }

    private suspend fun addPhotosInternal(spotId: String, uris: List<Uri>) {
        uris.forEach { uri ->
            runCatching { repository.addPhoto(spotId, uri) }
                .onSuccess { added ->
                    _detail.value = _detail.value.copy(photos = listOf(added) + _detail.value.photos)
                }
        }
    }

    fun updateSpotFields(
        spotId: String,
        description: String? = null,
        sport: String? = null,
        difficulty: String? = null
    ) {
        viewModelScope.launch {
            try {
                repository.updateSpot(spotId, description, sport, difficulty)
                selectSpot(spotId)
            } catch (_: Exception) { }
        }
    }

    fun loadReviews(spotId: String) {
        viewModelScope.launch {
            _detail.value = _detail.value.copy(isLoading = true, error = null)
            try {
                val reviews = repository.getReviews(spotId)
                _reviews.value = reviews
                _detail.value = _detail.value.copy(reviews = reviews, isLoading = false)
            } catch (e: Exception) {
                _reviews.value = emptyList()
                _detail.value = _detail.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun resetAddSpotState() {
        _addSpotState.value = AddSpotUIState.Idle
    }

    private fun mapDifficultyLabel(label: String): SpotDifficulty =
        when (label.lowercase()) {
            "facile" -> SpotDifficulty.EASY
            "moyen", "moyenne" -> SpotDifficulty.MEDIUM
            "difficile", "dur" -> SpotDifficulty.HARD
            "expert" -> SpotDifficulty.HARD
            else -> SpotDifficulty.MEDIUM
        }

    private fun toSpotUi(spot: Spot): SpotUi {
        val category = runCatching { SpotCategory.valueOf(spot.sport.uppercase()) }.getOrDefault(SpotCategory.HIKING)
        val difficulty = runCatching { SpotDifficulty.valueOf(spot.difficulty.uppercase()) }.getOrDefault(SpotDifficulty.MEDIUM)
        return SpotUi(
            id = spot.id,
            name = spot.name,
            lat = spot.lat,
            lng = spot.lng,
            description = spot.description,
            rating = spot.rating,
            ratingCount = spot.ratingCount,
            category = category,
            difficulty = difficulty,
            isFavorite = false
        )
    }
}
