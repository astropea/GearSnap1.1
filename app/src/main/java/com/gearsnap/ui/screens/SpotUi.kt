package com.gearsnap.ui.screens

import androidx.annotation.DrawableRes
import com.gearsnap.R

data class SpotUi(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val category: SpotCategory,
    val isFavorite: Boolean = false,
    val difficulty: SpotDifficulty = SpotDifficulty.MEDIUM
)

enum class SpotCategory(@DrawableRes val iconRes: Int, val display: String) {
    HIKING(R.drawable.ic_map_pin, "Randonnée"),
    CLIMBING(R.drawable.ic_map_pin, "Escalade"),
    URBEX(R.drawable.ic_map_pin, "Urbex"),
    EXPLORATION(R.drawable.ic_map_pin, "Exploration")
}

enum class SpotDifficulty(val display: String) {
    EASY("Facile"),
    MEDIUM("Moyen"),
    HARD("Difficile")
}
