package com.gearsnap.model

data class Equipment(
    val id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val description: String = "",
    val dailyPrice: Double = 0.0,
    val lat: Double? = null,
    val lng: Double? = null,
    val available: Boolean = true
)