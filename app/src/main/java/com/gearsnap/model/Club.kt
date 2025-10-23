package com.gearsnap.model

data class Club(
    val id: String = "",
    val name: String = "",
    val members: List<String> = emptyList(),
    val adminIds: List<String> = emptyList()
)