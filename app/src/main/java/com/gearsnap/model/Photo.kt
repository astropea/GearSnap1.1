package com.gearsnap.model

data class Photo(
    val id: String = "",
    val spotId: String = "",
    val url: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
