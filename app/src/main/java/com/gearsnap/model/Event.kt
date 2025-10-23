package com.gearsnap.model

import java.time.LocalDateTime

data class Event(
    val id: String = "",
    val title: String = "",
    val spotId: String? = null,
    val hostId: String = "",
    val start: LocalDateTime = LocalDateTime.now(),
    val end: LocalDateTime = LocalDateTime.now().plusHours(2),
    val participants: List<String> = emptyList()
)