package com.gearsnap.model

import java.time.LocalDate
import java.util.UUID

data class ActivityItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val time: String, // HH:MM
    val date: LocalDate,
)
