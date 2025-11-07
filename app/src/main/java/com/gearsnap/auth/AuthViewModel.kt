package com.gearsnap.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Placeholder pour AppleSignInService (non implémenté sur Android)
class AppleSignInService

/**
 * ViewModel pour la gestion de l'authentification
 * Utilise Firebase Auth pour les vraies connexions
 */
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val googleSignInService: GoogleSignInService? = null,
    private val appleSignInService: AppleSignInService? = null
) : ViewModel() {

    private var _isAuthenticated by mutableStateOf(false)
    val isAuthenticated: Boolean get() = _isAuthenticated

    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading

    private var _errorMessage by mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage

    // Vérifier l'état d'authentification au démarrage
    init {
        checkAuthState()
    }

    /**
     * Vérifier l'état d'authentification au démarrage
     */
    private fun checkAuthState() {
        _isAuthenticated = authRepository.isUserSignedIn()
    }

    /**
     * Authentification avec email et mot de passe
     */
    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _isLoading = true
            _errorMessage = null

            try {
                val result = authRepository.loginWithEmail(email, password)
                if (result.user != null) {
                    setAuthenticated(true)
                } else {
                    throw Exception("Échec de la connexion")
                }
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Erreur de connexion"
            } finally {
                _isLoading = false
            }
        }
    }

    /**
     * Inscription avec email et mot de passe
     */
    fun register(email: String, password: String, name: String? = null) {
        viewModelScope.launch {
            _isLoading = true
            _errorMessage = null

            try {
                val result = authRepository.registerWithEmail(email, password, name)
                if (result.user != null) {
                    setAuthenticated(true)
                } else {
                    throw Exception("Échec de l'inscription")
                }
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Erreur d'inscription"
            } finally {
                _isLoading = false
            }
        }
    }

    /**
     * Connexion avec Google
     * Retourne l'intent à lancer pour l'activité de connexion
     */
    fun onGoogleSignIn(): android.content.Intent? {
        return if (googleSignInService != null) {
            try {
                googleSignInService.getSignInIntent()
            } catch (e: Exception) {
                _errorMessage = "Erreur Google Sign-In: ${e.message}"
                null
            }
        } else {
            _errorMessage = "Google Sign-In non configuré. Voir README pour la configuration."
            null
        }
    }

    /**
     * Traiter le résultat de Google Sign-In
     */
    fun onGoogleSignInResult(data: android.content.Intent?) {
        if (googleSignInService == null) {
            _errorMessage = "Google Sign-In non configuré"
            return
        }

        viewModelScope.launch {
            _isLoading = true
            _errorMessage = null

            try {
                // Récupérer le token ID depuis Google
                val idToken = googleSignInService.handleSignInResult(data)

                // Authentifier avec Firebase
                val result = authRepository.signInWithGoogle(idToken)

                if (result.user != null) {
                    setAuthenticated(true)
                } else {
                    throw Exception("Échec de la connexion Google")
                }
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Erreur de connexion Google"
            } finally {
                _isLoading = false
            }
        }
    }

    /**
     * Connexion avec Apple
     */
    fun onAppleSignIn() {
        // Temporairement désactivé
        _errorMessage = "Apple Sign-In nécessite une configuration supplémentaire sur Android"
    }

    /**
     * Déconnexion
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                // Déconnexion Firebase
                authRepository.signOut()

                // Déconnexion Google si configuré
                googleSignInService?.signOut()

                setAuthenticated(false)
            } catch (e: Exception) {
                _errorMessage = "Erreur de déconnexion: ${e.message}"
            }
        }
    }

    /**
     * Envoyer un email de réinitialisation
     */
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _isLoading = true
            _errorMessage = null

            try {
                authRepository.sendPasswordResetEmail(email)
                _errorMessage = "Email de réinitialisation envoyé !"
            } catch (e: Exception) {
                _errorMessage = e.message ?: "Erreur d'envoi"
            } finally {
                _isLoading = false
            }
        }
    }

    /**
     * Définit l'état d'authentification
     */
    private fun setAuthenticated(authenticated: Boolean) {
        _isAuthenticated = authenticated
        // TODO: Sauvegarder dans DataStore pour persistance
        // dataStore.saveAuthState(authenticated)
    }

    /**
     * Efface les messages d'erreur
     */
    fun clearError() {
        _errorMessage = null
    }
}