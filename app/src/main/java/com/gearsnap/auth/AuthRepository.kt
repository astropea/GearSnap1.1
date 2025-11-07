package com.gearsnap.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await
import com.google.firebase.FirebaseException
import java.io.IOException

/**
 * Repository pour gérer l'authentification Firebase
 * Gère l'authentification par email, Google et Apple
 */
class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /**
     * Connexion avec email et mot de passe
     */
    suspend fun loginWithEmail(email: String, password: String): AuthResult {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseException) {
            throw Exception("Erreur de connexion Firebase: ${e.message}")
        } catch (e: IOException) {
            throw Exception("Erreur réseau: Vérifiez votre connexion")
        }
    }

    /**
     * Inscription avec email et mot de passe
     */
    suspend fun registerWithEmail(email: String, password: String, name: String? = null): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            // Optionnel: Mettre à jour le profil avec le nom
            name?.let {
                result.user?.updateProfile(
                    com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                )?.await()
            }

            result
        } catch (e: FirebaseException) {
            val message = e.message
            when {
                message?.contains("email-already-in-use") == true ->
                    throw Exception("Cette adresse e-mail est déjà utilisée")
                message?.contains("invalid-email") == true ->
                    throw Exception("Adresse e-mail invalide")
                message?.contains("weak-password") == true ->
                    throw Exception("Mot de passe trop faible")
                else ->
                    throw Exception("Erreur d'inscription: ${message ?: "Erreur inconnue"}")
            }
        } catch (e: IOException) {
            throw Exception("Erreur réseau: Vérifiez votre connexion")
        }
    }

    /**
     * Connexion avec Google
     */
    suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
        } catch (e: FirebaseException) {
            throw Exception("Erreur de connexion Google: ${e.message}")
        } catch (e: IOException) {
            throw Exception("Erreur réseau: Vérifiez votre connexion")
        }
    }

    /**
     * Déconnexion
     */
    suspend fun signOut() {
        firebaseAuth.signOut()
    }

    /**
     * Récupérer l'utilisateur actuellement connecté
     */
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    /**
     * Vérifier si un utilisateur est connecté
     */
    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    /**
     * Envoyer un email de réinitialisation de mot de passe
     */
    suspend fun sendPasswordResetEmail(email: String) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (e: FirebaseException) {
            throw Exception("Erreur d'envoi: ${e.message}")
        } catch (e: IOException) {
            throw Exception("Erreur réseau: Vérifiez votre connexion")
        }
    }

    /**
     * Supprimer un compte utilisateur
     */
    suspend fun deleteAccount() {
        try {
            firebaseAuth.currentUser?.delete()?.await()
        } catch (e: FirebaseException) {
            throw Exception("Erreur de suppression: ${e.message}")
        } catch (e: IOException) {
            throw Exception("Erreur réseau: Vérifiez votre connexion")
        }
    }
}