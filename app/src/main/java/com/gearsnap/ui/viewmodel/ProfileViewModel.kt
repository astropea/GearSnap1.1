package com.gearsnap.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gearsnap.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Minimal ProfileViewModel implementation to satisfy usages from ProfileScreen.
 * This is intentionally lightweight: it exposes StateFlows and simple stub implementations
 * for upload/toggle/save/sign-out used by the UI. Replace with real logic as needed.
 */
class ProfileViewModel : ViewModel() {
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    fun uploadAvatar(uri: Uri, callback: (success: Boolean, message: String?) -> Unit) {
        // Fake upload: set uploading state, delay, then update user photoUrl and call callback
        viewModelScope.launch {
            _isUploading.value = true
            try {
                // simulate upload delay
                delay(800)
                // Update the user with a mock photo URL (in real app you'd get the remote URL)
                _user.value = _user.value.copy(photoUrl = uri.toString())
                callback(true, null)
            } catch (e: Exception) {
                callback(false, e.message)
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun updateDisplayName(name: String) {
        _user.value = _user.value.copy(displayName = name)
    }

    fun saveChanges() {
        // No-op placeholder for saving changes to backend
    }

    fun signOut() {
        // No-op placeholder for sign-out logic
    }
}

