package com.gearsnap.data

import java.util.*

// Modèle de données pour une annonce de location, compatible Firestore
data class RentalItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val pricePerDay: Double = 0.0,
    val category: String = "",
    val condition: String = "",
    val photos: List<String> = emptyList(), // URLs
    val location: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerRating: Double = 0.0,
    val datePosted: Long = System.currentTimeMillis()
)
