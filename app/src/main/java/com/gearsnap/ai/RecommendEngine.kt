package com.gearsnap.ai

import com.gearsnap.model.*

object RecommendEngine {
    // TODO: brancher un modèle TFLite ou une API distante.
    fun recommendSpots(user: User, all: List<Spot>): List<Spot> {
        return all.sortedByDescending { it.rating }.take(5)
    }
    fun recommendEquipment(user: User, all: List<Equipment>): List<Equipment> {
        return all.take(5)
    }
    fun recommendEvents(user: User, all: List<Event>): List<Event> {
        return all.take(5)
    }
}