package com.gearsnap.model

data class Message(
    val id: String = "",
    val fromId: String = "",
    val toId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)