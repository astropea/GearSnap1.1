package com.gearsnap.auth

import android.content.Context
import android.content.Intent
import com.gearsnap.R
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Tasks

/**
 * Service pour gérer Google Sign-In
 *
 * Configuration requise :
 * 1. Ajouter le SHA-1 dans Firebase Console
 * 2. Télécharger le nouveau google-services.json
 * 3. Configurer le Web Client ID dans strings.xml (google_web_client_id)
 */
class GoogleSignInService(private val context: Context) {
    private val googleSignInClient: GoogleSignInClient

    init {
        // Récupérer le Web Client ID depuis strings.xml
        val webClientId = context.getString(R.string.google_web_client_id)

        // Configuration Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    /**
     * Lancer l'intent de connexion Google
     */
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    /**
     * Résultat de l'activité Google Sign-In
     * Retourne le token ID pour l'authentification Firebase
     */
    suspend fun handleSignInResult(data: Intent?): String {
        return suspendCancellableCoroutine { continuation ->
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken != null) {
                    continuation.resume(idToken)
                } else {
                    continuation.resumeWithException(Exception("Token ID null"))
                }
            } catch (e: ApiException) {
                when (e.statusCode) {
                    12501 -> continuation.resumeWithException(Exception("Connexion annulée"))
                    12502 -> continuation.resumeWithException(Exception("Connexion requise"))
                    else -> continuation.resumeWithException(Exception("Erreur Google Sign-In: ${e.message}"))
                }
            }
        }
    }

    /**
     * Déconnexion Google
     */
    suspend fun signOut() {
        try {
            Tasks.await(googleSignInClient.signOut())
        } catch (e: Exception) {
            // Ignorer l'erreur de déconnexion
        }
    }

    /**
     * Révoquer les permissions Google
     */
    suspend fun revokeAccess() {
        try {
            Tasks.await(googleSignInClient.revokeAccess())
        } catch (e: Exception) {
            // Ignorer l'erreur de révocation
        }
    }
}
