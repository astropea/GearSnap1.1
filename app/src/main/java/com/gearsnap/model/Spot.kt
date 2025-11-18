package com.gearsnap.model

data class Spot(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val sport: String = "",
    val difficulty: String = "",
    val photos: List<String> = emptyList(),
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val creatorId: String = ""
)
