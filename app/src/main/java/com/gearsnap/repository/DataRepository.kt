package com.gearsnap.repository

import com.gearsnap.model.*
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    fun streamSpotsNearby(lat: Double, lng: Double): Flow<List<Spot>>
    fun streamFeed(): Flow<List<Message>>
    suspend fun listEquipmentNearby(lat: Double, lng: Double): List<Equipment>
    suspend fun createEvent(event: Event): String
    suspend fun likePost(messageId: String)
}