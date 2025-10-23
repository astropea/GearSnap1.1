package com.gearsnap.model

data class User(
    val id: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val sports: List<String> = emptyList(),
    val badges: List<Badge> = emptyList(),
    val level: Int = 1
)