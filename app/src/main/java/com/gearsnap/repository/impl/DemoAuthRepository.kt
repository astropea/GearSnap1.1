package com.gearsnap.repository.impl

import com.gearsnap.model.User
import com.gearsnap.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow

class DemoAuthRepository: AuthRepository {
    private val _user = MutableStateFlow<User?>(User(id = "demo", displayName = "Demo User"))
    override val currentUser: Flow<User?> = _user.asStateFlow()
    override suspend fun signInAnonymously(): User {
        val u = User(id = "anon", displayName = "Anonyme")
        _user.value = u
        return u
    }
    override suspend fun signOut() { _user.value = null }
}