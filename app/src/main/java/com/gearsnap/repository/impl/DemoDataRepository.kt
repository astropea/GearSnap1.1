package com.gearsnap.repository.impl

import com.gearsnap.model.*
import com.gearsnap.repository.DataRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DemoDataRepository: DataRepository {
    override fun streamSpotsNearby(lat: Double, lng: Double): Flow<List<Spot>> = flow {
        while(true) {
            emit(listOf(
                Spot(id="s1", name="Falaises de Nolay", lat=46.95, lng=4.63, sport="Escalade", difficulty="5c"),
                Spot(id="s2", name="Boucle Vouglans", lat=46.47, lng=5.62, sport="Randonnée", difficulty="Moyen")
            ))
            delay(5000)
        }
    }
    override fun streamFeed(): Flow<List<Message>> = flow {
        emit(listOf(Message(id="m1", fromId="u1", toId="all", content="Sortie grimpe dimanche !", timestamp=System.currentTimeMillis())))
    }
    override suspend fun listEquipmentNearby(lat: Double, lng: Double): List<Equipment> = listOf(
        Equipment(id="e1", ownerId="u2", title="Baudrier Petzl", description="Taille M", dailyPrice=5.0),
        Equipment(id="e2", ownerId="u3", title="Corde 60m", description="Beal 9.8", dailyPrice=8.0)
    )
    override suspend fun createEvent(event: Event): String = "evt_demo_123"
    override suspend fun likePost(messageId: String) { /* no-op */ }
}