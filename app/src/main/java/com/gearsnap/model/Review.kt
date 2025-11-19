package com.gearsnap.model

data class Review(
    val id: String = "",
    val spotId: String = "",
    val authorId: String? = null,
    val authorName: String? = null,
    val rating: Int = 0,
    val comment: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
