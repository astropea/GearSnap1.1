package com.gearsnap.repository

import com.gearsnap.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInAnonymously(): User
    suspend fun signOut()
}